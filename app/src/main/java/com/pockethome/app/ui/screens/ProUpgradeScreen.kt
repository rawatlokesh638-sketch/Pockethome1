package com.pockethome.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pockethome.app.data.model.UtrPaymentRequest
import com.pockethome.app.ui.viewmodel.GrihaUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val FeatureComparisonList = listOf(
    Pair("Monthly Transactions", Pair("Unlimited", "Unlimited")),
    Pair("Family Sharing", Pair("❌ None", "✅ Full Multi-Device Sync")),
    Pair("Advanced AI Analytics", Pair("❌ None", "✅ Donut Charts & AI Forecasts")),
    Pair("Receipt Photo Scanner", Pair("❌ None", "✅ AI Receipt OCR")),
    Pair("Export Statements", Pair("❌ None", "✅ PDF & Excel Statements")),
    Pair("Savings Goals", Pair("Max 2 Goals", "✅ Unlimited Goals"))
)

@Composable
fun ProUpgradeScreen(
    state: GrihaUiState,
    onBackClick: () -> Unit,
    onSubmitUtrRequest: (utr: String, txnRef: String, plan: String, amount: Double, callback: (Boolean, String?) -> Unit) -> Unit,
    onApproveUtrRequest: (request: UtrPaymentRequest, callback: (Boolean, String?) -> Unit) -> Unit,
    onRejectUtrRequest: (reqId: String, userId: String, callback: (Boolean, String?) -> Unit) -> Unit
) {
    val context = LocalContext.current
    var selectedPlan by remember { mutableStateOf("PRO_MONTHLY") } // PRO_MONTHLY or PRO_ANNUAL
    val isAlreadyPro = state.profile.isProUser

    var utrInput by remember { mutableStateOf("") }
    var txnRefInput by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    var showAdminPanel by remember { mutableStateOf(false) }

    val phonePeNumber = "9050884894"
    val textDark = Color(0xFF0F172A)
    val textMuted = Color(0xFF64748B)

    val userRequests = remember(state.paymentRequests, state.profile.uid) {
        state.paymentRequests.filter { it.userId == state.profile.uid }
    }
    val pendingRequests = remember(state.paymentRequests) {
        state.paymentRequests.filter { it.status == "PENDING" }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(horizontal = 18.dp)
    ) {
        // Header
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.testTag("pro_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = textDark
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Pocket Home PRO 👑",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 19.sp
                ),
                color = textDark
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { showAdminPanel = !showAdminPanel }) {
                Icon(
                    imageVector = Icons.Default.AdminPanelSettings,
                    contentDescription = "Admin Approval Panel",
                    tint = if (showAdminPanel) Color(0xFF4F46E5) else textMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Admin Panel Mode (Review & Approve Requests)
            if (showAdminPanel) {
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCA5A5)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "👑 Admin Approval Panel",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFF991B1B)
                                )
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFEF4444)
                                ) {
                                    Text(
                                        text = "${pendingRequests.size} Pending",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Approve user PhonePe UTR payment submissions to unlock PRO features in Firebase:",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF7F1D1D),
                                fontSize = 11.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            if (pendingRequests.isEmpty()) {
                                Text(
                                    text = "✅ No pending payment requests right now.",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                    color = Color(0xFF166534)
                                )
                            } else {
                                pendingRequests.forEach { req ->
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color.White,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFECACA)),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 10.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text(
                                                text = "User: ${req.userName} (${req.userEmail})",
                                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                                color = textDark
                                            )
                                            Text(
                                                text = "UTR No: ${req.utrNumber} • Plan: ${req.planTier} (₹${req.amount.toInt()})",
                                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                                color = Color(0xFF4F46E5)
                                            )
                                            if (req.transactionRef.isNotBlank()) {
                                                Text(
                                                    text = "Ref/Note: ${req.transactionRef}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = textMuted,
                                                    fontSize = 11.sp
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(10.dp))

                                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                Button(
                                                    onClick = {
                                                        onApproveUtrRequest(req) { success, _ ->
                                                            if (success) {
                                                                Toast.makeText(context, "APPROVED! PRO unlocked for ${req.userName} 🎉", Toast.LENGTH_LONG).show()
                                                            }
                                                        }
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                                    shape = RoundedCornerShape(10.dp),
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Text("Approve 🟢", fontWeight = FontWeight.Bold)
                                                }

                                                Button(
                                                    onClick = {
                                                        onRejectUtrRequest(req.requestId, req.userId) { success, _ ->
                                                            if (success) {
                                                                Toast.makeText(context, "Request rejected.", Toast.LENGTH_SHORT).show()
                                                            }
                                                        }
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                                                    shape = RoundedCornerShape(10.dp),
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Text("Reject 🔴", fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // PRO Hero Banner Card
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF4F46E5), Color(0xFF7C3AED), Color(0xFF2563EB))
                            ),
                            shape = RoundedCornerShape(22.dp)
                        )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isAlreadyPro) "👑 PRO Membership Active" else "Unlock Full Pocket Home PRO 🚀",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            ),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (isAlreadyPro) "Valid until ${state.profile.proExpiryDate} • All Features Unlocked" else "Unlimited Family Sync, AI Receipt Scanner & Advanced Statements",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.White.copy(alpha = 0.9f)
                            ),
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Plan Selection Cards (PRO PLUS ₹50/mo, PRO Monthly ₹199, PRO Annual ₹1,499)
            if (!isAlreadyPro) {
                item {
                    Text(
                        text = "Choose Subscription Plan:",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = textDark
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        // PRO PLUS AI Plan Card (₹50 / mo)
                        val isProPlusSelected = selectedPlan == "PRO_PLUS_AI"
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isProPlusSelected) Color(0xFFF0FDF4) else Color.White,
                            border = androidx.compose.foundation.BorderStroke(
                                2.dp,
                                if (isProPlusSelected) Color(0xFF16A34A) else Color(0xFFE2E8F0)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedPlan = "PRO_PLUS_AI" }
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "PRO PLUS (AI Coach & Planner)",
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = textDark
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(0xFF16A34A)
                                        ) {
                                            Text(
                                                text = "POPULAR",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                fontSize = 9.sp
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "AI Advisor, Expense-to-Saving, Daily Savings & What-If Planner",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = textMuted,
                                        fontSize = 11.sp
                                    )
                                }

                                Text(
                                    text = "₹50 / mo",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF16A34A)
                                    )
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Monthly Plan Card (₹199)
                            val isMonthlySelected = selectedPlan == "PRO_MONTHLY"
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (isMonthlySelected) Color(0xFFEEF2FF) else Color.White,
                                border = androidx.compose.foundation.BorderStroke(
                                    2.dp,
                                    if (isMonthlySelected) Color(0xFF4F46E5) else Color(0xFFE2E8F0)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedPlan = "PRO_MONTHLY" }
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "PRO Full Monthly",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = textDark,
                                        fontSize = 12.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "₹199 / mo",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF4F46E5)
                                        )
                                    )
                                    Text(
                                        text = "+ Family Sync",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = textMuted,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            // Annual Plan Card (₹1,499)
                            val isAnnualSelected = selectedPlan == "PRO_ANNUAL"
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (isAnnualSelected) Color(0xFFFEF3C7) else Color.White,
                                border = androidx.compose.foundation.BorderStroke(
                                    2.dp,
                                    if (isAnnualSelected) Color(0xFFD97706) else Color(0xFFE2E8F0)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedPlan = "PRO_ANNUAL" }
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "PRO Full Annual",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = textDark,
                                        fontSize = 12.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "₹1,499 / yr",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFB45309)
                                        )
                                    )
                                    Text(
                                        text = "Save 37%",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = textMuted,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // PhonePe Payment Instructions & UTR Submission Card
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                    .clip(CircleShape)
                                        .background(Color(0xFFEDE9FE)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = Color(0xFF6D28D9), modifier = Modifier.size(20.dp))
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column {
                                    Text(
                                        text = "PhonePe UPI Payment",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = textDark
                                    )
                                    Text(
                                        text = "Direct PhonePe Transfer & UTR Submission",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = textMuted,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // PhonePe Number Box with Copy Action
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFF3E8FF),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDDD6FE)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = "PhonePe Number:",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color(0xFF6B21A8)
                                        )
                                        Text(
                                            text = phonePeNumber,
                                            style = MaterialTheme.typography.headlineSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF581C87)
                                            )
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            val clip = ClipData.newPlainText("PhonePe Number", phonePeNumber)
                                            clipboard.setPrimaryClip(clip)
                                            Toast.makeText(context, "PhonePe Number 9050884894 Copied!", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7E22CE)),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Copy No", fontSize = 12.sp)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "💡 Payment Steps:\n1. Open PhonePe -> Pay to Mobile Number 9050884894\n2. Send ₹${if (selectedPlan == "PRO_MONTHLY") "199" else "1,499"}\n3. Copy the 12-Digit UTR / Transaction ID from PhonePe receipt\n4. Enter UTR below & submit for Admin Approval 🚀",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF475569),
                                fontSize = 11.sp
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // UTR Number Input
                            OutlinedTextField(
                                value = utrInput,
                                onValueChange = { if (it.length <= 16) utrInput = it },
                                label = { Text("12-Digit PhonePe UTR Number") },
                                placeholder = { Text("e.g. 428592019482") },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = textMuted) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("utr_number_input")
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Transaction Ref / Note
                            OutlinedTextField(
                                value = txnRefInput,
                                onValueChange = { txnRefInput = it },
                                label = { Text("Transaction Reference / Note (Optional)") },
                                placeholder = { Text("e.g. PhonePe payment from Lokesh") },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("utr_ref_input")
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Submit UTR Button
                            Button(
                                onClick = {
                                    if (utrInput.trim().length < 8) {
                                        Toast.makeText(context, "Please enter a valid 12-digit UTR Number.", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }

                                    isSubmitting = true
                                    val amount = if (selectedPlan == "PRO_MONTHLY") 199.0 else 1499.0
                                    onSubmitUtrRequest(utrInput, txnRefInput, selectedPlan, amount) { success, err ->
                                        isSubmitting = false
                                        if (success) {
                                            utrInput = ""
                                            txnRefInput = ""
                                            Toast.makeText(context, "UTR Submitted successfully! Request sent to Admin for Approval. ⏳", Toast.LENGTH_LONG).show()
                                        } else {
                                            Toast.makeText(context, err ?: "Submission failed.", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                enabled = !isSubmitting,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("submit_utr_button")
                            ) {
                                if (isSubmitting) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                                } else {
                                    Text("Submit UTR for Admin Approval 🚀", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                }
                            }
                        }
                    }
                }

                // Recent User Submissions Status Card
                if (userRequests.isNotEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Your Submitted Payment Requests:",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = textDark
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                userRequests.forEach { req ->
                                    val statusBg = when (req.status) {
                                        "APPROVED" -> Color(0xFFDCFCE7)
                                        "REJECTED" -> Color(0xFFFEE2E2)
                                        else -> Color(0xFFFEF3C7)
                                    }
                                    val statusColor = when (req.status) {
                                        "APPROVED" -> Color(0xFF15803D)
                                        "REJECTED" -> Color(0xFFB91C1C)
                                        else -> Color(0xFFB45309)
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = Color(0xFFF8FAFC),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column {
                                                Text(
                                                    text = "UTR: ${req.utrNumber}",
                                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = textDark
                                                )
                                                Text(
                                                    text = "Plan: ${req.planTier} (₹${req.amount.toInt()})",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = textMuted,
                                                    fontSize = 11.sp
                                                )
                                            }

                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = statusBg
                                            ) {
                                                Text(
                                                    text = if (req.status == "PENDING") "⏳ PENDING ADMIN APPROVAL" else req.status,
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = statusColor,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                    fontSize = 10.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Feature Comparison List
            item {
                Text(
                    text = "Feature Comparison (Free vs PRO):",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = textDark
                )
            }

            items(FeatureComparisonList) { (title, comparison) ->
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
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = textDark,
                            modifier = Modifier.weight(1f)
                        )

                        Text(
                            text = comparison.second,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF4F46E5)
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
