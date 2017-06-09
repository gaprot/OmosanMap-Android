/*
 *  Created by Gaprot Dev Team
 *  Copyright © 2016年 Up-frontier, Inc. All rights reserved.
 */

package jp.gaprot.omosanmaps.folder

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import jp.gaprot.omosanmaps.R
import jp.gaprot.omosanmaps.data.Repository
import jp.gaprot.omosanmaps.data.model.Filter
import jp.gaprot.omosanmaps.data.model.Folder
import jp.gaprot.omosanmaps.eventbus.NewFilterEvent
import jp.gaprot.omosanmaps.eventbus.RxBus
import jp.gaprot.omosanmaps.extension.async
import jp.gaprot.omosanmaps.extension.onSuccess
import jp.gaprot.omosanmaps.filter.FilterDialogFragment
import jp.gaprot.omosanmaps.placemark.PlacemarkActivity
import jp.gaprot.omosanmaps.preference.PreferenceActivity
import jp.gaprot.omosanmaps.widget.recycler.EntityAdapter
import org.jetbrains.anko.alert
import org.jetbrains.anko.setContentView
import rx.android.schedulers.AndroidSchedulers
import rx.subscriptions.CompositeSubscription

class FolderListActivity : AppCompatActivity() {

    companion object {

        private val TAG: String = FolderListActivity::class.java.simpleName

    }

    private val ui: FolderListActivityUI by lazy {
        FolderListActivityUI(
                folderAdapter = folderAdapter,
                onRetryButtonClick = { refresh() }
        )
    }

    private var folders: List<Folder> = emptyList()
        set(value) {
            ui.setFolders(value)
            field = value
        }

    private val folderAdapter: EntityAdapter<Folder>

    private val subscription = CompositeSubscription()

    init {
        folderAdapter = EntityAdapter(folders).apply {
            setOnItemClickListener { onFolderItemClick(it) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui.setContentView(this)
    }

    private fun getFolders() {
        subscription.add(Repository.getInstance(this)
                .getFolders()
                .async()
                .doOnSubscribe {
                    ui.setLoadingView(true)
                }
                .onSuccess {
                    folders = it
                    ui.setLoadingView(false)
                    ui.setEmptyView(it.isEmpty())
                }
                .onError {
                    Log.e(TAG, "getFolders: onError", it)
                    alert {
                        message("Error")
                        positiveButton("Try again", { getFolders() })
                    }.show()
                }
                .subscribe())
    }

    private fun refresh() {
        subscription.add(Repository.getInstance(this)
                .refresh()
                .async()
                .doOnSubscribe {
                    ui.setLoadingView(true)
                }
                .onSuccess {
                    folders = it.folders
                    ui.setLoadingView(false)
                    ui.setEmptyView(it.folders.isEmpty())
                }
                .onError {
                    Log.e(TAG, "refresh: onError", it)
                    alert {
                        message("Error")
                        positiveButton("Try again", { refresh() })
                    }.show()
                }
                .subscribe())
    }

    private fun onFolderItemClick(folder: Folder) {
        val filter = Filter.Builder()
                .setFolderName(folder.name).build()
        showPlacemarksWithFilter(filter)
    }

    private fun showPlacemarksWithFilter(filter: Filter) {
        PlacemarkActivity.createIntent(this, filter).let {
            startActivity(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_folder, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item == null) {
            return super.onOptionsItemSelected(item)
        }
        when (item.itemId) {
            R.id.action_map -> {
                PlacemarkActivity.createIntent(this, Filter.None).let { startActivity(it) }
            }
            R.id.action_filter -> {
                FilterDialogFragment.newInstance(Filter.None)
                        .show(supportFragmentManager, FilterDialogFragment::class.java.simpleName)
            }
            R.id.action_starred -> {
                val filterStarred = Filter.Builder().setStarred(true).build()
                PlacemarkActivity.createIntent(this, filterStarred).let { startActivity(it) }
            }
            R.id.action_refresh -> {
                refresh()
            }
            R.id.action_settings -> {
                PreferenceActivity.createIntent(this).let { startActivity(it) }
            }
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        if (folders.isEmpty()) {
            getFolders()
        }
        subscription.add(RxBus.observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    when (it) {
                        is NewFilterEvent -> showPlacemarksWithFilter(it.filter)
                    }
                }
        )
    }

    override fun onPause() {
        super.onPause()
        subscription.clear()
    }
}
