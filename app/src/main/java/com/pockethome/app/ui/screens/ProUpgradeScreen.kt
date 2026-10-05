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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pockethome.app.ui.viewmodel.GrihaUiState

data class PlanFeatureComparison(
    val title: String,
    val freeDesc: String,
    val proDesc: String,
    val isProExclusive: Boolean = false
)

val FeatureComparisonList = listOf(
    PlanFeatureComparison("Transactions Limit", "50 per month", "Unlimited", false),
    PlanFeatureComparison("Monthly Budgets", "1 Overall Budget", "Unlimited Category Budgets", false),
    PlanFeatureComparison("Bills & Due Tracker", "Basic 2 bills", "Full Tracker & Auto-deduct", false),
    PlanFeatureComparison("Bill Due Reminders", "❌ None", "✅ Push & Alarm Alerts", true),
    PlanFeatureComparison("EMI & Loan Tracker", "❌ None", "✅ Full Loan & Repayment Tracker", true),
    PlanFeatureComparison("Family Sharing", "❌ Single User", "✅ Sharma Family Multi-device", true),
    PlanFeatureComparison("Cloud Synchronization", "❌ Local only", "✅ Instant Real-time Cloud Sync", true),
    PlanFeatureComparison("PDF & Excel Export", "❌ None", "✅ Full Statements + WhatsApp", true),
    PlanFeatureComparison("Advanced Visual Charts", "Basic bar only", "✅ Donut Charts & Heatmaps", true),
    PlanFeatureComparison("Savings Goals", "❌ 1 Goal max", "✅ Phone, Bike, Vacation Goals", true),
    PlanFeatureComparison("Recurring Schedules", "❌ Manual entry", "✅ Auto-recurring Salary & EMI", true),
    PlanFeatureComparison("Receipt Photo & OCR", "❌ None", "✅ AI Receipt Scanner", true),
    PlanFeatureComparison("Multiple Devices", "❌ 1 device", "✅ All Family Android Phones", true),
    PlanFeatureComparison("Advanced Analytics", "❌ Basic 7 days", "✅ Monthly Forecasts & Trends", true),
    PlanFeatureComparison("Ad-Free Experience", "✅ 100% Ad-Free", "✅ 100% Ad-Free Clean UI", false),
    PlanFeatureComparison("AI Spending Insights", "❌ None", "✅ Smart Spending Advisor", true)
)

@Composable
fun ProUpgradeScreen(
    state: GrihaUiState,
    onBackClick: () -> Unit,
    onUpgradeToPro: (String) -> Unit,
    onDowngradeToFree: () -> Unit
) {
    val context = LocalContext.current
    var selectedPlan by remember { mutableStateOf("PRO_MONTHLY") } // PRO_MONTHLY or PRO_ANNUAL
    val isAlreadyPro = state.profile.isProUser

    val textDark = Color(0xFF0F172A)
    val textMuted = Color(0xFF64748B)

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
                    fontSize = 20.sp
                ),
                color = textDark
            )
            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(48.dp))
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Hero Banner
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFF1E1B4B), Color(0xFF0F172A))
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFEF3C7)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("👑", fontSize = 28.sp)
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = if (isAlreadyPro) "You are on Pocket Home PRO!" else "Unlock Complete Financial Freedom",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                ),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = if (isAlreadyPro) "Your PRO Plan is active until ${state.profile.proExpiryDate}" else "Only ₹20/month — 1 cup of chai to save ₹2,000–₹5,000 every month for your family.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFCBD5E1),
                                textAlign = TextAlign.Center
                            )

                            if (isAlreadyPro) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFF16A34A).copy(alpha = 0.2f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF4ADE80), modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("All 15+ PRO Features Unlocked", fontWeight = FontWeight.Bold, color = Color(0xFF4ADE80), fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Plan Selector Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Monthly Plan (₹20/month)
                    val isMonthlySelected = selectedPlan == "PRO_MONTHLY"
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isMonthlySelected) Color(0xFFEEF2FF) else Color.White
                        ),
                        border = if (isMonthlySelected) androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF4F46E5)) else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedPlan = "PRO_MONTHLY" }
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Monthly Plan", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color(0xFF6366F1))
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text("₹20", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = textDark)
                                Text("/mo", style = MaterialTheme.typography.bodySmall, color = textMuted)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Cancel anytime • UPI", style = MaterialTheme.typography.bodySmall, color = textMuted, fontSize = 11.sp)
                        }
                    }

                    // Annual Plan (₹199/year)
                    val isAnnualSelected = selectedPlan == "PRO_ANNUAL"
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isAnnualSelected) Color(0xFFFEF3C7).copy(alpha = 0.5f) else Color.White
                        ),
                        border = if (isAnnualSelected) androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFD97706)) else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedPlan = "PRO_ANNUAL" }
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFDCFCE7)) {
                                Text("SAVE 17% 🌟", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp, color = Color(0xFF16A34A)), modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text("₹199", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = textDark)
                                Text("/year", style = MaterialTheme.typography.bodySmall, color = textMuted)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("~₹16.5/mo • Best Value", style = MaterialTheme.typography.bodySmall, color = textMuted, fontSize = 11.sp)
                        }
                    }
                }
            }

            // Upgrade Button
            item {
                if (!isAlreadyPro) {
                    Button(
                        onClick = {
                            onUpgradeToPro(selectedPlan)
                            val planText = if (selectedPlan == "PRO_ANNUAL") "Annual Plan (₹199/yr)" else "Monthly Plan (₹20/mo)"
                            Toast.makeText(context, "Welcome to Pocket Home PRO! ($planText) 👑", Toast.LENGTH_LONG).show()
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("activate_pro_btn")
                    ) {
                        Icon(Icons.Default.WorkspacePremium, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (selectedPlan == "PRO_ANNUAL") "Activate PRO Annual • ₹199/yr" else "Activate PRO Monthly • ₹20/month",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                Toast.makeText(context, "Subscription is active & valid until ${state.profile.proExpiryDate}", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Active Member 👑", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                onDowngradeToFree()
                                Toast.makeText(context, "Switched back to Free Tier", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF64748B)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Switch to Free")
                        }
                    }
                }
            }

            // Feature Comparison Matrix Section Header
            item {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Plan Comparison Matrix",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = textDark
                    )
                    Text(
                        text = "FREE vs PRO",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF4F46E5)
                    )
                }
            }

            // Comparison Table Header
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Feature", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = Color.White, modifier = Modifier.weight(1.3f))
                        Text("FREE", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = Color(0xFF94A3B8), modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                        Text("₹20/mo PRO 👑", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = Color(0xFFFBBF24), modifier = Modifier.weight(1.3f), textAlign = TextAlign.End)
                    }
                }
            }

            // Comparison Rows
            items(FeatureComparisonList) { feat ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (feat.isProExclusive) Color(0xFFF8FAFC) else Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1.3f)) {
                            Text(
                                text = feat.title,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = textDark
                            )
                        }

                        Text(
                            text = feat.freeDesc,
                            style = MaterialTheme.typography.bodySmall,
                            color = textMuted,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            fontSize = 11.sp
                        )

                        Text(
                            text = feat.proDesc,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF16A34A)
                            ),
                            modifier = Modifier.weight(1.3f),
                            textAlign = TextAlign.End,
                            fontSize = 11.sp
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
