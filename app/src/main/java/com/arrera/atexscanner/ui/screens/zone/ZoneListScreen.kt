package com.arrera.atexscanner.ui.screens.zone

import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import com.arrera.atexscanner.data.ZoneAtex
import com.arrera.atexscanner.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZoneListScreen(
    siteId: Long,
    siteNom: String,
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onZoneClick: (ZoneAtex) -> Unit
) {
    val zonesFlow = remember(siteId) { viewModel.getZonesBySite(siteId) }
    val zones by zonesFlow.collectAsState()
    val context = LocalContext.current
    
    var showAddDialog by remember { mutableStateOf(false) }
    var zoneToEdit by remember { mutableStateOf<ZoneAtex?>(null) }
    var zoneToDelete by remember { mutableStateOf<ZoneAtex?>(null) }
    var showDeleteSiteDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(siteNom, style = MaterialTheme.typography.titleMedium)
                        Text("Zones ATEX", style = MaterialTheme.typography.bodySmall)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.exportSite(context, siteId, siteNom) }) {
                        Icon(
                            Icons.Default.FileDownload,
                            contentDescription = "Exporter en Excel",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = { showDeleteSiteDialog = true }) {
                        Icon(
                            Icons.Default.Delete, 
                            contentDescription = "Supprimer le site",
                            tint = MaterialTheme.colorScheme.error
                        )
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
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nouvelle zone")
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            if (zones.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Aucune zone dans ce site",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Créez votre première zone ATEX pour commencer l'inventaire.",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Créer la première zone")
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(zones) { zone ->
                        ZoneCard(
                            zone = zone,
                            onEdit = { zoneToEdit = zone },
                            onClick = { onZoneClick(zone) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        ZoneDialog(
            title = "Nouvelle Zone ATEX",
            onDismiss = { showAddDialog = false },
            onConfirm = { nom, section, sousSection, typeAtmo, classification, groupe, temp ->
                viewModel.addZone(siteId, nom, section, sousSection, typeAtmo, classification, groupe, temp)
            }
        )
    }

    if (zoneToEdit != null) {
        ZoneDialog(
            title = "Modifier la Zone",
            initialNom = zoneToEdit!!.nom,
            initialSection = zoneToEdit!!.section,
            initialSousSection = zoneToEdit!!.sousSection,
            initialTypeAtmo = zoneToEdit!!.typeAtmosphere,
            initialClassification = zoneToEdit!!.exigenceClassification,
            initialGroupe = zoneToEdit!!.exigenceGroupe,
            initialTemp = zoneToEdit!!.exigenceTemperature,
            onDismiss = { zoneToEdit = null },
            onConfirm = { nom, section, sousSection, typeAtmo, classification, groupe, temp ->
                viewModel.updateZone(zoneToEdit!!.copy(
                    nom = nom,
                    section = section,
                    sousSection = sousSection,
                    typeAtmosphere = typeAtmo,
                    exigenceClassification = classification,
                    exigenceGroupe = groupe,
                    exigenceTemperature = temp
                ))
            },
            onDelete = {
                zoneToDelete = zoneToEdit
                zoneToEdit = null
            }
        )
    }

    if (zoneToDelete != null) {
        AlertDialog(
            onDismissRequest = { zoneToDelete = null },
            title = { Text("Supprimer la zone") },
            text = { Text("Êtes-vous sûr de vouloir supprimer la zone '${zoneToDelete!!.nom}' ? Cela supprimera également tous les équipements et inspections associés.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteZone(zoneToDelete!!)
                        zoneToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Supprimer")
                }
            },
            dismissButton = {
                TextButton(onClick = { zoneToDelete = null }) {
                    Text("Annuler")
                }
            }
        )
    }

    if (showDeleteSiteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteSiteDialog = false },
            title = { Text("Supprimer le site") },
            text = { Text("Êtes-vous sûr de vouloir supprimer le site '$siteNom' ? Cela supprimera TOUTES les zones, équipements et inspections de ce site. Cette action est irréversible.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteSite(siteId, siteNom)
                        showDeleteSiteDialog = false
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Supprimer définitivement")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteSiteDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }
}

@Composable
fun ZoneDialog(
    title: String,
    initialNom: String = "",
    initialSection: String = "",
    initialSousSection: String = "",
    initialTypeAtmo: String = "Gaz",
    initialClassification: String = "0",
    initialGroupe: String = "IIA",
    initialTemp: String = "T1",
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, String, String, String) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    var zoneNom by remember { mutableStateOf(initialNom) }
    var section by remember { mutableStateOf(initialSection) }
    var sousSection by remember { mutableStateOf(initialSousSection) }
    var typeAtmo by remember { mutableStateOf(initialTypeAtmo) }
    var classification by remember { mutableStateOf(if (typeAtmo == "Gaz") initialClassification.ifEmpty { "2" } else initialClassification.ifEmpty { "22" }) }
    var groupe by remember { mutableStateOf(if (typeAtmo == "Gaz") initialGroupe.ifEmpty { "IIB" } else initialGroupe.ifEmpty { "IIIB" }) }
    var temperature by remember { mutableStateOf(if (typeAtmo == "Gaz") initialTemp.ifEmpty { "T4" } else initialTemp) }

    val typeAtmoOptions = listOf("Gaz", "Poussière")
    
    val gazClassificationOptions = listOf("0", "1", "2")
    val gazGroupeOptions = listOf("IIA", "IIB", "IIC")
    val gazTempOptions = listOf("T1", "T2", "T3", "T4", "T5", "T6")

    val poussiereClassificationOptions = listOf("20", "21", "22")
    val poussiereGroupeOptions = listOf("IIIA", "IIIB", "IIIC")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title)
                if (onDelete != null) {
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Default.Delete, 
                            contentDescription = "Supprimer",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = zoneNom,
                    onValueChange = { zoneNom = it },
                    label = { Text("Nom") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = section,
                    onValueChange = { section = it },
                    label = { Text("Section") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = sousSection,
                    onValueChange = { sousSection = it },
                    label = { Text("Sous-section") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                AtexDropdown(
                    label = "Type d'atmosphère",
                    options = typeAtmoOptions,
                    selectedOption = typeAtmo,
                    onOptionSelected = { 
                        typeAtmo = it 
                        // Reset defaults on type change
                        if (it == "Gaz") {
                            classification = "2"
                            groupe = "IIB"
                            temperature = "T4"
                        } else {
                            classification = "22"
                            groupe = "IIIB"
                            temperature = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                if (typeAtmo == "Gaz") {
                    AtexDropdown(
                        label = "Classification",
                        options = gazClassificationOptions,
                        selectedOption = classification,
                        onOptionSelected = { classification = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AtexDropdown(
                            label = "Groupe",
                            options = gazGroupeOptions,
                            selectedOption = groupe,
                            onOptionSelected = { groupe = it },
                            modifier = Modifier.weight(1f)
                        )
                        AtexDropdown(
                            label = "Temp",
                            options = gazTempOptions,
                            selectedOption = temperature,
                            onOptionSelected = { temperature = it },
                            modifier = Modifier.weight(1f)
                        )
                    }
                } else {
                    AtexDropdown(
                        label = "Classification",
                        options = poussiereClassificationOptions,
                        selectedOption = classification,
                        onOptionSelected = { classification = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AtexDropdown(
                            label = "Groupe",
                            options = poussiereGroupeOptions,
                            selectedOption = groupe,
                            onOptionSelected = { groupe = it },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = temperature,
                            onValueChange = { temperature = it },
                            label = { Text("Temp (°C)") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (zoneNom.isNotBlank()) {
                    onConfirm(zoneNom, section, sousSection, typeAtmo, classification, groupe, temperature)
                    onDismiss()
                }
            }) { Text("Enregistrer") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        }
    )
}

@Composable
fun ZoneCard(zone: ZoneAtex, onEdit: () -> Unit, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.LocationOn, 
                contentDescription = null, 
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(zone.nom, style = MaterialTheme.typography.titleLarge)
                if (zone.section.isNotBlank() || zone.sousSection.isNotBlank()) {
                    Text(
                        "${zone.section}${if (zone.section.isNotBlank() && zone.sousSection.isNotBlank()) " / " else ""}${zone.sousSection}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Text(
                    "Exigence : ${zone.typeAtmosphere} Zone ${zone.exigenceClassification} / ${zone.exigenceGroupe} ${if (zone.typeAtmosphere == "Poussière" && zone.exigenceTemperature.isNotEmpty()) "${zone.exigenceTemperature}°C" else zone.exigenceTemperature}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Modifier", tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AtexDropdown(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}
