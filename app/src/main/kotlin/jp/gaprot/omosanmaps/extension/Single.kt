/*
 *  Created by Gaprot Dev Team
 *  Copyright © 2016年 Up-frontier, Inc. All rights reserved.
 */

package jp.gaprot.omosanmaps.extension

import rx.Single
import rx.Subscriber
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 *  Scheduler.io で subscribe して、UI スレッドで observe
 */
fun <T> Single<T>.async(): Single<T> {
    return subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
}

fun <T> Single<T>.onSuccess(block: (T) -> Unit): KSingleSubscription<T> {
    return KSingleSubscription(this).onSuccess(block)
}

fun <T> Single<T>.onError(block: (Throwable) -> Unit): KSingleSubscription<T> {
    return KSingleSubscription(this).onError(block)
}

class KSingleSubscription<T>(val single: Single<T>) {

    private var success: (T) -> Unit = {}
    private var error: (Throwable) -> Unit = { throw it }

    fun onSuccess(block: (T) -> Unit): KSingleSubscription<T> {
        success = block
        return this
    }

    fun onError(block: (Throwable) -> Unit): KSingleSubscription<T> {
        error = block
        return this
    }

    fun subscribe(): Subscription = single.subscribe(object : Subscriber<T>() {
        override fun onNext(t: T) {
            success.invoke(t)
        }

        override fun onError(e: Throwable?) {
            error.invoke(e ?: Throwable("Unknown error"))
        }

        override fun onCompleted() {
            // Do nothing
        }
    })
}