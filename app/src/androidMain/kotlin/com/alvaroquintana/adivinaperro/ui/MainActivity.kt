package com.alvaroquintana.adivinaperro.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
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
import com.alvaroquintana.adivinaperro.ui.composables.rememberRewardedAdState
import com.alvaroquintana.adivinaperro.ui.game.BiggerSmallerScreenContent
import com.alvaroquintana.adivinaperro.ui.game.BiggerSmallerViewModel
import com.alvaroquintana.adivinaperro.ui.game.DescriptionScreenContent
import com.alvaroquintana.adivinaperro.ui.game.DescriptionViewModel
import com.alvaroquintana.adivinaperro.ui.game.FciTriviaScreenContent
import com.alvaroquintana.adivinaperro.ui.game.FciTriviaViewModel

import com.alvaroquintana.adivinaperro.ui.game.GameScreen
import com.alvaroquintana.adivinaperro.ui.game.GameViewModel
import com.alvaroquintana.adivinaperro.ui.info.InfoScreen
import com.alvaroquintana.adivinaperro.ui.info.InfoViewModel
import com.alvaroquintana.adivinaperro.ui.navigation.BiggerSmaller
import com.alvaroquintana.adivinaperro.ui.navigation.Description
import com.alvaroquintana.adivinaperro.ui.navigation.FciTrivia

