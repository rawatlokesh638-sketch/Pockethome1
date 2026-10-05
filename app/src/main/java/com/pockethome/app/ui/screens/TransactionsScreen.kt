package com.pockethome.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.pockethome.app.data.model.ExpenseCategory
import com.pockethome.app.data.model.PaymentMethod
import com.pockethome.app.data.model.TransactionItem
import com.pockethome.app.data.model.TransactionType
import com.pockethome.app.ui.components.CategoryIconBadge
import com.pockethome.app.ui.viewmodel.GrihaUiState

@Composable
fun TransactionsScreen(
    state: GrihaUiState,
    onBackClick: () -> Unit,
    onDeleteTransaction: (String) -> Unit,
    onDuplicateTransaction: (TransactionItem) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTypeFilter by remember { mutableStateOf("All") } // All, Expense, Income
    var selectedCategoryFilter by remember { mutableStateOf("All") }
    var selectedPaymentMethodFilter by remember { mutableStateOf("All") }
    var sortByOption by remember { mutableStateOf("Date Newest") }
    var showAdvancedFilters by remember { mutableStateOf(false) }

    var isSortDropdownExpanded by remember { mutableStateOf(false) }

    val textDark = Color(0xFF0F172A)
    val textMuted = Color(0xFF475569)

    // Multi-criteria Filter & Search Engine
    val filteredList = state.transactions.filter { item ->
        val query = searchQuery.trim()

        // 1. Search Query matches: Name, Amount, Category, Date, or Notes
        val matchesSearch = if (query.isEmpty()) true else {
            val matchesName = item.title.contains(query, ignoreCase = true)
            val matchesAmount = item.amount.toString().contains(query) ||
                    String.format("%.0f", item.amount).contains(query) ||
                    "₹${item.amount.toInt()}".contains(query)
            val matchesCategory = item.category.contains(query, ignoreCase = true)
            val matchesDate = item.dateString.contains(query, ignoreCase = true)
            val matchesNote = item.note.contains(query, ignoreCase = true)
            val matchesPayment = item.paymentMethod.contains(query, ignoreCase = true)

            matchesName || matchesAmount || matchesCategory || matchesDate || matchesNote || matchesPayment
        }

        // 2. Filter Type (All, Expense, Income)
        val matchesType = when (selectedTypeFilter) {
            "Expenses" -> item.type == TransactionType.EXPENSE.name
            "Income" -> item.type == TransactionType.INCOME.name
            else -> true
        }

        // 3. Filter Category
        val matchesCategory = if (selectedCategoryFilter == "All") true else item.category.equals(selectedCategoryFilter, ignoreCase = true)

        // 4. Filter Payment Method
        val matchesPaymentMethod = if (selectedPaymentMethodFilter == "All") true else item.paymentMethod.equals(selectedPaymentMethodFilter, ignoreCase = true)

        matchesSearch && matchesType && matchesCategory && matchesPaymentMethod
    }.sortedWith { a, b ->
        when (sortByOption) {
            "Amount High-Low" -> b.amount.compareTo(a.amount)
            "Amount Low-High" -> a.amount.compareTo(b.amount)
            "Date Oldest" -> a.timestamp.compareTo(b.timestamp)
            else -> b.timestamp.compareTo(a.timestamp)
        }
    }

    val totalFilteredAmount = filteredList.sumOf { if (it.type == TransactionType.INCOME.name) it.amount else -it.amount }

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
                modifier = Modifier.testTag("tx_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = textDark
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Search & Transactions",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = textDark
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = { showAdvancedFilters = !showAdvancedFilters },
                modifier = Modifier.testTag("tx_filter_toggle_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Toggle Filters",
                    tint = if (showAdvancedFilters || selectedCategoryFilter != "All" || selectedPaymentMethodFilter != "All") Color(0xFF4F46E5) else textMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Input Field (Name, Amount, Category, Date, Notes)
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search name, ₹ amount, category, date, notes...", color = textMuted, fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = textMuted) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = textMuted)
                    }
                }
            },
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = textDark, fontSize = 14.sp),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = textDark,
                unfocusedTextColor = textDark,
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                focusedBorderColor = Color(0xFF4F46E5),
                unfocusedBorderColor = Color(0xFFE2E8F0)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("tx_search_field")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Type Filter Pills (All / Expenses / Income) & Sort Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf("All", "Expenses", "Income").forEach { filter ->
                val isSelected = selectedTypeFilter == filter
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) Color(0xFF4F46E5) else Color.White,
                    border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)) else null,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { selectedTypeFilter = filter }
                ) {
                    Text(
                        text = filter,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        ),
                        color = if (isSelected) Color.White else Color(0xFF64748B),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Sort Dropdown
            Box {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.clickable { isSortDropdownExpanded = true }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sort,
                            contentDescription = "Sort",
                            tint = Color(0xFF4F46E5),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = sortByOption,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = textDark
                        )
                    }
                }

                DropdownMenu(
                    expanded = isSortDropdownExpanded,
                    onDismissRequest = { isSortDropdownExpanded = false }
                ) {
                    listOf("Date Newest", "Date Oldest", "Amount High-Low", "Amount Low-High").forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option, color = textDark) },
                            onClick = {
                                sortByOption = option
                                isSortDropdownExpanded = false
                            }
                        )
                    }
                }
            }
        }

        // Advanced Category & Payment Method Filter Bar
        AnimatedVisibility(visible = showAdvancedFilters || selectedCategoryFilter != "All" || selectedPaymentMethodFilter != "All") {
            Column(modifier = Modifier.padding(top = 10.dp)) {
                // Category Filter Scroll Row
                Text(
                    text = "Category:",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = textMuted
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val categories = listOf("All") + ExpenseCategory.entries.map { it.displayName }
                    categories.forEach { cat ->
                        val isSelected = selectedCategoryFilter == cat
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) Color(0xFF3B82F6) else Color.White,
                            border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)) else null,
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { selectedCategoryFilter = cat }
                        ) {
                            Text(
                                text = cat,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = if (isSelected) Color.White else textDark,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Payment Method Filter Scroll Row
                Text(
                    text = "Payment Method:",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = textMuted
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val methods = listOf("All") + PaymentMethod.entries.map { it.displayName }
                    methods.forEach { method ->
                        val isSelected = selectedPaymentMethodFilter == method
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) Color(0xFF10B981) else Color.White,
                            border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)) else null,
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { selectedPaymentMethodFilter = method }
                        ) {
                            Text(
                                text = method,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = if (isSelected) Color.White else textDark,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                // Reset Filters Button
                if (selectedCategoryFilter != "All" || selectedPaymentMethodFilter != "All" || searchQuery.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    TextButton(
                        onClick = {
                            searchQuery = ""
                            selectedCategoryFilter = "All"
                            selectedPaymentMethodFilter = "All"
                            selectedTypeFilter = "All"
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Reset All Filters", color = Color(0xFFEF4444), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Results Count Summary
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${filteredList.size} Transactions Found",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = textMuted
            )
            Text(
                text = if (totalFilteredAmount >= 0) "Net: +₹${String.format("%,.0f", totalFilteredAmount)}" else "Net: -₹${String.format("%,.0f", -totalFilteredAmount)}",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (totalFilteredAmount >= 0) Color(0xFF16A34A) else Color(0xFFDC2626)
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Transactions List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (filteredList.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🔍 No Matching Transactions",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = textDark
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Try adjusting your search keywords, category, or payment filters.",
                                style = MaterialTheme.typography.bodySmall,
                                color = textMuted,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(filteredList, key = { it.id }) { item ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CategoryIconBadge(
                                categoryName = item.category
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    ),
                                    color = textDark
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = item.dateString,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = textMuted
                                    )
                                    Text(text = "•", style = MaterialTheme.typography.bodySmall, color = textMuted)
                                    Text(
                                        text = item.paymentMethod,
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                        color = Color(0xFF4F46E5)
                                    )
                                }

                                if (item.note.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "📝 ${item.note}",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                val isExpense = item.type == TransactionType.EXPENSE.name
                                val color = if (isExpense) Color(0xFFEF4444) else Color(0xFF22C55E)
                                val prefix = if (isExpense) "-₹" else "+₹"

                                Text(
                                    text = "$prefix${String.format("%,.0f", item.amount)}",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    ),
                                    color = color
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    IconButton(
                                        onClick = { onDuplicateTransaction(item) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Duplicate",
                                            tint = Color(0xFF94A3B8),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = { onDeleteTransaction(item.id) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = Color(0xFFEF4444).copy(alpha = 0.7f),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
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
}
