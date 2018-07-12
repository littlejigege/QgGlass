package com.qgstudio.qgglass

import android.util.Log
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable

fun <T> Observable<T>.fastObserve(tag: String, onNext: (result: T) -> Unit) {
    this.subscribeOn(io.reactivex.schedulers.Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<T> {
                override fun onComplete() {

                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: T) {
                    onNext(t)
                }

                override fun onError(e: Throwable) {
                    Log.e(tag, e.message)
                }
            })
}