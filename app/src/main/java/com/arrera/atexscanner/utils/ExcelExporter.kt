package com.arrera.atexscanner.utils

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.arrera.atexscanner.data.Equipement
import com.arrera.atexscanner.data.ZoneAtex
import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class ExcelExporter(private val context: Context) {

    fun exportSite(siteNom: String, equipments: List<Equipement>, zonesMap: Map<Long, ZoneAtex>): Boolean {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Inventaire ATEX")

        // Styles
        val headerStyle = workbook.createCellStyle().apply {
            fillForegroundColor = IndexedColors.GREY_25_PERCENT.index
            fillPattern = FillPatternType.SOLID_FOREGROUND
            alignment = HorizontalAlignment.CENTER
            verticalAlignment = VerticalAlignment.CENTER
            borderBottom = BorderStyle.THIN
            borderTop = BorderStyle.THIN
            borderLeft = BorderStyle.THIN
            borderRight = BorderStyle.THIN
            wrapText = true
        }

        val cellStyle = workbook.createCellStyle().apply {
            borderBottom = BorderStyle.THIN
            borderTop = BorderStyle.THIN
            borderLeft = BorderStyle.THIN
            borderRight = BorderStyle.THIN
            verticalAlignment = VerticalAlignment.CENTER
            alignment = HorizontalAlignment.CENTER
            wrapText = true
        }

        // --- En-têtes (Lignes 1 et 2) ---
        val row1 = sheet.createRow(0)
        row1.heightInPoints = 40f
        createCell(row1, 0, "N°", headerStyle)
        createCell(row1, 1, "LOCALISATION", headerStyle)
        sheet.addMergedRegion(CellRangeAddress(0, 0, 1, 3))
        createCell(row1, 4, "Zone ATEX\ndéfinie par le\nclient", headerStyle)
        createCell(row1, 5, "Matériel", headerStyle)
        sheet.addMergedRegion(CellRangeAddress(0, 0, 5, 10))
        createCell(row1, 11, "Marquage", headerStyle)
        sheet.addMergedRegion(CellRangeAddress(0, 0, 11, 18))
        createCell(row1, 19, "Conformité", headerStyle)
        sheet.addMergedRegion(CellRangeAddress(0, 0, 19, 21))
        createCell(row1, 22, "Action corrective proposée", headerStyle)

        val row2 = sheet.createRow(1)
        row2.heightInPoints = 30f
        createCell(row2, 1, "Section", headerStyle)
        createCell(row2, 2, "Sous-section", headerStyle)
        createCell(row2, 3, "Nom Zone", headerStyle)
        createCell(row2, 5, "N° TAG", headerStyle)
        createCell(row2, 6, "Type", headerStyle)
        createCell(row2, 7, "Fabricant", headerStyle)
        createCell(row2, 8, "Numéro de série", headerStyle)
        createCell(row2, 9, "Indice de Protection", headerStyle)
        createCell(row2, 10, "Année de Fab.", headerStyle)
        createCell(row2, 11, "Selon Directives", headerStyle)
        sheet.addMergedRegion(CellRangeAddress(1, 1, 11, 12))
        createCell(row2, 13, "Selon normes", headerStyle)
        sheet.addMergedRegion(CellRangeAddress(1, 1, 13, 16))
        createCell(row2, 17, "N° attestation", headerStyle)
        sheet.addMergedRegion(CellRangeAddress(1, 1, 17, 18))
        createCell(row2, 19, "Conformité\nC/NA/SO/NE\n(*)", headerStyle)
        createCell(row2, 20, "Type d'observation", headerStyle)
        createCell(row2, 21, "Nature des observations", headerStyle)

        // --- Données ---
        var currentRow = 2
        equipments.forEachIndexed { index, equip ->
            val zone = zonesMap[equip.zoneId]
            val dataRow = sheet.createRow(currentRow++)
            dataRow.heightInPoints = 25f

            createCell(dataRow, 0, (index + 1).toString(), cellStyle)
            createCell(dataRow, 1, zone?.section ?: "", cellStyle)
            createCell(dataRow, 2, zone?.sousSection ?: "", cellStyle)
            createCell(dataRow, 3, zone?.nom ?: "", cellStyle)

            val zoneInfo = if (zone != null) {
                "${zone.typeAtmosphere}\nZone ${zone.exigenceClassification}\n${zone.exigenceGroupe}\n${zone.exigenceTemperature}"
            } else ""
            createCell(dataRow, 4, zoneInfo, cellStyle)

            createCell(dataRow, 5, equip.tagNumber, cellStyle)
            createCell(dataRow, 6, equip.typeMateriel, cellStyle)
            createCell(dataRow, 7, equip.fabricant, cellStyle)
            createCell(dataRow, 8, equip.numeroSerie, cellStyle)
            createCell(dataRow, 9, equip.indiceProtection, cellStyle)
            createCell(dataRow, 10, equip.anneeFabrication, cellStyle)

            val dirInfo = "${equip.dirGroupe} ${equip.dirCategorie}${equip.dirAtmosphere}"
            createCell(dataRow, 11, dirInfo, cellStyle)
            sheet.addMergedRegion(CellRangeAddress(currentRow - 1, currentRow - 1, 11, 12))

            val normInfo = "${equip.normeProtection} ${equip.normeGroupe} ${equip.normeTemperature} ${equip.normeEPL}"
            createCell(dataRow, 13, normInfo, cellStyle)
            sheet.addMergedRegion(CellRangeAddress(currentRow - 1, currentRow - 1, 13, 16))

            createCell(dataRow, 17, equip.numeroAttestation, cellStyle)
            sheet.addMergedRegion(CellRangeAddress(currentRow - 1, currentRow - 1, 17, 18))

            createCell(dataRow, 19, "", cellStyle)
            createCell(dataRow, 20, "", cellStyle)
            createCell(dataRow, 21, "", cellStyle)
            createCell(dataRow, 22, "", cellStyle)
        }

        // Ajustement des colonnes
        for (i in 0..22) {
            sheet.setColumnWidth(i, 18 * 256)
        }

        // Sauvegarde Excel et ZIP
        val excelFileName = "Rapport_ATEX_${siteNom.replace(" ", "_")}.xlsx"
        val zipFileName = "Photos_ATEX_${siteNom.replace(" ", "_")}.zip"

        val excelSuccess = saveToDocuments(excelFileName, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") { out ->
            workbook.write(out)
            workbook.close()
        }

        val photosWithPaths = equipments.filter { it.photoPlaquePath != null }
        if (photosWithPaths.isNotEmpty()) {
            saveToDocuments(zipFileName, "application/zip") { out ->
                ZipOutputStream(out).use { zipOut ->
                    photosWithPaths.forEach { equip ->
                        val photoUri = Uri.parse(equip.photoPlaquePath!!)
                        context.contentResolver.openInputStream(photoUri)?.use { input ->
                            val entryName = "${equip.tagNumber.replace("/", "_")}.jpg"
                            zipOut.putNextEntry(ZipEntry(entryName))
                            input.copyTo(zipOut)
                            zipOut.closeEntry()
                        }
                    }
                }
            }
        }

        return excelSuccess
    }

    private fun saveToDocuments(fileName: String, mimeType: String, writer: (OutputStream) -> Unit): Boolean {
        return try {
            val outputStream: OutputStream?
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + "/ATEX_Scanner")
                }
                val uri = context.contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
                outputStream = uri?.let { context.contentResolver.openOutputStream(it) }
            } else {
                val documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                val appDir = File(documentsDir, "ATEX_Scanner")
                if (!appDir.exists()) appDir.mkdirs()
                val file = File(appDir, fileName)
                outputStream = FileOutputStream(file)
            }

            outputStream?.use { writer(it) }
            outputStream != null
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun createCell(row: Row, column: Int, value: String, style: CellStyle) {
        val cell = row.createCell(column)
        cell.setCellValue(value)
        cell.cellStyle = style
    }
}
