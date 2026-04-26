package com.alvaroquintana.adivinaperro.application

import android.app.Activity
import android.app.Application
import com.alvaroquintana.adivinaperro.BuildConfig
import com.alvaroquintana.adivinaperro.managers.ActivityHolder
import com.alvaroquintana.adivinaperro.managers.AndroidConsentGate
import com.alvaroquintana.adivinaperro.managers.AndroidIntentLauncher
import com.alvaroquintana.adivinaperro.managers.AndroidSettings
import com.alvaroquintana.adivinaperro.managers.AndroidSoundPlayer
import com.alvaroquintana.adivinaperro.managers.ConsentGate
import com.alvaroquintana.adivinaperro.managers.ConsentManager
import com.alvaroquintana.adivinaperro.managers.IntentLauncher
import com.alvaroquintana.adivinaperro.managers.Settings
import com.alvaroquintana.adivinaperro.managers.SoundPlayer
import com.alvaroquintana.adivinaperro.ui.game.BiggerSmallerViewModel
import com.alvaroquintana.adivinaperro.ui.game.DescriptionViewModel
import com.alvaroquintana.adivinaperro.ui.game.FciTriviaViewModel
import com.alvaroquintana.adivinaperro.ui.game.GameViewModel
import com.alvaroquintana.adivinaperro.ui.info.InfoViewModel
import com.alvaroquintana.adivinaperro.ui.result.ResultViewModel
import com.alvaroquintana.adivinaperro.ui.select.SelectViewModel
import com.alvaroquintana.data.datasource.BreedEsDataBaseSourceImpl
import com.alvaroquintana.data.datasource.DataBaseSource
import com.alvaroquintana.data.db.DriverFactory
import com.alvaroquintana.data.db.createDatabase
import com.alvaroquintana.data.repository.BreedByIdRepository
import com.alvaroquintana.usecases.GetBreedById
import com.alvaroquintana.usecases.GetBreedList
import com.alvaroquintana.usecases.GetRandomBreedsWithDescription
import com.alvaroquintana.usecases.GetRandomBreedsWithFciGroup
import com.alvaroquintana.usecases.GetRandomBreedsWithWeight
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.crashlytics.crashlytics
import dev.gitlive.firebase.firestore.firestore
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

private fun requireActivity(): Activity = ActivityHolder.current
    ?: error("No Activity bound. Did MainActivity register in onResume()?")

private val appModule = module {
    single { DriverFactory(androidContext()) }
    single { createDatabase(get()) }

    single { Firebase.firestore }
    single { Firebase.crashlytics }
    factory<DataBaseSource> { BreedEsDataBaseSourceImpl(get(), get(), get()) }
    single<Settings> { AndroidSettings(androidContext()) }
    single<SoundPlayer> { AndroidSoundPlayer(androidContext()) }
    single<IntentLauncher> { AndroidIntentLauncher { requireActivity() } }
    single<ConsentGate> {
        AndroidConsentGate(
            activityProvider = { requireActivity() },
            consent = ConsentManager.getInstance(androidContext())
        )
    }
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
    viewModel { ResultViewModel(get()) }
    viewModel { InfoViewModel(get()) }

    factory { GetBreedById(get()) }
    factory { GetBreedList(get()) }
    factory { GetRandomBreedsWithWeight(get()) }
    factory { GetRandomBreedsWithDescription(get()) }
    factory { GetRandomBreedsWithFciGroup(get()) }
}
