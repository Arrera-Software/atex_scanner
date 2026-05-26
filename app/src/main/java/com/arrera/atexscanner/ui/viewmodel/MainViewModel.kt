package com.arrera.atexscanner.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.arrera.atexscanner.data.ScannerRepository
import com.arrera.atexscanner.data.Site
import com.arrera.atexscanner.data.ZoneAtex
import com.arrera.atexscanner.data.Equipement
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(private val repository: ScannerRepository) : ViewModel() {

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

    // Récupérer les zones d'un site
    fun getZonesBySite(siteId: Long): StateFlow<List<ZoneAtex>> {
        return repository.getZonesBySite(siteId).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    // Ajouter une zone
    fun addZone(siteId: Long, nom: String, classification: String, groupe: String, temperature: String) {
        viewModelScope.launch {
            repository.insertZone(
                ZoneAtex(
                    siteId = siteId,
                    nom = nom,
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
}

// Factory pour instancier le ViewModel avec le Repository
class MainViewModelFactory(private val repository: ScannerRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
