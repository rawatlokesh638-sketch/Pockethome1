package com.pockethome.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pockethome.app.ui.viewmodel.GrihaUiState

@Composable
fun FinancialOverviewScreen(
    state: GrihaUiState,
    onBackClick: () -> Unit,
    onOpenBudgetClick: () -> Unit = {},
    onOpenEmiClick: () -> Unit = {}
) {
    val currency = state.profile.currencyCode
    val textDark = Color(0xFF0F172A)
    val textMuted = Color(0xFF64748B)
    val overview = state.financialOverview

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(horizontal = 16.dp)
    ) {
        // Top Header
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.testTag("overview_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = textDark
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Advanced Financial Overview 📊",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 19.sp
                ),
                color = textDark
            )
            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(48.dp))
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Net Worth / Net Balance Card
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Estimated Net Balance",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF94A3B8)
                                )
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (overview.netBalance >= 0) Color(0xFF16A34A).copy(alpha = 0.2f) else Color(0xFFEF4444).copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = if (overview.netBalance >= 0) "Surplus" else "Deficit",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (overview.netBalance >= 0) Color(0xFF4ADE80) else Color(0xFFF87171)
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "$currency${String.format("%,.0f", overview.netBalance)}",
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (overview.netBalance >= 0) Color.White else Color(0xFFF87171)
                                )
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                            androidx.compose.material3.HorizontalDivider(color = Color(0xFF334155))
                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.TrendingUp, contentDescription = null, tint = Color(0xFF4ADE80), modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Total Assets", style = MaterialTheme.typography.bodySmall, color = Color(0xFF94A3B8))
                                    }
                                    Text(
                                        text = "$currency${String.format("%,.0f", overview.totalAssets)}",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.ArrowDownward, contentDescription = null, tint = Color(0xFFF87171), modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Total Liabilities", style = MaterialTheme.typography.bodySmall, color = Color(0xFF94A3B8))
                                    }
                                    Text(
                                        text = "$currency${String.format("%,.0f", overview.totalLiabilities)}",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFFF87171))
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Monthly Savings & Savings Rate Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Monthly Savings Card
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFDCFCE7)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Savings, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Monthly Savings", style = MaterialTheme.typography.bodySmall, color = textMuted)
                            Text(
                                text = "$currency${String.format("%,.0f", overview.monthlySavings)}",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                            )
                        }
                    }

                    // Savings Rate Card
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEEF2FF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.PieChart, contentDescription = null, tint = Color(0xFF4F46E5), modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Savings Rate", style = MaterialTheme.typography.bodySmall, color = textMuted)
                            Text(
                                text = "${overview.savingsRatePct}% of Income",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = Color(0xFF4F46E5))
                            )
                        }
                    }
                }
            }

            // Fixed vs Variable Spending Breakdown Card
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Cost Structure Breakdown", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = textDark)
                            Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFF1F5F9)) {
                                Text("Fixed vs Variable", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = textMuted, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Fixed Expenses
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFF3B82F6)))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Fixed Monthly Outflow 🔒", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = textDark)
                                    Text("EMIs, Rent, Electricity, Wi-Fi, Subscriptions", style = MaterialTheme.typography.bodySmall, color = textMuted, fontSize = 11.sp)
                                }
                            }
                            Text("$currency${String.format("%,.0f", overview.monthlyFixedExpenses)}", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = textDark)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Variable Expenses
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFFF59E0B)))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Variable Expenses 🛒", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = textDark)
                                    Text("Groceries, Food dining, Shopping, Fuel", style = MaterialTheme.typography.bodySmall, color = textMuted, fontSize = 11.sp)
                                }
                            }
                            Text("$currency${String.format("%,.0f", overview.variableExpenses)}", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = textDark)
                        }
                    }
                }
            }

            // Essential vs Non-Essential (50-30-20 Rule Analysis)
            item {
                val totalSpent = state.totalExpenses.coerceAtLeast(1.0)
                val essentialPct = ((overview.essentialExpenses / totalSpent) * 100).toInt()
                val nonEssentialPct = (100 - essentialPct).coerceAtLeast(0)

                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Essential vs Non-Essential (50-30-20) ⚖️", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = textDark)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Needs (Groceries, Health, Fuel) vs Wants (Dining, Shopping)", style = MaterialTheme.typography.bodySmall, color = textMuted)

                        Spacer(modifier = Modifier.height(14.dp))

                        // Split Progress Bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight((essentialPct.coerceAtLeast(1)).toFloat())
                                    .fillMaxSize()
                                    .background(Color(0xFF10B981))
                            )
                            Box(
                                modifier = Modifier
                                    .weight((nonEssentialPct.coerceAtLeast(1)).toFloat())
                                    .fillMaxSize()
                                    .background(Color(0xFFEF4444))
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF10B981)))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Needs: $currency${String.format("%,.0f", overview.essentialExpenses)} ($essentialPct%)", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Color(0xFF10B981), fontSize = 11.sp)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFEF4444)))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Wants: $currency${String.format("%,.0f", overview.nonEssentialExpenses)} ($nonEssentialPct%)", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Color(0xFFEF4444), fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFF1F5F9),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF4F46E5), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (essentialPct in 45..65) "Great balance! Your essential spending is aligned with the healthy 50% target." else "Caution: High discretionary spending. Try cutting dining out to save more.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = textDark,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}
