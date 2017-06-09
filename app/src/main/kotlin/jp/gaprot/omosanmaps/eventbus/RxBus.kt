/*
 *  Created by Gaprot Dev Team
 *  Copyright © 2016年 Up-frontier, Inc. All rights reserved.
 */

package jp.gaprot.omosanmaps.eventbus

import rx.Observable
import rx.subjects.PublishSubject
import rx.subjects.SerializedSubject
import rx.subjects.Subject

/**
 *  RxJava による EventBus 実装
 */
object RxBus {

    private val subject: Subject<Any, Any> = SerializedSubject(PublishSubject.create())

    val observable: Observable<Any> get() = subject

    fun post(event: Any) {
        subject.onNext(event)
    }
}