package com.alvaroquintana.adivinaperro.ui.ranking

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alvaroquintana.adivinaperro.R
import com.alvaroquintana.adivinaperro.ui.components.LoadingState
import com.alvaroquintana.adivinaperro.ui.theme.GameBronze
import com.alvaroquintana.adivinaperro.ui.theme.GameCream
import com.alvaroquintana.adivinaperro.ui.theme.GameDark
import com.alvaroquintana.adivinaperro.ui.theme.GameGold
import com.alvaroquintana.adivinaperro.ui.theme.GameMuted
import com.alvaroquintana.adivinaperro.ui.theme.GameOrange
import com.alvaroquintana.adivinaperro.ui.theme.GameSilver
import com.alvaroquintana.adivinaperro.ui.theme.GameWhite
import com.alvaroquintana.adivinaperro.ui.theme.GeistFamily
import com.alvaroquintana.adivinaperro.ui.theme.JetBrainsMonoFamily
import com.alvaroquintana.domain.User

@Composable
fun RankingScreen(viewModel: RankingViewModel) {
    val rankingList by viewModel.rankingList.collectAsStateWithLifecycle()
    val progress by viewModel.progress.collectAsStateWithLifecycle()

    val isLoading = progress is RankingViewModel.UiModel.Loading &&
            (progress as RankingViewModel.UiModel.Loading).show

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GameCream)
    ) {
        AnimatedVisibility(
            visible = !isLoading && rankingList.isNotEmpty(),
            enter = fadeIn()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                // Header
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Clasificación",
                            fontFamily = GeistFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 28.sp,
                            color = GameDark
                        )
                        Text(
                            text = "Los mejores jugadores",
                            fontFamily = GeistFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            color = GameMuted
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Podium (top 3)
                if (rankingList.size >= 3) {
                    item {
                        PodiumSection(
                            first = rankingList[0],
                            second = rankingList[1],
                            third = rankingList[2]
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }

                // Remaining rankings (from 4th onward)
                if (rankingList.size > 3) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = GameWhite,
                            shadowElevation = 2.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                rankingList.drop(3).forEachIndexed { index, user ->
                                    RankingListItem(
                                        position = index + 4,
                                        user = user
                                    )
                                    if (index < rankingList.size - 4) {
                                        HorizontalDivider(
                                            color = Color(0x0A000000),
                                            thickness = 1.dp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        if (isLoading) {
            LoadingState()
        }
    }
}

@Composable
private fun PodiumSection(
    first: User,
    second: User,
    third: User
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        // 2nd place
        PodiumCard(
            user = second,
            position = 2,
            color = GameSilver,
            modifier = Modifier.weight(1f)
        )

        // 1st place (taller)
        PodiumCard(
            user = first,
            position = 1,
            color = GameGold,
            modifier = Modifier.weight(1f),
            isFirst = true
        )

        // 3rd place
        PodiumCard(
            user = third,
            position = 3,
            color = GameBronze,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun PodiumCard(
    user: User,
    position: Int,
    color: Color,
    modifier: Modifier = Modifier,
    isFirst: Boolean = false
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = GameWhite,
        shadowElevation = if (isFirst) 6.dp else 3.dp
    ) {
        Column(
            modifier = Modifier.padding(
                top = 16.dp,
                bottom = if (isFirst) 20.dp else 16.dp,
                start = 8.dp,
                end = 8.dp
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Crown for 1st place
            if (isFirst) {
                Icon(
                    imageVector = Icons.Rounded.Star,
                    contentDescription = null,
                    tint = GameGold,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Position badge
            Surface(
                modifier = Modifier.size(36.dp),
                shape = CircleShape,
                color = color.copy(alpha = 0.2f),
                border = BorderStroke(2.dp, color)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = position.toString(),
                        fontFamily = JetBrainsMonoFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        color = color
                    )
                }
            }

            // Name
            Text(
                text = user.name ?: "",
                fontFamily = GeistFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = GameDark,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            // Score
            Text(
                text = "${user.points ?: 0} pts",
                fontFamily = JetBrainsMonoFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = if (isFirst) GameOrange else GameMuted
            )
        }
    }
}

@Composable
private fun RankingListItem(
    position: Int,
    user: User
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Position number
        Text(
            text = position.toString(),
            fontFamily = JetBrainsMonoFamily,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 16.sp,
            color = GameMuted,
            modifier = Modifier.width(28.dp)
        )

        // Avatar circle placeholder
        Surface(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape),
            shape = CircleShape,
            color = GameOrange.copy(alpha = 0.15f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(R.drawable.ic_paw),
                    contentDescription = null,
                    tint = GameOrange,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Name and score
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = user.name ?: "",
                fontFamily = GeistFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = GameDark,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${user.points ?: 0} pts",
                fontFamily = GeistFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = GameMuted
            )
        }

        // Star icon
        Icon(
            imageVector = Icons.Rounded.Star,
            contentDescription = null,
            tint = GameMuted.copy(alpha = 0.4f),
            modifier = Modifier.size(20.dp)
        )
    }
}
