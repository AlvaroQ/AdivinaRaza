package com.alvaroquintana.adivinaperro.ui.info

import adivinaraza.app.generated.resources.Res
import adivinaraza.app.generated.resources.info_barking_level_format
import adivinaraza.app.generated.resources.info_children_format
import adivinaraza.app.generated.resources.info_coat_type_format
import adivinaraza.app.generated.resources.info_colors_format
import adivinaraza.app.generated.resources.info_common_diseases_format
import adivinaraza.app.generated.resources.info_dogs_format
import adivinaraza.app.generated.resources.info_empty_message
import adivinaraza.app.generated.resources.info_empty_search
import adivinaraza.app.generated.resources.info_error_loading
import adivinaraza.app.generated.resources.info_exercise_needs_format
import adivinaraza.app.generated.resources.info_fci_group_format
import adivinaraza.app.generated.resources.info_fci_section_format
import adivinaraza.app.generated.resources.info_fun_fact_format
import adivinaraza.app.generated.resources.info_grooming_needs_format
import adivinaraza.app.generated.resources.info_header_subtitle
import adivinaraza.app.generated.resources.info_header_title
import adivinaraza.app.generated.resources.info_height_format
import adivinaraza.app.generated.resources.info_hygiene_format
import adivinaraza.app.generated.resources.info_life_span_format
import adivinaraza.app.generated.resources.info_loss_hair_format
import adivinaraza.app.generated.resources.info_not_available
import adivinaraza.app.generated.resources.info_nutrition_format
import adivinaraza.app.generated.resources.info_other_names_format
import adivinaraza.app.generated.resources.info_search_clear
import adivinaraza.app.generated.resources.info_search_label
import adivinaraza.app.generated.resources.info_search_placeholder
import adivinaraza.app.generated.resources.info_section_care
import adivinaraza.app.generated.resources.info_section_description
import adivinaraza.app.generated.resources.info_section_extra
import adivinaraza.app.generated.resources.info_section_fci
import adivinaraza.app.generated.resources.info_section_metrics
import adivinaraza.app.generated.resources.info_size_format
import adivinaraza.app.generated.resources.info_trainability_format
import adivinaraza.app.generated.resources.info_unknown
import adivinaraza.app.generated.resources.info_weight_format
import adivinaraza.app.generated.resources.info_years_short

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alvaroquintana.adivinaperro.ui.components.EmptyState
import com.alvaroquintana.adivinaperro.ui.components.ErrorState
import com.alvaroquintana.adivinaperro.ui.components.LoadingState
import com.alvaroquintana.adivinaperro.ui.composables.BreedImage
import com.alvaroquintana.adivinaperro.ui.theme.dynaPuffFamily
import com.alvaroquintana.adivinaperro.ui.theme.LocalWindowSizeClass
import com.alvaroquintana.adivinaperro.ui.theme.isCompact
import com.alvaroquintana.adivinaperro.ui.theme.isExpanded
import com.alvaroquintana.adivinaperro.ui.theme.getBackgroundGradient
import com.alvaroquintana.domain.Dog
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import java.util.Locale

