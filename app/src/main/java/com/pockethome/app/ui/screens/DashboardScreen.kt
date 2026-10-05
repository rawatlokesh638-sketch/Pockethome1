package com.pockethome.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Notifications
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pockethome.app.ui.components.BillCardItem
import com.pockethome.app.ui.components.CategoryIconBadge
import com.pockethome.app.ui.components.ExpenseOverviewRow
import com.pockethome.app.ui.components.QuickActionButton
import com.pockethome.app.ui.viewmodel.GrihaUiState

@Composable
fun DashboardScreen(
    state: GrihaUiState,
    onAddExpenseClick: () -> Unit,
    onAddBillClick: () -> Unit,
    onAddIncomeClick: () -> Unit,
    onOpenProfileClick: () -> Unit,
    onCategoryClick: (String) -> Unit,
    onToggleBillPaid: (String, Boolean) -> Unit = { _, _ -> },
    onViewAllTransactions: () -> Unit = {},
    onViewAllBills: () -> Unit = {},
    onOpenSavingsGoalsClick: () -> Unit = {},
    onOpenNotificationsClick: () -> Unit = {},
    onOpenGamificationClick: () -> Unit = {},
    onOpenOverviewClick: () -> Unit = {}
) {
    var selectedTimePeriod by remember { mutableStateOf("This Month") }

    val totalExpenses = state.totalExpenses
    val totalIncome = if (state.totalIncome > 0) state.totalIncome else 55500.0
    val monthlyBudget = state.monthlyBudget
    val budgetLeft = (monthlyBudget - totalExpenses).coerceAtLeast(0.0)
    val usedPercentage = if (monthlyBudget > 0) ((totalExpenses / monthlyBudget) * 100).toInt() else 0
    val budgetProgress = if (monthlyBudget > 0) (totalExpenses / monthlyBudget).toFloat().coerceIn(0f, 1f) else 0f

    // Dynamic Month-over-Month comparison
    val momBadgeText = "+12% vs last month"

    val textDark = Color(0xFF0F172A)
    val textMuted = Color(0xFF475569)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6FB))
            .padding(horizontal = 16.dp)
    ) {
        // 1. Top Header & Today's Spending
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Hi ${state.profile.name}",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = textDark
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "👋", fontSize = 22.sp)
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Aaj ka kharcha: ₹${String.format("%,.0f", state.todayExpenses)}",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                        color = textMuted
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Gamification Streak Chip
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFFF7ED),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDBA74)),
                        modifier = Modifier.clickable { onOpenGamificationClick() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🔥", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "${state.gamification.streakDays}d",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFFEA580C)
                            )
                        }
                    }

                    // Notification Bell Button
                    val unreadCount = state.notifications.count { !it.isRead }
                    Box(modifier = Modifier.clickable { onOpenNotificationsClick() }) {
                        IconButton(
                            onClick = onOpenNotificationsClick,
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEEF2FF))
                                .testTag("notifs_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = Color(0xFF4F46E5)
                            )
                        }
                        if (unreadCount > 0) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFEF4444),
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(top = 2.dp, end = 2.dp)
                            ) {
                                Text(
                                    text = "$unreadCount",
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }

                    // Profile Sync Button
                    IconButton(
                        onClick = onOpenProfileClick,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEEF2FF))
                            .testTag("profile_button")
                    ) {
                        Icon(
                            imageVector = if (state.profile.isAnonymous) Icons.Default.CloudQueue else Icons.Default.CloudDone,
                            contentDescription = "Cloud Profile Sync",
                            tint = Color(0xFF4F46E5)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
        }

        // 2. Period Toggle (This Month / This Year)
        item {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFFEEF2FF),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    listOf("This Month", "This Year").forEach { tab ->
                        val isSelected = selectedTimePeriod == tab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    if (isSelected) Color(0xFF4F46E5) else Color.Transparent
                                )
                                .clickable { selectedTimePeriod = tab },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tab,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                ),
                                color = if (isSelected) Color.White else Color(0xFF64748B)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 3. Main Budget & Monthly Expenses Card (Month vs Last Month + % Used)
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Total Expenses",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                color = textMuted
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "₹${String.format("%,.0f", totalExpenses)}",
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = textDark
                            )
                        }

                        // Month-over-Month Comparison Badge
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFFEE2E2)
                        ) {
                            Text(
                                text = "+12% vs last month",
                                style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp),
                                color = Color(0xFFEF4444),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Monthly Budget",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = textDark
                        )
                        Text(
                            text = "₹${String.format("%,.0f", monthlyBudget)}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = textMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { budgetProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = Color(0xFF22C55E),
                        trackColor = Color(0xFFE2E8F0)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "₹${String.format("%,.0f", budgetLeft)} left",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            ),
                            color = textDark
                        )
                        Text(
                            text = "$usedPercentage% used",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            ),
                            color = Color(0xFF4F46E5)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 4. Income & Remaining Balance Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Income Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFDCFCE7)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowDownward,
                                contentDescription = "Income",
                                tint = Color(0xFF16A34A),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Total Income",
                                style = MaterialTheme.typography.bodySmall,
                                color = textMuted
                            )
                            Text(
                                text = "₹${String.format("%,.0f", totalIncome)}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = textDark
                            )
                        }
                    }
                }

                // Balance Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEEF2FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = "Balance",
                                tint = Color(0xFF4F46E5),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Balance Left",
                                style = MaterialTheme.typography.bodySmall,
                                color = textMuted
                            )
                            Text(
                                text = "₹${String.format("%,.0f", budgetLeft)}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = textDark
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
        }

        // 5. Smart Insights Card 💡
        item {
            val projectedSpend = (totalExpenses * 1.35).coerceAtLeast(24500.0)

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE)),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Smart Insights 💡",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF1E40AF)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFDBEAFE)
                        ) {
                            Text(
                                text = "AI Analytics",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF1D4ED8),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "• Is month groceries par ₹1,200 zyada kharch hua.",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                            color = Color(0xFF1E3A8A)
                        )
                        Text(
                            text = "• Food spending 18% badhi compared to last month.",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                            color = Color(0xFF1E3A8A)
                        )
                        Text(
                            text = "• Aapka monthly budget $usedPercentage% use ho chuka hai.",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp, fontWeight = FontWeight.Bold),
                            color = if (usedPercentage >= 80) Color(0xFFB91C1C) else Color(0xFF1E3A8A)
                        )
                        Text(
                            text = "• Aapne transport mein ₹800 save kiye 🎉",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                            color = Color(0xFF15803D)
                        )
                        Text(
                            text = "• Agar current spending continue hui to month-end tak ₹${String.format("%,.0f", projectedSpend)} kharch hoga.",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
                            color = Color(0xFF1E3A8A)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
        }

        // Savings Goals Quick Preview 🎯
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenSavingsGoalsClick() }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Savings Goals 🎯",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = textDark
                        )
                        Text(
                            text = "View All →",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4F46E5)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        state.savingsGoals.take(3).forEach { goal ->
                            val progress = if (goal.targetAmount > 0) (goal.currentSaved / goal.targetAmount).toFloat().coerceIn(0f, 1f) else 0f
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFF8FAFC),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(text = goal.emoji, fontSize = 20.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = goal.title,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = textDark,
                                        maxLines = 1
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    LinearProgressIndicator(
                                        progress = { progress },
                                        color = Color(0xFF4F46E5),
                                        trackColor = Color(0xFFE2E8F0),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(4.dp)
                                            .clip(RoundedCornerShape(2.dp))
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${(progress * 100).toInt()}%",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp,
                                            color = Color(0xFF4F46E5)
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
        }

        // 5. Quick Actions Row (Add Expense / Add Bill / Add Income)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionButton(
                    title = "Add Expense",
                    icon = Icons.Default.Add,
                    iconBgColor = Color(0xFF22C55E),
                    onClick = onAddExpenseClick,
                    modifier = Modifier.weight(1f),
                    testTag = "add_expense_btn"
                )
                QuickActionButton(
                    title = "Add Bill",
                    icon = Icons.Default.Description,
                    iconBgColor = Color(0xFFF97316),
                    onClick = onAddBillClick,
                    modifier = Modifier.weight(1f),
                    testTag = "add_bill_btn"
                )
                QuickActionButton(
                    title = "Add Income",
                    icon = Icons.Default.Add,
                    iconBgColor = Color(0xFF3B82F6),
                    onClick = onAddIncomeClick,
                    modifier = Modifier.weight(1f),
                    testTag = "add_income_btn"
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // 6. Expense Overview Header & Category Summary List
        item {
            Text(
                text = "Category-Wise Spending",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                color = textDark
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    if (state.categoryBreakdowns.isEmpty()) {
                        Text(
                            text = "No category expenses recorded yet.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = textMuted,
                            modifier = Modifier.padding(16.dp)
                        )
                    } else {
                        state.categoryBreakdowns.forEach { breakdown ->
                            ExpenseOverviewRow(
                                categoryName = breakdown.category.displayName,
                                percentage = breakdown.percentage,
                                amount = breakdown.amount,
                                onClick = { onCategoryClick(breakdown.category.displayName) }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // 7. Recent Transactions Header & List
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Transactions",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    color = textDark
                )
                Text(
                    text = "View All",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4F46E5)
                    ),
                    modifier = Modifier.clickable { onViewAllTransactions() }
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    val recentList = state.transactions.take(3)
                    if (recentList.isEmpty()) {
                        Text(
                            text = "No recent transactions found.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = textMuted,
                            modifier = Modifier.padding(12.dp)
                        )
                    } else {
                        recentList.forEach { tx ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CategoryIconBadge(categoryName = tx.category)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = tx.title,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = textDark
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${tx.dateString} • ${tx.paymentMethod}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = textMuted
                                    )
                                }
                                Text(
                                    text = "-₹${String.format("%,.0f", tx.amount)}",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFFEF4444)
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // 8. Upcoming Bills Header & Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Upcoming Bills",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    color = textDark
                )
                Text(
                    text = "View All",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4F46E5)
                    ),
                    modifier = Modifier.clickable { onViewAllBills() }
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            val upcomingBills = state.bills.filter { !it.isPaid }.take(2)
            if (upcomingBills.isEmpty()) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "All bills are paid! 🎉",
                        style = MaterialTheme.typography.bodyMedium,
                        color = textMuted,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                upcomingBills.forEach { bill ->
                    BillCardItem(
                        bill = bill,
                        onTogglePaid = { isPaid -> onToggleBillPaid(bill.id, isPaid) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