import com.alvaroquintana.adivinaperro.ui.navigation.Game
import com.alvaroquintana.adivinaperro.ui.navigation.Info
import com.alvaroquintana.adivinaperro.ui.navigation.Result
import com.alvaroquintana.adivinaperro.ui.navigation.Select
import com.alvaroquintana.adivinaperro.ui.navigation.Settings
import com.alvaroquintana.adivinaperro.ui.navigation.Splash
import com.alvaroquintana.adivinaperro.ui.splash.SplashScreen
import com.alvaroquintana.adivinaperro.ui.result.ResultScreen
import com.alvaroquintana.adivinaperro.ui.result.ResultViewModel
import com.alvaroquintana.adivinaperro.ui.select.SelectScreen
import com.alvaroquintana.adivinaperro.ui.settings.SettingsScreen
import com.alvaroquintana.adivinaperro.ui.theme.AdivinaPerroTheme
import com.alvaroquintana.adivinaperro.ui.theme.ThemeMode
import com.alvaroquintana.adivinaperro.ui.theme.rememberWindowSizeClass
import com.alvaroquintana.adivinaperro.utils.Constants.TOTAL_BREED
import com.alvaroquintana.adivinaperro.utils.log
import com.alvaroquintana.adivinaperro.utils.playBarkSound
import com.alvaroquintana.adivinaperro.utils.playFailSound
import com.alvaroquintana.adivinaperro.utils.playSuccessSound
import com.alvaroquintana.adivinaperro.utils.rateApp
import com.alvaroquintana.adivinaperro.utils.shareApp
import com.google.android.gms.ads.MobileAds
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.alvaroquintana.adivinaperro.managers.ConsentManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
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
        // Helps diagnose Firestore queries on connected device/emulator only.
        FirebaseFirestore.setLoggingEnabled(BuildConfig.DEBUG)

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
                getSharedPreferences("${packageName}_preferences", Context.MODE_PRIVATE)
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

            val windowSizeClass = rememberWindowSizeClass()

            AdivinaPerroTheme(themeMode = themeMode, windowSizeClass = windowSizeClass) {
                val navController = rememberNavController()
                AppNavHost(
                    navController = navController,
                    onThemeModeChanged = { mode ->
                        themeMode = mode
                        prefs.edit { putString("theme_mode", mode.name) }
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
            FirebaseCrashlytics.getInstance().setUserId(user.uid)
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
                onNavigateToFciTrivia = {
                    Analytics.analyticsGameModeSelected(Analytics.MODE_FCI_TRIVIA)
                    navController.navigate(FciTrivia)
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

        composable<FciTrivia> {
            FciTriviaRoute(navController = navController)
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

// region FCI Trivia Route

@Composable
private fun FciTriviaRoute(navController: NavHostController) {
    val viewModel: FciTriviaViewModel = koinViewModel()
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    var showBanner by remember { mutableStateOf(false) }

    val rewardedAdState = rememberRewardedAdState(
        adUnitId = stringResource(R.string.BONIFICADO_GAME),
        adLocation = Analytics.AD_LOC_GAME
    )

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is FciTriviaViewModel.Event.NavigateToResult -> {
                    navController.navigate(Result(event.points)) {
                        popUpTo<Select>()
                    }
                }
                is FciTriviaViewModel.Event.ShowBannerAd -> showBanner = event.show
                is FciTriviaViewModel.Event.ShowRewardedAd -> rewardedAdState.show()
            }
        }
    }

    GameScreenLayout(
        title = stringResource(R.string.mode_fci_trivia),
        onBackClick = { navController.popBackStack() },
        showLives = false,
        lives = state.lives,
        showBanner = showBanner,
        bannerAdUnitId = stringResource(R.string.BANNER_GAME),
        bannerAdLocation = Analytics.AD_LOC_GAME
    ) {
        FciTriviaScreenContent(viewModel = viewModel, context = context)
    }
}


// endregion

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

    LaunchedEffect(Unit) {
        viewModel.navigation.collect { navigation ->
            when (navigation) {
                is GameViewModel.Navigation.Result -> {
                    navController.navigate(Result(points)) {
                        popUpTo<Select>()
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
        title = stringResource(R.string.mode_classic_title),
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

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is BiggerSmallerViewModel.Event.NavigateToResult -> {
                    navController.navigate(Result(event.points)) {
                        popUpTo<Select>()
                    }
                }
                is BiggerSmallerViewModel.Event.ShowBannerAd -> showBanner = event.show
                is BiggerSmallerViewModel.Event.ShowRewardedAd -> rewardedAdState.show()
            }
        }
    }

    GameScreenLayout(
        title = stringResource(R.string.mode_bigger_smaller),
        onBackClick = { navController.popBackStack() },
        showLives = false,
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

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is DescriptionViewModel.Event.NavigateToResult -> {
                    navController.navigate(Result(event.points)) {
                        popUpTo<Select>()
                    }
                }
                is DescriptionViewModel.Event.ShowBannerAd -> showBanner = event.show
                is DescriptionViewModel.Event.ShowRewardedAd -> rewardedAdState.show()
            }
        }
    }

    GameScreenLayout(
        title = stringResource(R.string.mode_description),
        onBackClick = { navController.popBackStack() },
        showLives = false,
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

    LaunchedEffect(Unit) {
        playBarkSound(context)
        viewModel.getPersonalRecord(gamePoints)
    }

    LaunchedEffect(Unit) {
        viewModel.navigation.collect { navigation ->
            when (navigation) {
                ResultViewModel.Navigation.Select -> {
                    navController.navigate(Select) {
                        popUpTo<Select>()
                    }
                }
                ResultViewModel.Navigation.Rate -> rateApp(context)
                is ResultViewModel.Navigation.Share -> shareApp(context, navigation.points)
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
            onPlayAgain = { viewModel.navigateToSelect() },
            onShare = { viewModel.navigateToShare(gamePoints) },
            onRate = { viewModel.navigateToRate() }
        )
    }
}

// endregion

// region Info Route

@Composable
private fun InfoRoute(navController: NavHostController) {
    val viewModel: InfoViewModel = koinViewModel()
    val selectedDog by viewModel.selectedDog.collectAsStateWithLifecycle()

    var currentPage by rememberSaveable { mutableIntStateOf(0) }

    val rewardedAdState = rememberRewardedAdState(
        adUnitId = stringResource(R.string.BONIFICADO_GAME),
        adLocation = Analytics.AD_LOC_INFO
    )

    LaunchedEffect(Unit) {
        viewModel.showingAds.collect { model ->
            when (model) {
                is InfoViewModel.UiModel.ShowReewardAd -> rewardedAdState.show()
                else -> {}
            }
        }
    }

    BackHandler(enabled = selectedDog != null) {
        viewModel.closeDogDetail()
    }

    GameScreenLayout(
        title = stringResource(R.string.info_title),
        onBackClick = {
            if (selectedDog != null) {
                viewModel.closeDogDetail()
            } else {
                navController.popBackStack()
            }
        },
        showLives = false,
        showBanner = selectedDog != null,
        bannerAdUnitId = stringResource(R.string.BANNER_INFO),
        bannerAdLocation = Analytics.AD_LOC_INFO
    ) {
        InfoScreen(
            viewModel = viewModel,
            currentPage = currentPage,
            onLoadMore = { nextPage ->
                currentPage = nextPage
                viewModel.loadMoreDogList(nextPage)
            }
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
                prefs.edit { putBoolean("sound", enabled) }
            },
            onThemeModeChanged = { mode ->
                themeMode = mode
                prefs.edit { putString("theme_mode", mode.name) }
                onThemeModeChanged(mode)
            },
            onRateApp = { rateApp(context) },
            onShare = { shareApp(context, -1) },
            onPrivacyOptions = {
                consentManager.showPrivacyOptionsForm(context as android.app.Activity) { _ -> }
            },
            onPrivacyPolicy = {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        "https://sites.google.com/view/alvaroquintana-privacy-policy".toUri()
                    )
                )
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .navigationBarsPadding()
    ) {
        GameAppBar(
            title = title,
            onBackClick = onBackClick,
            showLives = showLives,
            lives = lives
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(modifier = Modifier.widthIn(max = 680.dp)) {
                content()
            }
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
