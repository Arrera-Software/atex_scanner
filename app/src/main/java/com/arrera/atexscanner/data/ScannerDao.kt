package com.arrera.atexscanner.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ScannerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSite(site: Site): Long

    @Query("SELECT * FROM sites ORDER BY dateCreation DESC")
    fun getAllSites(): Flow<List<Site>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertZone(zone: ZoneAtex): Long

    @Update
    suspend fun updateZone(zone: ZoneAtex)

    @Delete
    suspend fun deleteZone(zone: ZoneAtex)

    @Query("SELECT * FROM zones_atex WHERE siteId = :siteId")
    fun getZonesBySite(siteId: Long): Flow<List<ZoneAtex>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEquipement(equipement: Equipement): Long

    @Query("SELECT * FROM equipements WHERE zoneId = :zoneId")
    fun getEquipementsByZone(zoneId: Long): Flow<List<Equipement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInspection(inspection: Inspection): Long

    @Query("SELECT * FROM inspections WHERE equipementId = :equipementId ORDER BY date DESC")
    fun getInspectionsByEquipement(equipementId: Long): Flow<List<Inspection>>
}
