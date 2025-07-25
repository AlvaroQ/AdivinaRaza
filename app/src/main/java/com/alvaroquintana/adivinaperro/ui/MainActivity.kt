package com.alvaroquintana.adivinaperro.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.alvaroquintana.adivinaperro.BuildConfig
import com.alvaroquintana.adivinaperro.R
import com.alvaroquintana.adivinaperro.managers.Analytics
import com.alvaroquintana.adivinaperro.ui.animation.NavTransitions
import com.alvaroquintana.adivinaperro.ui.composables.AdBannerView
import com.alvaroquintana.adivinaperro.ui.composables.GameAppBar
import com.alvaroquintana.adivinaperro.ui.composables.SaveRecordDialog
import com.alvaroquintana.adivinaperro.ui.composables.rememberInterstitialAdState
import com.alvaroquintana.adivinaperro.ui.composables.rememberRewardedAdState
import com.alvaroquintana.adivinaperro.ui.game.BiggerSmallerScreenContent
import com.alvaroquintana.adivinaperro.ui.game.BiggerSmallerViewModel
import com.alvaroquintana.adivinaperro.ui.game.DescriptionScreenContent
import com.alvaroquintana.adivinaperro.ui.game.DescriptionViewModel
import com.alvaroquintana.adivinaperro.ui.game.GameScreen
import com.alvaroquintana.adivinaperro.ui.game.GameViewModel
import com.alvaroquintana.adivinaperro.ui.info.InfoScreen
import com.alvaroquintana.adivinaperro.ui.info.InfoViewModel
import com.alvaroquintana.adivinaperro.ui.navigation.BiggerSmaller
import com.alvaroquintana.adivinaperro.ui.navigation.Description
import com.alvaroquintana.adivinaperro.ui.navigation.Game
import com.alvaroquintana.adivinaperro.ui.navigation.Info
import com.alvaroquintana.adivinaperro.ui.navigation.Ranking
import com.alvaroquintana.adivinaperro.ui.navigation.Result
import com.alvaroquintana.adivinaperro.ui.navigation.Select
import com.alvaroquintana.adivinaperro.ui.navigation.Settings
import com.alvaroquintana.adivinaperro.ui.navigation.Splash
import com.alvaroquintana.adivinaperro.ui.ranking.RankingScreen
import com.alvaroquintana.adivinaperro.ui.splash.SplashScreen
import com.alvaroquintana.adivinaperro.ui.ranking.RankingViewModel
import com.alvaroquintana.adivinaperro.ui.result.ResultScreen
import com.alvaroquintana.adivinaperro.ui.result.ResultViewModel
import com.alvaroquintana.adivinaperro.ui.select.SelectScreen
import com.alvaroquintana.adivinaperro.ui.settings.SettingsScreen
import com.alvaroquintana.adivinaperro.ui.theme.AdivinaPerroTheme
import com.alvaroquintana.adivinaperro.ui.theme.ThemeMode
import com.alvaroquintana.adivinaperro.utils.Constants.TOTAL_BREED
import com.alvaroquintana.adivinaperro.utils.log
import com.alvaroquintana.adivinaperro.utils.playBarkSound
import com.alvaroquintana.adivinaperro.utils.playFailSound
import com.alvaroquintana.adivinaperro.utils.playSuccessSound
import com.alvaroquintana.adivinaperro.utils.rateApp
import com.alvaroquintana.adivinaperro.utils.screenOrientationPortrait
import com.alvaroquintana.adivinaperro.utils.shareApp
import com.alvaroquintana.domain.User
import com.google.android.gms.ads.MobileAds
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.alvaroquintana.adivinaperro.managers.ConsentManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

class MainActivity : AppCompatActivity() {

