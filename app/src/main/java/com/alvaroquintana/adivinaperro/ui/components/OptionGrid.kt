package com.alvaroquintana.adivinaperro.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun OptionGrid(
    options: List<String>,
    modifier: Modifier = Modifier,
    optionContent: @Composable (index: Int, text: String, modifier: Modifier) -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        options.forEachIndexed { index, option ->
            optionContent(index, option, Modifier.fillMaxWidth())
        }
    }
}
