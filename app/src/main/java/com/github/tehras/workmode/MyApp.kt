package com.github.tehras.workmode

import android.app.Application
import timber.log.Timber
import javax.inject.Inject

class MyApp : Application() {
    @Inject
    lateinit var debugTree: Timber.DebugTree

    companion object {
        lateinit var graph: AppComponent
    }

    override fun onCreate() {
        super.onCreate()

        initDependencyGraph()

        if (BuildConfig.DEBUG) {
            Timber.plant(debugTree)
        }
    }

    private fun initDependencyGraph() {
        graph = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()
        graph.injectTo(this)
    }
}