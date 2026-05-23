package com.arrera.atexscanner.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// L'annotation @Entity indique à Room que cette classe est une table de base de données
@Entity(tableName = "table_zone_atex")
data class ZoneAtex(
    // @PrimaryKey définit l'identifiant unique. autoGenerate = true crée l'ID tout seul (1, 2, 3...)
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val nomDuSite: String,
    val classificationZone: String, // Par exemple : "Zone 1", "Zone 2", "Zone 21"...
    val groupeGazOuPoussiere: String, // Par exemple : "IIA", "IIB", "IIC"
    val temperatureMax: String // Par exemple : "T1", "T4"...
)