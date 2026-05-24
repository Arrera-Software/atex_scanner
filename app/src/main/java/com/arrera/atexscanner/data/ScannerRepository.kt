package com.arrera.atexscanner.data

import kotlinx.coroutines.flow.Flow

class ScannerRepository(private val scannerDao: ScannerDao) {

    // --- Sites ---
    val allSites: Flow<List<Site>> = scannerDao.getAllSites()

    suspend fun insertSite(site: Site): Long {
        return scannerDao.insertSite(site)
    }

    // --- Zones ---
    fun getZonesBySite(siteId: Long): Flow<List<ZoneAtex>> {
        return scannerDao.getZonesBySite(siteId)
    }

    suspend fun insertZone(zone: ZoneAtex): Long {
        return scannerDao.insertZone(zone)
    }

    // --- Equipements ---
    fun getEquipementsByZone(zoneId: Long): Flow<List<Equipement>> {
        return scannerDao.getEquipementsByZone(zoneId)
    }

    suspend fun insertEquipement(equipement: Equipement): Long {
        return scannerDao.insertEquipement(equipement)
    }

    // --- Inspections ---
    fun getInspectionsByEquipement(equipementId: Long): Flow<List<Inspection>> {
        return scannerDao.getInspectionsByEquipement(equipementId)
    }

    suspend fun insertInspection(inspection: Inspection): Long {
        return scannerDao.insertInspection(inspection)
    }
}
