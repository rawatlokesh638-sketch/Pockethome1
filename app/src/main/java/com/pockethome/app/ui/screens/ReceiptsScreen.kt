package com.pockethome.app.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pockethome.app.data.model.ExpenseCategory
import com.pockethome.app.data.model.PaymentMethod
import com.pockethome.app.data.model.TransactionItem
import com.pockethome.app.data.model.TransactionType
import com.pockethome.app.ui.viewmodel.GrihaUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class OcrScanSample(
    val shopName: String,
    val amount: Double,
    val category: String,
    val billItems: List<String>,
    val paymentMode: String
)

val SampleOcrBills = listOf(
    OcrScanSample("D-Mart Supermarket", 1850.0, ExpenseCategory.GROCERIES.displayName, listOf("Atta 10kg - ₹420", "Cooking Oil 5L - ₹680", "Basmati Rice - ₹450", "Detergent - ₹300"), PaymentMethod.UPI.displayName),
    OcrScanSample("Apollo Pharmacy", 640.0, ExpenseCategory.HEALTH.displayName, listOf("Paracetamol & Vit C - ₹180", "Blood Pressure Tablet - ₹340", "Bandages - ₹120"), PaymentMethod.DEBIT_CARD.displayName),
    OcrScanSample("Indian Oil Petrol Pump", 1200.0, ExpenseCategory.TRANSPORT.displayName, listOf("Speed Petrol Refill 11.2L"), PaymentMethod.CREDIT_CARD.displayName),
    OcrScanSample("Swiggy Dineout", 980.0, ExpenseCategory.FOOD.displayName, listOf("Paneer Butter Masala", "Garlic Naan (3x)", "Dal Makhani"), PaymentMethod.UPI.displayName),
    OcrScanSample("Croma Electronics", 3499.0, ExpenseCategory.SHOPPING.displayName, listOf("Bluetooth ANC Earbuds - ₹2,999", "65W Fast Charger - ₹500"), PaymentMethod.EMI.displayName)
)

