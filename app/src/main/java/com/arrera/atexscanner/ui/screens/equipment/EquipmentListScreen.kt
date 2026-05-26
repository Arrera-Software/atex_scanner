package com.arrera.atexscanner.ui.screens.equipment

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.arrera.atexscanner.data.Equipement
import com.arrera.atexscanner.ui.viewmodel.MainViewModel
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquipmentListScreen(
    zoneId: Long,
    zoneNom: String,
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onLaunchCamera: (String) -> Unit,
    onImageSelected: (String, Uri) -> Unit
) {
    val equipmentsFlow = remember(zoneId) { viewModel.getEquipementsByZone(zoneId) }
    val equipments by equipmentsFlow.collectAsState(initial = emptyList())
    
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    
    val columns = when {
        screenWidth >= 1200 -> 3
        screenWidth >= 600 -> 2
        else -> 1
    }

    var showTagDialog by remember { mutableStateOf(false) }
    var showSourceDialog by remember { mutableStateOf(false) }
    var equipmentTag by remember { mutableStateOf("") }
    
    var equipmentToEdit by remember { mutableStateOf<Equipement?>(null) }
    var equipmentToDelete by remember { mutableStateOf<Equipement?>(null) }
    var fullScreenImagePath by remember { mutableStateOf<String?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            onImageSelected(equipmentTag, it)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(zoneNom, style = MaterialTheme.typography.titleMedium)
                        Text("Équipements & Inspections", style = MaterialTheme.typography.bodySmall)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showTagDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nouvel équipement")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (equipments.isEmpty()) {
                EmptyState()
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(columns),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(equipments) { equipment ->
                        EquipmentCard(
                            equipment = equipment,
                            onEdit = { equipmentToEdit = equipment }
                        )
                    }
                }
            }
        }
    }

    if (showTagDialog) {
        AlertDialog(
            onDismissRequest = { showTagDialog = false },
            title = { Text("Identification du matériel") },
            text = {
                OutlinedTextField(
                    value = equipmentTag,
                    onValueChange = { equipmentTag = it },
                    label = { Text("N° TAG (Obligatoire)") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (equipmentTag.isNotBlank()) {
                            showTagDialog = false
                            showSourceDialog = true
                        }
                    }
                ) { Text("Continuer") }
            },
            dismissButton = {
                TextButton(onClick = { showTagDialog = false }) { Text("Annuler") }
            }
        )
    }

    if (showSourceDialog) {
        AlertDialog(
            onDismissRequest = { showSourceDialog = false },
            title = { Text("Source de la photo") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Voulez-vous prendre une photo ou en choisir une depuis la galerie ?")
                    
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            showSourceDialog = false
                            onLaunchCamera(equipmentTag)
                        }
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Appareil Photo")
                    }
                    
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            showSourceDialog = false
                            galleryLauncher.launch("image/*")
                        }
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Galerie Photos")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSourceDialog = false }) { Text("Fermer") }
            }
        )
    }

    if (equipmentToEdit != null) {
        EquipmentEditDialog(
            equipment = equipmentToEdit!!,
            onDismiss = { equipmentToEdit = null },
            onConfirm = { updatedEquip ->
                viewModel.updateEquipement(updatedEquip)
                equipmentToEdit = null
            },
            onDelete = {
                equipmentToDelete = equipmentToEdit
                equipmentToEdit = null
            },
            onImageClick = { path -> fullScreenImagePath = path }
        )
    }

    if (fullScreenImagePath != null) {
        FullScreenImageDialog(
            photoPath = fullScreenImagePath!!,
            onDismiss = { fullScreenImagePath = null }
        )
    }

    if (equipmentToDelete != null) {
        AlertDialog(
            onDismissRequest = { equipmentToDelete = null },
            title = { Text("Supprimer l'équipement") },
            text = { Text("Êtes-vous sûr de vouloir supprimer l'équipement '${equipmentToDelete!!.tagNumber}' ?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteEquipement(equipmentToDelete!!)
                        equipmentToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Supprimer")
                }
            },
            dismissButton = {
                TextButton(onClick = { equipmentToDelete = null }) {
                    Text("Annuler")
                }
            }
        )
    }
}

