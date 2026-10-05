package com.pockethome.app.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.School
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pockethome.app.data.model.EmiLoanItem
import com.pockethome.app.ui.viewmodel.GrihaUiState

val LoanTypeOptions = listOf("Home Loan", "Car Loan", "Personal Loan", "Electronics EMI", "Education", "Gold Loan")

fun getLoanTypeIcon(type: String): ImageVector {
    return when (type) {
        "Home Loan" -> Icons.Default.Home
        "Car Loan" -> Icons.Default.DirectionsCar
        "Electronics EMI" -> Icons.Default.PhoneAndroid
        "Education" -> Icons.Default.School
        else -> Icons.Default.CreditCard
    }
}

@Composable
fun EmiLoanTrackerScreen(
    state: GrihaUiState,
    onBackClick: () -> Unit,
    onAddEmiLoan: (title: String, loanType: String, lender: String, loanAmount: Double, emiAmount: Double, interestRate: Double, dueDay: Int, totalInstallments: Int, colorHex: String) -> Unit,
    onPayEmiInstallment: (String) -> Unit,
    onToggleEmiReminder: (String, Boolean) -> Unit,
    onDeleteEmiLoan: (String) -> Unit
) {
    val context = LocalContext.current
    val currency = state.profile.currencyCode
    val textDark = Color(0xFF0F172A)
    val textMuted = Color(0xFF64748B)

    var showAddLoanDialog by remember { mutableStateOf(false) }
    var loanToPayConfirm by remember { mutableStateOf<EmiLoanItem?>(null) }

    // Summary calculations
    val totalLoansCount = state.emiLoans.size
    val totalMonthlyEmi = state.emiLoans.filter { !it.isCompleted }.sumOf { it.emiAmount }
    val totalRemainingDebt = state.emiLoans.sumOf { it.remainingAmount }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
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
                modifier = Modifier.testTag("emi_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = textDark
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "EMI & Loan Tracker 🧮",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = textDark
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = { showAddLoanDialog = true },
                modifier = Modifier.testTag("add_loan_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Loan",
                    tint = Color(0xFF4F46E5)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Total Debt & EMI Summary Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
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
                            text = "Total Monthly EMI Outflow",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF94A3B8)
                        )
                        Text(
                            text = "$currency${String.format("%,.0f", totalMonthlyEmi)}",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF1E293B)
                    ) {
                        Text(
                            text = "$totalLoansCount Active Loans",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF38BDF8),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                androidx.compose.material3.HorizontalDivider(color = Color(0xFF334155))
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Total Remaining Debt", style = MaterialTheme.typography.bodySmall, color = Color(0xFF94A3B8))
                        Text(
                            text = "$currency${String.format("%,.0f", totalRemainingDebt)}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFFF87171))
                        )
                    }

                    Button(
                        onClick = { showAddLoanDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5)),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+ Add Loan", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Active EMIs & Repayment Plans",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = textDark
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Loans List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(state.emiLoans, key = { it.id }) { loan ->
                val cardAccent = try {
                    Color(android.graphics.Color.parseColor(loan.colorHex))
                } catch (e: Exception) {
                    Color(0xFF4F46E5)
                }

                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Title & Lender Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(cardAccent.copy(alpha = 0.14f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = getLoanTypeIcon(loan.loanType),
                                    contentDescription = loan.loanType,
                                    tint = cardAccent,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = loan.title,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = textDark
                                )
                                Text(
                                    text = "${loan.lender} • ${loan.loanType} • ${loan.interestRate}% p.a.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = textMuted
                                )
                            }

                            IconButton(
                                onClick = { onDeleteEmiLoan(loan.id) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFF94A3B8), modifier = Modifier.size(18.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // EMI Amount & Due Date
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Monthly EMI", style = MaterialTheme.typography.bodySmall, color = textMuted)
                                Text(
                                    text = "$currency${String.format("%,.0f", loan.emiAmount)}",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = cardAccent)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFFEF3C7)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "📅 Due: ${loan.dueDateString}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color(0xFFB45309)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Progress Bar & Installments count
                        LinearProgressIndicator(
                            progress = { loan.progressPct / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = cardAccent,
                            trackColor = Color(0xFFE2E8F0)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${loan.paidInstallments} Paid (${loan.progressPct}%)",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = Color(0xFF16A34A)
                            )
                            Text(
                                text = "${loan.remainingInstallments} Installments Left",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = Color(0xFFDC2626)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Remaining Loan Amount & Principal
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF8FAFC), RoundedCornerShape(10.dp))
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Original Loan", style = MaterialTheme.typography.bodySmall, color = textMuted, fontSize = 11.sp)
                                Text("$currency${String.format("%,.0f", loan.loanAmount)}", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = textDark)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Remaining Debt", style = MaterialTheme.typography.bodySmall, color = textMuted, fontSize = 11.sp)
                                Text("$currency${String.format("%,.0f", loan.remainingAmount)}", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = Color(0xFFDC2626))
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Action Controls: Reminder switch & Pay Installment button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (loan.remindersEnabled) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                                    contentDescription = null,
                                    tint = if (loan.remindersEnabled) Color(0xFFD97706) else Color(0xFF94A3B8),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Reminders", style = MaterialTheme.typography.bodySmall, color = textDark)
                                Spacer(modifier = Modifier.width(6.dp))
                                Switch(
                                    checked = loan.remindersEnabled,
                                    onCheckedChange = { onToggleEmiReminder(loan.id, it) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = Color(0xFF4F46E5)
                                    ),
                                    modifier = Modifier.size(36.dp)
                                )
                            }

                            if (!loan.isCompleted) {
                                Button(
                                    onClick = { loanToPayConfirm = loan },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Pay Installment", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFDCFCE7)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Loan Closed 🎉", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color(0xFF16A34A))
                                    }
                                }
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

    // Pay EMI Installment Confirmation Dialog
    loanToPayConfirm?.let { loan ->
        AlertDialog(
            onDismissRequest = { loanToPayConfirm = null },
            title = { Text("Confirm EMI Payment 💳", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Pay monthly installment for ${loan.title}?")
                    Text("• Amount: $currency${String.format("%,.0f", loan.emiAmount)}", fontWeight = FontWeight.SemiBold)
                    Text("• Installment: ${loan.paidInstallments + 1} of ${loan.totalInstallments}")
                    Text("• Remaining Debt after this: $currency${String.format("%,.0f", (loan.remainingAmount - loan.emiAmount).coerceAtLeast(0.0))}")
                    Text("This payment will be automatically recorded in your expense history under the 'EMI' category.", style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onPayEmiInstallment(loan.id)
                        Toast.makeText(context, "EMI Paid & Logged: $currency${loan.emiAmount.toInt()} 🎉", Toast.LENGTH_SHORT).show()
                        loanToPayConfirm = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A))
                ) {
                    Text("Confirm Payment")
                }
            },
            dismissButton = {
                TextButton(onClick = { loanToPayConfirm = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Add New Loan Modal Dialog
    if (showAddLoanDialog) {
        var loanTitle by remember { mutableStateOf("") }
        var lenderName by remember { mutableStateOf("") }
        var selectedType by remember { mutableStateOf("Home Loan") }
        var principalAmount by remember { mutableStateOf("") }
        var monthlyEmi by remember { mutableStateOf("") }
        var interestPct by remember { mutableStateOf("8.5") }
        var totalMonths by remember { mutableStateOf("60") }
        var dueDay by remember { mutableStateOf("5") }

        AlertDialog(
            onDismissRequest = { showAddLoanDialog = false },
            title = { Text("Add New Loan / EMI 🧮", fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = loanTitle,
                            onValueChange = { loanTitle = it },
                            label = { Text("Loan Title (e.g. HDFC Home Loan)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = lenderName,
                            onValueChange = { lenderName = it },
                            label = { Text("Bank / Lender (e.g. SBI, Bajaj Finserv)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        Text("Loan Type:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            LoanTypeOptions.take(3).forEach { type ->
                                val isSelected = selectedType == type
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) Color(0xFF4F46E5) else Color(0xFFF1F5F9),
                                    modifier = Modifier.weight(1f).clickable { selectedType = type }
                                ) {
                                    Text(
                                        text = type.replace(" Loan", ""),
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (isSelected) Color.White else textDark,
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = principalAmount,
                            onValueChange = { if (it.all { c -> c.isDigit() }) principalAmount = it },
                            label = { Text("Total Loan Amount ($currency)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = monthlyEmi,
                            onValueChange = { if (it.all { c -> c.isDigit() }) monthlyEmi = it },
                            label = { Text("Monthly EMI Amount ($currency)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = interestPct,
                                onValueChange = { interestPct = it },
                                label = { Text("Interest % p.a.") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = totalMonths,
                                onValueChange = { if (it.all { c -> c.isDigit() }) totalMonths = it },
                                label = { Text("Tenure (Months)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = dueDay,
                            onValueChange = { if (it.all { c -> c.isDigit() }) dueDay = it },
                            label = { Text("Due Day of Month (e.g. 5)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val p = principalAmount.toDoubleOrNull() ?: 100000.0
                        val emi = monthlyEmi.toDoubleOrNull() ?: (p / (totalMonths.toIntOrNull() ?: 12))
                        val rate = interestPct.toDoubleOrNull() ?: 8.5
                        val months = totalMonths.toIntOrNull() ?: 36
                        val day = dueDay.toIntOrNull() ?: 5
                        val title = loanTitle.ifBlank { "$selectedType ($lenderName)" }
                        val lender = lenderName.ifBlank { "Bank" }

                        onAddEmiLoan(title, selectedType, lender, p, emi, rate, day, months, "#3B82F6")
                        Toast.makeText(context, "Loan Added Successfully!", Toast.LENGTH_SHORT).show()
                        showAddLoanDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5))
                ) {
                    Text("Save Loan Plan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddLoanDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
