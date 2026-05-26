package com.arrera.atexscanner.ui.screens.ocr

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
            Text("Vérifiez et complétez les informations extraites de la plaque.", style = MaterialTheme.typography.bodyMedium)
            
            OutlinedTextField(
                value = equipment.tagNumber,
                onValueChange = { viewModel.updatePendingEquipement(equipment.copy(tagNumber = it)) },
                label = { Text("N° TAG") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = equipment.fabricant,
                    onValueChange = { viewModel.updatePendingEquipement(equipment.copy(fabricant = it)) },
                    label = { Text("Fabricant") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = equipment.typeMateriel,
                    onValueChange = { viewModel.updatePendingEquipement(equipment.copy(typeMateriel = it)) },
                    label = { Text("Type") },
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedTextField(
                value = equipment.numeroSerie,
                onValueChange = { viewModel.updatePendingEquipement(equipment.copy(numeroSerie = it)) },
                label = { Text("Numéro de série") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = equipment.indiceProtection,
                    onValueChange = { viewModel.updatePendingEquipement(equipment.copy(indiceProtection = it)) },
                    label = { Text("IP") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = equipment.anneeFabrication,
                    onValueChange = { viewModel.updatePendingEquipement(equipment.copy(anneeFabrication = it)) },
                    label = { Text("Année") },
                    modifier = Modifier.weight(1f)
                )
            }

            Text("Marquage Directives", style = MaterialTheme.typography.titleSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = equipment.dirGroupe,
                    onValueChange = { viewModel.updatePendingEquipement(equipment.copy(dirGroupe = it)) },
                    label = { Text("Groupe") },
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

            Text("Marquage Normes", style = MaterialTheme.typography.titleSmall)
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
                    label = { Text("Temp.") },
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
}