    private val tag = "MainActivity"
    private lateinit var auth: FirebaseAuth
    private lateinit var consentManager: ConsentManager
    private val isMobileAdsInitialized = AtomicBoolean(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        screenOrientationPortrait()

        auth = Firebase.auth
        Analytics.initialize(this)

        consentManager = ConsentManager.getInstance(this)
        consentManager.gatherConsent(this) { error ->
            if (error != null) {
                log(tag, "Consent error: ${error.errorCode} - ${error.message}")
            }
            if (consentManager.canRequestAds) {
                initializeMobileAdsSdk()
            }
        }
        // Fast path: use consent from previous session
        if (consentManager.canRequestAds) {
            initializeMobileAdsSdk()
        }

        setContent {
            val prefs = remember {
                getSharedPreferences(packageName + "_preferences", Context.MODE_PRIVATE)
            }
            var themeMode by remember {
                mutableStateOf(
                    try {
                        ThemeMode.valueOf(prefs.getString("theme_mode", ThemeMode.SYSTEM.name) ?: ThemeMode.SYSTEM.name)
                    } catch (_: Exception) {
                        ThemeMode.SYSTEM
                    }
                )
            }

            AdivinaPerroTheme(themeMode = themeMode) {
                val navController = rememberNavController()
                AppNavHost(
                    navController = navController,
                    onThemeModeChanged = { mode ->
                        themeMode = mode
                        prefs.edit().putString("theme_mode", mode.name).apply()
                    }
                )
            }
        }
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun signInAnonymously() {
        auth.signInAnonymously()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    log(tag, "signInAnonymously:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    log(tag, "signInAnonymously:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        val isSignedIn = user != null
        log(tag, "updateUI, isSignedON = $isSignedIn")

        if (!isSignedIn) {
            signInAnonymously()
        } else {
            FirebaseCrashlytics.getInstance().setUserId(user?.uid!!)
            log(tag, "updateUI, you are login in")
        }
    }

    private fun initializeMobileAdsSdk() {
        if (isMobileAdsInitialized.getAndSet(true)) return
        MobileAds.initialize(this)
    }
}

@Composable
private fun AppNavHost(
    navController: NavHostController,
    onThemeModeChanged: (ThemeMode) -> Unit = {}
) {
    NavHost(
        navController = navController,
        startDestination = Splash,
        enterTransition = { NavTransitions.enterTransition },
        exitTransition = { NavTransitions.exitTransition },
        popEnterTransition = { NavTransitions.popEnterTransition },
        popExitTransition = { NavTransitions.popExitTransition }
    ) {
        composable<Splash>(
            enterTransition = { NavTransitions.fadeEnterTransition },
            exitTransition = { NavTransitions.fadeExitTransition }
        ) {
            SplashScreen(
                onNavigateToSelect = {
                    navController.navigate(Select) {
                        popUpTo<Splash> { inclusive = true }
                    }
                }
            )
        }

        composable<Select> {
            val selectViewModel: com.alvaroquintana.adivinaperro.ui.select.SelectViewModel = koinViewModel()
            SelectScreen(
                onNavigateToGame = {
                    Analytics.analyticsGameModeSelected(Analytics.MODE_CLASSIC)
                    navController.navigate(Game)
                },
                onNavigateToBiggerSmaller = {
                    Analytics.analyticsGameModeSelected(Analytics.MODE_BIGGER_SMALLER)
                    navController.navigate(BiggerSmaller)
                },
                onNavigateToDescription = {
                    Analytics.analyticsGameModeSelected(Analytics.MODE_DESCRIPTION)
                    navController.navigate(Description)
                },
                onNavigateToLearn = {
                    Analytics.analyticsClicked(Analytics.BTN_LEARN)
                    navController.navigate(Info)
                },
                onNavigateToSettings = {
                    Analytics.analyticsClicked(Analytics.BTN_SETTINGS)
                    navController.navigate(Settings)
                }
            )
        }

        composable<Game> {
            GameRoute(navController = navController)
        }

        composable<BiggerSmaller> {
            BiggerSmallerRoute(navController = navController)
        }

        composable<Description> {
            DescriptionRoute(navController = navController)
        }

        composable<Result>(
            enterTransition = { NavTransitions.resultEnterTransition },
            exitTransition = { NavTransitions.resultExitTransition },
            popEnterTransition = { NavTransitions.resultEnterTransition },
            popExitTransition = { NavTransitions.resultExitTransition }
        ) { backStackEntry ->
            val result: Result = backStackEntry.toRoute()
            ResultRoute(navController = navController, gamePoints = result.points)
        }

        composable<Ranking> {
            RankingRoute(navController = navController)
        }

        composable<Info>(
            enterTransition = { NavTransitions.fadeEnterTransition },
            exitTransition = { NavTransitions.fadeExitTransition },
            popEnterTransition = { NavTransitions.fadeEnterTransition },
            popExitTransition = { NavTransitions.fadeExitTransition }
        ) {
            InfoRoute(navController = navController)
        }

        composable<Settings>(
            enterTransition = { NavTransitions.fadeEnterTransition },
            exitTransition = { NavTransitions.fadeExitTransition },
            popEnterTransition = { NavTransitions.fadeEnterTransition },
            popExitTransition = { NavTransitions.fadeExitTransition }
        ) {
            SettingsRoute(
                navController = navController,
                onThemeModeChanged = onThemeModeChanged
            )
        }
    }
}

// region Game Classic Route

@Composable
private fun GameRoute(navController: NavHostController) {
    val viewModel: GameViewModel = koinViewModel()
    val context = LocalContext.current

    var life by rememberSaveable { mutableIntStateOf(3) }
    var stage by rememberSaveable { mutableIntStateOf(1) }
    var points by rememberSaveable { mutableIntStateOf(0) }
    var showBanner by remember { mutableStateOf(false) }

    val rewardedAdState = rememberRewardedAdState(
        adUnitId = stringResource(R.string.BONIFICADO_GAME),
        adLocation = Analytics.AD_LOC_GAME
    )

    // Pre-load interstitial for game over
    val interstitialAdState = rememberInterstitialAdState(
        adUnitId = stringResource(R.string.INTERSTICIAL_GAME_OVER),
        adLocation = Analytics.AD_LOC_GAME_OVER
    )

    LaunchedEffect(Unit) {
        viewModel.navigation.collect { navigation ->
            when (navigation) {
                is GameViewModel.Navigation.Result -> {
                    interstitialAdState.show {
                        navController.navigate(Result(points)) {
                            popUpTo<Select>()
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.showingAds.collect { model ->
            when (model) {
                is GameViewModel.UiModel.ShowBannerAd -> showBanner = model.show
                is GameViewModel.UiModel.ShowReewardAd -> rewardedAdState.show()
                else -> {}
            }
        }
    }

    GameScreenLayout(
        title = stage.toString(),
        onBackClick = { navController.popBackStack() },
        showLives = false,
        lives = life,
        showBanner = showBanner,
        bannerAdUnitId = stringResource(R.string.BANNER_GAME),
        bannerAdLocation = Analytics.AD_LOC_GAME
    ) {
        GameScreen(
            viewModel = viewModel,
            stage = stage,
            lives = life,
            points = points,
            onAnswerSelected = { selectedIndex, correctName, options ->
                val selectedText = options[selectedIndex]
                val isCorrect = selectedText == correctName

                Analytics.analyticsGameAnswer(isCorrect, stage, Analytics.MODE_CLASSIC)
                FirebaseCrashlytics.getInstance().apply {
                    setCustomKey("game_mode", Analytics.MODE_CLASSIC)
                    setCustomKey("current_stage", stage)
                    setCustomKey("current_score", points)
                    setCustomKey("lives_remaining", life)
                }

                if (isCorrect) {
                    playSuccessSound(context)
                    points += 1
                } else {
                    playFailSound(context)
                    life--
                }

                stage += 1
            }
        )
    }

    LaunchedEffect(stage) {
        if (stage > 1) {
            delay(TimeUnit.MILLISECONDS.toMillis(1000))
            if (stage > TOTAL_BREED || life < 1) {
                viewModel.navigateToResult(points.toString())
            } else {
                viewModel.generateNewStage()
                if (stage != 0 && stage % 6 == 0) viewModel.showRewardedAd()
            }
        }
    }
}

// endregion

// region Bigger/Smaller Route

@Composable
private fun BiggerSmallerRoute(navController: NavHostController) {
    val viewModel: BiggerSmallerViewModel = koinViewModel()
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    var showBanner by remember { mutableStateOf(false) }

    val rewardedAdState = rememberRewardedAdState(
        adUnitId = stringResource(R.string.BONIFICADO_GAME),
        adLocation = Analytics.AD_LOC_GAME
    )

    // Pre-load interstitial for game over
    val interstitialAdState = rememberInterstitialAdState(
        adUnitId = stringResource(R.string.INTERSTICIAL_GAME_OVER),
        adLocation = Analytics.AD_LOC_GAME_OVER
    )

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is BiggerSmallerViewModel.Event.NavigateToResult -> {
                    interstitialAdState.show {
                        navController.navigate(Result(event.points)) {
                            popUpTo<Select>()
                        }
                    }
                }
                is BiggerSmallerViewModel.Event.ShowBannerAd -> showBanner = event.show
                is BiggerSmallerViewModel.Event.ShowRewardedAd -> rewardedAdState.show()
            }
        }
    }

    GameScreenLayout(
        title = state.stage.toString(),
        onBackClick = { navController.popBackStack() },
        showLives = true,
        lives = state.lives,
        showBanner = showBanner,
        bannerAdUnitId = stringResource(R.string.BANNER_GAME),
        bannerAdLocation = Analytics.AD_LOC_GAME
    ) {
        BiggerSmallerScreenContent(viewModel = viewModel, context = context)
    }
}

// endregion

// region Description Route

@Composable
private fun DescriptionRoute(navController: NavHostController) {
    val viewModel: DescriptionViewModel = koinViewModel()
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    var showBanner by remember { mutableStateOf(false) }

    val rewardedAdState = rememberRewardedAdState(
        adUnitId = stringResource(R.string.BONIFICADO_GAME),
        adLocation = Analytics.AD_LOC_GAME
    )

    // Pre-load interstitial for game over
    val interstitialAdState = rememberInterstitialAdState(
        adUnitId = stringResource(R.string.INTERSTICIAL_GAME_OVER),
        adLocation = Analytics.AD_LOC_GAME_OVER
    )

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is DescriptionViewModel.Event.NavigateToResult -> {
                    interstitialAdState.show {
                        navController.navigate(Result(event.points)) {
                            popUpTo<Select>()
                        }
                    }
                }
                is DescriptionViewModel.Event.ShowBannerAd -> showBanner = event.show
                is DescriptionViewModel.Event.ShowRewardedAd -> rewardedAdState.show()
            }
        }
    }

    GameScreenLayout(
        title = state.stage.toString(),
        onBackClick = { navController.popBackStack() },
        showLives = true,
        lives = state.lives,
        showBanner = showBanner,
        bannerAdUnitId = stringResource(R.string.BANNER_GAME),
        bannerAdLocation = Analytics.AD_LOC_GAME
    ) {
        DescriptionScreenContent(viewModel = viewModel, context = context)
    }
}

// endregion

// region Result Route

@Composable
private fun ResultRoute(navController: NavHostController, gamePoints: Int) {
    val viewModel: ResultViewModel = koinViewModel()
    val context = LocalContext.current

    var showDialog by remember { mutableStateOf(false) }
    var dialogPoints by remember { mutableStateOf("") }

    val rewardedAdState = rememberRewardedAdState(
        adUnitId = stringResource(R.string.BONIFICADO_GAME_OVER),
        adLocation = Analytics.AD_LOC_GAME_OVER
    )

    LaunchedEffect(Unit) {
        playBarkSound(context)
        viewModel.getPersonalRecord(gamePoints, context)
        viewModel.setPersonalRecordOnServer(gamePoints)
    }

    LaunchedEffect(Unit) {
        viewModel.navigation.collect { navigation ->
            when (navigation) {
                ResultViewModel.Navigation.Game -> {
                    navController.navigate(Game) {
                        popUpTo<Select>()
                    }
                }
                ResultViewModel.Navigation.Rate -> rateApp(context)
                ResultViewModel.Navigation.Ranking -> navController.navigate(Ranking)
                is ResultViewModel.Navigation.Share -> shareApp(context, navigation.points)
                is ResultViewModel.Navigation.Open -> {
                    try {
                        context.startActivity(
                            Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${navigation.url}"))
                        )
                    } catch (_: ActivityNotFoundException) {
                        context.startActivity(
                            Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${navigation.url}"))
                        )
                    }
                }
                is ResultViewModel.Navigation.Dialog -> {
                    dialogPoints = navigation.points
                    showDialog = true
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.showingAds.collect { model ->
            if (model is ResultViewModel.UiModel.ShowAd) {
                rewardedAdState.show()
            }
        }
    }

    GameScreenLayout(
        title = stringResource(R.string.resultado_screen_title),
        onBackClick = {
            navController.navigate(Select) {
                popUpTo<Select> { inclusive = true }
            }
        },
        showLives = false,
        showBanner = false
    ) {
        ResultScreen(
            viewModel = viewModel,
            gamePoints = gamePoints,
            onPlayAgain = { viewModel.navigateToGame() },
            onShare = { viewModel.navigateToShare(gamePoints) },
            onRate = { viewModel.navigateToRate() },
            onRanking = { viewModel.navigateToRanking() },
            onAppClicked = { url -> viewModel.onAppClicked(url) }
        )
    }

    if (showDialog) {
        SaveRecordDialog(
            onDismiss = { showDialog = false },
            onSave = { name ->
                viewModel.saveTopScore(User(name, dialogPoints.toInt()))
                showDialog = false
            }
        )
    }
}

// endregion

// region Ranking Route

@Composable
private fun RankingRoute(navController: NavHostController) {
    val viewModel: RankingViewModel = koinViewModel()

    GameScreenLayout(
        title = stringResource(R.string.ranking_screen_title),
        onBackClick = { navController.popBackStack() },
        showLives = false,
        showBanner = true,
        bannerAdUnitId = stringResource(R.string.BANNER_RANKING),
        bannerAdLocation = Analytics.AD_LOC_RANKING
    ) {
        RankingScreen(viewModel = viewModel)
    }
}

// endregion

// region Info Route

@Composable
private fun InfoRoute(navController: NavHostController) {
    val viewModel: InfoViewModel = koinViewModel()

    var currentPage by rememberSaveable { mutableIntStateOf(0) }
    var showBanner by remember { mutableStateOf(false) }

    val rewardedAdState = rememberRewardedAdState(
        adUnitId = stringResource(R.string.BONIFICADO_GAME),
        adLocation = Analytics.AD_LOC_INFO
    )

    LaunchedEffect(Unit) {
        viewModel.showingAds.collect { model ->
            when (model) {
                is InfoViewModel.UiModel.ShowAd -> showBanner = model.show
                is InfoViewModel.UiModel.ShowReewardAd -> rewardedAdState.show()
                else -> {}
            }
        }
    }

    GameScreenLayout(
        title = stringResource(R.string.info_title),
        onBackClick = { navController.popBackStack() },
        showLives = false,
        showBanner = showBanner,
        bannerAdUnitId = stringResource(R.string.BANNER_INFO),
        bannerAdLocation = Analytics.AD_LOC_INFO
    ) {
        InfoScreen(
            viewModel = viewModel,
            currentPage = currentPage,
            onLoadMore = { nextPage ->
                currentPage = nextPage
                viewModel.loadMoreDogList(nextPage)
            },
            onShowRewardedAd = { viewModel.showRewardedAd() }
        )
    }
}

// endregion

// region Settings Route

@Composable
private fun SettingsRoute(
    navController: NavHostController,
    onThemeModeChanged: (ThemeMode) -> Unit = {}
) {
    val context = LocalContext.current
    val prefs = remember {
        context.getSharedPreferences(
            context.packageName + "_preferences",
            Context.MODE_PRIVATE
        )
    }
    var isSoundEnabled by remember { mutableStateOf(prefs.getBoolean("sound", true)) }
    var themeMode by remember {
        mutableStateOf(
            try {
                ThemeMode.valueOf(prefs.getString("theme_mode", ThemeMode.SYSTEM.name) ?: ThemeMode.SYSTEM.name)
            } catch (_: Exception) {
                ThemeMode.SYSTEM
            }
        )
    }
    val versionText = "${stringResource(R.string.settings_version)} ${BuildConfig.VERSION_NAME} (Build ${BuildConfig.VERSION_CODE})"

    LaunchedEffect(Unit) {
        Analytics.analyticsScreenViewed(Analytics.SCREEN_SETTINGS)
    }

    GameScreenLayout(
        title = stringResource(R.string.settings),
        onBackClick = { navController.popBackStack() },
        showLives = false,
        showBanner = false
    ) {
        val consentManager = remember { ConsentManager.getInstance(context) }

        SettingsScreen(
            isSoundEnabled = isSoundEnabled,
            themeMode = themeMode,
            versionText = versionText,
            showPrivacyOptions = consentManager.isPrivacyOptionsRequired,
            onSoundToggle = { enabled ->
                isSoundEnabled = enabled
                prefs.edit().putBoolean("sound", enabled).apply()
            },
            onThemeModeChanged = { mode ->
                themeMode = mode
                prefs.edit().putString("theme_mode", mode.name).apply()
                onThemeModeChanged(mode)
            },
            onRateApp = { rateApp(context) },
            onMoreApps = {
                try {
                    context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/collection/cluster?clp=igM4ChkKEzg4Nzc2MDA3NjYwNDEzMDc4NTIQCBgDEhkKEzg4Nzc2MDA3NjYwNDEzMDc4NTIQCBgDGAA%3D:S:ANO1ljItPd0&gsr=CjuKAzgKGQoTODg3NzYwMDc2NjA0MTMwNzg1MhAIGAMSGQoTODg3NzYwMDc2NjA0MTMwNzg1MhAIGAMYAA%3D%3D:S:ANO1ljLjm34")
                        )
                    )
                } catch (_: ActivityNotFoundException) {
                }
            },
            onShare = { shareApp(context, -1) },
            onPrivacyOptions = {
                consentManager.showPrivacyOptionsForm(context as android.app.Activity) { _ -> }
            }
        )
    }
}

// endregion

// region Shared Layout

@Composable
private fun GameScreenLayout(
    title: String,
    onBackClick: () -> Unit,
    showLives: Boolean,
    lives: Int = 0,
    showBanner: Boolean = false,
    bannerAdUnitId: String = "",
    bannerAdLocation: String = Analytics.AD_LOC_GAME,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().background(com.alvaroquintana.adivinaperro.ui.theme.GameCream)) {
        GameAppBar(
            title = title,
            onBackClick = onBackClick,
            showLives = showLives,
            lives = lives
        )

        Box(modifier = Modifier.weight(1f)) {
            content()
        }

        if (showBanner && bannerAdUnitId.isNotEmpty()) {
            AdBannerView(
                adUnitId = bannerAdUnitId,
                modifier = Modifier.fillMaxWidth(),
                adLocation = bannerAdLocation
            )
        }
    }
}

// endregion
