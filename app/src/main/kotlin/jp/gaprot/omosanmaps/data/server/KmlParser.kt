/*
 *  Created by Gaprot Dev Team
 *  Copyright © 2016年 Up-frontier, Inc. All rights reserved.
 */

package jp.gaprot.omosanmaps.data.server

import android.content.Context
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import jp.gaprot.omosanmaps.data.model.Document
import jp.gaprot.omosanmaps.data.model.Folder
import jp.gaprot.omosanmaps.data.model.Placemark
import jp.gaprot.omosanmaps.map.LatLngFactory
import jp.gaprot.omosanmaps.preference.PreferenceManager
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException

class KmlParser(private val parser: XmlPullParser, context: Context? = null) {

    companion object {
        private val TAG = KmlParser::class.java.simpleName
    }

    private val origin: LatLng?

    init {
        if (context == null) {
            origin = null
        } else {
            origin = PreferenceManager.getOrigin(context)
            Log.d(TAG, "origin=${origin.latitude}, ${origin.longitude}")
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    fun parseDocument(): Document {
        val builder = Document.Builder()
        var eventType = parser.eventType
        var tagName = parser.name
        while (!isDocumentEndTag(eventType, tagName)) {
            if (isStartTag(eventType)) {
                when (tagName) {
                    Tag.document -> Log.d(TAG, "parseDocument: New document TAG!!")
                    Tag.name -> builder.setName(parser.nextText())
                    Tag.description -> builder.setDescription(parser.nextText())
                    Tag.folder -> builder.addFolder(parseFolder(parser, eventType))
                    Tag.style -> parser.next()
                    else -> Log.d(TAG, "parseDocument: Didn't parse " + tagName)
                }
            }
            eventType = parser.next()
            tagName = parser.name
        }
        val document = builder.build()
        Log.d(TAG, "parseDocument: " + document.toString())
        return builder.build()
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun parseFolder(parser: XmlPullParser, type: Int): Folder {
        var eventType = type
        if (!isFolderStartTag(eventType, parser.name)) {
            throw XmlPullParserException("parseFolder must start from its start TAG!")
        }
        val builder = Folder.Builder()
        var tagName = parser.name
        while (!isFolderEndTag(eventType, tagName)) {
            if (isStartTag(eventType)) {
                when (tagName) {
                    Tag.folder -> Log.d(TAG, "parseFolder: New folder TAG!")
                    Tag.name -> builder.setName(parser.nextText())
                    Tag.placemark -> builder.addPlacemark(parsePlacemark(parser, eventType))
                    else -> Log.d(TAG, "parseFolder: Didn't parse " + tagName)
                }
            }
            eventType = parser.next()
            tagName = parser.name
        }
        return builder.build()
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun parsePlacemark(parser: XmlPullParser, type: Int): Placemark {
        var eventType = type
        if (!isPlacemarkStartTag(eventType, parser.name)) {
            throw XmlPullParserException("parsePlacemark must start from its start TAG!")
        }
        val builder = Placemark.Builder()
        var tagName = parser.name
        while (!isPlacemarkEndTag(eventType, tagName)) {
            if (isStartTag(eventType)) {
                when (parser.name) {
                    Tag.placemark -> Log.d(TAG, "parsePlacemark: New placemark TAG!")
                    Tag.name -> builder.setName(parser.nextText())
                    Tag.description -> builder.setDescription(parser.nextText())
                    Tag.styleUrl -> builder.setStyleUrl(parser.nextText())
                    Tag.point -> builder.setLatLng(parsePoint(parser, eventType))
                    Tag.extendedData -> builder.setImageUrls(parseExtendedData(parser, eventType))
                    else -> Log.d(TAG, "parsePlacemark: Didn't parse " + tagName)
                }
            }
            eventType = parser.next()
            tagName = parser.name
        }
        return builder.build(origin)
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun parsePoint(parser: XmlPullParser, eventType: Int): LatLng {
        if (!isPointStartTag(eventType, parser.name)) {
            throw XmlPullParserException("parsePoint must start from its start TAG!")
        }
        var type = 0
        var tagName = ""
        while (!isCoordinatesStartTag(type, tagName)) {
            type = parser.next()

            // XmlPullParser#getName() が null だと IllegalStateException を
            // スローされるからそれ対策のワークアラウンド
            try {
                tagName = parser.name
            } catch (e: IllegalStateException) {
                tagName = ""
            }
            if (Tag.coordinates == tagName) {

                // KML には、longitude, latitude の順で値が格納されている
                // ex) <coordinates>139.70347900000002,35.669591,0.0</coordinates>
                val lnglat = parser.nextText()
                        .split(",".toRegex()) // CSV を List に変換
                return LatLngFactory.from(
                        latitudeStr = lnglat[1],
                        longitudeStr = lnglat[0]
                )
            }
        }
        throw XmlPullParserException("Could not find coordinate start TAG!")
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun parseExtendedData(parser: XmlPullParser, eventType: Int): List<String> {
        if (!isExtendedDataStartTag(eventType, parser.name)) {
            throw XmlPullParserException("parseExtendedData must start from its start TAG!")
        }
        var type = 0
        var tagName = ""
        while (!isValueStartTag(type, tagName)) {
            type = parser.next()
            try {
                tagName = parser.name
            } catch (e: IllegalStateException) {
                tagName = ""
            }
            if (Tag.value == tagName) {
                val value = parser.nextText()
                val urls = value.split(" ".toRegex())
                Log.d(TAG, "parseExtendedData: $urls")
                return urls
            }
        }
        throw XmlPullParserException("Could not find value start TAG!")
    }

    private fun isDocumentEndTag(eventType: Int, tagName: String?): Boolean {
        return isEndTag(eventType) && (Tag.document == tagName)
    }

    private fun isFolderStartTag(eventType: Int, tagName: String?): Boolean {
        return isStartTag(eventType) && Tag.folder == tagName
    }

    private fun isFolderEndTag(eventType: Int, tagName: String?): Boolean {
        return isEndTag(eventType) && Tag.folder == tagName
    }

    private fun isPlacemarkStartTag(eventType: Int, tagName: String?): Boolean {
        return isStartTag(eventType) && Tag.placemark == tagName
    }

    private fun isPlacemarkEndTag(eventType: Int, tagName: String?): Boolean {
        return isEndTag(eventType) && Tag.placemark == tagName
    }

    private fun isCoordinatesStartTag(eventType: Int, tagName: String?): Boolean {
        return isStartTag(eventType) && Tag.coordinates == tagName
    }

    private fun isPointStartTag(eventType: Int, tagName: String?): Boolean {
        return isStartTag(eventType) && Tag.point == tagName
    }

    private fun isExtendedDataStartTag(eventType: Int, tagName: String?): Boolean {
        return isStartTag(eventType) && Tag.extendedData == tagName
    }

    private fun isValueStartTag(eventType: Int, tagName: String?): Boolean {
        return isStartTag(eventType) && Tag.value == tagName
    }

    private fun isStartTag(eventType: Int): Boolean {
        return eventType == XmlPullParser.START_TAG
    }

    private fun isEndTag(eventType: Int): Boolean {
        return eventType == XmlPullParser.END_TAG
    }

    private object Tag {
        val document = "Document"
        val name = "name"
        val description = "description"
        val folder = "Folder"
        val placemark = "Placemark"
        val styleUrl = "styleUrl"
        val extendedData = "ExtendedData"
        val data = "Data"
        val value = "value"
        val point = "Point"
        val coordinates = "coordinates"
        val style = "Style"
        val iconStyle = "IconStyle"
        val scale = "scale"
        val icon = "Icon"
        val hotSopt = "hotSpot"
        val balloonStyle = "BalloonStyle"
        val text = "text"
    }
}
