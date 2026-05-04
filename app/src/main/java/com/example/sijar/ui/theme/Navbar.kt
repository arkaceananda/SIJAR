package com.example.sijar.ui.theme

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sijar.AppDestinations

@Composable
fun FloatingNavBar(
    destinations: List<AppDestinations>,
    currentDestination: AppDestinations,
    onDestinationSelected: (AppDestinations) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp, vertical = 20.dp)
            .navigationBarsPadding()
    ) {
        Surface(
            modifier = Modifier
                .wrapContentWidth()
                .align(Alignment.Center),
            shape = RoundedCornerShape(50.dp),
            color = BlueDarker.copy(alpha = 0.93f),
            border = BorderStroke(
                width = 0.5.dp,
                color = BlueLight.copy(alpha = 0.25f)
            ),
            shadowElevation = 16.dp,
            tonalElevation = 0.dp
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                destinations.forEach { destination ->
                    NavBarItem(
                        destination = destination,
                        isSelected = destination == currentDestination,
                        onClick = { onDestinationSelected(destination) }
                    )
                }
            }
        }
    }
}

@Composable
fun NavBarItem(
    destination: AppDestinations,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val horizontalPadding by animateDpAsState(
        targetValue = if (isSelected) 18.dp else 12.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "h_padding"
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(
                color = if (isSelected) BluePrimary else BlueDarker.copy(alpha = 0f)
            )
            .clickable { onClick() }
            .padding(horizontal = horizontalPadding, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        val label = stringResource(id = destination.labelResId)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = if (isSelected) destination.iconSelected else destination.iconUnselected,
                contentDescription = label,
                tint = if (isSelected) White else White.copy(alpha = 0.55f),
                modifier = Modifier.size(20.dp)
            )

            AnimatedVisibility(
                visible = isSelected,
                enter = fadeIn(animationSpec = tween(200)),
                exit = fadeOut(animationSpec = tween(150))
            ) {
                Text(
                    text = label,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = White,
                    maxLines = 1
                )
            }
        }
    }
}