package com.pockethome.app.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pockethome.app.ui.viewmodel.GrihaUiState

@Composable
fun SettingsScreen(
    state: GrihaUiState,
    onBackClick: () -> Unit,
    onOpenProfileClick: () -> Unit,
    onOpenFamilySharingClick: () -> Unit,
    onOpenPrivacySecurityClick: () -> Unit,
    onOpenExportBackupClick: () -> Unit,
    onToggleAutoReminders: (Boolean) -> Unit,
    onOpenReportsClick: () -> Unit = {},
    onOpenBudgetClick: () -> Unit = {},
    onOpenRecurringClick: () -> Unit = {},
    onOpenSavingsGoalsClick: () -> Unit = {},
    onToggleOfflineMode: (Boolean) -> Unit = {},
    onOpenPersonalizationClick: () -> Unit = {},
    onOpenReceiptsClick: () -> Unit = {},
    onOpenCalendarClick: () -> Unit = {},
    onOpenEmiTrackerClick: () -> Unit = {},
    onOpenProUpgradeClick: () -> Unit = {},
    onOpenOverviewClick: () -> Unit = {},
    onOpenNotificationsClick: () -> Unit = {},
    onOpenGamificationClick: () -> Unit = {},
    onOpenAxioSmsSyncClick: () -> Unit = {}
) {
    val context = LocalContext.current
    var showAboutDialog by remember { mutableStateOf(false) }
    var showCurrencyDialog by remember { mutableStateOf(false) }
    var selectedCurrency by remember { mutableStateOf("₹ INR (Indian Rupee)") }

    val textDark = Color(0xFF0F172A)
    val textMuted = Color(0xFF64748B)

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
                modifier = Modifier.testTag("settings_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = textDark
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Settings & Features",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = textDark
            )
            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(48.dp))
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. Profile & Account Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenProfileClick() }
                        .testTag("settings_profile_card")
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
                                .clip(CircleShape)
                                .background(Color(0xFFEEF2FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = state.profile.name.take(1).uppercase(),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4F46E5)
                                )
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = state.profile.name,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = textDark
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (state.profile.email.isNotBlank()) state.profile.email else "Tap to connect Google / Email Sync",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (state.profile.email.isNotBlank()) Color(0xFF16A34A) else Color(0xFF4F46E5)
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = textMuted
                        )
                    }
                }
            }

            // PRO Upgrade Banner Card
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (state.profile.isProUser) Color(0xFF0F172A) else Color(0xFF1E1B4B)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenProUpgradeClick() }
                        .testTag("settings_pro_banner")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFEF3C7)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("👑", fontSize = 22.sp)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (state.profile.isProUser) "Pocket Home PRO Active" else "Upgrade to PRO (₹20/mo)",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (state.profile.isProUser) Color(0xFF16A34A) else Color(0xFFF59E0B)
                                ) {
                                    Text(
                                        text = if (state.profile.isProUser) "PRO" else "₹20/mo",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color.White),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                        fontSize = 10.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (state.profile.isProUser) "Active until ${state.profile.proExpiryDate} • All 15+ PRO features unlocked" else "Unlock EMI Tracker, Family Sync, Cloud Sync & PDF Export",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFCBD5E1),
                                fontSize = 11.sp
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            }

            // Section 1: Security & Cloud Management
            item {
                Text(
                    text = "Security & Cloud 🔒",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = textMuted,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
            }

            // Privacy & Security Card
            item {
                SettingsActionTile(
                    title = "Privacy & Security 🔐",
                    subtitle = "4-Digit PIN Lock, Biometrics & Session controls",
                    icon = Icons.Default.Lock,
                    iconTint = Color(0xFF4F46E5),
                    onClick = onOpenPrivacySecurityClick,
                    badge = if (state.profile.isPinEnabled) "PIN Active" else null,
                    badgeColor = Color(0xFF16A34A),
                    testTag = "settings_privacy_btn"
                )
            }

            // Export & Backup Card
            item {
                SettingsActionTile(
                    title = "Export & Backup Center 📤",
                    subtitle = "Download PDF statement, Excel sheets, CSV or Share",
                    icon = Icons.Default.Share,
                    iconTint = Color(0xFF2563EB),
                    onClick = onOpenExportBackupClick,
                    testTag = "settings_export_btn"
                )
            }

            // Section 2: Family & Budget Tools
            item {
                Text(
                    text = "Household & Tools 👨‍👩‍👧",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = textMuted,
                    modifier = Modifier.padding(start = 4.dp, top = 6.dp)
                )
            }

            // Family Sharing
            item {
                SettingsActionTile(
                    title = "Family Sharing 👨‍👩‍👧",
                    subtitle = "Manage family members, shared expenses & permissions",
                    icon = Icons.Default.FamilyRestroom,
                    iconTint = Color(0xFF059669),
                    onClick = onOpenFamilySharingClick,
                    testTag = "settings_family_btn"
                )
            }

            // Recurring Rules
            item {
                SettingsActionTile(
                    title = "Recurring Transactions 🔄",
                    subtitle = "Salary, Rent, EMI, Subscriptions & Insurance rules",
                    icon = Icons.Default.Repeat,
                    iconTint = Color(0xFFE11D48),
                    onClick = onOpenRecurringClick,
                    testTag = "settings_recurring_btn"
                )
            }

            // Category Budgets
            item {
                SettingsActionTile(
                    title = "Monthly Category Budgets 🎯",
                    subtitle = "Set expense limits for Groceries, Bills, Dining & Fuel",
                    icon = Icons.Default.PieChart,
                    iconTint = Color(0xFF7C3AED),
                    onClick = onOpenBudgetClick,
                    testTag = "settings_budgets_btn"
                )
            }

            // Savings Goals
            item {
                SettingsActionTile(
                    title = "Savings Goals 🎯",
                    subtitle = "Phone, Bike, Vacation, Home down payments & tracking",
                    icon = Icons.Default.AccountBalanceWallet,
                    iconTint = Color(0xFF10B981),
                    onClick = onOpenSavingsGoalsClick,
                    testTag = "settings_goals_btn"
                )
            }

            // EMI & Loan Tracker
            item {
                SettingsActionTile(
                    title = "EMI & Loan Tracker 🧮",
                    subtitle = "Home, Car, Personal loans, interest & repayment progress",
                    icon = Icons.Default.Calculate,
                    iconTint = Color(0xFF6366F1),
                    onClick = onOpenEmiTrackerClick,
                    badge = "${state.emiLoans.size} Loans",
                    badgeColor = Color(0xFF6366F1),
                    testTag = "settings_emi_btn"
                )
            }

            // Reports & Visual Charts
            item {
                SettingsActionTile(
                    title = "Reports & Analytics 📊",
                    subtitle = "Donut breakdown charts and spending percentages",
                    icon = Icons.Default.Analytics,
                    iconTint = Color(0xFFD97706),
                    onClick = onOpenReportsClick,
                    testTag = "settings_reports_btn"
                )
            }

            // Receipts & OCR Vault
            item {
                SettingsActionTile(
                    title = "Receipts & OCR Vault 📸",
                    subtitle = "Scan retail bills, extract amounts & view receipt gallery",
                    icon = Icons.Default.DocumentScanner,
                    iconTint = Color(0xFF0284C7),
                    onClick = onOpenReceiptsClick,
                    testTag = "settings_receipts_btn"
                )
            }

            // Financial Calendar View
            item {
                SettingsActionTile(
                    title = "Financial Calendar 📅",
                    subtitle = "Daily expense breakdown, bill due alerts & income dates",
                    icon = Icons.Default.CalendarMonth,
                    iconTint = Color(0xFF16A34A),
                    onClick = onOpenCalendarClick,
                    testTag = "settings_calendar_btn"
                )
            }

            // Advanced Financial Overview
            item {
                SettingsActionTile(
                    title = "Advanced Financial Overview 📊",
                    subtitle = "Net balance, total assets vs liabilities & 50-30-20 rule",
                    icon = Icons.Default.TrendingUp,
                    iconTint = Color(0xFF059669),
                    onClick = onOpenOverviewClick,
                    badge = "Net Balance",
                    badgeColor = Color(0xFF059669),
                    testTag = "settings_overview_btn"
                )
            }

            // Smart Notifications
            item {
                val unread = state.notifications.count { !it.isRead }
                SettingsActionTile(
                    title = "Smart Notifications 🔔",
                    subtitle = "Bill dues, budget alerts, overspending & summaries",
                    icon = Icons.Default.Notifications,
                    iconTint = Color(0xFFD97706),
                    onClick = onOpenNotificationsClick,
                    badge = if (unread > 0) "$unread New" else null,
                    badgeColor = Color(0xFFEF4444),
                    testTag = "settings_notifications_btn"
                )
            }

            // Financial Health & Trophies
            item {
                SettingsActionTile(
                    title = "Financial Health & Trophies 🏆",
                    subtitle = "Score ${state.gamification.financialScore}/100 • ${state.gamification.streakDays}-Day Streak • Badges & Challenges",
                    icon = Icons.Default.EmojiEvents,
                    iconTint = Color(0xFFF59E0B),
                    onClick = onOpenGamificationClick,
                    badge = "${state.gamification.financialScore}/100",
                    badgeColor = Color(0xFFF59E0B),
                    testTag = "settings_gamification_btn"
                )
            }

            // SMS Auto-Sync & Axio Plan
            item {
                SettingsActionTile(
                    title = "SMS Auto-Sync & Axio Plan ⚡",
                    subtitle = "Scan bank SMS, auto-sync transactions & learn Axio features",
                    icon = Icons.Default.Message,
                    iconTint = Color(0xFF4F46E5),
                    onClick = onOpenAxioSmsSyncClick,
                    badge = "Auto-Sync",
                    badgeColor = Color(0xFF4F46E5),
                    testTag = "settings_axio_sms_btn"
                )
            }

            // Section 3: Preferences & App Info
            item {
                Text(
                    text = "Preferences & App ⚙️",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = textMuted,
                    modifier = Modifier.padding(start = 4.dp, top = 6.dp)
                )
            }

            // Auto Reminders Switch
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFEF3C7)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = "Reminders",
                                    tint = Color(0xFFD97706),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Bill Due Reminders",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = textDark
                                )
                                Text(
                                    text = "Automatic alerts before due dates",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = textMuted
                                )
                            }
                        }

                        Switch(
                            checked = state.profile.autoRemindersEnabled,
                            onCheckedChange = { onToggleAutoReminders(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF4F46E5)
                            ),
                            modifier = Modifier.testTag("settings_reminders_switch")
                        )
                    }
                }
            }

            // Offline Mode & Local Storage Switch
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE0E7FF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudDone,
                                    contentDescription = "Offline Mode",
                                    tint = Color(0xFF4F46E5),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Offline Mode & Local Cache 📱",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = textDark
                                )
                                Text(
                                    text = if (state.isOfflineMode) "Working offline. Syncs when connected." else "Full offline caching & automatic cloud sync",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (state.isOfflineMode) Color(0xFFD97706) else textMuted
                                )
                            }
                        }

                        Switch(
                            checked = state.isOfflineMode,
                            onCheckedChange = { onToggleOfflineMode(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF4F46E5)
                            ),
                            modifier = Modifier.testTag("settings_offline_switch")
                        )
                    }
                }
            }

            // UI & Personalization
            item {
                SettingsActionTile(
                    title = "UI & Personalization 🎨",
                    subtitle = "Dark mode, themes, currencies, languages & custom widgets",
                    icon = Icons.Default.Palette,
                    iconTint = Color(0xFF8B5CF6),
                    onClick = onOpenPersonalizationClick,
                    badge = state.profile.themeMode,
                    badgeColor = Color(0xFF8B5CF6),
                    testTag = "settings_personalization_btn"
                )
            }

            // Pocket Home PRO Upgrade / Manage
            item {
                SettingsActionTile(
                    title = "Pocket Home PRO 👑",
                    subtitle = if (state.profile.isProUser) "Active PRO Member • All features unlocked" else "Upgrade for ₹20/mo • Unlimited budgets, EMI & cloud sync",
                    icon = Icons.Default.WorkspacePremium,
                    iconTint = Color(0xFFD97706),
                    onClick = onOpenProUpgradeClick,
                    badge = if (state.profile.isProUser) "ACTIVE" else "₹20/mo",
                    badgeColor = if (state.profile.isProUser) Color(0xFF16A34A) else Color(0xFFD97706),
                    testTag = "settings_pro_upgrade_btn"
                )
            }

            // Currency Selector
            item {
                SettingsActionTile(
                    title = "Currency & Format 🇮🇳",
                    subtitle = selectedCurrency,
                    icon = Icons.Default.CurrencyRupee,
                    iconTint = Color(0xFF0284C7),
                    onClick = { showCurrencyDialog = true },
                    testTag = "settings_currency_btn"
                )
            }

            // About Pocket Home
            item {
                SettingsActionTile(
                    title = "About Pocket Home ℹ️",
                    subtitle = "Version 1.2.0 • Offline Persistence & Cloud Ready",
                    icon = Icons.Default.Info,
                    iconTint = Color(0xFF64748B),
                    onClick = { showAboutDialog = true },
                    testTag = "settings_about_btn"
                )
            }

            // Bottom space ensuring navigation bar never overlaps content
            item {
                Spacer(modifier = Modifier.height(110.dp))
            }
        }
    }

    // Currency Picker Dialog
    if (showCurrencyDialog) {
        AlertDialog(
            onDismissRequest = { showCurrencyDialog = false },
            title = { Text("Select Default Currency", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("₹ INR (Indian Rupee)", "$ USD (US Dollar)", "€ EUR (Euro)", "£ GBP (British Pound)").forEach { curr ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (selectedCurrency == curr) Color(0xFFEEF2FF) else Color.White,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedCurrency = curr
                                    showCurrencyDialog = false
                                }
                        ) {
                            Text(
                                text = curr,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (selectedCurrency == curr) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedCurrency == curr) Color(0xFF4F46E5) else textDark
                                ),
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCurrencyDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // About Pocket Home Modal
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("Pocket Home Budget", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Version 1.2.0 (Build 2026)", fontWeight = FontWeight.SemiBold, color = textDark)
                    Text("Complete offline-first household finance manager with real-time Firebase Cloud sync, 4-digit PIN lock, family expense sharing, and multi-format PDF/Excel reporting.", style = MaterialTheme.typography.bodySmall, color = textMuted)
                }
            },
            confirmButton = {
                Button(
                    onClick = { showAboutDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5))
                ) {
                    Text("Got It")
                }
            }
        )
    }
}

@Composable
fun SettingsActionTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    onClick: () -> Unit,
    badge: String? = null,
    badgeColor: Color = Color(0xFF16A34A),
    testTag: String = ""
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        ),
                        color = Color(0xFF0F172A)
                    )
                    badge?.let {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = badgeColor.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                ),
                                color = badgeColor,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF64748B)
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color(0xFF94A3B8)
            )
        }
    }
}