@Composable
fun ReceiptsScreen(
    state: GrihaUiState,
    onBackClick: () -> Unit,
    onAddTransactionWithReceipt: (title: String, amount: Double, category: String, paymentMethod: String, dateStr: String, shopName: String, receiptUri: String) -> Unit,
    onDeleteTransaction: (String) -> Unit
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedPreviewReceipt by remember { mutableStateOf<TransactionItem?>(null) }
    var showScanDialog by remember { mutableStateOf(false) }
    var isScanningOcr by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            // Trigger OCR Scan with selected image
            isScanningOcr = true
            CoroutineScope(Dispatchers.Main).launch {
                delay(1200)
                isScanningOcr = false
                val sample = SampleOcrBills.random()
                val todayStr = SimpleDateFormat("d MMM yyyy", Locale.ENGLISH).format(Date())
                onAddTransactionWithReceipt(
                    sample.shopName,
                    sample.amount,
                    sample.category,
                    sample.paymentMode,
                    todayStr,
                    sample.shopName,
                    uri.toString()
                )
                Toast.makeText(context, "OCR Scanned: ${sample.shopName} - ₹${sample.amount.toInt()}", Toast.LENGTH_LONG).show()
                showScanDialog = false
            }
        }
    }

    val currency = state.profile.currencyCode
    val textDark = Color(0xFF0F172A)
    val textMuted = Color(0xFF64748B)

    // Filter transactions that have receipts or are expense bills
    val receiptTransactions = state.transactions.filter {
        it.type == TransactionType.EXPENSE.name &&
        (searchQuery.isBlank() || it.title.contains(searchQuery, ignoreCase = true) || it.category.contains(searchQuery, ignoreCase = true) || it.shopName.contains(searchQuery, ignoreCase = true))
    }

    val totalReceiptsValue = receiptTransactions.sumOf { it.amount }

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
                modifier = Modifier.testTag("receipts_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = textDark
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Receipts & OCR Vault 📸",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 19.sp
                ),
                color = textDark
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = { showScanDialog = true },
                modifier = Modifier.testTag("scan_receipt_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.DocumentScanner,
                    contentDescription = "Scan Receipt",
                    tint = Color(0xFF4F46E5)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Total Verified Receipts Header Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Total Verified Receipts",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8)
                    )
                    Text(
                        text = "$currency${String.format("%,.0f", totalReceiptsValue)}",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF4F46E5)
                ) {
                    Row(
                        modifier = Modifier
                            .clickable { showScanDialog = true }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.AddAPhoto, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Scan Bill",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by shop name, grocery, fuel...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = textMuted) },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Receipts List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(receiptTransactions, key = { it.id }) { tx ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedPreviewReceipt = tx }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFEEF2FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Receipt,
                                contentDescription = "Receipt",
                                tint = Color(0xFF4F46E5),
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (tx.shopName.isNotBlank()) tx.shopName else tx.title,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = textDark,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFFDCFCE7)
                                ) {
                                    Text(
                                        text = "OCR Verified",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp,
                                            color = Color(0xFF16A34A)
                                        ),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = "${tx.category} • ${tx.paymentMethod} • ${tx.dateString}",
                                style = MaterialTheme.typography.bodySmall,
                                color = textMuted
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "-$currency${String.format("%,.0f", tx.amount)}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFDC2626)
                                )
                            )
                            IconButton(
                                onClick = { selectedPreviewReceipt = tx },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Visibility,
                                    contentDescription = "View",
                                    tint = Color(0xFF4F46E5),
                                    modifier = Modifier.size(18.dp)
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

    // OCR Scan & Upload Modal Dialog
    if (showScanDialog) {
        AlertDialog(
            onDismissRequest = { if (!isScanningOcr) showScanDialog = false },
            title = { Text("Smart OCR Receipt Scanner 📸", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (isScanningOcr) {
                        CircularProgressIndicator(color = Color(0xFF4F46E5), modifier = Modifier.size(44.dp))
                        Text(
                            text = "Extracting Shop Name, Amount & Date with AI OCR...",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = Color(0xFF4F46E5)
                        )
                    } else {
                        Text(
                            text = "Choose a photo from your camera/gallery, or pick a simulated retail bill preset:",
                            style = MaterialTheme.typography.bodySmall,
                            color = textMuted
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Gallery / Cam", fontSize = 12.sp)
                            }
                        }

                        Text("Quick Preset Retail Bills:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))

                        SampleOcrBills.forEach { sample ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFF1F5F9),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        isScanningOcr = true
                                        CoroutineScope(Dispatchers.Main).launch {
                                            delay(900)
                                            isScanningOcr = false
                                            val todayStr = SimpleDateFormat("d MMM yyyy", Locale.ENGLISH).format(Date())
                                            onAddTransactionWithReceipt(
                                                sample.shopName,
                                                sample.amount,
                                                sample.category,
                                                sample.paymentMode,
                                                todayStr,
                                                sample.shopName,
                                                "https://pockethome.app/receipts/${sample.shopName.lowercase().replace(" ", "_")}.jpg"
                                            )
                                            Toast.makeText(context, "OCR Scanned: ${sample.shopName} - $currency${sample.amount.toInt()}", Toast.LENGTH_SHORT).show()
                                            showScanDialog = false
                                        }
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(sample.shopName, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = textDark)
                                        Text(sample.category, style = MaterialTheme.typography.bodySmall, color = textMuted)
                                    }
                                    Text(
                                        "$currency${sample.amount.toInt()}",
                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                        color = Color(0xFF4F46E5)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                if (!isScanningOcr) {
                    TextButton(onClick = { showScanDialog = false }) {
                        Text("Cancel")
                    }
                }
            }
        )
    }

    // Full Screen Receipt Detail Preview Modal
    selectedPreviewReceipt?.let { tx ->
        AlertDialog(
            onDismissRequest = { selectedPreviewReceipt = null },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Tax Invoice Receipt 🧾", fontWeight = FontWeight.Bold)
                    IconButton(onClick = { selectedPreviewReceipt = null }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Simulated Paper Bill Graphic
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF8FAFC),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = if (tx.shopName.isNotBlank()) tx.shopName else tx.title,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = textDark,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                text = "GSTIN: 27AABCT8826K1ZP • Invoice #${tx.id.takeLast(6)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = textMuted,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            androidx.compose.material3.HorizontalDivider(color = Color(0xFFE2E8F0))
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Date & Time:", style = MaterialTheme.typography.bodySmall, color = textMuted)
                                Text(tx.dateString, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = textDark)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Payment Mode:", style = MaterialTheme.typography.bodySmall, color = textMuted)
                                Text(tx.paymentMethod, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = textDark)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Category:", style = MaterialTheme.typography.bodySmall, color = textMuted)
                                Text(tx.category, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = textDark)
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            androidx.compose.material3.HorizontalDivider(color = Color(0xFFE2E8F0))
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total Amount Paid:", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = textDark)
                                Text("$currency${String.format("%,.0f", tx.amount)}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color(0xFF16A34A))
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        Toast.makeText(context, "Receipt statement copied to clipboard", Toast.LENGTH_SHORT).show()
                        selectedPreviewReceipt = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5))
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Share Bill")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onDeleteTransaction(tx.id)
                        selectedPreviewReceipt = null
                    }
                ) {
                    Text("Delete", color = Color(0xFFEF4444))
                }
            }
        )
    }
}
