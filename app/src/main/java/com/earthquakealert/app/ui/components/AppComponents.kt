package com.earthquakealert.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.earthquakealert.app.domain.model.AlertSeverity
import com.earthquakealert.app.ui.theme.AppDimensions
import com.earthquakealert.app.ui.theme.AppRadius
import com.earthquakealert.app.ui.theme.AppSpacing
import com.earthquakealert.app.ui.theme.AppTypography
import com.earthquakealert.app.ui.theme.EarthquakeTheme

enum class ProtectionStatus {
    ACTIVE,
    UPDATING,
    LIMITED,
    DISABLED
}

@Composable
fun ProtectionStatusIndicator(
    status: ProtectionStatus,
    modifier: Modifier = Modifier
) {
    val (statusColor, label) = when (status) {
        ProtectionStatus.ACTIVE -> EarthquakeTheme.colors.success to "Protection active"
        ProtectionStatus.UPDATING -> EarthquakeTheme.colors.info to "Updating status"
        ProtectionStatus.LIMITED -> EarthquakeTheme.colors.warning to "Protection limited"
        ProtectionStatus.DISABLED -> EarthquakeTheme.colors.emergency to "Protection disabled"
    }

    Box(
        modifier = modifier
            .semantics(mergeDescendants = true) {
                contentDescription = "Status: $label"
            }
            .border(
                BorderStroke(1.dp, statusColor.copy(alpha = 0.45f)),
                RoundedCornerShape(AppRadius.smallControl)
            )
            .background(
                statusColor.copy(alpha = 0.1f),
                RoundedCornerShape(AppRadius.smallControl)
            )
            .padding(horizontal = AppSpacing.sm, vertical = AppSpacing.micro)
    ) {
        Text(
            text = label,
            style = AppTypography.labelSmall,
            color = statusColor,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun <T> SegmentedPicker(
    options: List<Pair<T, String>>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(
                color = EarthquakeTheme.colors.background,
                shape = RoundedCornerShape(AppRadius.smallControl)
            )
            .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        options.forEach { (option, label) ->
            val isSelected = option == selectedOption
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(AppRadius.smallControl - 2.dp))
                    .background(
                        if (isSelected) EarthquakeTheme.colors.surface else Color.Transparent
                    )
                    .then(
                        if (isSelected) {
                            Modifier.border(
                                width = 1.dp,
                                color = EarthquakeTheme.colors.border,
                                shape = RoundedCornerShape(AppRadius.smallControl - 2.dp)
                            )
                        } else Modifier
                    )
                    .clickable { onOptionSelected(option) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    style = AppTypography.labelMedium,
                    color = if (isSelected) EarthquakeTheme.colors.textPrimary else EarthquakeTheme.colors.textSecondary,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun SeverityBadge(
    severity: AlertSeverity,
    modifier: Modifier = Modifier
) {
    if (severity == AlertSeverity.NONE) return

    val (textColor, label) = when (severity) {
        AlertSeverity.HIGH -> EarthquakeTheme.colors.emergency to "HIGH ALERT"
        AlertSeverity.WARNING -> EarthquakeTheme.colors.warning to "WARNING"
        AlertSeverity.INFO -> EarthquakeTheme.colors.info to "INFO"
        AlertSeverity.NONE -> EarthquakeTheme.colors.textSecondary to ""
    }

    Box(
        modifier = modifier
            .border(
                BorderStroke(AppDimensions.borderWidth, textColor),
                RoundedCornerShape(AppRadius.smallControl)
            )
            .padding(horizontal = AppSpacing.xs, vertical = AppSpacing.micro)
    ) {
        Text(
            text = label,
            style = AppTypography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isEmergency: Boolean = false
) {
    val containerColor = if (isEmergency) {
        EarthquakeTheme.colors.emergency
    } else {
        EarthquakeTheme.colors.textPrimary
    }
    val contentColor = EarthquakeTheme.colors.surface

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = AppDimensions.buttonHeight),
        enabled = enabled,
        shape = RoundedCornerShape(AppRadius.button),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = EarthquakeTheme.colors.muted.copy(alpha = 0.3f),
            disabledContentColor = EarthquakeTheme.colors.muted
        ),
        contentPadding = PaddingValues(horizontal = 20.dp)
    ) {
        Text(
            text = text,
            style = AppTypography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = AppDimensions.buttonHeight),
        enabled = enabled,
        shape = RoundedCornerShape(AppRadius.button),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = EarthquakeTheme.colors.textPrimary
        ),
        border = BorderStroke(AppDimensions.borderWidth, EarthquakeTheme.colors.border),
        contentPadding = PaddingValues(horizontal = 20.dp)
    ) {
        Text(
            text = text,
            style = AppTypography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun TextActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: Color = EarthquakeTheme.colors.textPrimary
) {
    TextButton(
        onClick = onClick,
        modifier = modifier.heightIn(min = AppDimensions.minTouchTarget),
        enabled = enabled
    ) {
        Text(
            text = text,
            style = AppTypography.labelLarge,
            color = color,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun AppDivider(
    modifier: Modifier = Modifier
) {
    HorizontalDivider(
        modifier = modifier,
        thickness = AppDimensions.borderWidth,
        color = EarthquakeTheme.colors.border
    )
}

@Composable
fun InfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = EarthquakeTheme.colors.textPrimary
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = AppSpacing.xs),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = AppTypography.bodyMedium,
            color = EarthquakeTheme.colors.textSecondary
        )
        Text(
            text = value,
            style = AppTypography.bodyMedium,
            color = valueColor,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun EmptyState(
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = AppSpacing.xl, horizontal = AppSpacing.md),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = AppTypography.headlineSmall,
            color = EarthquakeTheme.colors.textPrimary,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(AppSpacing.xs))
        Text(
            text = description,
            style = AppTypography.bodyMedium,
            color = EarthquakeTheme.colors.textSecondary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(
                BorderStroke(AppDimensions.borderWidth, EarthquakeTheme.colors.border),
                RoundedCornerShape(AppRadius.card)
            )
            .background(EarthquakeTheme.colors.surface, RoundedCornerShape(AppRadius.card))
            .padding(AppSpacing.md),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            style = AppTypography.bodyMedium,
            color = EarthquakeTheme.colors.textPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(AppSpacing.md))
        SecondaryButton(
            text = "Try Again",
            onClick = onRetry
        )
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = AppTypography.labelLarge,
        color = EarthquakeTheme.colors.textSecondary,
        fontWeight = FontWeight.SemiBold
    )
    Spacer(modifier = Modifier.height(AppSpacing.xs))
    AppDivider()
    Spacer(modifier = Modifier.height(AppSpacing.xs))
}

@Composable
fun SettingsToggleRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    enabled: Boolean = true
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = AppDimensions.minTouchTarget)
            .padding(vertical = AppSpacing.xs),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = AppSpacing.sm)) {
            Text(
                text = title,
                style = AppTypography.bodyLarge,
                color = if (enabled) EarthquakeTheme.colors.textPrimary else EarthquakeTheme.colors.muted
            )
            if (!subtitle.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = AppTypography.bodySmall,
                    color = EarthquakeTheme.colors.textSecondary
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = EarthquakeTheme.colors.surface,
                checkedTrackColor = EarthquakeTheme.colors.textPrimary,
                uncheckedThumbColor = EarthquakeTheme.colors.surface,
                uncheckedTrackColor = EarthquakeTheme.colors.border
            )
        )
    }
}


@Composable
fun SettingsActionRow(
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = AppDimensions.minTouchTarget)
            .clickable(onClick = onClick)
            .padding(vertical = AppSpacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = AppTypography.bodyLarge,
            color = EarthquakeTheme.colors.textPrimary
        )
        Text(
            text = "→",
            style = AppTypography.bodyLarge,
            color = EarthquakeTheme.colors.textSecondary
        )
    }
}

@Composable
fun SettingsInfoRow(
    title: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = AppDimensions.minTouchTarget)
            .padding(vertical = AppSpacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = AppTypography.bodyLarge,
            color = EarthquakeTheme.colors.textPrimary
        )
        Text(
            text = value,
            style = AppTypography.bodyMedium,
            color = EarthquakeTheme.colors.textSecondary
        )
    }
}
