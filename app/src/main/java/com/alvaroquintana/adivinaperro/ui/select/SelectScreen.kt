package com.alvaroquintana.adivinaperro.ui.select

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alvaroquintana.adivinaperro.R
import com.alvaroquintana.adivinaperro.ui.theme.GameCream
import com.alvaroquintana.adivinaperro.ui.theme.GameDark
import com.alvaroquintana.adivinaperro.ui.theme.GameOrange
import com.alvaroquintana.adivinaperro.ui.theme.GameWhite
import com.alvaroquintana.adivinaperro.ui.theme.JetBrainsMonoFamily
import com.alvaroquintana.adivinaperro.ui.theme.GeistFamily
import com.alvaroquintana.adivinaperro.ui.theme.PillShape

@Composable
fun SelectScreen(
    onNavigateToGame: () -> Unit,
    onNavigateToBiggerSmaller: () -> Unit,
    onNavigateToDescription: () -> Unit,
    onNavigateToLearn: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GameCream)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(62.dp))

        // Header: paw icon + title
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_paw),
                contentDescription = null,
                tint = GameOrange,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = stringResource(R.string.app_name),
                fontFamily = JetBrainsMonoFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = GameDark
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Dog image
        Image(
            painter = painterResource(R.drawable.landing),
            contentDescription = "Dog",
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .clip(MaterialTheme.shapes.large),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Subtitle
        Text(
            text = "Descubre razas y pon a prueba tu memoria",
            fontFamily = GeistFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            color = GameDark,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Buttons section
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Play button - filled orange pill
            Surface(
                onClick = onNavigateToGame,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = PillShape,
                color = GameOrange
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.start_game),
                        fontFamily = GeistFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = GameWhite
                    )
                }
            }

            // Learn button - outlined orange pill
            Surface(
                onClick = onNavigateToLearn,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = PillShape,
                color = GameWhite,
                border = androidx.compose.foundation.BorderStroke(2.dp, GameOrange)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.learn),
                        fontFamily = GeistFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = GameOrange
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}
