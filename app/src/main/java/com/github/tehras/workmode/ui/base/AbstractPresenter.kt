package com.github.tehras.workmode.ui.base

import android.app.Fragment
import android.content.Context
import android.support.v7.app.AppCompatActivity

abstract class AbstractPresenter<V : MvpView> : Presenter<V> {
    protected var view: V? = null

    override fun bindView(view: V) {
        this.view = view
    }

    override fun unbindView() {
        this.view = null
    }

    protected fun getContext(): Context {
        if (view is android.support.v4.app.Fragment)
            return (view as android.support.v4.app.Fragment).context
        else if (view is AppCompatActivity)
            return view as AppCompatActivity
        else
            throw RuntimeException("View was trying to get context but was class type $view")
    }

    override fun onDestroy() {
        // Hook for subclasses to clean up used resources
    }
}