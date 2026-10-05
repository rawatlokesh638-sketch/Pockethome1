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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pockethome.app.data.model.BillItem
import com.pockethome.app.ui.components.BillCardItem
import com.pockethome.app.ui.viewmodel.GrihaUiState

@Composable
fun BillsScreen(
    state: GrihaUiState,
    onBackClick: () -> Unit,
    onToggleBillPaid: (String, Boolean) -> Unit,
    onAddBillClick: (title: String, amount: Double, dueDate: String, dueDaysText: String, category: String, billType: String) -> Unit,
    onToggleAutoReminders: (Boolean) -> Unit
) {
    var selectedTab by remember { mutableStateOf("Upcoming") }
    var showAddBillDialog by remember { mutableStateOf(false) }

    val filteredBills = state.bills.filter { bill ->
        if (selectedTab == "Upcoming") !bill.isPaid else bill.isPaid
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
                modifier = Modifier.testTag("bills_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Bills & Reminders",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(48.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Toggle Tabs [Upcoming] [Past]
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFFEEF2FF),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                listOf("Upcoming", "Past").forEach { tab ->
                    val isSelected = selectedTab == tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                if (isSelected) Color(0xFF4F46E5) else Color.Transparent
                            )
                            .clickable { selectedTab = tab },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tab,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 14.sp
                            ),
                            color = if (isSelected) Color.White else Color(0xFF64748B)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (filteredBills.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (selectedTab == "Upcoming") "No upcoming bills! 🎉" else "No past bills.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                items(filteredBills, key = { it.id }) { bill ->
                    BillCardItem(
                        bill = bill,
                        onTogglePaid = { isPaid -> onToggleBillPaid(bill.id, isPaid) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
                // Add New Bill Button
                Button(
                    onClick = { showAddBillDialog = true },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("add_new_bill_btn")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Add New Bill",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Automatic Reminders Banner Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFFFEF3C7)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Reminders",
                                tint = Color(0xFFD97706),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Automatic Reminders",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFF78350F)
                                )
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close Banner",
                                    tint = Color(0xFF92400E),
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clickable { /* Dismiss banner */ }
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Get notified 1, 3, or 7 days before due date.",
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                                color = Color(0xFF92400E)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Switch(
                            checked = state.profile.autoRemindersEnabled,
                            onCheckedChange = { onToggleAutoReminders(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF2563EB)
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Reminder History Log Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Reminder History Log 🔔",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "• Electricity bill due in 6 days (Alert active)",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                            color = Color(0xFF475569)
                        )
                        Text(
                            text = "• Internet bill due in 8 days (Alert active)",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                            color = Color(0xFF475569)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Add Bill Dialog
    if (showAddBillDialog) {
        var billTitle by remember { mutableStateOf("") }
        var billAmount by remember { mutableStateOf("") }
        var billDueDate by remember { mutableStateOf("15 Oct 2026") }
        var selectedBillType by remember { mutableStateOf("electricity") }
        var isBillTypeDropdownExpanded by remember { mutableStateOf(false) }

        val billTypesList = listOf(
            "electricity" to "Electricity Bill",
            "water" to "Water Bill",
            "wifi" to "Internet / Wifi",
            "phone" to "Mobile Recharge",
            "gas" to "LPG / Gas Cylinder",
            "rent" to "House Rent",
            "insurance" to "Insurance",
            "emi" to "EMI / Loan",
            "education" to "School / College Fees",
            "subscription" to "Subscription Bill",
            "custom" to "Custom Bill"
        )

        AlertDialog(
            onDismissRequest = { showAddBillDialog = false },
            title = { Text("Add Bill Reminder", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Bill Type Selector Dropdown
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF8FAFC),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isBillTypeDropdownExpanded = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = billTypesList.find { it.first == selectedBillType }?.second ?: "Select Bill Type",
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                                    color = Color(0xFF0F172A)
                                )
                                Text("▼", fontSize = 12.sp, color = Color(0xFF64748B))
                            }
                        }

                        DropdownMenu(
                            expanded = isBillTypeDropdownExpanded,
                            onDismissRequest = { isBillTypeDropdownExpanded = false }
                        ) {
                            billTypesList.forEach { (typeKey, label) ->
                                DropdownMenuItem(
                                    text = { Text(label) },
                                    onClick = {
                                        selectedBillType = typeKey
                                        if (billTitle.isBlank()) {
                                            billTitle = label
                                        }
                                        isBillTypeDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = billTitle,
                        onValueChange = { billTitle = it },
                        label = { Text("Bill Name") },
                        placeholder = { Text("e.g. Electricity / House Rent") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = billAmount,
                        onValueChange = { billAmount = it },
                        label = { Text("Amount (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = billDueDate,
                        onValueChange = { billDueDate = it },
                        label = { Text("Due Date (e.g. 15 Oct 2026)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Reminder Lead Time Selection (1, 3, 7 days before)
                    var reminderLeadDays by remember { mutableStateOf("3 Days Before") }
                    Text(
                        text = "Reminder Lead Time",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF475569)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("1 Day", "3 Days", "7 Days", "Custom").forEach { lead ->
                            val label = if (lead == "Custom") "Custom" else "$lead Before"
                            val isSel = reminderLeadDays == label
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSel) Color(0xFF4F46E5) else Color(0xFFF1F5F9),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { reminderLeadDays = label }
                            ) {
                                Text(
                                    text = lead,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 11.sp
                                    ),
                                    color = if (isSel) Color.White else Color(0xFF475569),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amountVal = billAmount.toDoubleOrNull() ?: 0.0
                        if (billTitle.isNotBlank() && amountVal > 0) {
                            onAddBillClick(billTitle, amountVal, billDueDate, "Due soon", "Bills & Utilities", selectedBillType)
                            showAddBillDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5))
                ) {
                    Text("Add Bill")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddBillDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
