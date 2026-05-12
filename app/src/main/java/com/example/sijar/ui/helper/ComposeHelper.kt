package com.example.sijar.ui.helper

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sijar.ui.theme.BlueLighter
import com.example.sijar.ui.theme.TextMuted
import com.example.sijar.ui.theme.White
import com.example.sijar.ui.view.AnimatedDot
import kotlinx.coroutines.delay


/* HELPER COMPOSE */
@Composable
fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = TextMuted,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(start = 20.dp, bottom = 8.dp)
    )
}

@Composable
fun ModernCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            content()
        }
    }
}

@Composable
fun RowDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 56.dp, end = 16.dp),
        color = BlueLighter,
        thickness = 0.5.dp
    )
}

@Composable
fun LoadingDots() {
    val delays = listOf(0, 180, 360)

    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        delays.forEach { delayMs ->
            AnimatedDot(delayMs = delayMs)
        }
    }
}

@Composable
fun AnimatedCounter(
    count: Int,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.headlineMedium
) {
    var displayCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(count) {
        val duration = 600L
        val steps = count.coerceAtLeast(1)
        val delayPerStep = duration / steps

        (0..count).forEach { i ->
            displayCount = i
            delay(delayPerStep.coerceAtLeast(16L))
        }
    }

    Text(
        text = displayCount.toString(),
        modifier = modifier,
        style = style
    )
}

@Composable
fun ShakeEffect(
    trigger: Boolean,
    content: @Composable () -> Unit
) {
    val offSetX = remember { Animatable(0f) }

    LaunchedEffect(trigger) {
        if (trigger) {
            repeat(3) {
                offSetX.animateTo(
                    targetValue = 10f,
                    animationSpec = tween(50)
                )

                offSetX.animateTo(
                    targetValue = 10f,
                    animationSpec = tween(50)
                )
            }
            offSetX.animateTo(0f, tween(50))
        }
    }
    Box(
        modifier = Modifier.offset(x = offSetX.value.dp)
    ) {
        content()
    }
}

@Composable
fun PulsingBadge(
    text: String,
    color: Color
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "badge_alpha"
    )

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = alpha * 0.15f)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = color.copy(alpha = alpha)
        )
    }
}