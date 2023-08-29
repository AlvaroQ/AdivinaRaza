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
import com.alvaroquintana.data.repository.AppsRecommendedRepository
import com.alvaroquintana.data.repository.BreedByIdRepository
import com.alvaroquintana.data.repository.RankingRepository
import com.alvaroquintana.usecases.GetAppsRecommended
import com.alvaroquintana.usecases.GetBreedById
import com.alvaroquintana.usecases.GetBreedList
import com.alvaroquintana.usecases.GetRankingScore
import com.alvaroquintana.usecases.GetRecordScore
import com.alvaroquintana.usecases.SaveTopScore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun Application.initDI() {
    startKoin {
        androidLogger()
        androidContext(this@initDI)
        modules(appModule, dataModule, scopesModule)
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
    viewModel { SelectViewModel() }
    viewModel { GameViewModel(get()) }
    viewModel { ResultViewModel(get(), get(), get()) }
    viewModel { RankingViewModel(get()) }
    viewModel { InfoViewModel(get()) }

    factory { GetBreedById(get()) }
    factory { GetRecordScore(get()) }
    factory { GetAppsRecommended(get()) }
    factory { SaveTopScore(get()) }
    factory { GetRankingScore(get()) }
    factory { GetBreedList(get()) }
}
