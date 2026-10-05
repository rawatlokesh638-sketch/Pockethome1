package com.pockethome.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.pockethome.app.data.model.TransactionType
import com.pockethome.app.ui.components.CategoryIconBadge
import com.pockethome.app.ui.viewmodel.GrihaUiState

@Composable
fun CalendarScreen(
    state: GrihaUiState,
    onBackClick: () -> Unit,
    onAddExpenseClick: () -> Unit
) {
    var selectedDayNumber by remember { mutableIntStateOf(4) } // Default 4th Oct
    val selectedMonthName = "October 2026"
    val daysInMonth = 31

    val currency = state.profile.currencyCode
    val textDark = Color(0xFF0F172A)
    val textMuted = Color(0xFF64748B)

    // Calculate map of day -> total expense
    val dayExpenseMap = remember(state.transactions) {
        val map = mutableMapOf<Int, Double>()
        state.transactions.filter { it.type == TransactionType.EXPENSE.name }.forEach { tx ->
            // extract day from e.g. "4 Oct 2026"
            val parts = tx.dateString.split(" ")
            val day = parts.firstOrNull()?.toIntOrNull()
            if (day != null) {
                map[day] = (map[day] ?: 0.0) + tx.amount
            }
        }
        map
    }

    // Selected day transactions
    val selectedDateString = "$selectedDayNumber Oct 2026"
    val selectedDayTransactions = state.transactions.filter {
        it.dateString == selectedDateString || (selectedDayNumber == 4 && it.dateString.contains("4 Oct"))
    }

    val selectedDayExpenseTotal = selectedDayTransactions.filter { it.type == TransactionType.EXPENSE.name }.sumOf { it.amount }
    val selectedDayIncomeTotal = selectedDayTransactions.filter { it.type == TransactionType.INCOME.name }.sumOf { it.amount }

    // Check if bills due on selected day
    val billsDueOnSelectedDay = state.bills.filter { it.dueDateString.contains("$selectedDayNumber Oct") }

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
                modifier = Modifier.testTag("calendar_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = textDark
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Financial Calendar 📅",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = textDark
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = onAddExpenseClick,
                modifier = Modifier.testTag("calendar_add_tx_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Expense",
                    tint = Color(0xFF4F46E5)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Month Navigation Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {}, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Prev Month", tint = textDark)
                    }
                    Text(
                        text = selectedMonthName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = textDark
                    )
                    IconButton(onClick = {}, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Next Month", tint = textDark)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Days of week header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { dayLabel ->
                        Text(
                            text = dayLabel,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = textMuted,
                            modifier = Modifier.width(40.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Days Grid (31 days)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    val rows = (daysInMonth + 6) / 7
                    for (row in 0 until rows) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            for (col in 0 until 7) {
                                val dayNum = row * 7 + col + 1
                                if (dayNum <= daysInMonth) {
                                    val isSelected = selectedDayNumber == dayNum
                                    val expenseOnDay = dayExpenseMap[dayNum] ?: 0.0
                                    val isSpendingHeavy = expenseOnDay > 2000.0
                                    val hasBillDue = dayNum in listOf(10, 15, 20)
                                    val hasIncome = dayNum in listOf(1, 2, 3)

                                    val cellBg = when {
                                        isSelected -> Color(0xFF4F46E5)
                                        isSpendingHeavy -> Color(0xFFFEE2E2)
                                        expenseOnDay > 0 -> Color(0xFFEFF6FF)
                                        else -> Color.Transparent
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = cellBg,
                                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF4F46E5)) else null,
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .clickable { selectedDayNumber = dayNum }
                                    ) {
                                        Column(
                                            modifier = Modifier.fillMaxSize(),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = "$dayNum",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                                ),
                                                color = if (isSelected) Color.White else textDark
                                            )
                                            if (isSelected) {
                                                Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(Color.White))
                                            } else if (hasBillDue) {
                                                Text(text = "⚡", fontSize = 8.sp)
                                            } else if (hasIncome) {
                                                Text(text = "💰", fontSize = 8.sp)
                                            } else if (isSpendingHeavy) {
                                                Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(Color(0xFFEF4444)))
                                            }
                                        }
                                    }
                                } else {
                                    Spacer(modifier = Modifier.size(40.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Selected Date Details Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "$selectedDayNumber October 2026",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = textDark
                        )
                        Text(
                            text = "${selectedDayTransactions.size} transactions on this day",
                            style = MaterialTheme.typography.bodySmall,
                            color = textMuted
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (selectedDayExpenseTotal > 0) Color(0xFFFEE2E2) else Color(0xFFDCFCE7)
                    ) {
                        Text(
                            text = if (selectedDayExpenseTotal > 0) "-$currency${String.format("%,.0f", selectedDayExpenseTotal)}" else "No Spend 🎉",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (selectedDayExpenseTotal > 0) Color(0xFFDC2626) else Color(0xFF16A34A),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                if (billsDueOnSelectedDay.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFEF3C7),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("⚡ Bill Due Today: ${billsDueOnSelectedDay.first().title} ($currency${billsDueOnSelectedDay.first().amount.toInt()})", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color(0xFFD97706))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Transactions on that date
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(selectedDayTransactions, key = { it.id }) { item ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CategoryIconBadge(categoryName = item.category)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = textDark
                            )
                            Text(
                                text = "${item.category} • ${item.paymentMethod}",
                                style = MaterialTheme.typography.bodySmall,
                                color = textMuted
                            )
                        }
                        Text(
                            text = if (item.type == TransactionType.INCOME.name) "+$currency${String.format("%,.0f", item.amount)}" else "-$currency${String.format("%,.0f", item.amount)}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (item.type == TransactionType.INCOME.name) Color(0xFF16A34A) else Color(0xFFDC2626)
                            )
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}
