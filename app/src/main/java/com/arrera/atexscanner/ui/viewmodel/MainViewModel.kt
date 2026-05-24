package com.arrera.atexscanner.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.arrera.atexscanner.data.ScannerRepository
import com.arrera.atexscanner.data.Site
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

    // Fonction pour ajouter un site
    fun addSite(nom: String) {
        viewModelScope.launch {
            repository.insertSite(Site(nom = nom))
        }
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
