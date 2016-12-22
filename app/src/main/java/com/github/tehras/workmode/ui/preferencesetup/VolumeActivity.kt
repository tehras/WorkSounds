package com.github.tehras.workmode.ui.preferencesetup

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import com.github.tehras.workmode.AppComponent
import com.github.tehras.workmode.R
import com.github.tehras.workmode.ui.base.PresenterActivity

class VolumeActivity : PresenterActivity<VolumeView, VolumePresenter>(), VolumeView {
    override fun injectDependencies(graph: AppComponent) {
        graph.plus(VolumeModule(this)).injectTo(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_volume)
    }

    override fun onStart() {
        super.onStart()


        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()

            //todo add group
        }
    }

}