@Composable
fun InfoScreen(
    viewModel: InfoViewModel,
    currentPage: Int,
    onLoadMore: (Int) -> Unit
) {
    val progress by viewModel.progress.collectAsStateWithLifecycle()
    val dogList by viewModel.currentDogList.collectAsStateWithLifecycle()
    val hasMore by viewModel.hasMore.collectAsStateWithLifecycle()
    val selectedDog by viewModel.selectedDog.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    val isLoading = progress is InfoViewModel.UiModel.Loading &&
        (progress as InfoViewModel.UiModel.Loading).show

    var searchQuery by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val gridState = rememberLazyGridState()

    val filteredList = remember(dogList, searchQuery) {
        val query = searchQuery.trim().lowercase()
        if (query.isBlank()) {
            dogList
        } else {
            dogList.filter { dog ->
                dog.name.lowercase().contains(query) ||
                    dog.breedGroup.lowercase().contains(query) ||
                    dog.origin.lowercase().contains(query) ||
                    dog.temperament.lowercase().contains(query)
            }
        }
    }

    LaunchedEffect(gridState, currentPage, hasMore, isLoading, selectedDog) {
        snapshotFlow {
            val layoutInfo = gridState.layoutInfo
            val totalItemsCount = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleItemIndex to totalItemsCount
        }
            .distinctUntilChanged()
            .filter { (lastVisible, total) -> total > 0 && lastVisible >= total - 1 }
            .collect {
                if (selectedDog == null && hasMore && !isLoading) {
                    onLoadMore(currentPage + 1)
                }
            }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(getBackgroundGradient())
    ) {
        when {
            selectedDog != null -> {
                DogDetailContent(dog = selectedDog!!)
            }
            isLoading && dogList.isEmpty() -> LoadingState()
            errorMessage != null && dogList.isEmpty() -> {
                ErrorState(
                    message = stringResource(Res.string.info_error_loading),
                    onRetry = { viewModel.retryInitialLoad() }
                )
            }
            dogList.isEmpty() -> {
                EmptyState(message = stringResource(Res.string.info_empty_message))
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 150.dp),
                    state = gridState,
                    contentPadding = PaddingValues(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        InfoHeaderCard()
                    }

                    item(span = { GridItemSpan(maxLineSpan) }) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            singleLine = true,
                            label = { Text(text = stringResource(Res.string.info_search_label)) },
                            placeholder = { Text(text = stringResource(Res.string.info_search_placeholder)) },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = stringResource(Res.string.info_search_clear)
                                        )
                                    }
                                }
                            },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (filteredList.isEmpty()) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            EmptyState(
                                message = if (searchQuery.isBlank()) {
                                    stringResource(Res.string.info_empty_message)
                                } else {
                                    stringResource(Res.string.info_empty_search, searchQuery)
                                }
                            )
                        }
                    } else {
                        itemsIndexed(filteredList) { _, dog ->
                            DogInfoItem(
                                dog = dog,
                                onClick = { viewModel.selectDog(dog) }
                            )
                        }
                    }
                }
            }
        }

        if (isLoading && dogList.isNotEmpty() && selectedDog == null) {
            LoadingState()
        }
    }
}

