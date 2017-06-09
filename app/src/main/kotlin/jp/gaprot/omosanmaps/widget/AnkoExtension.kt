/*
 *  Created by Gaprot Dev Team
 *  Copyright © 2016年 Up-frontier, Inc. All rights reserved.
 */

package jp.gaprot.omosanmaps.widget

import android.support.design.widget.TextInputEditText
import android.view.ViewManager
import org.jetbrains.anko.custom.ankoView

/**
 *  Anko DSL methods for PlacemarkHeaderView
 */
@Suppress("unused")
fun ViewManager.placemarkHeaderView(theme: Int = 0): PlacemarkHeaderView = placemarkHeaderView(theme) {}

@Suppress("unused")
inline fun ViewManager.placemarkHeaderView(
        theme: Int = 0, init: PlacemarkHeaderView.() -> Unit
): PlacemarkHeaderView = ankoView(::PlacemarkHeaderView, theme, init)

/**
 *  Anko DSL methods for ActivatableIconView
 */
@Suppress("unused")
fun ViewManager.activatableIconView(theme: Int = 0): ActivatableIconView = activatableIconView(theme) {}

@Suppress("unused")
inline fun ViewManager.activatableIconView(
        theme: Int = 0, init: ActivatableIconView.() -> Unit
): ActivatableIconView = ankoView(::ActivatableIconView, theme, init)

/**
 *  Anko DSL methods for TextInputEditText
 */
@Suppress("unused")
fun ViewManager.textInputEditText(theme: Int = 0): TextInputEditText = textInputEditText(theme) {}
@Suppress("unused")
inline fun ViewManager.textInputEditText(
        theme: Int = 0, init: TextInputEditText.() -> Unit
): TextInputEditText = ankoView(::TextInputEditText, theme, init)

