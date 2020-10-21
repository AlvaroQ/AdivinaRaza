package com.alvaroquintana.adivinaperro.application

import android.app.Application
import com.alvaroquintana.adivinaperro.ui.game.GameActivity
import com.alvaroquintana.adivinaperro.ui.game.GameFragment
import com.alvaroquintana.adivinaperro.ui.game.GameViewModel
import com.alvaroquintana.adivinaperro.ui.result.ResultFragment
import com.alvaroquintana.adivinaperro.ui.select.SelectFragment
import com.alvaroquintana.adivinaperro.ui.select.SelectViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun Application.initDI() {
    startKoin {
        androidLogger()
        androidContext(this@initDI)
        koin.loadModules(listOf(
            appModule,
            dataModule,
            scopesModule
        ))
        koin.createRootScope()
    }
}

private val appModule = module {
    single<CoroutineDispatcher> { Dispatchers.Main }
}

val dataModule = module {

}

private val scopesModule = module {
    scope(named<SelectFragment>()) {
        viewModel { SelectViewModel() }
    }
    scope(named<GameFragment>()) {
        viewModel { GameViewModel() }
    }
    scope(named<ResultFragment>()) {
        viewModel { GameViewModel() }
    }

}
