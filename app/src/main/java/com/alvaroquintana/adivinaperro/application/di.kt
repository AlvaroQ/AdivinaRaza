package com.alvaroquintana.adivinaperro.application

import android.app.Application
import com.alvaroquintana.adivinaperro.ui.game.GameFragment
import com.alvaroquintana.adivinaperro.ui.game.GameViewModel
import com.alvaroquintana.adivinaperro.ui.result.ResultFragment
import com.alvaroquintana.adivinaperro.ui.result.ResultViewModel
import com.alvaroquintana.adivinaperro.ui.select.SelectFragment
import com.alvaroquintana.adivinaperro.ui.select.SelectViewModel
import com.alvaroquintana.data.datasource.DataBaseSource
import com.alvaroquintana.adivinaperro.datasource.DataBaseSourceImpl
import com.alvaroquintana.data.repository.AppsRecommendedRepository
import com.alvaroquintana.data.repository.BreedByIdRepository
import com.alvaroquintana.usecases.GetBreedById
import com.alvaroquintana.usecases.GetAppsRecommended
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
    factory<DataBaseSource> { DataBaseSourceImpl() }
}

val dataModule = module {
    factory { BreedByIdRepository(get()) }
    factory { AppsRecommendedRepository(get()) }
}

private val scopesModule = module {
    scope(named<SelectFragment>()) {
        viewModel { SelectViewModel() }
    }
    scope(named<GameFragment>()) {
        viewModel { GameViewModel(get()) }
        scoped { GetBreedById(get()) }
    }
    scope(named<ResultFragment>()) {
        viewModel { ResultViewModel(get()) }
        scoped { GetAppsRecommended(get()) }
    }

}
