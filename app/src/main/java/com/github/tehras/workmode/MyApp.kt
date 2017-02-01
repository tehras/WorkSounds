package com.github.tehras.workmode

import android.app.Application
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import io.fabric.sdk.android.Fabric
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

        Fabric.with(this, Crashlytics())
        Fabric.with(this, Answers())

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