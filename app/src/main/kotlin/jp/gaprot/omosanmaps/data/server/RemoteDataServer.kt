/*
 *  Created by Gaprot Dev Team
 *  Copyright © 2016年 Up-frontier, Inc. All rights reserved.
 */

package jp.gaprot.omosanmaps.data.server

import android.content.Context
import jp.gaprot.omosanmaps.data.DataServer
import jp.gaprot.omosanmaps.data.model.Document
import jp.gaprot.omosanmaps.data.model.Folder
import jp.gaprot.omosanmaps.data.model.Placemark
import jp.gaprot.omosanmaps.extension.onSuccess
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.xmlpull.v1.XmlPullParserException
import rx.Single
import java.io.IOException
import java.util.*

class RemoteDataServer(

        private val kmzUrl: String,

        private val context: Context

) : DataServer {

    private val kmlExtractor: KmlExtractor

    init {
        kmlExtractor = KmlExtractor(context)
    }

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build()
    }

    private val request: Request by lazy {
        Request.Builder().url(kmzUrl).get().build()
    }

    override fun getDocument(): Single<Document> {
        return Single.create { subscriber ->
            try {
                val response = httpClient.newCall(request).execute()

                val xmlParser = kmlExtractor.execute(response.body().byteStream())
                val kmlParser = KmlParser(xmlParser, context)
                subscriber.onSuccess(kmlParser.parseDocument())
            } catch (e: IOException) {
                e.printStackTrace()
                subscriber.onError(e)
            } catch (e: XmlPullParserException) {
                e.printStackTrace()
                subscriber.onError(e)
            }
        }
    }

    override fun getFolders(): Single<List<Folder>> {
        return Single.create { subscriber ->
            getDocument()
                    .onSuccess { document ->
                        subscriber.onSuccess(document.folders)
                    }
                    .onError { subscriber.onError(it) }
                    .subscribe()
        }
    }

    override fun getPlacemarks(): Single<List<Placemark>> {
        return Single.create { subscriber ->
            getDocument()
                    .onSuccess { document ->
                        val placemarks = ArrayList<Placemark>()
                        document.folders.forEach {
                            it.placemarks.forEach {
                                placemarks.add(it)
                            }
                        }
                        subscriber.onSuccess(placemarks.toList())
                    }
                    .onError { subscriber.onError(it) }
                    .subscribe()
        }
    }

    override fun getPlacemarksByPosition(positions: List<String>): Single<List<Placemark>> {
        return Single.create { subscriber ->
            getDocument()
                    .onSuccess { document ->
                        val placemarks = ArrayList<Placemark>()
                        document.folders.forEach {
                            it.placemarks.forEach { placemark ->
                                if (positions.contains(placemark.coordinate)) {
                                    placemarks.add(placemark)
                                }
                            }
                        }
                        subscriber.onSuccess(placemarks.toList())
                    }
                    .onError { subscriber.onError(it) }
                    .subscribe()
        }
    }
}