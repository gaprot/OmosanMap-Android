/*
 *  Created by Gaprot Dev Team
 *  Copyright © 2016年 Up-frontier, Inc. All rights reserved.
 */

package jp.gaprot.omosanmaps.data.model

import android.os.Parcelable
import jp.gaprot.omosanmaps.data.model.abst.Entity
import jp.gaprot.omosanmaps.data.model.abst.Wrappable
import nz.bradcampbell.paperparcel.PaperParcel
import nz.bradcampbell.paperparcel.PaperParcelable
import java.util.*

@PaperParcel
data class Folder(

        override val name: String,

        val placemarks: List<Placemark>

) : Entity, PaperParcelable, Wrappable {

    companion object {
        @JvmField val CREATOR = PaperParcelable.Creator(Folder::class.java)
    }

    override fun wrap(): Parcelable {
        return FolderParcel(this)
    }

    class Builder {

        private var name = ""

        private var placemarks: MutableList<Placemark> = ArrayList()

        fun setName(name: String): Builder {
            this.name = name
            return this
        }

        fun setPlacemarks(placemarks: MutableList<Placemark>): Builder {
            this.placemarks = placemarks
            return this
        }

        fun addPlacemark(placemark: Placemark): Builder {
            this.placemarks.add(placemark.copy(folderName = this.name))
            return this
        }

        fun build(): Folder {
            return Folder(name, placemarks)
        }
    }
}