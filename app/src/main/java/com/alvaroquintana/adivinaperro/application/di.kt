package com.alvaroquintana.adivinaperro.application

import com.alvaroquintana.adivinaperro.datasource.FirestoreDataSourceImpl
import com.alvaroquintana.data.datasource.FirestoreDataSource
import android.app.Application
import com.alvaroquintana.adivinaperro.ui.game.GameFragment
import com.alvaroquintana.adivinaperro.ui.game.GameViewModel
import com.alvaroquintana.adivinaperro.ui.result.ResultFragment
import com.alvaroquintana.adivinaperro.ui.result.ResultViewModel
import com.alvaroquintana.adivinaperro.ui.select.SelectFragment
import com.alvaroquintana.adivinaperro.ui.select.SelectViewModel
import com.alvaroquintana.data.datasource.DataBaseSource
import com.alvaroquintana.adivinaperro.datasource.DataBaseSourceImpl
import com.alvaroquintana.adivinaperro.ui.info.InfoFragment
import com.alvaroquintana.adivinaperro.ui.info.InfoViewModel
import com.alvaroquintana.adivinaperro.ui.ranking.RankingFragment
import com.alvaroquintana.adivinaperro.ui.ranking.RankingViewModel
import com.alvaroquintana.adivinaperro.ui.settings.SettingsFragment
import com.alvaroquintana.data.repository.AppsRecommendedRepository
import com.alvaroquintana.data.repository.BreedByIdRepository
import com.alvaroquintana.data.repository.RankingRepository
import com.alvaroquintana.usecases.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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
    factory { Firebase.firestore }
    single<CoroutineDispatcher> { Dispatchers.Main }
    factory<DataBaseSource> { DataBaseSourceImpl() }
    factory<FirestoreDataSource> { FirestoreDataSourceImpl(get()) }
}

val dataModule = module {
    factory { BreedByIdRepository(get()) }
    factory { AppsRecommendedRepository(get()) }
    factory { RankingRepository(get()) }
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
        viewModel { ResultViewModel(get(), get(), get()) }
        scoped { GetRecordScore(get()) }
        scoped { GetAppsRecommended(get()) }
        scoped { SaveTopScore(get()) }
    }
    scope(named<RankingFragment>()) {
        viewModel { RankingViewModel(get()) }
        scoped { GetRankingScore(get()) }
    }
    scope(named<InfoFragment>()) {
        viewModel { InfoViewModel(get()) }
        scoped { GetBreedList(get()) }
    }
}
