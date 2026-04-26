package com.alvaroquintana.adivinaperro.screenshot

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alvaroquintana.adivinaperro.R
import com.alvaroquintana.adivinaperro.ui.components.AnswerOptionCard
import com.alvaroquintana.adivinaperro.ui.components.AnswerState
import com.alvaroquintana.adivinaperro.ui.components.ConfettiOverlay
import com.alvaroquintana.adivinaperro.ui.components.OptionGrid
import com.alvaroquintana.adivinaperro.ui.select.SelectScreen
import com.alvaroquintana.adivinaperro.ui.settings.SettingsScreen
import com.alvaroquintana.adivinaperro.ui.splash.SplashScreen
import com.alvaroquintana.adivinaperro.ui.theme.DynaPuffFamily
import com.alvaroquintana.adivinaperro.ui.theme.GameRed
import com.alvaroquintana.adivinaperro.ui.theme.PillShape
import com.alvaroquintana.adivinaperro.ui.theme.ThemeMode
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Screenshot capture test for all app screens.
 *
 * Uses local drawable resources directly (via painterResource) instead of
 * BreedImage/Coil to ensure images render reliably in the test environment.
 *
 * Run: ./capture_screenshots.sh
 * Or:  ./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=...ScreenshotCaptureTest
 */
@RunWith(AndroidJUnit4::class)
class ScreenshotCaptureTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val breedNames = listOf(
        "Golden Retriever", "Chihuahua", "Labrador Retriever",
        "German Shepherd", "Bulldog", "Poodle"
    )

    // -- Helper --

    private fun captureRoot(name: String) {
        composeTestRule.waitForIdle()
        Thread.sleep(300)
        composeTestRule.waitForIdle()
        ScreenshotHelper.captureAndSave(
            composeTestRule.onAllNodes(isRoot()).onFirst(),
            name
        )
    }

    // =====================================================================
    // STATELESS SCREENS
    // =====================================================================

    @Test
    fun captureSplashScreen() {
        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.setContent {
            MaterialTheme {
                SplashScreen(onNavigateToSelect = {})
            }
        }
        composeTestRule.mainClock.advanceTimeBy(1000)
        captureRoot("01_splash")
    }

    @Test
    fun captureSelectScreen() {
        composeTestRule.setContent {
            MaterialTheme {
                SelectScreen(
                    onNavigateToGame = {},
                    onNavigateToBiggerSmaller = {},
                    onNavigateToDescription = {},
                    onNavigateToFciTrivia = {},
                    onNavigateToLearn = {},
                    onNavigateToSettings = {}
                )
            }
        }
        captureRoot("02_select")
    }

    @Test
    fun captureSettingsScreen() {
        composeTestRule.setContent {
            MaterialTheme {
                SettingsScreen(
                    isSoundEnabled = true,
                    themeMode = ThemeMode.SYSTEM,
                    versionText = "3.0.0",
                    showPrivacyOptions = true,
                    onSoundToggle = {},
                    onThemeModeChanged = {},
                    onRateApp = {},
                    onShare = {},
                    onPrivacyOptions = {},
                    onPrivacyPolicy = {}
                )
            }
        }
        captureRoot("09_settings")
    }

    // =====================================================================
    // GAME SCREENS — inline layout with local drawable images
    // =====================================================================

    @Test
    fun captureGameScreen() {
        val options = listOf("Golden Retriever", "Labrador", "Husky", "Beagle")
        composeTestRule.setContent {
            MaterialTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    // Top bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("3/450", fontFamily = DynaPuffFamily, fontWeight = FontWeight.Bold,
                            fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            repeat(3) { i ->
                                Icon(
                                    imageVector = if (2 > i) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                                    contentDescription = null, tint = GameRed, modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Text("15 pts", fontFamily = DynaPuffFamily, fontWeight = FontWeight.Bold,
                            fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    // Question image
                    Image(
                        painter = painterResource(R.drawable.landing_dogs_group),
                        contentDescription = "Question dog",
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("¿Qué raza es este perro?", fontFamily = DynaPuffFamily,
                        fontWeight = FontWeight.Bold, fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onBackground)
                    Spacer(modifier = Modifier.height(16.dp))
                    OptionGrid(options = options, modifier = Modifier.fillMaxWidth()) { _, option, cardModifier ->
                        AnswerOptionCard(text = option, state = AnswerState.NEUTRAL, enabled = true,
                            modifier = cardModifier, onClick = {})
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
        captureRoot("03_game")
    }

    @Test
    fun captureDescriptionScreen() {
        val options = listOf("Golden Retriever", "Labrador", "Husky", "Beagle")
        val descText = "This breed is known for being intelligent, friendly, devoted. It is a large sized dog. Originally from United Kingdom. It has a long coat. Life expectancy: 10-12 years."
        composeTestRule.setContent {
            MaterialTheme {
                Column(
                    modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
                        .padding(12.dp).verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(painter = painterResource(R.drawable.mascot_cooper),
                        contentDescription = "Cooper mascot",
                        modifier = Modifier.size(100.dp).clip(MaterialTheme.shapes.large),
                        contentScale = ContentScale.Crop)
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(modifier = Modifier.fillMaxWidth().background(
                        brush = Brush.horizontalGradient(listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.tertiaryContainer)),
                        shape = MaterialTheme.shapes.large
                    ).padding(horizontal = 16.dp, vertical = 14.dp)) {
                        Text("Adivina la raza", color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f))) {
                        Text(descText, color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodyLarge, fontStyle = FontStyle.Italic,
                            textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(20.dp),
                            lineHeight = 26.sp)
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    OptionGrid(options = options, modifier = Modifier.fillMaxWidth()) { _, option, cardModifier ->
                        AnswerOptionCard(text = option, state = AnswerState.NEUTRAL, enabled = true,
                            modifier = cardModifier, onClick = {})
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Score: 8", color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium))
                }
            }
        }
        captureRoot("05_description")
    }

    @Test
    fun captureResultScreen() {
        composeTestRule.setContent {
            MaterialTheme {
                Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
                    ConfettiOverlay()
                    Column(
                        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                            .padding(horizontal = 24.dp, vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Spacer(modifier = Modifier.height(24.dp))
                        Image(painter = painterResource(R.drawable.mascot_happy),
                            contentDescription = "Happy dog",
                            modifier = Modifier.size(160.dp).clip(CircleShape),
                            contentScale = ContentScale.Crop)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = stringResource(R.string.result, 18), fontFamily = DynaPuffFamily,
                            fontWeight = FontWeight.Bold, fontSize = 28.sp,
                            color = MaterialTheme.colorScheme.onBackground, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("¡Gran resultado!", fontFamily = DynaPuffFamily,
                            fontWeight = FontWeight.Normal, fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically) {
                            StatColumn("+18", "Puntos")
                            StatColumn("25", "Récord")
                            StatColumn("150", "Mundial")
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                        Surface(onClick = {}, modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = PillShape, color = MaterialTheme.colorScheme.primary) {
                            Row(modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.PlayArrow, null, tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                        Text(stringResource(R.string.play_again), fontFamily = DynaPuffFamily,
                                    fontWeight = FontWeight.Bold, fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onPrimary)
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        // App recommendations
                        LazyRow(contentPadding = PaddingValues(horizontal = 4.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(2) { index ->
                                Surface(modifier = Modifier.width(120.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.surface, onClick = {}) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Image(painter = painterResource(R.drawable.landing_dogs_group),
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxWidth().height(70.dp),
                                            contentScale = ContentScale.Crop)
                                        Text(if (index == 0) "AdivinaGato" else "AdivinaBandera",
                                                            fontFamily = DynaPuffFamily, fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onBackground,
                                            textAlign = TextAlign.Center, maxLines = 2,
                                            modifier = Modifier.fillMaxWidth()
                                                .padding(horizontal = 6.dp, vertical = 4.dp))
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
        captureRoot("06_result")
    }

    @Test
    fun captureInfoScreen() {
        composeTestRule.setContent {
            MaterialTheme {
                Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item(span = { GridItemSpan(2) }) {
                            Image(painter = painterResource(R.drawable.banner_happy_dogs),
                                contentDescription = "Happy Dogs",
                                modifier = Modifier.fillMaxWidth().height(140.dp)
                                    .clip(RoundedCornerShape(16.dp)),
                                contentScale = ContentScale.Crop)
                        }
                        items(breedNames.size) { index ->
                            Surface(shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surface, shadowElevation = 2.dp) {
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    Image(painter = painterResource(R.drawable.landing_dogs_group),
                                        contentDescription = breedNames[index],
                                        modifier = Modifier.fillMaxWidth().aspectRatio(0.85f)
                                            .clip(RoundedCornerShape(16.dp)),
                                        contentScale = ContentScale.Crop)
                                    Surface(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
                                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                                        shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)) {
                                                            Text(breedNames[index], fontFamily = DynaPuffFamily,
                                            fontWeight = FontWeight.Bold, fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            textAlign = TextAlign.Center, maxLines = 2,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.fillMaxWidth()
                                                .padding(horizontal = 8.dp, vertical = 10.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        captureRoot("08_info")
    }

    // -- Composable helpers --

    @Composable
    private fun StatColumn(value: String, label: String) {
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Icon(Icons.Rounded.Star, null, tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp))
                Text(value, fontFamily = DynaPuffFamily, fontWeight = FontWeight.Bold,
                fontSize = 18.sp, color = MaterialTheme.colorScheme.onBackground)
                Text(label, fontFamily = DynaPuffFamily, fontWeight = FontWeight.Normal,
                fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }

}
