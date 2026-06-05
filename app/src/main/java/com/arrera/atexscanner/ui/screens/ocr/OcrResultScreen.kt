package com.arrera.atexscanner.ui.screens.ocr

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.arrera.atexscanner.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OcrResultScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onSave: () -> Unit
) {
    val equipment = viewModel.pendingEquipement
    if (equipment == null) {
        onBack()
        return
    }

    var showFullScreenImage by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Validation des données") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.saveEquipement()
                        onSave()
                    }) {
                        Icon(Icons.Default.Check, contentDescription = "Enregistrer")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Affichage de la photo de la plaque pour vérification
            equipment.photoPlaquePath?.let { path ->
                AsyncImage(
                    model = path,
                    contentDescription = "Photo de la plaque",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .padding(bottom = 8.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium)
                        .clickable { showFullScreenImage = true }
                )
            }

            Text("Vérifiez et complétez les informations extraites de la plaque.", style = MaterialTheme.typography.bodyMedium)
            
            OutlinedTextField(
                value = equipment.tagNumber,
                onValueChange = { viewModel.updatePendingEquipement(equipment.copy(tagNumber = it)) },
                label = { Text("N° TAG") },
                modifier = Modifier.fillMaxWidth()
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            Text("Détails Matériel", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)

            OutlinedTextField(
                value = equipment.fabricant,
                onValueChange = { viewModel.updatePendingEquipement(equipment.copy(fabricant = it)) },
                label = { Text("Fabricant / Marque") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = equipment.typeMateriel,
                onValueChange = { viewModel.updatePendingEquipement(equipment.copy(typeMateriel = it)) },
                label = { Text("Type / Modèle") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = equipment.numeroAttestation,
                onValueChange = { viewModel.updatePendingEquipement(equipment.copy(numeroAttestation = it)) },
                label = { Text("N° de certificat / Attestation") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = equipment.numeroSerie,
                onValueChange = { viewModel.updatePendingEquipement(equipment.copy(numeroSerie = it)) },
                label = { Text("N° de Série (S/N)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = if (equipment.indiceProtection.startsWith("IP")) equipment.indiceProtection else "IP${equipment.indiceProtection}",
                onValueChange = { 
                    val newValue = if (it.startsWith("IP")) it else "IP"
                    viewModel.updatePendingEquipement(equipment.copy(indiceProtection = newValue)) 
                },
                label = { Text("Indice Protection (IP)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = equipment.anneeFabrication,
                onValueChange = { viewModel.updatePendingEquipement(equipment.copy(anneeFabrication = it)) },
                label = { Text("Année Fab.") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            Text("Marquage Directives", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = equipment.dirGroupe,
                    onValueChange = { viewModel.updatePendingEquipement(equipment.copy(dirGroupe = it)) },
                    label = { Text("Gr.") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = equipment.dirCategorie,
                    onValueChange = { viewModel.updatePendingEquipement(equipment.copy(dirCategorie = it)) },
                    label = { Text("Cat.") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = equipment.dirAtmosphere,
                    onValueChange = { viewModel.updatePendingEquipement(equipment.copy(dirAtmosphere = it)) },
                    label = { Text("Atmo.") },
                    modifier = Modifier.weight(1f)
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            Text("Marquage Normes", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = equipment.normeProtection,
                    onValueChange = { viewModel.updatePendingEquipement(equipment.copy(normeProtection = it)) },
                    label = { Text("Prot.") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = equipment.normeGroupe,
                    onValueChange = { viewModel.updatePendingEquipement(equipment.copy(normeGroupe = it)) },
                    label = { Text("Gr.") },
                    modifier = Modifier.weight(1f)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = equipment.normeTemperature,
                    onValueChange = { viewModel.updatePendingEquipement(equipment.copy(normeTemperature = it)) },
                    label = { Text("T") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = equipment.normeEPL,
                    onValueChange = { viewModel.updatePendingEquipement(equipment.copy(normeEPL = it)) },
                    label = { Text("EPL") },
                    modifier = Modifier.weight(1f)
                )
            }

            Button(
                onClick = {
                    viewModel.saveEquipement()
                    onSave()
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Valider l'équipement")
            }
        }
    }

    if (showFullScreenImage && equipment.photoPlaquePath != null) {
        FullScreenImageDialog(
            photoPath = equipment.photoPlaquePath!!,
            onDismiss = { showFullScreenImage = false }
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
