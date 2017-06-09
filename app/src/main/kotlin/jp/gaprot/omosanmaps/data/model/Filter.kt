/*
 *  Created by Gaprot Dev Team
 *  Copyright © 2016年 Up-frontier, Inc. All rights reserved.
 */

package jp.gaprot.omosanmaps.data.model

import android.os.Parcelable
import jp.gaprot.omosanmaps.data.model.abst.Unwrappable
import jp.gaprot.omosanmaps.data.model.abst.Wrappable
import nz.bradcampbell.paperparcel.PaperParcel
import nz.bradcampbell.paperparcel.PaperParcelable

@PaperParcel
data class Filter(

        val folderName: String? = null,

        val maxDistance: Int? = null,

        val starred: Boolean = false

) : PaperParcelable, Wrappable {

    companion object {

        @JvmField val CREATOR = PaperParcelable.Creator(Filter::class.java)

        val None = Filter()
    }

    override fun wrap(): Parcelable {
        return FilterParcel(this)
    }

    object Unwrapper : Unwrappable<Filter>

    fun isEmpty(): Boolean {
        return folderName.isNullOrEmpty() && maxDistance == null && starred == false
    }

    class Builder {

        private var folderName: String? = null

        private var maxDistance: Int? = null

        private var starred = false

        fun setFolderName(folderName: String): Builder {
            this.folderName = folderName
            return this
        }

        fun setMaxDistance(maxDistance: Int): Builder {
            this.maxDistance = maxDistance
            return this
        }

        fun setStarred(starred: Boolean): Builder {
            this.starred = starred
            return this
        }

        fun build(): Filter {
            return Filter(
                    folderName = folderName,
                    maxDistance = maxDistance,
                    starred = starred
            )
        }
    }
}