@Composable
private fun InfoHeaderCard() {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = stringResource(Res.string.info_header_title),
                fontFamily = dynaPuffFamily(),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(Res.string.info_header_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DogInfoItem(dog: Dog, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                BreedImage(
                    imageData = dog.icon,
                    contentDescription = dog.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.95f),
                    contentScale = ContentScale.Fit
                )
            }

            Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp)) {
                Text(
                    text = dog.name,
                    fontFamily = dynaPuffFamily(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun InfoTagRow(tags: List<String>) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        tags.take(2).forEach { tag ->
            InfoTag(text = tag, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun InfoTag(text: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
    ) {
        Text(
            text = text,
            fontFamily = dynaPuffFamily(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun DogDetailContent(dog: Dog) {
    val windowSizeClass = LocalWindowSizeClass.current
    val isWide = !windowSizeClass.isCompact

    LazyColumn(
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Header: image + name/tags — side by side on tablet
        item {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 1.dp
            ) {
                if (isWide) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .weight(0.4f)
                                .aspectRatio(1f)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            BreedImage(
                                imageData = dog.icon,
                                contentDescription = dog.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        }
                        Column(
                            modifier = Modifier
                                .weight(0.6f)
                                .padding(14.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = dog.name,
                                fontFamily = dynaPuffFamily(),
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            val tags = listOfNotNull(
                                dog.origin.takeIf { it.isNotBlank() },
                                dog.breedGroup.takeIf { it.isNotBlank() }
                            )
                            if (tags.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                InfoTagRow(tags = tags)
                            }
                            if (dog.temperament.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = dog.temperament,
                                    fontFamily = dynaPuffFamily(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            BreedImage(
                                imageData = dog.icon,
                                contentDescription = dog.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        }
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = dog.name,
                                fontFamily = dynaPuffFamily(),
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            val tags = listOfNotNull(
                                dog.origin.takeIf { it.isNotBlank() },
                                dog.breedGroup.takeIf { it.isNotBlank() }
                            )
                            if (tags.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                InfoTagRow(tags = tags)
                            }
                            if (dog.temperament.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = dog.temperament,
                                    fontFamily = dynaPuffFamily(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Description
        item {
            DetailSection(
                title = stringResource(Res.string.info_section_description),
                content = dog.description.ifBlank { stringResource(Res.string.info_not_available) }
            )
        }

        item { MetricsSection(dog = dog) }
        item { CareSection(dog = dog) }

        item {
            val aliases = dog.otherNames.toSeparatedList()
            val diseases = dog.commonDiseases.toSeparatedList()
            val colors = dog.colors.toSeparatedList()
            val bullets = mutableListOf<String>()
            if (aliases.isNotEmpty()) bullets += stringResource(Res.string.info_other_names_format, aliases.joinToString())
            if (colors.isNotEmpty()) bullets += stringResource(Res.string.info_colors_format, colors.joinToString())
            if (diseases.isNotEmpty()) bullets += stringResource(Res.string.info_common_diseases_format, diseases.joinToString())
            if (dog.funFact.isNotBlank()) bullets += stringResource(Res.string.info_fun_fact_format, dog.funFact)

            DetailListSection(
                title = stringResource(Res.string.info_section_extra),
                items = bullets.ifEmpty { listOf(stringResource(Res.string.info_not_available)) }
            )
        }

        item {
            DetailSection(
                title = stringResource(Res.string.info_section_fci),
                content = formatFciText(dog)
            )
        }
    }
}

@Composable
private fun MetricsSection(dog: Dog) {
    val items = listOf(
        stringResource(Res.string.info_weight_format, formatRange(dog.minWeightKg, dog.maxWeightKg, "kg")),
        stringResource(Res.string.info_height_format, formatRange(dog.minHeightCm, dog.maxHeightCm, "cm")),
        stringResource(Res.string.info_life_span_format, formatIntRange(dog.lifeSpanMin, dog.lifeSpanMax, stringResource(Res.string.info_years_short))),
        stringResource(Res.string.info_size_format, dog.sizeCategory.ifBlank { stringResource(Res.string.info_unknown) }),
        stringResource(Res.string.info_coat_type_format, dog.coatType.ifBlank { stringResource(Res.string.info_unknown) })
    )
    DetailListSection(
        title = stringResource(Res.string.info_section_metrics),
        items = items
    )
}

@Composable
private fun CareSection(dog: Dog) {
    val items = mutableListOf<String>()
    items += stringResource(Res.string.info_exercise_needs_format, formatRating(dog.exerciseNeeds))
    items += stringResource(Res.string.info_grooming_needs_format, formatRating(dog.groomingNeeds))
    items += stringResource(Res.string.info_trainability_format, formatRating(dog.trainability))
    items += stringResource(Res.string.info_children_format, formatRating(dog.goodWithChildren))
    items += stringResource(Res.string.info_dogs_format, formatRating(dog.goodWithOtherDogs))
    items += stringResource(Res.string.info_barking_level_format, formatRating(dog.barkingLevel))

    if (dog.nutrition.isNotBlank()) items += stringResource(Res.string.info_nutrition_format, dog.nutrition)
    if (dog.hygiene.isNotBlank()) items += stringResource(Res.string.info_hygiene_format, dog.hygiene)
    if (dog.lossHair.isNotBlank()) items += stringResource(Res.string.info_loss_hair_format, dog.lossHair)

    DetailListSection(
        title = stringResource(Res.string.info_section_care),
        items = items
    )
}

@Composable
private fun DetailSection(
    title: String,
    content: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DetailListSection(title: String, items: List<String>) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            items.forEach { value ->
                Row(verticalAlignment = Alignment.Top) {
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 1.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun formatFciText(dog: Dog): String {
    if (dog.fciGroup <= 0) {
        return stringResource(Res.string.info_not_available)
    }

    return buildString {
        append(stringResource(Res.string.info_fci_group_format, dog.fciGroup))
        if (dog.fciSection > 0) {
            append(" - ")
            append(stringResource(Res.string.info_fci_section_format, dog.fciSection))
        }
        if (dog.fciSectionType.isNotBlank()) {
            append("\n")
            append(dog.fciSectionType)
        }
    }
}

private fun String.toSeparatedList(): List<String> {
    return split(',')
        .map { it.trim() }
        .filter { it.isNotBlank() }
}

private fun formatRange(min: Double, max: Double, unit: String): String {
    return when {
        min > 0.0 && max > 0.0 -> String.format(Locale.getDefault(), "%.1f - %.1f %s", min, max, unit)
        max > 0.0 -> String.format(Locale.getDefault(), "%.1f %s", max, unit)
        else -> "-"
    }
}

private fun formatIntRange(min: Int, max: Int, unit: String): String {
    return when {
        min > 0 && max > 0 -> "$min-$max $unit"
        max > 0 -> "$max $unit"
        else -> "-"
    }
}

private fun formatRating(value: Int): String {
    if (value <= 0) return "-"
    return "$value/5"
}
