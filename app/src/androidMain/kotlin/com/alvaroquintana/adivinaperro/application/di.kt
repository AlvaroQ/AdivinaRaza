package com.alvaroquintana.adivinaperro.application

import android.app.Application
import com.alvaroquintana.adivinaperro.BuildConfig
import com.alvaroquintana.adivinaperro.datasource.BreedEsDataBaseSourceImpl
import com.alvaroquintana.data.db.DriverFactory
import com.alvaroquintana.data.db.createDatabase
import com.alvaroquintana.adivinaperro.ui.game.BiggerSmallerViewModel
import com.alvaroquintana.adivinaperro.ui.game.DescriptionViewModel
import com.alvaroquintana.adivinaperro.ui.game.FciTriviaViewModel
import com.alvaroquintana.adivinaperro.ui.game.GameViewModel
import com.alvaroquintana.adivinaperro.ui.info.InfoViewModel
import com.alvaroquintana.adivinaperro.ui.result.ResultViewModel
import com.alvaroquintana.adivinaperro.ui.select.SelectViewModel
import com.alvaroquintana.data.datasource.DataBaseSource
import com.alvaroquintana.data.repository.BreedByIdRepository
import com.alvaroquintana.usecases.GetBreedById
import com.alvaroquintana.usecases.GetBreedList
import com.alvaroquintana.usecases.GetRandomBreedsWithDescription
import com.alvaroquintana.usecases.GetRandomBreedsWithFciGroup
import com.alvaroquintana.usecases.GetRandomBreedsWithWeight
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

fun Application.initDI() {
    startKoin {
        if (BuildConfig.DEBUG) {
            androidLogger()
        }
        androidContext(this@initDI)
        modules(appModule, dataModule, scopesModule)
    }
}

private val appModule = module {
    single { DriverFactory(androidContext()) }
    single { createDatabase(get()) }

    factory { Firebase.firestore }
    factory<DataBaseSource> { BreedEsDataBaseSourceImpl(get(), get()) }
}

val dataModule = module {
    factory { BreedByIdRepository(get()) }
}

private val scopesModule = module {
    viewModel { SelectViewModel() }
    viewModel { GameViewModel(get()) }
    viewModel { BiggerSmallerViewModel(get()) }
    viewModel { DescriptionViewModel(get()) }
    viewModel { FciTriviaViewModel(get()) }
    viewModel { ResultViewModel() }
    viewModel { InfoViewModel(get()) }

    factory { GetBreedById(get()) }
    factory { GetBreedList(get()) }
    factory { GetRandomBreedsWithWeight(get()) }
    factory { GetRandomBreedsWithDescription(get()) }
    factory { GetRandomBreedsWithFciGroup(get()) }
}
