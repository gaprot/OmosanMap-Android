/*
 *  Created by Gaprot Dev Team
 *  Copyright © 2016年 Up-frontier, Inc. All rights reserved.
 */

package jp.gaprot.omosanmaps.data.model

import java.util.*

data class Document(

        val name: String,

        val description: String,

        val folders: List<Folder>

) {

    class Builder {

        private var name = ""

        private var description = ""

        private var folders: MutableList<Folder> = ArrayList()

        fun setName(name: String): Builder {
            this.name = name
            return this
        }

        fun setDescription(description: String): Builder {
            this.description = description
            return this
        }

        fun setFolders(folders: MutableList<Folder>): Builder {
            this.folders = folders
            return this
        }

        fun addFolder(folder: Folder): Builder {
            this.folders.add(folder)
            return this
        }

        fun build(): Document {
            return Document(name, description, folders)
        }
    }
}