package com.github.tehras.workmode.ui.base

import android.os.Bundle
import android.support.v4.app.Fragment
import com.github.tehras.workmode.AppComponent
import com.github.tehras.workmode.MyApp

abstract class BaseFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        injectDependencies(MyApp.graph)
    }

    abstract fun injectDependencies(graph: AppComponent)

}