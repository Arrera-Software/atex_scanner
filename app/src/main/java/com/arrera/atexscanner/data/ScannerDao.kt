package com.arrera.atexscanner.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ScannerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSite(site: Site): Long

    @Delete
    suspend fun deleteSite(site: Site)

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

    @Query("SELECT * FROM zones_atex WHERE id = :zoneId")
    fun getZoneById(zoneId: Long): Flow<ZoneAtex?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEquipement(equipement: Equipement): Long

    @Update
    suspend fun updateEquipement(equipement: Equipement)

    @Delete
    suspend fun deleteEquipement(equipement: Equipement)

    @Query("SELECT * FROM equipements WHERE zoneId = :zoneId")
    fun getEquipementsByZone(zoneId: Long): Flow<List<Equipement>>

    @Query("SELECT * FROM equipements WHERE zoneId IN (SELECT id FROM zones_atex WHERE siteId = :siteId)")
    suspend fun getEquipementsBySite(siteId: Long): List<Equipement>

    @Query("SELECT DISTINCT numeroAttestation FROM equipements WHERE zoneId IN (SELECT id FROM zones_atex WHERE siteId = :siteId) AND numeroAttestation != ''")
    fun getUniqueAttestationsBySite(siteId: Long): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInspection(inspection: Inspection): Long

    @Query("SELECT * FROM inspections WHERE equipementId = :equipementId ORDER BY date DESC")
    fun getInspectionsByEquipement(equipementId: Long): Flow<List<Inspection>>
}
