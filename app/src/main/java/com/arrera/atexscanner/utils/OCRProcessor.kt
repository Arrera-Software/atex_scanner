package com.arrera.atexscanner.utils

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.tasks.await

class OCRProcessor(private val context: Context) {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    suspend fun extractText(uri: Uri): String {
        return try {
            val image = InputImage.fromFilePath(context, uri)
            val result = recognizer.process(image).await()
            result.text
        } catch (e: Exception) {
            "Erreur OCR: ${e.message}"
        }
    }

    // Fonction de parsing basique pour extraire les champs
    // A améliorer selon les retours terrain
    fun parseAtexData(rawText: String): Map<String, String> {
        val lines = rawText.lines()
        val data = mutableMapOf<String, String>()
        
        // Exemples de patterns (très simplifiés)
        lines.forEach { line ->
            val upper = line.uppercase()
            if (upper.contains("SN") || upper.contains("S/N")) {
                data["sn"] = line.substringAfter(":").trim()
            }
            if (upper.contains("IP")) {
                data["ip"] = line.filter { it.isDigit() }
            }
            // ... autres patterns à ajouter ici
        }
        
        return data
    }
}
