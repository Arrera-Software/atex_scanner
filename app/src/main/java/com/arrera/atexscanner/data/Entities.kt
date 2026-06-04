package com.arrera.atexscanner.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "sites")
data class Site(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nom: String,
    val dateCreation: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "zones_atex",
    foreignKeys = [
        ForeignKey(
            entity = Site::class,
            parentColumns = ["id"],
            childColumns = ["siteId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ZoneAtex(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val siteId: Long,
    val nom: String = "", 
    val exigenceClassification: String = "2",
    val exigenceGroupe: String = "IIB",
    val exigenceTemperature: String = "T4"
)

@Entity(
    tableName = "equipements",
    foreignKeys = [
        ForeignKey(
            entity = ZoneAtex::class,
            parentColumns = ["id"],
            childColumns = ["zoneId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Equipement(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val zoneId: Long,
    
    // Localisation
    val emplacement1: String = "",
    val emplacement2: String = "",
    
    // Matériel
    val tagNumber: String = "",
    val typeMateriel: String = "",
    val fabricant: String = "",
    val numeroSerie: String = "",
    val indiceProtection: String = "",
    val anneeFabrication: String = "",
    
    // Marquage Selon Directives
    val dirGroupe: String = "",
    val dirCategorie: String = "",
    val dirAtmosphere: String = "",
    
    // Marquage Selon Normes
    val normeProtection: String = "",
    val normeGroupe: String = "",
    val normeTemperature: String = "",
    val normeEPL: String = "",
    
    val nature: String = "Électrique",
    val quantite: String = "1",
    
    val numeroAttestation: String = "",
    val photoPlaquePath: String? = null
)

@Entity(
    tableName = "inspections",
    foreignKeys = [
        ForeignKey(
            entity = Equipement::class,
            parentColumns = ["id"],
            childColumns = ["equipementId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Inspection(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val equipementId: Long,
    val date: Long = System.currentTimeMillis(),
    val inspecteurNom: String = "",
    
    // Adéquation & Remarques (colonnes Excel)
    val conformiteAtex: String = "OUI",
    val commentaires: String = "",
    val assistanceMiseConformite: String = "",
    
    val marquageRemarques: String = "",
    val presseEtoupes: String = "Conforme",
    val miseAlaTerre: String = "Conforme",
    val gainesCables: String = "Conforme",
    val boitierEnveloppe: String = "Conforme",
    val autres: String = ""
)
