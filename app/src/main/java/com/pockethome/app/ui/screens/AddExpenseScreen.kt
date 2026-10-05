package com.pockethome.app.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pockethome.app.data.model.ExpenseCategory
import com.pockethome.app.data.model.PaymentMethod
import com.pockethome.app.data.model.TransactionType
import com.pockethome.app.ui.components.getCategoryIcon
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddExpenseScreen(
    initialType: TransactionType = TransactionType.EXPENSE,
    onCloseClick: () -> Unit,
    onSaveClick: (amount: Double, category: String, type: TransactionType, note: String, paymentMethod: String, date: String, isRecurring: Boolean, receiptUri: String) -> Unit
) {
    var amountText by remember { mutableStateOf("250") }
    var selectedCategory by remember { mutableStateOf(ExpenseCategory.GROCERIES) }
    var selectedIncomeCatName by remember { mutableStateOf("Salary") }
    var noteText by remember { mutableStateOf("") }
    var selectedPaymentMethod by remember { mutableStateOf(PaymentMethod.CASH) }
    var isPaymentDropdownExpanded by remember { mutableStateOf(false) }
    var isRecurring by remember { mutableStateOf(false) }
    var receiptUri by remember { mutableStateOf<Uri?>(null) }
    var dateString by remember {
        mutableStateOf(SimpleDateFormat("d MMM yyyy", Locale.ENGLISH).format(Date()))
    }
    val transactionType by remember { mutableStateOf(initialType) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        receiptUri = uri
    }

    val textDark = Color(0xFF0F172A)
    val textMuted = Color(0xFF475569)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6FB))
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onCloseClick,
                modifier = Modifier.testTag("add_expense_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = textDark
                )
            }
            Text(
                text = if (transactionType == TransactionType.EXPENSE) "Add Expense" else "Add Income",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = textDark
            )
            IconButton(
                onClick = onCloseClick,
                modifier = Modifier.testTag("add_expense_close_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = textDark
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Large Amount Input Card
        Text(
            text = "Amount",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = textMuted
        )
        Spacer(modifier = Modifier.height(8.dp))
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFFEEF2FF),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "₹ ",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = textDark
                )
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textStyle = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = textDark
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = textDark,
                        unfocusedTextColor = textDark,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("amount_input_field")
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Category Selector Grid
        Text(
            text = "Category",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = textMuted
        )
        Spacer(modifier = Modifier.height(12.dp))

        if (transactionType == TransactionType.EXPENSE) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                maxItemsInEachRow = 4,
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ExpenseCategory.entries.forEach { category ->
                    val isSelected = selectedCategory == category
                    val icon = getCategoryIcon(category.displayName)

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(72.dp)
                            .clickable { selectedCategory = category }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(
                                    if (isSelected) Color(0xFFE0F2FE)
                                    else Color(0xFFF1F5F9)
                                )
                                .border(
                                    width = if (isSelected) 2.dp else 0.dp,
                                    color = if (isSelected) Color(0xFF0284C7) else Color.Transparent,
                                    shape = RoundedCornerShape(18.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = category.displayName,
                                tint = if (isSelected) Color(0xFF0284C7) else Color(0xFF475569),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = category.displayName.split(" ").first(),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = if (isSelected) Color(0xFF0284C7) else textDark
                        )
                    }
                }
            }
        } else {
            // Income Categories FlowRow
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                maxItemsInEachRow = 3,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                com.pockethome.app.data.model.IncomeCategory.entries.forEach { cat ->
                    val isSelected = selectedIncomeCatName == cat.displayName
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) Color(0xFFDCFCE7) else Color.White,
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) Color(0xFF16A34A) else Color(0xFFE2E8F0)
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                selectedIncomeCatName = cat.displayName
                            }
                    ) {
                        Text(
                            text = cat.displayName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp
                            ),
                            color = if (isSelected) Color(0xFF15803D) else textDark,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                        )
                    }
                }
            }
        }

        // Custom Category Action Button
        var customCategoryInput by remember { mutableStateOf("") }
        var isCustomCategoryDialogOpen by remember { mutableStateOf(false) }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFEEF2FF),
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { isCustomCategoryDialogOpen = true }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Custom Category",
                        tint = Color(0xFF4F46E5),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "+ Add Custom Category",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        ),
                        color = Color(0xFF4F46E5)
                    )
                }
            }
        }

        if (isCustomCategoryDialogOpen) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { isCustomCategoryDialogOpen = false },
                title = { Text("Create Custom Category", fontWeight = FontWeight.Bold) },
                text = {
                    OutlinedTextField(
                        value = customCategoryInput,
                        onValueChange = { customCategoryInput = it },
                        placeholder = { Text("e.g. Gym & Fitness / Subscriptions") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (customCategoryInput.isNotBlank()) {
                                selectedIncomeCatName = customCategoryInput.trim()
                                isCustomCategoryDialogOpen = false
                            }
                        }
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    androidx.compose.material3.TextButton(
                        onClick = { isCustomCategoryDialogOpen = false }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Date Field
        Text(
            text = "Date & Time",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = textMuted
        )
        Spacer(modifier = Modifier.height(8.dp))
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            shadowElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateString,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = textDark
                )
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "Select Date",
                    tint = textMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Note Field
        Text(
            text = "Note / Description",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = textMuted
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = noteText,
            onValueChange = { noteText = it },
            placeholder = { Text("e.g. Sabzi mandi se samaan", color = textMuted) },
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = textDark),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = textDark,
                unfocusedTextColor = textDark,
                unfocusedContainerColor = Color(0xFFF8FAFC),
                focusedContainerColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("note_input_field")
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Payment Method Field
        Text(
            text = "Payment Method (9 Options)",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = textMuted
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Horizontal visual scroll for quick 1-tap payment selection
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PaymentMethod.entries.forEach { method ->
                val isSelected = selectedPaymentMethod == method
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) Color(0xFF4F46E5) else Color.White,
                    border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)) else null,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { selectedPaymentMethod = method }
                        .testTag("pay_method_${method.name.lowercase()}")
                ) {
                    Text(
                        text = method.displayName,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = if (isSelected) Color.White else textDark,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Recurring Expense Switch & Frequency Selector
        var recurringFrequency by remember { mutableStateOf("Monthly") }

        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color.White,
            shadowElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Repeat,
                            contentDescription = "Recurring Expense",
                            tint = Color(0xFF4F46E5),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Auto-Recurring Schedule 🔄",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = textDark
                            )
                            Text(
                                text = "Repeat for Salary, Rent, EMI, Subscriptions",
                                style = MaterialTheme.typography.bodySmall,
                                color = textMuted
                            )
                        }
                    }

                    Switch(
                        checked = isRecurring,
                        onCheckedChange = { isRecurring = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF4F46E5)
                        ),
                        modifier = Modifier.testTag("recurring_toggle_switch")
                    )
                }

                if (isRecurring) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Repeat Frequency:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = textMuted
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Daily", "Weekly", "Monthly", "Yearly").forEach { freq ->
                            val isSelected = recurringFrequency == freq
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) Color(0xFF4F46E5) else Color(0xFFF1F5F9),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { recurringFrequency = freq }
                            ) {
                                Text(
                                    text = freq,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (isSelected) Color.White else textDark,
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Attach Receipt / Photo Section
        Text(
            text = "Attach Receipt / Bill Photo",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = textMuted
        )
        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            shadowElevation = 1.dp,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AttachFile,
                    contentDescription = "Attach Receipt",
                    tint = Color(0xFF4F46E5),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (receiptUri != null) "Receipt Photo Attached" else "Tap to upload bill/receipt photo",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = textDark
                    )
                    Text(
                        text = if (receiptUri != null) "Photo selected" else "Supports JPG, PNG",
                        style = MaterialTheme.typography.bodySmall,
                        color = textMuted
                    )
                }

                receiptUri?.let { _ ->
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFDCFCE7)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AttachFile,
                            contentDescription = "Receipt Attached",
                            tint = Color(0xFF16A34A),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Save Button
        Button(
            onClick = {
                val valAmount = amountText.toDoubleOrNull() ?: 0.0
                if (valAmount > 0) {
                    val finalCategory = if (transactionType == TransactionType.EXPENSE) selectedCategory.displayName else selectedIncomeCatName
                    onSaveClick(
                        valAmount,
                        finalCategory,
                        transactionType,
                        noteText,
                        selectedPaymentMethod.displayName,
                        dateString,
                        isRecurring,
                        receiptUri?.toString() ?: ""
                    )
                }
            },
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (transactionType == TransactionType.EXPENSE) Color(0xFF22C55E) else Color(0xFF3B82F6)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("save_expense_btn")
        ) {
            Text(
                text = if (transactionType == TransactionType.EXPENSE) "Save Expense" else "Save Income",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