@Composable
fun EquipmentEditDialog(
    equipment: Equipement,
    onDismiss: () -> Unit,
    onConfirm: (Equipement) -> Unit,
    onDelete: () -> Unit,
    onImageClick: (String) -> Unit
) {
    var tag by remember { mutableStateOf(equipment.tagNumber) }
    var fabricant by remember { mutableStateOf(equipment.fabricant) }
    var type by remember { mutableStateOf(equipment.typeMateriel) }
    var sn by remember { mutableStateOf(equipment.numeroSerie) }
    var ip by remember { mutableStateOf(equipment.indiceProtection) }
    var annee by remember { mutableStateOf(equipment.anneeFabrication) }
    
    var dirGr by remember { mutableStateOf(equipment.dirGroupe) }
    var dirCat by remember { mutableStateOf(equipment.dirCategorie) }
    var dirAtmo by remember { mutableStateOf(equipment.dirAtmosphere) }
    
    var normProt by remember { mutableStateOf(equipment.normeProtection) }
    var normGr by remember { mutableStateOf(equipment.normeGroupe) }
    var normT by remember { mutableStateOf(equipment.normeTemperature) }
    var normEPL by remember { mutableStateOf(equipment.normeEPL) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Modifier l'équipement")
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = MaterialTheme.colorScheme.error)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Affichage de la photo cliquable pour vérification
                equipment.photoPlaquePath?.let { path ->
                    AsyncImage(
                        model = path,
                        contentDescription = "Photo de la plaque",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(bottom = 8.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium)
                            .clickable { onImageClick(path) }
                    )
                }

                OutlinedTextField(value = tag, onValueChange = { tag = it }, label = { Text("N° TAG") })
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = fabricant, onValueChange = { fabricant = it }, label = { Text("Fabricant") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = type, onValueChange = { type = it }, label = { Text("Type") }, modifier = Modifier.weight(1f))
                }
                OutlinedTextField(value = sn, onValueChange = { sn = it }, label = { Text("S/N") }, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = ip, onValueChange = { ip = it }, label = { Text("IP") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = annee, onValueChange = { annee = it }, label = { Text("Année") }, modifier = Modifier.weight(1f))
                }
                
                Text("Marquage Directives", style = MaterialTheme.typography.titleSmall)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    OutlinedTextField(value = dirGr, onValueChange = { dirGr = it }, label = { Text("Gr") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = dirCat, onValueChange = { dirCat = it }, label = { Text("Cat") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = dirAtmo, onValueChange = { dirAtmo = it }, label = { Text("Atmo") }, modifier = Modifier.weight(1f))
                }
                
                Text("Marquage Normes", style = MaterialTheme.typography.titleSmall)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    OutlinedTextField(value = normProt, onValueChange = { normProt = it }, label = { Text("Prot") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = normGr, onValueChange = { normGr = it }, label = { Text("Gr") }, modifier = Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    OutlinedTextField(value = normT, onValueChange = { normT = it }, label = { Text("T") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = normEPL, onValueChange = { normEPL = it }, label = { Text("EPL") }, modifier = Modifier.weight(1f))
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(equipment.copy(
                    tagNumber = tag,
                    fabricant = fabricant,
                    typeMateriel = type,
                    numeroSerie = sn,
                    indiceProtection = ip,
                    anneeFabrication = annee,
                    dirGroupe = dirGr,
                    dirCategorie = dirCat,
                    dirAtmosphere = dirAtmo,
                    normeProtection = normProt,
                    normeGroupe = normGr,
                    normeTemperature = normT,
                    normeEPL = normEPL
                ))
            }) { Text("Enregistrer") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        }
    )
}

@Composable
fun EmptyState() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Aucun équipement inspecté",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Utilisez le bouton + pour scanner une plaque signalétique et commencer l'inventaire.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun EquipmentCard(equipment: Equipement, onEdit: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TAG: ${equipment.tagNumber}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Modifier", tint = MaterialTheme.colorScheme.primary)
                }
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp)
            
            DetailRow("Type", equipment.typeMateriel)
            DetailRow("Fabricant", equipment.fabricant)
            DetailRow("S/N", equipment.numeroSerie)
            DetailRow("IP", equipment.indiceProtection)
            DetailRow("Année", equipment.anneeFabrication)
            
            Spacer(modifier = Modifier.height(8.dp))
            Text("Marquage Directives", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Text(
                text = "${equipment.dirGroupe} ${equipment.dirCategorie}${equipment.dirAtmosphere}",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            Text("Marquage Normes", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Text(
                text = "${equipment.normeProtection} ${equipment.normeGroupe} ${equipment.normeTemperature} ${equipment.normeEPL}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
        Text(
            text = "$label : ",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(80.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun FullScreenImageDialog(photoPath: String, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .clickable { onDismiss() }
        ) {
            AsyncImage(
                model = photoPath,
                contentDescription = "Photo plein écran",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
            
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .background(Color.Black.copy(alpha = 0.5f), MaterialTheme.shapes.extraLarge)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Fermer", tint = Color.White)
            }
        }
    }
}
