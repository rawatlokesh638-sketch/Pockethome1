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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WorkspacePremium
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
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import com.pockethome.app.ui.viewmodel.GrihaUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.pow

data class SavingsChallengeItem(
    val id: String,
    val title: String,
    val targetAmount: Double,
    val durationText: String,
    val iconEmoji: String,
    var isJoined: Boolean = false,
    var currentSaved: Double = 0.0
)

@Composable
fun AiMoneyAdvisorScreen(
    state: GrihaUiState,
    onBackClick: () -> Unit = {},
    onOpenProUpgradeClick: () -> Unit = {}
) {
    val context = LocalContext.current
    var activeSubTab by remember { mutableStateOf("Daily Tips") } // Daily Tips, Goal Progress, Expense-to-Saving, Challenges, What-If Planner, Ask AI
    var showProPlusModal by remember { mutableStateOf(false) }

    // Pro Plus Plan Check (₹50/month)
    val isProUser = state.profile.isProUser

    // Daily Savings State
    var dailySavedToday by remember { mutableStateOf(false) }
    var dailyStreakCount by remember { mutableIntStateOf(14) }

    // State for Money Grow Calculator / What-If Planner
    var whatIfMonthlyAmt by remember { mutableDoubleStateOf(3000.0) }
    var whatIfGoalTarget by remember { mutableDoubleStateOf(100000.0) }

    // Savings Challenges State
    var challengesList by remember {
        mutableStateOf(
            listOf(
                SavingsChallengeItem("c1", "₹50 / Day Micro Challenge", 1500.0, "30 Days", "🎯", isJoined = true, currentSaved = 700.0),
                SavingsChallengeItem("c2", "₹100 / Day Power Challenge", 3000.0, "30 Days", "🚀", isJoined = false),
                SavingsChallengeItem("c3", "₹5,000 / Month Wealth Challenge", 5000.0, "1 Month", "💰", isJoined = true, currentSaved = 2500.0),
                SavingsChallengeItem("c4", "No-Spend Day Challenge", 0.0, "24 Hours", "🚫", isJoined = false),
                SavingsChallengeItem("c5", "30-Day Progressive Challenge", 4650.0, "30 Days", "🗓️", isJoined = false),
                SavingsChallengeItem("c6", "52-Week Step Savings", 137800.0, "52 Weeks", "📅", isJoined = false)
            )
        )
    }

    // State for Ask AI
    var aiQueryInput by remember { mutableStateOf("") }
    var aiChatHistory by remember {
        mutableStateOf(
            listOf(
                Pair(false, "Namaste! Main aapka AI Money Coach hoon (PRO PLUS ₹50/mo plan). Kahan kitne paise bacha sakte hain aur goals fast complete kar sakte hain, abhi poochein! 🤖🌱")
            )
        )
    }

    val textDark = Color(0xFF0F172A)
    val textMuted = Color(0xFF64748B)

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
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEEF2FF)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🤖", fontSize = 22.sp)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "AI Coach & Growth Planner",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = textDark
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFFEF3C7)
                    ) {
                        Text(
                            text = "PRO PLUS ₹50/mo",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFFB45309),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 9.sp
                        )
                    }
                }
                Text(
                    text = "Ghar ka kharcha bachaayein, paise badhane ke smart tareeke",
                    style = MaterialTheme.typography.bodySmall,
                    color = textMuted,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Sub-Tab Switcher (Scrollable / Compact)
        val tabs = listOf("Daily Tips", "Goal Progress", "Expense Cut", "Challenges", "What-If", "Ask AI")
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Color(0xFFEEF2FF),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(3.dp)
            ) {
                tabs.forEach { tab ->
                    val isSelected = activeSubTab == tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) Color(0xFF4F46E5) else Color.Transparent)
                            .clickable {
                                if (!isProUser && (tab == "Expense Cut" || tab == "What-If" || tab == "Ask AI")) {
                                    showProPlusModal = true
                                } else {
                                    activeSubTab = tab
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when(tab) {
                                "Daily Tips" -> "💡 Tips"
                                "Goal Progress" -> "🌳 Goals"
                                "Expense Cut" -> "🧾 Save"
                                "Challenges" -> "🏆 Challenge"
                                "What-If" -> "🔄 What-If"
                                else -> "💬 Ask"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 10.sp
                            ),
                            color = if (isSelected) Color.White else Color(0xFF475569)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // TAB 1: DAILY TIPS & STREAK (🔥 Feature 7)
            if (activeSubTab == "Daily Tips") {
                item {
                    // Daily Savings Target Card (🔥 Feature 7)
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF4F46E5)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
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
                                        text = "🔥 Daily Savings Target",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Personalized daily target based on your goals",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White.copy(alpha = 0.85f),
                                        fontSize = 11.sp
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color.White.copy(alpha = 0.2f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "$dailyStreakCount-Day Streak",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "Aaj ₹70 save karo! 🎯",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    if (!dailySavedToday) {
                                        dailySavedToday = true
                                        dailyStreakCount++
                                        Toast.makeText(context, "Awesome! ₹70 Saved today! Streak updated to $dailyStreakCount days 🔥", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                enabled = !dailySavedToday,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (dailySavedToday) Color(0xFF22C55E) else Color.White
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                if (dailySavedToday) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Today's ₹70 Saved! 🎉", color = Color.White, fontWeight = FontWeight.Bold)
                                } else {
                                    Text("Mark Today's ₹70 Saved 🎯", color = Color(0xFF4F46E5), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                item {
                    // Today's Money Tip Card
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Color(0xFFD97706))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Aaj ka Money Tip 🌿",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFFB45309)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Agar aap roz sirf ₹50 save karte hain, toh 1 saal mein ₹18,250 ban sakte hain. Choti choti bachat, bada fark laati hai. 💪",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                color = Color(0xFF78350F)
                            )
                        }
                    }
                }
            }

            // TAB 2: GOAL PROGRESS (🌳 Feature 9)
            if (activeSubTab == "Goal Progress") {
                item {
                    Text(
                        text = "🌳 Goal Progress & Completion Timelines",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = textDark
                    )
                }

                items(state.savingsGoals) { goal ->
                    val pct = if (goal.targetAmount > 0) ((goal.currentSaved / goal.targetAmount) * 100).toInt() else 0
                    val remaining = (goal.targetAmount - goal.currentSaved).coerceAtLeast(0.0)
                    val monthsLeft = if (goal.monthlyTarget > 0) (remaining / goal.monthlyTarget).toInt() else 12

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
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = goal.emoji, fontSize = 24.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = goal.title,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = textDark
                                        )
                                        Text(
                                            text = "Target: ₹${String.format("%,.0f", goal.targetAmount)} • Target Date: ${goal.targetDateString}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = textMuted,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFEEF2FF)
                                ) {
                                    Text(
                                        text = "$pct% Saved",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color(0xFF4F46E5),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            LinearProgressIndicator(
                                progress = { (pct / 100f).coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = Color(0xFF4F46E5),
                                trackColor = Color(0xFFE2E8F0)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            GoalDetailGridRow("Saved Amount:", "₹${String.format("%,.0f", goal.currentSaved)}")
                            GoalDetailGridRow("Remaining Balance:", "₹${String.format("%,.0f", remaining)}")
                            GoalDetailGridRow("Required Monthly Save:", "₹${String.format("%,.0f", goal.monthlyTarget)} / mo")
                            GoalDetailGridRow("Estimated Completion:", "$monthsLeft Months (${goal.targetDateString})")
                        }
                    }
                }
            }

            // TAB 3: EXPENSE-TO-SAVING SUGGESTIONS (🧾 Feature 6)
            if (activeSubTab == "Expense Cut") {
                item {
                    Text(
                        text = "🧾 Expense-to-Saving AI Analysis",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = textDark
                    )
                    Text(
                        text = "AI spending habits analyze karke bata raha hai ki kahan kitne paise bacha sakte hain:",
                        style = MaterialTheme.typography.bodySmall,
                        color = textMuted,
                        fontSize = 11.sp
                    )
                }

                item {
                    ExpenseSavingCard(
                        categoryName = "Food & Dining Delivery",
                        iconEmoji = "🍲",
                        unnecessarySpend = "₹1,800 spent this month on online food",
                        potentialSavings = "Save ₹800 / Month",
                        completionBoostNote = "⚡ Home Renovation goal 2 Months Pehle (Nov 2027) complete hoga!",
                        accentColor = Color(0xFF16A34A)
                    )
                }

                item {
                    ExpenseSavingCard(
                        categoryName = "Unused App Subscriptions",
                        iconEmoji = "⚡",
                        unnecessarySpend = "2 inactive streaming services",
                        potentialSavings = "Save ₹500 / Month",
                        completionBoostNote = "⚡ Vacation Goal 1.5 Months pehle complete hoga!",
                        accentColor = Color(0xFF2563EB)
                    )
                }

                item {
                    ExpenseSavingCard(
                        categoryName = "Impulse Online Shopping",
                        iconEmoji = "🛒",
                        unnecessarySpend = "₹2,500 non-essential clothes & gadgets",
                        potentialSavings = "Save ₹1,000 / Month",
                        completionBoostNote = "🔥 Emergency Fund Goal 4 Months pehle complete hoga!",
                        accentColor = Color(0xFFD97706)
                    )
                }
            }

            // TAB 4: SAVINGS CHALLENGES (🏆 Feature 8)
            if (activeSubTab == "Challenges") {
                item {
                    Text(
                        text = "🏆 Interactive Savings Challenges",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = textDark
                    )
                }

                items(challengesList) { challenge ->
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
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = challenge.iconEmoji, fontSize = 24.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = challenge.title,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = textDark
                                        )
                                        Text(
                                            text = "Target: ₹${String.format("%,.0f", challenge.targetAmount)} • Duration: ${challenge.durationText}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = textMuted,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                Button(
                                    onClick = {
                                        challengesList = challengesList.map {
                                            if (it.id == challenge.id) it.copy(isJoined = !it.isJoined) else it
                                        }
                                        Toast.makeText(context, if (!challenge.isJoined) "Joined ${challenge.title}! 🎉" else "Left challenge", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (challenge.isJoined) Color(0xFFDCFCE7) else Color(0xFF4F46E5)
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                        text = if (challenge.isJoined) "Joined 🟢" else "Join Challenge 🎯",
                                        color = if (challenge.isJoined) Color(0xFF15803D) else Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            if (challenge.isJoined && challenge.targetAmount > 0) {
                                Spacer(modifier = Modifier.height(10.dp))
                                val progressPct = ((challenge.currentSaved / challenge.targetAmount) * 100).toInt()
                                LinearProgressIndicator(
                                    progress = { (progressPct / 100f).coerceIn(0f, 1f) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = Color(0xFF16A34A)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "$progressPct% Completed (₹${challenge.currentSaved.toInt()} / ₹${challenge.targetAmount.toInt()})",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF166534),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }

            // TAB 5: WHAT-IF PLANNER (🔄 Feature 10)
            if (activeSubTab == "What-If") {
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "🔄 What-If Interactive Goal Timeline Planner",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = textDark
                            )
                            Text(
                                text = "Monthly saving amount change karke dekhein goal dates kaise fast hoti hain:",
                                style = MaterialTheme.typography.bodySmall,
                                color = textMuted,
                                fontSize = 11.sp
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            OutlinedTextField(
                                value = whatIfGoalTarget.toInt().toString(),
                                onValueChange = { whatIfGoalTarget = it.toDoubleOrNull() ?: 100000.0 },
                                label = { Text("Goal Target Amount (₹)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                item {
                    val monthsOptA = (whatIfGoalTarget / 2000.0).toInt()
                    val monthsOptB = (whatIfGoalTarget / 3000.0).toInt()
                    val monthsOptC = (whatIfGoalTarget / 5000.0).toInt()

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        WhatIfOptionCard(
                            savingText = "₹2,000 / Month",
                            monthsLeft = monthsOptA,
                            boostText = "Base Plan",
                            accentColor = Color(0xFF3B82F6)
                        )

                        WhatIfOptionCard(
                            savingText = "₹3,000 / Month",
                            monthsLeft = monthsOptB,
                            boostText = "⚡ ${monthsOptA - monthsOptB} Months Faster!",
                            accentColor = Color(0xFF8B5CF6)
                        )

                        WhatIfOptionCard(
                            savingText = "₹5,000 / Month",
                            monthsLeft = monthsOptC,
                            boostText = "🔥 ${monthsOptA - monthsOptC} Months Super Fast!",
                            accentColor = Color(0xFF10B981)
                        )
                    }
                }
            }

            // TAB 6: ASK AI
            if (activeSubTab == "Ask AI") {
                item {
                    Text(
                        text = "💬 Ask AI Money Coach",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = textDark
                    )
                }

                items(aiChatHistory) { (isUser, text) ->
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
                    ) {
                        Surface(
                            shape = RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = if (isUser) 16.dp else 4.dp,
                                bottomEnd = if (isUser) 4.dp else 16.dp
                            ),
                            color = if (isUser) Color(0xFF4F46E5) else Color.White,
                            shadowElevation = 1.dp,
                            modifier = Modifier.widthIn(max = 280.dp)
                        ) {
                            Text(
                                text = text,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isUser) Color.White else textDark,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = aiQueryInput,
                            onValueChange = { aiQueryInput = it },
                            placeholder = { Text("Ask AI Coach...") },
                            singleLine = true,
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                if (aiQueryInput.isNotBlank()) {
                                    val q = aiQueryInput
                                    aiQueryInput = ""
                                    val ans = "Sahi sawal! Aapke current ₹${String.format("%,.0f", state.monthlyBudget)} budget ke hisaab se, $q ka best solution hai ki aap ₹1,500/month SIP start karein."
                                    aiChatHistory = aiChatHistory + Pair(true, q) + Pair(false, ans)
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF4F46E5))
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }

    // PRO PLUS ₹50/Month Upgrade Modal
    if (showProPlusModal) {
        AlertDialog(
            onDismissRequest = { showProPlusModal = false },
            title = {
                Text(
                    text = "👑 PRO PLUS Plan Needed (₹50 / Month)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Text(
                    text = "AI Money Advisor, Expense Cut Insights, What-If Planner & Savings Challenges PRO PLUS (₹50/Month) Plan ke under aate hain.\n\nPhonePe 9050884894 par ₹50 transfer karke UTR submit karein aur instant unlock paayein! 🚀",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showProPlusModal = false
                        onOpenProUpgradeClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5))
                ) {
                    Text("Upgrade to PRO PLUS (₹50/mo) 🚀")
                }
            },
            dismissButton = {
                TextButton(onClick = { showProPlusModal = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun ExpenseSavingCard(
    categoryName: String,
    iconEmoji: String,
    unnecessarySpend: String,
    potentialSavings: String,
    completionBoostNote: String,
    accentColor: Color
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = iconEmoji, fontSize = 20.sp)
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = categoryName,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = unnecessarySpend,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF64748B),
                        fontSize = 11.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = accentColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = potentialSavings,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = accentColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFF0FDF4)
            ) {
                Text(
                    text = completionBoostNote,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF15803D),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun GoalDetailGridRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B), fontSize = 11.sp)
        Text(text = value, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Color(0xFF0F172A), fontSize = 11.sp)
    }
}

@Composable
private fun WhatIfOptionCard(
    savingText: String,
    monthsLeft: Int,
    boostText: String,
    accentColor: Color
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, accentColor.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = savingText,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = accentColor
                )
                Text(
                    text = "Estimated Time: $monthsLeft Months",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF64748B),
                    fontSize = 11.sp
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = accentColor.copy(alpha = 0.12f)
            ) {
                Text(
                    text = boostText,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = accentColor,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    fontSize = 11.sp
                )
            }
        }
    }
}
