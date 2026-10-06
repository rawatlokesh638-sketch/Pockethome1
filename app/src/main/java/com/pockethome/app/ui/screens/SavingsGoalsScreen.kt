package com.pockethome.app.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Savings
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pockethome.app.data.model.SavingsGoal
import com.pockethome.app.ui.viewmodel.GrihaUiState

@Composable
fun SavingsGoalsScreen(
    state: GrihaUiState,
    onBackClick: () -> Unit,
    onAddGoalClick: (goal: SavingsGoal) -> Unit,
    onDepositClick: (goalId: String, amount: Double) -> Unit,
    onDeleteGoalClick: (goalId: String) -> Unit
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedDepositGoalId by remember { mutableStateOf<String?>(null) }
    var depositAmountText by remember { mutableStateOf("1000") }

    val textDark = Color(0xFF0F172A)
    val textMuted = Color(0xFF64748B)

    val totalTarget = state.savingsGoals.sumOf { it.targetAmount }
    val totalSaved = state.savingsGoals.sumOf { it.currentSaved }
    val totalProgressPct = if (totalTarget > 0) ((totalSaved / totalTarget) * 100).toInt() else 0

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
                modifier = Modifier.testTag("savings_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = textDark
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Savings Goals 🎯",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = textDark
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = { showCreateDialog = true },
                modifier = Modifier.testTag("add_savings_goal_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Goal",
                    tint = Color(0xFF4F46E5)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Total Savings Summary Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF4F46E5)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Total Accumulated Savings",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFE0E7FF)
                        )
                        Text(
                            text = "₹${String.format("%,.0f", totalSaved)}",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "$totalProgressPct% Reached",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                LinearProgressIndicator(
                    progress = { if (totalTarget > 0) (totalSaved / totalTarget).toFloat().coerceIn(0f, 1f) else 0f },
                    color = Color(0xFF34D399),
                    trackColor = Color.White.copy(alpha = 0.25f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${state.savingsGoals.size} Active Goals",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFE0E7FF)
                    )
                    Text(
                        text = "Target: ₹${String.format("%,.0f", totalTarget)}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Goals List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(state.savingsGoals, key = { it.id }) { goal ->
                val progress = if (goal.targetAmount > 0) (goal.currentSaved / goal.targetAmount).toFloat().coerceIn(0f, 1f) else 0f
                val pct = (progress * 100).toInt()
                val goalColor = try {
                    Color(android.graphics.Color.parseColor(goal.colorHex))
                } catch (e: Exception) {
                    Color(0xFF4F46E5)
                }

                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(goalColor.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = goal.emoji, fontSize = 22.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = goal.title,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = textDark
                                    )
                                    if (goal.isCompleted) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = Color(0xFFDCFCE7)
                                        ) {
                                            Text(
                                                text = "Completed 🎉",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 9.sp
                                                ),
                                                color = Color(0xFF16A34A),
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = "Target Date: ${goal.targetDateString}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = textMuted
                                )
                            }
                            IconButton(
                                onClick = { onDeleteGoalClick(goal.id) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Goal",
                                    tint = Color(0xFFEF4444).copy(alpha = 0.6f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Progress Bar
                        LinearProgressIndicator(
                            progress = { progress },
                            color = goalColor,
                            trackColor = Color(0xFFF1F5F9),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Amounts Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "₹${String.format("%,.0f", goal.currentSaved)} of ₹${String.format("%,.0f", goal.targetAmount)}",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = textDark
                                )
                                if (goal.monthlyTarget > 0 && !goal.isCompleted) {
                                    Text(
                                        text = "Target: ₹${String.format("%,.0f", goal.monthlyTarget)} / month",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF2563EB)
                                    )
                                }
                            }
                            Text(
                                text = "$pct%",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = goalColor
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Deposit Action Button
                        if (!goal.isCompleted) {
                            Button(
                                onClick = {
                                    selectedDepositGoalId = goal.id
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = goalColor),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Savings, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("+ Deposit Money")
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(90.dp))
            }
        }
    }

    // Deposit Money Dialog
    selectedDepositGoalId?.let { goalId ->
        AlertDialog(
            onDismissRequest = { selectedDepositGoalId = null },
            title = { Text("Deposit to Savings Goal 💰", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Enter amount you want to add to this goal:", style = MaterialTheme.typography.bodySmall, color = textMuted)
                    OutlinedTextField(
                        value = depositAmountText,
                        onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) depositAmountText = it },
                        label = { Text("Amount (₹)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(500, 1000, 2000, 5000).forEach { quickAmt ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFEEF2FF),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { depositAmountText = quickAmt.toString() }
                            ) {
                                Text(
                                    text = "+₹$quickAmt",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFF4F46E5),
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = depositAmountText.toDoubleOrNull() ?: 1000.0
                        onDepositClick(goalId, amt)
                        selectedDepositGoalId = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A))
                ) {
                    Text("Confirm Deposit")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedDepositGoalId = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Create New Goal Dialog
    if (showCreateDialog) {
        var goalTitle by remember { mutableStateOf("") }
        var goalEmoji by remember { mutableStateOf("🎯") }
        var goalAmount by remember { mutableStateOf("25000") }
        var goalTargetDate by remember { mutableStateOf("Dec 2026") }
        var goalMonthlyTarget by remember { mutableStateOf("2500") }
        var goalColorHex by remember { mutableStateOf("#4F46E5") }

        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Create Savings Goal 🎯", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Emoji Picker Row
                    Text("Select Emoji:", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("📱", "🏍️", "✈️", "🏠", "💻", "🎓", "🚗", "🏖️", "💍", "🎯", "🚲", "🎮").forEach { em ->
                            Surface(
                                shape = CircleShape,
                                color = if (goalEmoji == em) Color(0xFFEEF2FF) else Color.White,
                                border = if (goalEmoji == em) androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF4F46E5)) else null,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .clickable { goalEmoji = em }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = em, fontSize = 20.sp)
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = goalTitle,
                        onValueChange = { goalTitle = it },
                        label = { Text("Goal Name (e.g. New Phone / Bike)") },
                        placeholder = { Text("e.g. iPhone 16 / Royal Enfield") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = goalAmount,
                        onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) goalAmount = it },
                        label = { Text("Target Amount (₹)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = goalTargetDate,
                        onValueChange = { goalTargetDate = it },
                        label = { Text("Target Date") },
                        placeholder = { Text("e.g. Dec 2026") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = goalMonthlyTarget,
                        onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) goalMonthlyTarget = it },
                        label = { Text("Monthly Saving Target (₹)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = goalAmount.toDoubleOrNull() ?: 25000.0
                        val monthly = goalMonthlyTarget.toDoubleOrNull() ?: 2500.0
                        val newGoal = SavingsGoal(
                            id = "goal_${System.currentTimeMillis()}",
                            title = goalTitle.ifBlank { "My Goal" },
                            category = "Custom Goal",
                            emoji = goalEmoji,
                            targetAmount = amt,
                            currentSaved = 0.0,
                            targetDateString = goalTargetDate.ifBlank { "Dec 2026" },
                            monthlyTarget = monthly,
                            strategy = "Moderate",
                            investmentType = "SIP",
                            colorHex = goalColorHex
                        )
                        onAddGoalClick(newGoal)
                        showCreateDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5))
                ) {
                    Text("Create Goal")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
