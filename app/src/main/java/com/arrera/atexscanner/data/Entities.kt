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
    val nom: String, // ex: Gazomètre aciérie
    val exigenceClassification: String, // ex: 2
    val exigenceGroupe: String,         // ex: IIB
    val exigenceTemperature: String     // ex: T1
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
    val emplacement1: String, // ex: Au sol
    val emplacement2: String, // ex: Proximité gazomètre au sud
    
    // Matériel
    val tagNumber: String,
    val typeMateriel: String,
    val fabricant: String,
    val numeroSerie: String,
    val indiceProtection: String,
    val anneeFabrication: String,
    
    // Marquage Selon Directives
    val dirGroupe: String,    // ex: II
    val dirCategorie: String, // ex: 2
    val dirAtmosphere: String, // ex: G
    
    // Marquage Selon Normes
    val normeProtection: String, // ex: de
    val normeGroupe: String,     // ex: IIB
    val normeTemperature: String, // ex: T4
    val normeEPL: String,        // ex: Gb
    
    val numeroAttestation: String,
    val photoPlaquePath: String?
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
    val inspecteurNom: String,
    val statusConformite: String, // C, NA, NC, NE
    val typeObservation: String,  // ex: Marquage
    val commentaires: String
)
