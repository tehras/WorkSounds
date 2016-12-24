package com.github.tehras.workmode.ui.base

import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Provider

abstract class PresenterFragment<V : MvpView, T : Presenter<V>> : BaseFragment(),
        LoaderManager.LoaderCallbacks<T> {

    private val LOADER_ID = 1
    protected lateinit var presenter: T
    protected var firstLoad: Boolean = true

    // We call to bind presenter from onActivityCreated and onStart
    // This flag will ensure only one is used
    protected var viewIsBind: Boolean = false

    // Boolean flag to avoid delivering the Presenter twice. Calling initLoader in onActivityCreated means
    // onLoadFinished will be called twice during configuration change.
    // this is why everyone loves fragments...
    private var delivered = false

    @Inject
    protected lateinit var presenterLoaderProvider: Provider<PresenterLoader<T>>


    /**
     * Override this method.
     * This will happen slightly after onCreate and should happen
     * before any UI takes place into the screen
     * So good place to override
     * Overriding this will ensure that the presenter is available since it happens
     * after the Loader comes back
     */
    @CallSuper
    protected open fun onPresenterReady() {
        if (!viewIsBind)
            bindPresenter()
    }

    override fun onStart() {
        super.onStart()

        onPresenterReady()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initLoader()
    }

    private fun initLoader() {
        loaderManager.initLoader<T>(LOADER_ID, null, this)
    }

    @CallSuper
    protected fun onPresenterProvided(presenter: T) {
        this.presenter = presenter
    }

    @CallSuper
    protected fun onPresenterDestroyed() {
        // Hook for subclasses
    }

    @CallSuper
    override fun onStop() {
        super.onStop()
        Timber.d("unbindView")
        presenter.unbindView()
        viewIsBind = false
    }

    protected fun getViewLayer(): V {
        @Suppress("UNCHECKED_CAST")
        return this as V
    }

    override fun onLoadFinished(loader: Loader<T>?, presenter: T) {
        if (!delivered) {
            onPresenterProvided(presenter)
            delivered = true
        }
        onPresenterReady()
    }

    private fun bindPresenter() {
        Timber.d("bindPresenter")
        presenter.bindView(getViewLayer())
        firstLoad = false
        viewIsBind = true
    }

    override fun onCreateLoader(id: Int, bundle: Bundle?): Loader<T> {
        return presenterLoaderProvider.get()
    }

    override fun onLoaderReset(loader: Loader<T>?) {
        onPresenterDestroyed()
    }
}