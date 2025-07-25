package com.alvaroquintana.adivinaperro.ui.result

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alvaroquintana.adivinaperro.R
import com.alvaroquintana.adivinaperro.ui.components.ConfettiOverlay
import com.alvaroquintana.adivinaperro.ui.components.LoadingState
import com.alvaroquintana.adivinaperro.ui.composables.BreedImage
import com.alvaroquintana.adivinaperro.ui.theme.GameCream
import com.alvaroquintana.adivinaperro.ui.theme.GameDark
import com.alvaroquintana.adivinaperro.ui.theme.GameGreen
import com.alvaroquintana.adivinaperro.ui.theme.GameMuted
import com.alvaroquintana.adivinaperro.ui.theme.GameOrange
import com.alvaroquintana.adivinaperro.ui.theme.GameWhite
import com.alvaroquintana.adivinaperro.ui.theme.GeistFamily
import com.alvaroquintana.adivinaperro.ui.theme.JetBrainsMonoFamily
import com.alvaroquintana.adivinaperro.ui.theme.PillShape
import com.alvaroquintana.domain.App

@Composable
fun ResultScreen(
    viewModel: ResultViewModel,
    gamePoints: Int,
    onPlayAgain: () -> Unit,
    onShare: () -> Unit,
    onRate: () -> Unit,
    onRanking: () -> Unit,
    onAppClicked: (String) -> Unit
) {
    val progress by viewModel.progress.collectAsStateWithLifecycle()
    val appList by viewModel.list.collectAsStateWithLifecycle()
    val personalRecord by viewModel.personalRecord.collectAsStateWithLifecycle()
    val worldRecord by viewModel.worldRecord.collectAsStateWithLifecycle()

    val isLoading = progress is ResultViewModel.UiModel.Loading &&
            (progress as ResultViewModel.UiModel.Loading).show

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GameCream)
    ) {
        ConfettiOverlay()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Success badge
            Surface(
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                color = GameGreen
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = null,
                        tint = GameWhite,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Title
            Text(
                text = stringResource(R.string.result, gamePoints),
                fontFamily = GeistFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 28.sp,
                color = GameDark,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Text(
                text = if (gamePoints > 5) "¡Gran resultado!" else "¡Sigue intentándolo!",
                fontFamily = GeistFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = GameMuted
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Score stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatItem(
                    icon = { Icon(Icons.Rounded.Star, null, tint = GameOrange, modifier = Modifier.size(24.dp)) },
                    value = "+$gamePoints",
                    label = "Puntos"
                )

                if (personalRecord.isNotEmpty()) {
                    StatItem(
                        icon = { Icon(Icons.Rounded.Star, null, tint = GameOrange, modifier = Modifier.size(24.dp)) },
                        value = personalRecord,
                        label = "Récord"
                    )
                }

                if (worldRecord.isNotEmpty()) {
                    StatItem(
                        icon = { Icon(Icons.Rounded.Star, null, tint = GameOrange, modifier = Modifier.size(24.dp)) },
                        value = worldRecord,
                        label = "Mundial"
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Play again - orange pill
                Surface(
                    onClick = onPlayAgain,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = PillShape,
                    color = GameOrange
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.PlayArrow,
                            contentDescription = null,
                            tint = GameWhite,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.play_again),
                            fontFamily = GeistFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = GameWhite
                        )
                    }
                }

                // Ranking - outlined pill
                Surface(
                    onClick = onRanking,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = PillShape,
                    color = GameWhite,
                    border = BorderStroke(1.5.dp, GameOrange)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_paw),
                            contentDescription = null,
                            tint = GameOrange,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.best_score),
                            fontFamily = GeistFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = GameOrange
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Share & Rate row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Surface(
                    onClick = onShare,
                    shape = PillShape,
                    color = GameCream
                ) {
                    Text(
                        text = stringResource(R.string.share),
                        fontFamily = GeistFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = GameOrange,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Surface(
                    onClick = onRate,
                    shape = PillShape,
                    color = GameCream
                ) {
                    Text(
                        text = stringResource(R.string.rate_on_play_store),
                        fontFamily = GeistFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = GameOrange,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }

            // Other apps section
            if (appList.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))

                var appsExpanded by remember { mutableStateOf(true) }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = GameWhite.copy(alpha = 0.7f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { appsExpanded = !appsExpanded }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.other_apps),
                            fontFamily = GeistFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = GameDark,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Rounded.KeyboardArrowDown,
                            contentDescription = null,
                            tint = GameMuted
                        )
                    }
                }

                AnimatedVisibility(
                    visible = appsExpanded,
                    enter = expandVertically() + fadeIn()
                ) {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(appList) { app ->
                            AppItem(
                                app = app,
                                onClick = { app.url?.let { onAppClicked(it) } }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        if (isLoading) {
            LoadingState()
        }
    }
}

@Composable
private fun StatItem(
    icon: @Composable () -> Unit,
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        icon()
        Text(
            text = value,
            fontFamily = JetBrainsMonoFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = GameDark
        )
        Text(
            text = label,
            fontFamily = GeistFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            color = GameMuted
        )
    }
}

@Composable
private fun AppItem(
    app: App,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.width(120.dp),
        shape = RoundedCornerShape(16.dp),
        color = GameWhite,
        onClick = onClick
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BreedImage(
                imageData = app.image ?: "",
                contentDescription = app.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp),
                contentScale = ContentScale.Crop
            )
            Text(
                text = app.name ?: "",
                fontFamily = GeistFamily,
                fontSize = 12.sp,
                color = GameDark,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 4.dp)
            )
        }
    }
}
