package com.pockethome.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pockethome.app.data.model.ExpenseCategory
import com.pockethome.app.ui.viewmodel.CategoryBreakdown

@Composable
fun DonutChart(
    breakdowns: List<CategoryBreakdown>,
    totalAmount: Double,
    modifier: Modifier = Modifier,
    chartSize: Dp = 200.dp,
    strokeWidth: Dp = 32.dp
) {
    val defaultColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier.size(chartSize),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidthPx = strokeWidth.toPx()
            var startAngle = -90f
            val total = if (totalAmount > 0) totalAmount else 1.0

            if (breakdowns.isEmpty()) {
                // Draw empty placeholder arc
                drawArc(
                    color = Color.LightGray.copy(alpha = 0.4f),
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = strokeWidthPx)
                )
            } else {
                breakdowns.forEach { breakdown ->
                    val sweepAngle = ((breakdown.amount / total) * 360f).toFloat()
                    val hex = breakdown.category.hexColor
                    val color = try {
                        Color(android.graphics.Color.parseColor(hex))
                    } catch (e: Exception) {
                        defaultColor
                    }

                    drawArc(
                        color = color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle - 2f, // 2deg gap for sleek separation
                        useCenter = false,
                        style = Stroke(width = strokeWidthPx, cap = StrokeCap.Butt)
                    )
                    startAngle += sweepAngle
                }
            }
        }

        // Center Content
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "₹${String.format("%,.0f", totalAmount)}",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Total",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
