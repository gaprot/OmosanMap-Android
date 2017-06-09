/*
 *  Created by Gaprot Dev Team
 *  Copyright © 2016年 Up-frontier, Inc. All rights reserved.
 */

package jp.gaprot.omosanmaps.data.server

import android.content.Context
import android.util.Log
import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

/**
 *  KMZ を解凍して、KML のパーサを作成
 */
class KmlExtractor(val context: Context) {

    companion object {

        private val TAG: String = KmlExtractor::class.java.simpleName

        private val KML_FILE_NAME = "doc.kml"
    }

    @Throws(IOException::class)
    fun execute(inputStream: InputStream): XmlPullParser {
        val outputFile = File.createTempFile("rawmap", ".kmz")
        val path = saveTempFile(inputStream, outputFile)
        val filename = extract(path) ?: throw IOException()
        val inputFile = context.openFileInput(filename)
        return Xml.newPullParser().apply { setInput(inputFile, "UTF-8") }
    }

    private fun saveTempFile(inputStream: InputStream, outputFile: File): String {
        val fos = FileOutputStream(outputFile)

        val buffer = ByteArray(1024)
        var len = inputStream.read(buffer)
        while (len != -1) {
            fos.write(buffer, 0, len)
            len = inputStream.read(buffer)
        }
        fos.close()
        inputStream.close()
        return outputFile.path
    }

    private fun extract(path: String): String? {
        val zipin = ZipInputStream(FileInputStream(path))
        var entry: ZipEntry? = zipin.nextEntry

        while (entry != null) {
            val newFile = File(entry.name)

            val newFilename = newFile.name
            Log.d(TAG, "extract: " + newFilename)

            if (KML_FILE_NAME == newFilename) {
                val newPath = "${context.filesDir}/${newFile.name}"
                Log.d(TAG, "extract: " + newPath)
                val file = FileOutputStream(newPath)
                val out = BufferedOutputStream(file)

                val buffer = ByteArray(1024)
                var len = zipin.read(buffer)
                while (len != -1) {
                    out.write(buffer, 0, len)
                    len = zipin.read(buffer)
                }
                Log.d(TAG, "extract: out=" + out.toString())
                zipin.closeEntry()
                out.close()
                return newFilename
            }
            entry = zipin.nextEntry
        }
        return null
    }
}