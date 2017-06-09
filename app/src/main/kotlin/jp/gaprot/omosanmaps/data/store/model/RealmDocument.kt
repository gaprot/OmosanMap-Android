/*
 *  Created by Gaprot Dev Team
 *  Copyright © 2016年 Up-frontier, Inc. All rights reserved.
 */

package jp.gaprot.omosanmaps.data.store.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import jp.gaprot.omosanmaps.data.model.Document
import jp.gaprot.omosanmaps.data.model.abst.Convertible
import jp.gaprot.omosanmaps.data.store.model.util.convert
import jp.gaprot.omosanmaps.data.store.model.util.realmFolders

open class RealmDocument(

        @PrimaryKey
        var name: String = "",

        var description: String = "",

        var realmFolders: RealmList<RealmFolder> = RealmList()

) : RealmObject(), Convertible<Document> {

    constructor(document: Document) : this(
            name = document.name,
            description = document.description,
            realmFolders = document.realmFolders()
    )

    override fun convert(): Document {
        return Document.Builder()
                .setName(name)
                .setDescription(description)
                .setFolders(realmFolders.convert().toMutableList())
                .build()
    }
}