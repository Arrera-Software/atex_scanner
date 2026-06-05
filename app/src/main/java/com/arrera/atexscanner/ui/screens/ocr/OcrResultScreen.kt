package com.arrera.atexscanner.ui.screens.ocr

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.foundation.BorderStroke
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
    var showProtKeyboard by remember { mutableStateOf(false) }
    var showEplKeyboard by remember { mutableStateOf(false) }

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
                    onValueChange = {},
                    label = { Text("Prot.") },
                    modifier = Modifier.weight(1f).clickable { showProtKeyboard = true },
                    readOnly = true,
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                
                var expandedGr by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expandedGr,
                    onExpandedChange = { expandedGr = !expandedGr },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = equipment.normeGroupe,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Gr.") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGr) },
                        modifier = Modifier.menuAnchor(),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedGr,
                        onDismissRequest = { expandedGr = false }
                    ) {
                        listOf("IIA", "IIB", "IIC", "IIIA", "IIIB", "IIIC").forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    viewModel.updatePendingEquipement(equipment.copy(normeGroupe = option))
                                    expandedGr = false
                                }
                            )
                        }
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                var expandedT by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expandedT,
                    onExpandedChange = { expandedT = !expandedT },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = equipment.normeTemperature,
                        onValueChange = { input ->
                            if (input.isEmpty()) {
                                viewModel.updatePendingEquipement(equipment.copy(normeTemperature = ""))
                            } else if (input.all { it.isDigit() || it == '°' || it == 'C' }) {
                                val digits = input.filter { it.isDigit() }
                                val newValue = if (digits.isNotEmpty()) "${digits}°C" else ""
                                viewModel.updatePendingEquipement(equipment.copy(normeTemperature = newValue))
                            }
                        },
                        label = { Text("T") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedT) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedT,
                        onDismissRequest = { expandedT = false }
                    ) {
                        listOf("T1", "T2", "T3", "T4", "T5", "T6").forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    viewModel.updatePendingEquipement(equipment.copy(normeTemperature = option))
                                    expandedT = false
                                }
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = equipment.normeEPL,
                    onValueChange = {},
                    label = { Text("EPL") },
                    modifier = Modifier.weight(1f).clickable { showEplKeyboard = true },
                    readOnly = true,
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            OutlinedTextField(
                value = equipment.numeroAttestation,
                onValueChange = { viewModel.updatePendingEquipement(equipment.copy(numeroAttestation = it)) },
                label = { Text("N° de certificat / Attestation") },
                modifier = Modifier.fillMaxWidth()
            )

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

    if (showProtKeyboard) {
        ProtKeyboardDialog(
            initialValue = equipment.normeProtection,
            onDismiss = { showProtKeyboard = false },
            onConfirm = { 
                viewModel.updatePendingEquipement(equipment.copy(normeProtection = it))
                showProtKeyboard = false
            }
        )
    }

    if (showEplKeyboard) {
        AtexKeyboardDialog(
            title = "Niveau de protection (EPL)",
            initialValue = equipment.normeEPL,
            keys = listOf("Ga", "Gb", "Gc", "Da", "Db", "Dc"),
            onDismiss = { showEplKeyboard = false },
            onConfirm = { 
                viewModel.updatePendingEquipement(equipment.copy(normeEPL = it))
                showEplKeyboard = false
            }
        )
    }
}

@Composable
fun ProtKeyboardDialog(
    initialValue: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    AtexKeyboardDialog(
        title = "Mode de protection (Prot)",
        initialValue = initialValue,
        keys = listOf("d", "e", "m", "ia", "ib", "ic", "p", "o", "h", "c", "nA", "n", "q", "nR", "b", "K"),
        onDismiss = onDismiss,
        onConfirm = onConfirm
    )
}

@Composable
fun AtexKeyboardDialog(
    title: String,
    initialValue: String,
    keys: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var currentValue by remember { mutableStateOf(initialValue) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Surface(
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Box(contentAlignment = Alignment.CenterStart, modifier = Modifier.padding(horizontal = 12.dp)) {
                        Text(text = currentValue, style = MaterialTheme.typography.headlineSmall)
                    }
                }

                val columns = if (keys.size <= 6) 3 else 4
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    keys.chunked(columns).forEach { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            row.forEach { key ->
                                Button(
                                    onClick = { currentValue += key },
                                    modifier = Modifier.weight(1f).height(48.dp),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text(key)
                                }
                            }
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { currentValue = "" },
                            modifier = Modifier.weight(1f).height(48.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(Icons.Default.DeleteForever, contentDescription = "Clear")
                        }
                        OutlinedButton(
                            onClick = { if (currentValue.isNotEmpty()) currentValue = currentValue.dropLast(1) },
                            modifier = Modifier.weight(1f).height(48.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Backspace, contentDescription = "Backspace")
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(currentValue) }) {
                Text("Valider")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
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
