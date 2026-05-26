package com.arrera.atexscanner

import android.app.Application
import com.arrera.atexscanner.data.AppDatabase
import com.arrera.atexscanner.data.ScannerRepository
import com.arrera.atexscanner.utils.OCRProcessor

class AtexScannerApplication : Application() {
    // On utilise "lazy" pour que la base de données et le repository
    // ne soient créés que lorsqu'ils sont réellement nécessaires.
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { ScannerRepository(database.scannerDao()) }
    val ocrProcessor by lazy { OCRProcessor(this) }
}
