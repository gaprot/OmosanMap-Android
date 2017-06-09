/*
 *  Created by Gaprot Dev Team
 *  Copyright © 2016年 Up-frontier, Inc. All rights reserved.
 */

package jp.gaprot.omosanmaps

import android.view.View

abstract class ListUiContract {

    abstract protected var listContainer: View

    abstract protected var loadingView: View

    abstract protected var emptyView: View

    fun setLoadingView(loading: Boolean) {
        if (loading) {
            loadingView.visibility = View.VISIBLE
            listContainer.visibility = View.GONE
        } else {
            loadingView.visibility = View.GONE
            listContainer.visibility = View.VISIBLE
        }
        emptyView.visibility = View.GONE
    }

    fun setEmptyView(empty: Boolean) {
        if (empty) {
            emptyView.visibility = View.VISIBLE
            listContainer.visibility = View.GONE
        } else {
            emptyView.visibility = View.GONE
            listContainer.visibility = View.VISIBLE
        }
        loadingView.visibility = View.GONE
    }
}
