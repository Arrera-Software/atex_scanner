package com.arrera.atexscanner.ui.screens.equipment

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arrera.atexscanner.data.Equipement
import com.arrera.atexscanner.ui.viewmodel.MainViewModel

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
    
    var showTagDialog by remember { mutableStateOf(false) }
    var showSourceDialog by remember { mutableStateOf(false) }
    var equipmentTag by remember { mutableStateOf("") }

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
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                if (equipments.isEmpty()) {
                    EmptyState()
                } else {
                    EquipmentTable(equipments)
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
fun EquipmentTable(equipments: List<Equipement>) {
    val scrollState = rememberScrollState()
    
    Column(modifier = Modifier.horizontalScroll(scrollState)) {
        TableHeader()
        LazyColumn(modifier = Modifier.fillMaxHeight()) {
            items(equipments) { equipment ->
                TableRow(equipment)
                HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
            }
        }
    }
}

@Composable
fun TableHeader() {
    Row(
        modifier = Modifier
            .background(Color.DarkGray)
            .padding(vertical = 8.dp)
    ) {
        TableCell("N° TAG", 100.dp, isHeader = true)
        TableCell("Type", 120.dp, isHeader = true)
        TableCell("Fabricant", 120.dp, isHeader = true)
        TableCell("S/N", 120.dp, isHeader = true)
        TableCell("IP", 60.dp, isHeader = true)
        TableCell("Année", 70.dp, isHeader = true)
        TableCell("Dir. (Gr/Cat/Atm)", 130.dp, isHeader = true)
        TableCell("Norme (Prot/Gr/T/EPL)", 160.dp, isHeader = true)
        TableCell("Verdict", 80.dp, isHeader = true)
    }
}

@Composable
fun TableRow(equipment: Equipement) {
    Row(
        modifier = Modifier.padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TableCell(equipment.tagNumber, 100.dp)
        TableCell(equipment.typeMateriel, 120.dp)
        TableCell(equipment.fabricant, 120.dp)
        TableCell(equipment.numeroSerie, 120.dp)
        TableCell(equipment.indiceProtection, 60.dp)
        TableCell(equipment.anneeFabrication, 70.dp)
        TableCell("${equipment.dirGroupe} ${equipment.dirCategorie}${equipment.dirAtmosphere}", 130.dp)
        TableCell("${equipment.normeProtection} ${equipment.normeGroupe} ${equipment.normeTemperature} ${equipment.normeEPL}", 160.dp)
        
        Text(
            text = "C",
            modifier = Modifier.width(80.dp).padding(4.dp),
            textAlign = TextAlign.Center,
            color = Color(0xFF2E7D32),
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun TableCell(
    text: String,
    width: androidx.compose.ui.unit.Dp,
    isHeader: Boolean = false
) {
    Text(
        text = text,
        modifier = Modifier
            .width(width)
            .padding(horizontal = 8.dp),
        style = if (isHeader) 
            androidx.compose.ui.text.TextStyle(color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp) 
        else 
            androidx.compose.ui.text.TextStyle(fontSize = 12.sp),
        textAlign = TextAlign.Start,
        maxLines = 2
    )
}
