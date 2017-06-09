/*
 *  Created by Gaprot Dev Team
 *  Copyright © 2016年 Up-frontier, Inc. All rights reserved.
 */

package jp.gaprot.omosanmaps.data

import android.content.Context
import android.util.Log
import jp.gaprot.omosanmaps.data.model.Document
import jp.gaprot.omosanmaps.data.model.Filter
import jp.gaprot.omosanmaps.data.model.Folder
import jp.gaprot.omosanmaps.data.model.Placemark
import jp.gaprot.omosanmaps.data.server.RemoteDataServer
import jp.gaprot.omosanmaps.data.store.RealmDataStore
import jp.gaprot.omosanmaps.preference.PreferenceManager
import rx.Single
import java.util.*

class Repository private constructor(context: Context) : DataStore {

    companion object {

        val TAG: String = Repository::class.java.simpleName

        private var instance: Repository? = null

        @JvmStatic
        fun getInstance(context: Context): Repository {
            if (instance == null) {
                instance = Repository(context.applicationContext)
            }
            return instance!!
        }
    }

    private val server: DataServer

    private val store: DataStore

    /**
     *  star した Placemark の coordinate をキャッシュするためのリスト
     */
    private var starredPositions: MutableList<String> = ArrayList()
        private set

    /**
     *  Filter をキーにした Placemark のリストのキャッシュ
     */
    private var cache: MutableMap<Filter, List<Placemark>> = HashMap()
        private set

    init {
        val kmzUrl = PreferenceManager.getKmzUrl(context)
        server = RemoteDataServer(kmzUrl, context)
        store = RealmDataStore
    }

    /**
     *  サーバから KML をフェッチして、ローカルのデータを上書き
     *  @see RemoteDataServer.getDocument and
     *  @see RealmDataStore.saveDocument
     */
    fun refresh(): Single<Document> {
        Log.d(TAG, "refresh: ")
        return server.getDocument()
                .doOnSuccess { document ->
                    saveDocument(document).subscribe()
                }
    }

    /**
     *  ローカルの Document を取得
     *  @see RealmDataStore.getDocument
     */
    override fun getDocument(): Single<Document> {
        return store.getDocument()
    }

    /**
     *  ローカルの Folder を取得
     *  @see RealmDataStore.getFolders
     */
    override fun getFolders(): Single<List<Folder>> {
        return store.getFolders()
    }

    /**
     *  ローカルの Placemark を取得
     *  @see RealmDataStore.getPlacemarks
     */
    override fun getPlacemarks(): Single<List<Placemark>> {
        return store.getPlacemarks()
    }

    /**
     *  Filter を元に、ローカルの Placemark を取得
     *  キャッシュがあれば、それを返す
     *  @see RealmDataStore.getPlacemarksWithFilter
     */
    override fun getPlacemarksWithFilter(filter: Filter): Single<List<Placemark>> {
        if (filter.isEmpty()) {
            return getPlacemarks()
        }
        if (cache.contains(filter)) {
            // キャッシュヒット
            return Single.just(cache[filter])
        }
        return store.getPlacemarksWithFilter(filter)
                .doOnSuccess { cache[filter] = it }
    }

    /**
     *  LatLng の csv を元に検索
     *  star した Placemark の csv のリストからの検索を想定
     *  @see RealmDataStore.getPlacemarksByPosition and
     *  @see Repository.getStarredPositions
     */
    override fun getPlacemarksByPosition(positions: List<String>): Single<List<Placemark>> {
        return store.getPlacemarksByPosition(positions)
    }

    /**
     *  @see RealmDataStore.saveDocument
     */
    override fun saveDocument(document: Document): Single<Unit> {
        Log.d(TAG, "saveDocument: ${document.folders}")
        return store.saveDocument(document)
    }

    /**
     *  star が更新されるので、キャッシュはクリア
     *  @see RealmDataStore.saveStarredPosition
     */
    override fun saveStarredPosition(position: String): Single<Unit> {
        starredPositions.add(position)
        cache.clear()
        return store.saveStarredPosition(position)
    }

    /**
     *  @see RealmDataStore.getStarredPositions
     */
    override fun getStarredPositions(): Single<List<String>> {
        if (starredPositions.isEmpty()) {
            return store.getStarredPositions()
                    .doOnSuccess { starredPositions = it.toMutableList() }
        }
        // キャッシュを返す
        return Single.just(starredPositions)
    }

    /**
     *  @see RealmDataStore.removeStarredPosition
     */
    override fun removeStarredPosition(position: String): Single<Unit> {
        starredPositions.remove(position)
        cache.clear()
        return store.removeStarredPosition(position)
    }

    /**
     *  @param position Placemark.coordinate
     */
    fun toggleStarred(position: String): Single<Unit> {
        if (starredPositions.isEmpty()) {
            starredPositions = getStarredPositions()
                    .toObservable().toBlocking().single().toMutableList()
        }
        val starred = starredPositions.contains(position)
        if (starred) {
            Log.d(TAG, "updateStar: Remove")
            return removeStarredPosition(position)
        } else {
            Log.d(TAG, "updateStar: Save")
            return saveStarredPosition(position)
        }
    }

    fun isStarred(position: String): Boolean {
        if (starredPositions.isEmpty()) {
            starredPositions = getStarredPositions()
                    .toObservable().toBlocking().single().toMutableList()
        }
        return starredPositions.contains(position)
    }
}