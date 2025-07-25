package com.alvaroquintana.adivinaperro.ui.info

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alvaroquintana.adivinaperro.ui.theme.GameCream
import com.alvaroquintana.adivinaperro.ui.components.AppCard
import com.alvaroquintana.adivinaperro.ui.components.LoadingState
import com.alvaroquintana.adivinaperro.ui.composables.BreedImage
import com.alvaroquintana.adivinaperro.utils.Constants.TOTAL_BREED
import com.alvaroquintana.adivinaperro.utils.Constants.TOTAL_ITEM_EACH_LOAD
import com.alvaroquintana.domain.Dog
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@Composable
fun InfoScreen(
    viewModel: InfoViewModel,
    currentPage: Int,
    onLoadMore: (Int) -> Unit,
    onShowRewardedAd: () -> Unit
) {
    val progress by viewModel.progress.collectAsStateWithLifecycle()
    val isLoading = progress is InfoViewModel.UiModel.Loading &&
            (progress as InfoViewModel.UiModel.Loading).show

    val dogList by viewModel.currentDogList.collectAsStateWithLifecycle()

    val listState = rememberLazyListState()

    LaunchedEffect(listState, currentPage) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val totalItemsCount = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleItemIndex to totalItemsCount
        }
            .distinctUntilChanged()
            .filter { (lastVisible, total) -> total > 0 && lastVisible >= total - 1 }
            .collect { (_, _) ->
                val nextPage = currentPage + 1
                if (currentPage * TOTAL_ITEM_EACH_LOAD < TOTAL_BREED) {
                    onLoadMore(nextPage)
                }
                if (nextPage % 4 == 0) {
                    onShowRewardedAd()
                }
            }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GameCream)
    ) {
        AnimatedVisibility(
            visible = dogList.isNotEmpty(),
            enter = fadeIn()
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(dogList) { _, dog ->
                    DogInfoItem(dog = dog)
                }
            }
        }

        if (isLoading) {
            LoadingState()
        }
    }
}

@Composable
private fun DogInfoItem(dog: Dog) {
    AppCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            BreedImage(
                imageData = dog.icon,
                contentDescription = dog.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                contentScale = ContentScale.Fit
            )

            Text(
                text = dog.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }
    }
}
