package com.pockethome.app.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pockethome.app.data.model.ExpenseCategory
import com.pockethome.app.ui.components.CategoryIconBadge
import com.pockethome.app.ui.components.getCategoryColor
import com.pockethome.app.ui.viewmodel.GrihaUiState

@Composable
fun BudgetScreen(
    state: GrihaUiState,
    onBackClick: () -> Unit,
    onUpdateMonthlyBudget: (Double) -> Unit,
    onUpdateCategoryBudget: (String, Double) -> Unit
) {
    var showEditMonthlyDialog by remember { mutableStateOf(false) }
    var editingCategoryName by remember { mutableStateOf<String?>(null) }

    val monthlyBudget = state.monthlyBudget
    val totalExpenses = state.totalExpenses
    val budgetLeft = (monthlyBudget - totalExpenses).coerceAtLeast(0.0)
    val usedPercentage = if (monthlyBudget > 0) ((totalExpenses / monthlyBudget) * 100).toInt() else 0
    val budgetProgress = if (monthlyBudget > 0) (totalExpenses / monthlyBudget).toFloat().coerceIn(0f, 1f) else 0f

    val textDark = Color(0xFF0F172A)
    val textMuted = Color(0xFF475569)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6FB))
            .padding(horizontal = 16.dp)
    ) {
        // Header
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.testTag("budget_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = textDark
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Budget",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = textDark
            )
            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(48.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Month & Budget Period Selector
        var selectedBudgetPeriod by remember { mutableStateOf("Monthly") }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { /* Previous month */ }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Previous Month",
                        tint = textMuted
                    )
                }
                Text(
                    text = "October 2026",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = textDark
                )
                IconButton(onClick = { /* Next month */ }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Next Month",
                        tint = textMuted
                    )
                }
            }

            // Period Selector Chips
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                listOf("Monthly", "Weekly", "Custom").forEach { period ->
                    val isSel = selectedBudgetPeriod == period
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSel) Color(0xFF4F46E5) else Color.White,
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { selectedBudgetPeriod = period }
                    ) {
                        Text(
                            text = period,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 11.sp
                            ),
                            color = if (isSel) Color.White else Color(0xFF64748B),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Main Monthly Budget Card
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
                                    text = "Monthly Budget",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                    color = textMuted
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "₹${String.format("%,.0f", monthlyBudget)}",
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        fontSize = 30.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = textDark
                                )
                            }

                            // Edit Button
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFEEF2FF),
                                modifier = Modifier.clickable { showEditMonthlyDialog = true }
                            ) {
                                Text(
                                    text = "Edit",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF4F46E5)
                                    ),
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        val progressColor = when {
                            usedPercentage >= 100 -> Color(0xFFEF4444) // Red
                            usedPercentage >= 90 -> Color(0xFFF97316)  // Orange
                            usedPercentage >= 80 -> Color(0xFFF59E0B)  // Amber
                            else -> Color(0xFF22C55E)                  // Green
                        }

                        LinearProgressIndicator(
                            progress = { budgetProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp)),
                            color = progressColor,
                            trackColor = Color(0xFFE2E8F0)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Alert Status Badge
                        if (usedPercentage >= 80) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = when {
                                    usedPercentage >= 100 -> Color(0xFFFEE2E2)
                                    usedPercentage >= 90 -> Color(0xFFFFEDD5)
                                    else -> Color(0xFFFEF3C7)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                            ) {
                                Text(
                                    text = when {
                                        usedPercentage >= 100 -> "🚨 Alert: Monthly budget overspent by ₹${String.format("%,.0f", totalExpenses - monthlyBudget)}!"
                                        usedPercentage >= 90 -> "⚠️ Critical Alert: 90% budget threshold reached!"
                                        else -> "⚠️ Warning Alert: 80% budget threshold reached!"
                                    },
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = when {
                                        usedPercentage >= 100 -> Color(0xFF991B1B)
                                        usedPercentage >= 90 -> Color(0xFF9A3412)
                                        else -> Color(0xFF92400E)
                                    },
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "₹${String.format("%,.0f", budgetLeft)} left",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = textDark
                            )
                            Text(
                                text = "$usedPercentage% used",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = progressColor
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Previous Month Budget Comparison Banner
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF8FAFC),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Previous Month Comparison",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = textDark
                                    )
                                    Text(
                                        text = "Spent ₹16,740 in Sep 2026",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = textMuted
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFFDCFCE7)
                                ) {
                                    Text(
                                        text = "On Track",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color(0xFF15803D),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Category Budgets Header
            item {
                Text(
                    text = "Category Budgets",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    color = textDark
                )
            }

            // Category Budget List
            items(ExpenseCategory.entries) { cat ->
                val spent = state.transactions
                    .filter { it.category == cat.displayName }
                    .sumOf { it.amount }
                val catBudget = state.categoryBudgets.find { it.categoryName == cat.displayName }
                val limit = catBudget?.limitAmount ?: when (cat) {
                    ExpenseCategory.GROCERIES -> 8000.0
                    ExpenseCategory.BILLS -> 5000.0
                    ExpenseCategory.FOOD -> 3000.0
                    ExpenseCategory.TRANSPORT -> 2500.0
                    ExpenseCategory.HEALTH -> 2000.0
                    ExpenseCategory.SHOPPING -> 2500.0
                    ExpenseCategory.EDUCATION -> 2000.0
                    ExpenseCategory.OTHERS -> 4000.0
                    else -> 3000.0
                }

                val pct = if (limit > 0) ((spent / limit) * 100).toInt() else 0
                val progress = if (limit > 0) (spent / limit).toFloat().coerceIn(0f, 1f) else 0f
                val color = getCategoryColor(cat.displayName)

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { editingCategoryName = cat.displayName }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CategoryIconBadge(categoryName = cat.displayName)
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = cat.displayName,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = textDark
                                )
                                Text(
                                    text = "$pct%",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = textMuted
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "₹${String.format("%,.0f", spent)} / ₹${String.format("%,.0f", limit)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = textMuted
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = color,
                                trackColor = Color(0xFFF1F5F9)
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Dialog for Editing Monthly Budget
    if (showEditMonthlyDialog) {
        var inputVal by remember { mutableStateOf(monthlyBudget.toInt().toString()) }
        AlertDialog(
            onDismissRequest = { showEditMonthlyDialog = false },
            title = { Text("Edit Monthly Budget", color = textDark) },
            text = {
                OutlinedTextField(
                    value = inputVal,
                    onValueChange = { inputVal = it },
                    label = { Text("Budget Amount (₹)", color = textMuted) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        inputVal.toDoubleOrNull()?.let { onUpdateMonthlyBudget(it) }
                        showEditMonthlyDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E))
                ) {
                    Text("Save", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditMonthlyDialog = false }) {
                    Text("Cancel", color = textMuted)
                }
            }
        )
    }

    // Dialog for Editing Category Budget
    editingCategoryName?.let { catName ->
        val currentLimit = state.categoryBudgets.find { it.categoryName == catName }?.limitAmount ?: 3000.0
        var inputVal by remember { mutableStateOf(currentLimit.toInt().toString()) }
        AlertDialog(
            onDismissRequest = { editingCategoryName = null },
            title = { Text("Set $catName Budget", color = textDark) },
            text = {
                OutlinedTextField(
                    value = inputVal,
                    onValueChange = { inputVal = it },
                    label = { Text("Category Limit (₹)", color = textMuted) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        inputVal.toDoubleOrNull()?.let { onUpdateCategoryBudget(catName, it) }
                        editingCategoryName = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E))
                ) {
                    Text("Save", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingCategoryName = null }) {
                    Text("Cancel", color = textMuted)
                }
            }
        )
    }
}
