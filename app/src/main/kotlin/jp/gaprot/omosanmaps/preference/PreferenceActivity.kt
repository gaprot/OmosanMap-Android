/*
 *  Created by Gaprot Dev Team
 *  Copyright © 2016年 Up-frontier, Inc. All rights reserved.
 */

package jp.gaprot.omosanmaps.preference

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import jp.gaprot.omosanmaps.eventbus.PreferenceUpdateEvent
import jp.gaprot.omosanmaps.eventbus.RxBus
import org.jetbrains.anko.setContentView
import rx.android.schedulers.AndroidSchedulers
import rx.subscriptions.CompositeSubscription

class PreferenceActivity : AppCompatActivity() {

    companion object {
        @JvmStatic fun createIntent(context: Context): Intent {
            return Intent(context, PreferenceActivity::class.java)
        }
    }

    private val preferences: List<Preference> by lazy {
        initPrefs()
    }

    private val ui: PreferenceActivityUI by lazy {
        PreferenceActivityUI(preferences = preferences, onItemClick = { showDialog(it) })
    }

    private val subscription = CompositeSubscription()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui.setContentView(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initPrefs(): List<Preference> {
        return Preference.values().toList()
    }

    private fun showDialog(preference: Preference) {
        PreferenceDialogBuilder(preference, this).create().show()
    }

    override fun onResume() {
        super.onResume()
        subscription.add(RxBus.observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    when (it) {
                        is PreferenceUpdateEvent -> ui.update(it.key)
                    }
                }
        )
    }

    override fun onPause() {
        super.onPause()
        subscription.clear()
    }
}