/*
 *  Created by Gaprot Dev Team
 *  Copyright © 2016年 Up-frontier, Inc. All rights reserved.
 */

package jp.gaprot.omosanmaps.data.store

import android.util.Log
import io.realm.Realm
import jp.gaprot.omosanmaps.data.DataStore
import jp.gaprot.omosanmaps.data.model.Document
import jp.gaprot.omosanmaps.data.model.Filter
import jp.gaprot.omosanmaps.data.model.Folder
import jp.gaprot.omosanmaps.data.model.Placemark
import jp.gaprot.omosanmaps.data.store.model.RealmDocument
import jp.gaprot.omosanmaps.data.store.model.RealmFolder
import jp.gaprot.omosanmaps.data.store.model.RealmPlacemark
import jp.gaprot.omosanmaps.data.store.model.RealmStarred
import jp.gaprot.omosanmaps.data.store.model.util.convert
import rx.Single

object RealmDataStore : DataStore {

    private val tag = RealmDataStore::class.java.simpleName

    private val default: Realm
        get() = Realm.getDefaultInstance()

    override fun saveDocument(document: Document): Single<Unit> {
        return Single.create {
            val _realm = default
            _realm.executeTransaction { realm ->
                val realmDocument = RealmDocument(document)
                realm.copyToRealmOrUpdate(realmDocument)
                Log.d(tag, "saved= " + realmDocument.realmFolders.first().realmPlacemarks.first().name)
            }
            it.onSuccess(Unit)
            _realm.close()
        }
    }

    override fun getDocument(): Single<Document> {
        return Single.create {
            val _realm = default
            val result = _realm
                    .where(RealmDocument::class.java)
                    .findFirst()
            if (result == null) {
                it.onError(Throwable("There is no RealmDocument!"))
                _realm.close()
                return@create
            }
            it.onSuccess(result.convert())
            _realm.close()
        }
    }

    override fun getPlacemarks(): Single<List<Placemark>> {
        return Single.create {
            val _realm = default
            val result = _realm
                    .where(RealmPlacemark::class.java)
                    .findAll()
            if (result == null) {
                it.onError(Throwable("There is no RealmPlacemark!"))
                _realm.close()
                return@create
            }
            it.onSuccess(result.convert())
            _realm.close()
        }
    }

    override fun getPlacemarksWithFilter(filter: Filter): Single<List<Placemark>> {
        return Single.create {
            val queryBuilder = RealmPlacemark.QueryBuilder()
                    .setFolderName(filter.folderName)
                    .setMaxDistance(filter.maxDistance)
            if (filter.starred) {
                val positions = getStarredPositions()
                        .toObservable().toBlocking().single()
                queryBuilder.setPositions(positions)
            }
            val realm = default
            val result = queryBuilder
                    .create(realm)
                    .findAll()
            it.onSuccess(result.convert())
            realm.close()
        }
    }

    override fun getPlacemarksByPosition(positions: List<String>): Single<List<Placemark>> {
        return Single.create {
            val realm = default
            val query = RealmPlacemark.QueryBuilder()
                    .setPositions(positions)
                    .create(realm)

            val result = query.findAll()

            it.onSuccess(result.convert())
            realm.close()
        }
    }

    override fun getFolders(): Single<List<Folder>> {
        return Single.create {
            val realm = default
            val result = realm.where(RealmFolder::class.java)
                    .findAll()
            it.onSuccess(result.convert())
            realm.close()
        }
    }

    override fun saveStarredPosition(position: String): Single<Unit> {
        return Single.create {
            val _realm = default
            _realm.executeTransaction { realm ->
                realm.createObject(RealmStarred::class.java, position)
            }
            it.onSuccess(Unit)
            _realm.close()
        }
    }

    override fun getStarredPositions(): Single<List<String>> {
        val realm = default
        val result = realm
                .where(RealmStarred::class.java)
                .findAll()
                .convert()
        realm.close()
        return Single.just(result)
    }

    override fun removeStarredPosition(position: String): Single<Unit> {
        return Single.create {
            val _realm = default
            _realm.executeTransaction { realm ->
                realm.where(RealmStarred::class.java)
                        .equalTo(RealmStarred.Field.position, position)
                        .findFirst()
                        .deleteFromRealm()
            }
            _realm.close()
            it.onSuccess(Unit)
        }
    }
}