package com.arrera.atexscanner.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.arrera.atexscanner.data.ScannerRepository
import com.arrera.atexscanner.data.Site
import com.arrera.atexscanner.data.ZoneAtex
import com.arrera.atexscanner.data.Equipement
import com.arrera.atexscanner.utils.OCRProcessor
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(private val repository: ScannerRepository, private val ocrProcessor: OCRProcessor) : ViewModel() {

    // État pour l'équipement en cours de création via OCR
    var pendingEquipement by mutableStateOf<Equipement?>(null)
        private set

    fun setPendingTagAndZone(tag: String, zoneId: Long) {
        pendingEquipement = Equipement(
            zoneId = zoneId,
            tagNumber = tag
        )
    }

    fun processImage(uri: Uri, onComplete: () -> Unit) {
        viewModelScope.launch {
            val rawText = ocrProcessor.extractText(uri)
            val extractedData = ocrProcessor.parseAtexData(rawText)
            
            pendingEquipement = pendingEquipement?.copy(
                numeroSerie = extractedData["sn"] ?: "",
                indiceProtection = extractedData["ip"] ?: "",
                photoPlaquePath = uri.toString()
                // ... remplir les autres champs extraits
            )
            onComplete()
        }
    }

    fun saveEquipement() {
        pendingEquipement?.let {
            viewModelScope.launch {
                repository.insertEquipement(it)
                pendingEquipement = null
            }
        }
    }

    fun updatePendingEquipement(equipement: Equipement) {
        pendingEquipement = equipement
    }

    // Récupère tous les sites et les expose sous forme de StateFlow pour Compose
    val allSites: StateFlow<List<Site>> = repository.allSites.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Fonction pour ajouter un site et retourner son ID
    suspend fun addSite(nom: String): Long {
        return repository.insertSite(Site(nom = nom))
    }

    // Supprimer un site
    fun deleteSite(siteId: Long, nom: String) {
        viewModelScope.launch {
            repository.deleteSite(Site(id = siteId, nom = nom))
        }
    }

    // Récupérer les zones d'un site
    fun getZonesBySite(siteId: Long): StateFlow<List<ZoneAtex>> {
        return repository.getZonesBySite(siteId).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    // Récupérer une zone par son ID
    fun getZoneById(zoneId: Long): StateFlow<ZoneAtex?> {
        return repository.getZoneById(zoneId).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    }

    // Ajouter une zone
    fun addZone(siteId: Long, nom: String, section: String, sousSection: String, typeAtmo: String, classification: String, groupe: String, temperature: String) {
        viewModelScope.launch {
            repository.insertZone(
                ZoneAtex(
                    siteId = siteId,
                    nom = nom,
                    section = section,
                    sousSection = sousSection,
                    typeAtmosphere = typeAtmo,
                    exigenceClassification = classification,
                    exigenceGroupe = groupe,
                    exigenceTemperature = temperature
                )
            )
        }
    }

    // Modifier une zone
    fun updateZone(zone: ZoneAtex) {
        viewModelScope.launch {
            repository.updateZone(zone)
        }
    }

    // Supprimer une zone
    fun deleteZone(zone: ZoneAtex) {
        viewModelScope.launch {
            repository.deleteZone(zone)
        }
    }

    // Récupérer les équipements d'une zone
    fun getEquipementsByZone(zoneId: Long): StateFlow<List<Equipement>> {
        return repository.getEquipementsByZone(zoneId).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    // Ajouter un équipement
    fun addEquipement(equipement: Equipement) {
        viewModelScope.launch {
            repository.insertEquipement(equipement)
        }
    }

    // Modifier un équipement
    fun updateEquipement(equipement: Equipement) {
        viewModelScope.launch {
            repository.updateEquipement(equipement)
        }
    }

    // Supprimer un équipement
    fun deleteEquipement(equipement: Equipement) {
        viewModelScope.launch {
            repository.deleteEquipement(equipement)
        }
    }
}

// Factory pour instancier le ViewModel avec le Repository
class MainViewModelFactory(
    private val repository: ScannerRepository,
    private val ocrProcessor: OCRProcessor
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository, ocrProcessor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
