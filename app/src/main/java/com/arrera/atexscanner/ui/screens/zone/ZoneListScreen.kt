package com.arrera.atexscanner.ui.screens.zone

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arrera.atexscanner.data.ZoneAtex
import com.arrera.atexscanner.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZoneListScreen(
    siteId: Long,
    siteNom: String,
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val zonesFlow = remember(siteId) { viewModel.getZonesBySite(siteId) }
    val zones by zonesFlow.collectAsState()
    
    var showAddDialog by remember { mutableStateOf(false) }
    var zoneToEdit by remember { mutableStateOf<ZoneAtex?>(null) }
    var zoneToDelete by remember { mutableStateOf<ZoneAtex?>(null) }
    
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
                            onEdit = { zoneToEdit = zone }
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
            onConfirm = { nom, classification, groupe, temp ->
                viewModel.addZone(siteId, nom, classification, groupe, temp)
            }
        )
    }

    if (zoneToEdit != null) {
        ZoneDialog(
            title = "Modifier la Zone",
            initialNom = zoneToEdit!!.nom,
            initialClassification = zoneToEdit!!.exigenceClassification,
            initialGroupe = zoneToEdit!!.exigenceGroupe,
            initialTemp = zoneToEdit!!.exigenceTemperature,
            onDismiss = { zoneToEdit = null },
            onConfirm = { nom, classification, groupe, temp ->
                viewModel.updateZone(zoneToEdit!!.copy(
                    nom = nom,
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
}

@Composable
fun ZoneDialog(
    title: String,
    initialNom: String = "",
    initialClassification: String = "2",
    initialGroupe: String = "IIB",
    initialTemp: String = "T4",
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    var zoneNom by remember { mutableStateOf(initialNom) }
    var classification by remember { mutableStateOf(initialClassification) }
    var groupe by remember { mutableStateOf(initialGroupe) }
    var temperature by remember { mutableStateOf(initialTemp) }

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
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = zoneNom,
                    onValueChange = { zoneNom = it },
                    label = { Text("Nom") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = classification,
                    onValueChange = { classification = it },
                    label = { Text("Classification") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = groupe,
                        onValueChange = { groupe = it },
                        label = { Text("Groupe") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = temperature,
                        onValueChange = { temperature = it },
                        label = { Text("Temp") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (zoneNom.isNotBlank()) {
                    onConfirm(zoneNom, classification, groupe, temperature)
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
fun ZoneCard(zone: ZoneAtex, onEdit: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
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
                Text(
                    "Exigence : Zone ${zone.exigenceClassification} / ${zone.exigenceGroupe} ${zone.exigenceTemperature}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Modifier", tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
