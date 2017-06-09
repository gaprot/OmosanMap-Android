/*
 *  Created by Gaprot Dev Team
 *  Copyright © 2016年 Up-frontier, Inc. All rights reserved.
 */

package jp.gaprot.omosanmaps.eventbus

import android.util.Log
import com.google.android.gms.maps.model.Marker
import jp.gaprot.omosanmaps.data.model.Filter
import jp.gaprot.omosanmaps.data.model.Placemark

class PlacemarkSelectedEvent(val placemark: Placemark) {
    init {
        Log.d(PlacemarkSelectedEvent::class.java.simpleName, "fire! $placemark")
    }
}

class NewFilterEvent(val filter: Filter) {
    init {
        Log.d(NewFilterEvent::class.java.simpleName, "fire! $filter")
    }
}

class NewSortEvent(val placemarks: List<Placemark>) {
    init {
        Log.d(NewSortEvent::class.java.simpleName, "fire! ")
    }
}

class MarkerClickEvent(val marker: Marker) {
    init {
        Log.d(MarkerClickEvent::class.java.simpleName, "fire! $marker")
    }
}

class PreferenceUpdateEvent(val key: String) {
    init {
        Log.d(PreferenceUpdateEvent::class.java.simpleName, "fire! $key")
    }
}