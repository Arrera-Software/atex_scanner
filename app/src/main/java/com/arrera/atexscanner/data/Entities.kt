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
    val nom: String,
    val classification: String,
    val groupeGaz: String,
    val classeTemperature: String
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
    val nom: String,
    val marque: String,
    val modele: String,
    val numeroSerie: String,
    val protection: String,
    val groupeMateriel: String,
    val tempMateriel: String,
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
    val isConforme: Boolean,
    val commentaires: String
)
