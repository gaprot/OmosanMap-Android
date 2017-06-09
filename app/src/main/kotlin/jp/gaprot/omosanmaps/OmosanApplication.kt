/*
 *  Created by Gaprot Dev Team
 *  Copyright © 2016年 Up-frontier, Inc. All rights reserved.
 */

package jp.gaprot.omosanmaps

import android.app.Application

import io.realm.Realm

class OmosanApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Realm.init(this)
    }
}
