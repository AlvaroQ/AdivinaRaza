package com.alvaroquintana.adivinaperro.ui.components

import adivinaraza.app.generated.resources.Res
import adivinaraza.app.generated.resources.points_short

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.stringResource
import com.alvaroquintana.adivinaperro.ui.theme.dynaPuffCondensedFamily
import com.alvaroquintana.adivinaperro.ui.theme.GameRed

@Composable
fun GameStatusRow(
    stageLabel: String,
    lives: Int,
    score: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stageLabel,
            fontFamily = dynaPuffCondensedFamily(),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground
        )

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            repeat(3) { index ->
                Icon(
                    imageVector = if (lives > index) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                    contentDescription = null,
                    tint = GameRed,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Text(
            text = stringResource(Res.string.points_short, score),
            fontFamily = dynaPuffCondensedFamily(),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

