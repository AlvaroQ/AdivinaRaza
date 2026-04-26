package com.alvaroquintana.adivinaperro.ui.settings

import adivinaraza.app.generated.resources.Res
import adivinaraza.app.generated.resources.ic_privacy
import adivinaraza.app.generated.resources.ic_share
import adivinaraza.app.generated.resources.ic_star
import adivinaraza.app.generated.resources.ic_version
import adivinaraza.app.generated.resources.ic_volume
import adivinaraza.app.generated.resources.settings_privacy_options
import adivinaraza.app.generated.resources.settings_privacy_options_summary
import adivinaraza.app.generated.resources.settings_privacy_policy
import adivinaraza.app.generated.resources.settings_privacy_policy_summary
import adivinaraza.app.generated.resources.settings_rate_app
import adivinaraza.app.generated.resources.settings_rate_app_summary
import adivinaraza.app.generated.resources.settings_section_about
import adivinaraza.app.generated.resources.settings_section_general
import adivinaraza.app.generated.resources.settings_share
import adivinaraza.app.generated.resources.settings_share_summary
import adivinaraza.app.generated.resources.settings_sounds
import adivinaraza.app.generated.resources.settings_theme
import adivinaraza.app.generated.resources.settings_theme_dark
import adivinaraza.app.generated.resources.settings_theme_follow_system
import adivinaraza.app.generated.resources.settings_theme_light
import adivinaraza.app.generated.resources.settings_theme_system
import adivinaraza.app.generated.resources.settings_version
import adivinaraza.app.generated.resources.sounds_off
import adivinaraza.app.generated.resources.sounds_on

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.alvaroquintana.adivinaperro.ui.theme.ThemeMode
import com.alvaroquintana.adivinaperro.ui.theme.getBackgroundGradient

@Composable
fun SettingsScreen(
    isSoundEnabled: Boolean,
    themeMode: ThemeMode,
    versionText: String,
    showPrivacyOptions: Boolean = false,
    onSoundToggle: (Boolean) -> Unit,
    onThemeModeChanged: (ThemeMode) -> Unit,
    onRateApp: () -> Unit,
    onShare: () -> Unit,
    onPrivacyOptions: () -> Unit = {},
    onPrivacyPolicy: () -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(getBackgroundGradient())
            .padding(16.dp)
    ) {
        // General section
        item {
            Text(
                text = stringResource(Res.string.settings_section_general),
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column {
                    SoundToggleItem(
                        isEnabled = isSoundEnabled,
                        onToggle = onSoundToggle
                    )

                    ThemeSelectorItem(
                        currentMode = themeMode,
                        onModeSelected = onThemeModeChanged
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }

        // About section
        item {
            Text(
                text = stringResource(Res.string.settings_section_about),
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column {
                    SettingsClickableItem(
                        iconRes = Res.drawable.ic_star,
                        title = stringResource(Res.string.settings_rate_app),
                        summary = stringResource(Res.string.settings_rate_app_summary),
                        onClick = onRateApp
                    )


                    SettingsClickableItem(
                        iconRes = Res.drawable.ic_share,
                        title = stringResource(Res.string.settings_share),
                        summary = stringResource(Res.string.settings_share_summary),
                        onClick = onShare
                    )

                    if (showPrivacyOptions) {
                        SettingsClickableItem(
                            iconRes = Res.drawable.ic_version,
                            title = stringResource(Res.string.settings_privacy_options),
                            summary = stringResource(Res.string.settings_privacy_options_summary),
                            onClick = onPrivacyOptions
                        )
                    }

                    SettingsClickableItem(
                        iconRes = Res.drawable.ic_privacy,
                        title = stringResource(Res.string.settings_privacy_policy),
                        summary = stringResource(Res.string.settings_privacy_policy_summary),
                        onClick = onPrivacyPolicy
                    )

                    SettingsInfoItem(
                        iconRes = Res.drawable.ic_version,
                        title = stringResource(Res.string.settings_version),
                        summary = versionText
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemeSelectorItem(
    currentMode: ThemeMode,
    onModeSelected: (ThemeMode) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_star),
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = stringResource(Res.string.settings_theme),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = when (currentMode) {
                        ThemeMode.SYSTEM -> stringResource(Res.string.settings_theme_follow_system)
                        ThemeMode.LIGHT -> stringResource(Res.string.settings_theme_light)
                        ThemeMode.DARK -> stringResource(Res.string.settings_theme_dark)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ThemeMode.entries.forEach { mode ->
                val isSelected = mode == currentMode
                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    },
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onModeSelected(mode) }
                ) {
                    Text(
                        text = when (mode) {
                            ThemeMode.SYSTEM -> stringResource(Res.string.settings_theme_system)
                            ThemeMode.LIGHT -> stringResource(Res.string.settings_theme_light)
                            ThemeMode.DARK -> stringResource(Res.string.settings_theme_dark)
                        },
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        ),
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SoundToggleItem(
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle(!isEnabled) }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_volume),
            contentDescription = null,
            modifier = Modifier.size(28.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = stringResource(Res.string.settings_sounds),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = if (isEnabled) stringResource(Res.string.sounds_on) else stringResource(Res.string.sounds_off),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Switch(
            checked = isEnabled,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
    }
}

@Composable
private fun SettingsClickableItem(
    iconRes: org.jetbrains.compose.resources.DrawableResource,
    title: String,
    summary: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier.size(28.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = summary,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SettingsInfoItem(
    iconRes: org.jetbrains.compose.resources.DrawableResource,
    title: String,
    summary: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier.size(28.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = summary,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
