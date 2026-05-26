package com.arrera.atexscanner.ui.screens.equipment

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    onBack: () -> Unit
) {
    val equipmentsFlow = remember(zoneId) { viewModel.getEquipementsByZone(zoneId) }
    val equipments by equipmentsFlow.collectAsState(initial = emptyList())

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
                onClick = { /* TODO: Ajouter équipement (Scan OCR) */ },
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
            // Header ou boutons d'actions rapides si besoin
            
            Box(modifier = Modifier.fillMaxSize()) {
                if (equipments.isEmpty()) {
                    EmptyState()
                } else {
                    EquipmentTable(equipments)
                }
            }
        }
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
    
    // On utilise un scroll horizontal pour simuler le tableur Excel
    Column(modifier = Modifier.horizontalScroll(scrollState)) {
        // En-tête du tableau
        TableHeader()
        
        // Contenu du tableau
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
        
        // Verdict (simulé pour le moment car Inspection n'est pas encore liée ici)
        Text(
            text = "C", // Par défaut
            modifier = Modifier.width(80.dp).padding(4.dp),
            textAlign = TextAlign.Center,
            color = Color(0xFF2E7D32), // Vert
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
            TextStyle(color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp) 
        else 
            TextStyle(fontSize = 12.sp),
        textAlign = TextAlign.Start,
        maxLines = 2
    )
}

// Utilisation de TextStyle explicite pour éviter les conflits si besoin
private fun TextStyle(
    color: Color = Color.Unspecified,
    fontWeight: FontWeight? = null,
    fontSize: androidx.compose.ui.unit.TextUnit = androidx.compose.ui.unit.TextUnit.Unspecified
) = androidx.compose.ui.text.TextStyle(
    color = color,
    fontWeight = fontWeight,
    fontSize = fontSize
)
