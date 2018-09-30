package com.kachscovsky.boris.userdashboard.utils

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean


class Action<T> : MutableLiveData<T>() {

    private val atomicIsValueSet = AtomicBoolean(false)

    override fun observe(owner: LifecycleOwner, observer: Observer<T>) {
        super.observe(owner, Observer<T> { t ->
            if (atomicIsValueSet.compareAndSet(true, false)) {
                observer.onChanged(t)
            }
        })
    }

    override fun setValue(t: T?) {
        atomicIsValueSet.set(true)
        super.setValue(t)
    }

    fun call() {
        this.value = null
    }

}