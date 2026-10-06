package com.pockethome.app.ui.navigation

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pockethome.app.data.model.TransactionType
import com.pockethome.app.ui.components.AppLockOverlay
import com.pockethome.app.ui.screens.AddExpenseScreen
import com.pockethome.app.ui.screens.AiMoneyAdvisorScreen
import com.pockethome.app.ui.screens.AuthScreen
import com.pockethome.app.ui.screens.AxioSmsSyncScreen
import com.pockethome.app.ui.screens.BillsScreen
import com.pockethome.app.ui.screens.BudgetScreen
import com.pockethome.app.ui.screens.CalendarScreen
import com.pockethome.app.ui.screens.DashboardScreen
import com.pockethome.app.ui.screens.EmiLoanTrackerScreen
import com.pockethome.app.ui.screens.ExportBackupScreen
import com.pockethome.app.ui.screens.FamilySharingScreen
import com.pockethome.app.ui.screens.FinancialOverviewScreen
import com.pockethome.app.ui.screens.GamificationScreen
import com.pockethome.app.ui.screens.PersonalizationScreen
import com.pockethome.app.ui.screens.PrivacySecurityScreen
import com.pockethome.app.ui.screens.ProUpgradeScreen
import com.pockethome.app.ui.screens.ReceiptsScreen
import com.pockethome.app.ui.screens.RecurringTransactionsScreen
import com.pockethome.app.ui.screens.ReportsScreen
import com.pockethome.app.ui.screens.SavingsGoalsScreen
import com.pockethome.app.ui.screens.SettingsScreen
import com.pockethome.app.ui.screens.SmartNotificationsScreen
import com.pockethome.app.ui.screens.TransactionsScreen
import com.pockethome.app.ui.screens.UserProfileScreen
import com.pockethome.app.ui.screens.WelcomeAuthScreen
import com.pockethome.app.ui.viewmodel.GrihaBudgetViewModel
import com.pockethome.app.ui.viewmodel.GrihaUiState

enum class BottomTab(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    HOME("home", "Home", Icons.Filled.Home, Icons.Outlined.Home, "nav_home"),
    TRANSACTIONS("transactions", "Transactions", Icons.Filled.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet, "nav_transactions"),
    AI_ADVISOR("ai_advisor", "AI Coach", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome, "nav_ai_advisor"),
    BUDGET("budget", "Budget", Icons.Filled.PieChart, Icons.Outlined.PieChart, "nav_budget"),
    MORE("more", "More", Icons.Filled.MoreHoriz, Icons.Outlined.MoreHoriz, "nav_more")
}

enum class OverlayScreen {
    NONE,
    ADD_EXPENSE,
    ADD_INCOME,
    ADD_BILL,
    PROFILE_SYNC,
    REPORTS,
    FAMILY_SHARING,
    PRIVACY_SECURITY,
    EXPORT_BACKUP,
    RECURRING_TRANSACTIONS,
    SAVINGS_GOALS,
    PERSONALIZATION,
    RECEIPTS,
    CALENDAR,
    EMI_TRACKER,
    PRO_UPGRADE,
    FINANCIAL_OVERVIEW,
    SMART_NOTIFICATIONS,
    GAMIFICATION,
    AXIO_SMS_SYNC
}

@Composable
fun MainNavGraph(
    viewModel: GrihaBudgetViewModel,
    uiState: GrihaUiState
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("pocket_home_prefs", Context.MODE_PRIVATE) }
    var hasCompletedAuth by remember {
        mutableStateOf(prefs.getBoolean("has_completed_auth", false))
    }

    if (!hasCompletedAuth) {
        WelcomeAuthScreen(
            state = uiState,
            onSignInWithEmail = { email, pass, onResult ->
                viewModel.signInWithEmail(email, pass) { success, err ->
                    if (success) {
                        prefs.edit().putBoolean("has_completed_auth", true).apply()
                        hasCompletedAuth = true
                    }
                    onResult(success, err)
                }
            },
            onSignUpWithEmail = { email, pass, name, role, budget, onResult ->
                viewModel.signUpWithEmail(email, pass, name, role, budget) { success, err ->
                    if (success) {
                        prefs.edit().putBoolean("has_completed_auth", true).apply()
                        hasCompletedAuth = true
                    }
                    onResult(success, err)
                }
            },
            onQuickStart = { name, email, onResult ->
                viewModel.signInWithGoogle(name, email) { success, err ->
                    if (success) {
                        prefs.edit().putBoolean("has_completed_auth", true).apply()
                        hasCompletedAuth = true
                    }
                    onResult(success, err)
                }
            },
            onAuthSuccess = {
                prefs.edit().putBoolean("has_completed_auth", true).apply()
                hasCompletedAuth = true
            }
        )
        return
    }

    var activeTab by remember { mutableStateOf(BottomTab.HOME) }
    var activeOverlay by remember { mutableStateOf(OverlayScreen.NONE) }
    var isAppUnlocked by remember { mutableStateOf(!uiState.profile.isPinEnabled) }

    // Intercept back button when overlay or non-home tab is active
    if (activeOverlay != OverlayScreen.NONE) {
        BackHandler {
            activeOverlay = OverlayScreen.NONE
        }
    } else if (activeTab != BottomTab.HOME) {
        BackHandler {
            activeTab = BottomTab.HOME
        }
    }

    Scaffold(
        bottomBar = {
            if (activeOverlay == OverlayScreen.NONE) {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp,
                    modifier = Modifier.navigationBarsPadding()
                ) {
                    BottomTab.entries.forEach { tab ->
                        val isSelected = activeTab == tab
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { activeTab = tab },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                    contentDescription = tab.title,
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = tab.title,
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF4F46E5),
                                selectedTextColor = Color(0xFF4F46E5),
                                indicatorColor = Color(0xFFEEF2FF),
                                unselectedIconColor = Color(0xFF64748B),
                                unselectedTextColor = Color(0xFF64748B)
                            ),
                            modifier = Modifier.testTag(tab.testTag)
                        )
                    }
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        if (activeOverlay != OverlayScreen.NONE) {
            when (activeOverlay) {
                OverlayScreen.ADD_EXPENSE -> {
                    AddExpenseScreen(
                        initialType = TransactionType.EXPENSE,
                        onCloseClick = { activeOverlay = OverlayScreen.NONE },
                        onSaveClick = { amount, category, type, note, paymentMethod, date, isRecurring, receiptUri ->
                            viewModel.addTransaction(category, amount, category, type, note, paymentMethod, date, isRecurring, receiptUri)
                            activeOverlay = OverlayScreen.NONE
                        }
                    )
                }
                OverlayScreen.ADD_INCOME -> {
                    AddExpenseScreen(
                        initialType = TransactionType.INCOME,
                        onCloseClick = { activeOverlay = OverlayScreen.NONE },
                        onSaveClick = { amount, category, type, note, paymentMethod, date, isRecurring, receiptUri ->
                            viewModel.addTransaction("Income - $category", amount, category, type, note, paymentMethod, date, isRecurring, receiptUri)
                            activeOverlay = OverlayScreen.NONE
                        }
                    )
                }
                OverlayScreen.PROFILE_SYNC -> {
                    UserProfileScreen(
                        state = uiState,
                        onBackClick = { activeOverlay = OverlayScreen.NONE },
                        onUpdateName = { newName -> viewModel.updateProfileName(newName) },
                        onSignOut = {
                            prefs.edit().putBoolean("has_completed_auth", false).apply()
                            hasCompletedAuth = false
                            activeOverlay = OverlayScreen.NONE
                            viewModel.signOut()
                        }
                    )
                }
                OverlayScreen.REPORTS -> {
                    ReportsScreen(
                        state = uiState,
                        onBackClick = { activeOverlay = OverlayScreen.NONE },
                        onExportClick = { activeOverlay = OverlayScreen.EXPORT_BACKUP }
                    )
                }
                OverlayScreen.FAMILY_SHARING -> {
                    FamilySharingScreen(
                        state = uiState,
                        onBackClick = { activeOverlay = OverlayScreen.NONE }
                    )
                }
                OverlayScreen.PRIVACY_SECURITY -> {
                    PrivacySecurityScreen(
                        state = uiState,
                        onBackClick = { activeOverlay = OverlayScreen.NONE },
                        onUpdatePinLock = { enabled, pin -> viewModel.updatePinLock(enabled, pin) },
                        onUpdateBiometricLock = { enabled -> viewModel.updateBiometricLock(enabled) },
                        onLogoutAllDevices = { viewModel.logoutAllDevices() },
                        onDeleteFinancialData = { cb -> viewModel.deleteFinancialData(cb) },
                        onDeleteAccount = { cb -> viewModel.deleteAccount(cb) },
                        onOpenAuthScreen = { activeOverlay = OverlayScreen.PROFILE_SYNC }
                    )
                }
                OverlayScreen.EXPORT_BACKUP -> {
                    ExportBackupScreen(
                        state = uiState,
                        onBackClick = { activeOverlay = OverlayScreen.NONE }
                    )
                }
                OverlayScreen.RECURRING_TRANSACTIONS -> {
                    RecurringTransactionsScreen(
                        state = uiState,
                        onBackClick = { activeOverlay = OverlayScreen.NONE },
                        onAddRecurringClick = { title, amount, category, type, frequency, paymentMethod ->
                            viewModel.addTransaction(
                                title = title,
                                amount = amount,
                                category = category,
                                type = type,
                                note = "Auto-Recurring Schedule ($frequency)",
                                paymentMethod = paymentMethod,
                                dateString = java.text.SimpleDateFormat("d MMM yyyy", java.util.Locale.ENGLISH).format(java.util.Date()),
                                isRecurring = true
                            )
                        },
                        onDeleteTransaction = { id -> viewModel.deleteTransaction(id) }
                    )
                }
                OverlayScreen.SAVINGS_GOALS -> {
                    SavingsGoalsScreen(
                        state = uiState,
                        onBackClick = { activeOverlay = OverlayScreen.NONE },
                        onAddGoalClick = { goal ->
                            viewModel.addSavingsGoal(goal)
                        },
                        onDepositClick = { goalId, amount ->
                            viewModel.addMoneyToSavingsGoal(goalId, amount)
                        },
                        onDeleteGoalClick = { goalId ->
                            viewModel.deleteSavingsGoal(goalId)
                        }
                    )
                }
                OverlayScreen.PERSONALIZATION -> {
                    PersonalizationScreen(
                        state = uiState,
                        onBackClick = { activeOverlay = OverlayScreen.NONE },
                        onUpdateTheme = { mode, hex -> viewModel.updateTheme(mode, hex) },
                        onUpdateCurrency = { code, name -> viewModel.updateCurrency(code, name) },
                        onUpdateLanguage = { lang -> viewModel.updateLanguage(lang) },
                        onUpdateDashboardPreferences = { daily, goals, insights, donut ->
                            viewModel.updateDashboardPreferences(daily, goals, insights, donut)
                        },
                        onUpdateCustomProfile = { name, emoji, role, budget ->
                            viewModel.updateCustomProfile(name, emoji, role, budget)
                        }
                    )
                }
                OverlayScreen.RECEIPTS -> {
                    ReceiptsScreen(
                        state = uiState,
                        onBackClick = { activeOverlay = OverlayScreen.NONE },
                        onAddTransactionWithReceipt = { title, amount, category, paymentMethod, dateStr, shopName, receiptUri ->
                            viewModel.addTransaction(
                                title = title,
                                amount = amount,
                                category = category,
                                type = TransactionType.EXPENSE,
                                note = "OCR Scanned Receipt ($shopName)",
                                paymentMethod = paymentMethod,
                                dateString = dateStr,
                                receiptUri = receiptUri,
                                shopName = shopName,
                                ocrScanned = true
                            )
                        },
                        onDeleteTransaction = { id -> viewModel.deleteTransaction(id) }
                    )
                }
                OverlayScreen.CALENDAR -> {
                    CalendarScreen(
                        state = uiState,
                        onBackClick = { activeOverlay = OverlayScreen.NONE },
                        onAddExpenseClick = { activeOverlay = OverlayScreen.ADD_EXPENSE }
                    )
                }
                OverlayScreen.EMI_TRACKER -> {
                    EmiLoanTrackerScreen(
                        state = uiState,
                        onBackClick = { activeOverlay = OverlayScreen.NONE },
                        onAddEmiLoan = { title, loanType, lender, loanAmount, emiAmount, interestRate, dueDay, totalInstallments, colorHex ->
                            viewModel.addEmiLoan(title, loanType, lender, loanAmount, emiAmount, interestRate, dueDay, totalInstallments, colorHex)
                        },
                        onPayEmiInstallment = { loanId ->
                            viewModel.payEmiInstallment(loanId)
                        },
                        onToggleEmiReminder = { loanId, enabled ->
                            viewModel.toggleEmiReminder(loanId, enabled)
                        },
                        onDeleteEmiLoan = { loanId ->
                            viewModel.deleteEmiLoan(loanId)
                        }
                    )
                }
                OverlayScreen.PRO_UPGRADE -> {
                    ProUpgradeScreen(
                        state = uiState,
                        onBackClick = { activeOverlay = OverlayScreen.NONE },
                        onSubmitUtrRequest = { utr, ref, plan, amount, cb ->
                            viewModel.submitUtrPaymentRequest(utr, ref, plan, amount, cb)
                        },
                        onApproveUtrRequest = { req, cb ->
                            viewModel.approveUtrPaymentRequest(req, cb)
                        },
                        onRejectUtrRequest = { reqId, userId, cb ->
                            viewModel.rejectUtrPaymentRequest(reqId, userId, cb)
                        }
                    )
                }
                OverlayScreen.FINANCIAL_OVERVIEW -> {
                    FinancialOverviewScreen(
                        state = uiState,
                        onBackClick = { activeOverlay = OverlayScreen.NONE },
                        onOpenBudgetClick = { activeTab = BottomTab.BUDGET },
                        onOpenEmiClick = { activeOverlay = OverlayScreen.EMI_TRACKER }
                    )
                }
                OverlayScreen.SMART_NOTIFICATIONS -> {
                    SmartNotificationsScreen(
                        state = uiState,
                        onBackClick = { activeOverlay = OverlayScreen.NONE },
                        onMarkAsRead = { id -> viewModel.markNotificationAsRead(id) },
                        onMarkAllAsRead = { viewModel.markAllNotificationsAsRead() },
                        onDeleteNotification = { id -> viewModel.deleteNotification(id) },
                        onTriggerTestNotification = { type -> viewModel.triggerSimulatedNotification(type) }
                    )
                }
                OverlayScreen.GAMIFICATION -> {
                    GamificationScreen(
                        state = uiState,
                        onBackClick = { activeOverlay = OverlayScreen.NONE },
                        onJoinChallenge = { id -> viewModel.joinChallenge(id) },
                        onLogNoSpendDay = { viewModel.logNoSpendDay() }
                    )
                }
                OverlayScreen.AXIO_SMS_SYNC -> {
                    AxioSmsSyncScreen(
                        state = uiState,
                        onBackClick = { activeOverlay = OverlayScreen.NONE },
                        onAddSyncedTransactions = { list ->
                            list.forEach { item ->
                                viewModel.addTransaction(item)
                            }
                        }
                    )
                }
                else -> {}
            }
        } else {
            Modifier.padding(innerPadding)
            when (activeTab) {
                BottomTab.HOME -> {
                    DashboardScreen(
                        state = uiState,
                        onAddExpenseClick = { activeOverlay = OverlayScreen.ADD_EXPENSE },
                        onAddBillClick = { activeOverlay = OverlayScreen.ADD_BILL },
                        onAddIncomeClick = { activeOverlay = OverlayScreen.ADD_INCOME },
                        onOpenProfileClick = { activeOverlay = OverlayScreen.PROFILE_SYNC },
                        onCategoryClick = { activeOverlay = OverlayScreen.REPORTS },
                        onToggleBillPaid = { id, paid -> viewModel.toggleBillPaid(id, paid) },
                        onViewAllTransactions = { activeTab = BottomTab.TRANSACTIONS },
                        onViewAllBills = { activeOverlay = OverlayScreen.RECURRING_TRANSACTIONS },
                        onOpenSavingsGoalsClick = { activeOverlay = OverlayScreen.SAVINGS_GOALS },
                        onOpenNotificationsClick = { activeOverlay = OverlayScreen.SMART_NOTIFICATIONS },
                        onOpenGamificationClick = { activeOverlay = OverlayScreen.GAMIFICATION },
                        onOpenOverviewClick = { activeOverlay = OverlayScreen.FINANCIAL_OVERVIEW },
                        onOpenAxioSmsSyncClick = { activeOverlay = OverlayScreen.AXIO_SMS_SYNC }
                    )
                }
                BottomTab.TRANSACTIONS -> {
                    TransactionsScreen(
                        state = uiState,
                        onBackClick = { activeTab = BottomTab.HOME },
                        onDeleteTransaction = { id -> viewModel.deleteTransaction(id) },
                        onDuplicateTransaction = { item -> viewModel.duplicateTransaction(item) }
                    )
                }
                BottomTab.AI_ADVISOR -> {
                    AiMoneyAdvisorScreen(
                        state = uiState,
                        onBackClick = { activeTab = BottomTab.HOME }
                    )
                }
                BottomTab.BUDGET -> {
                    BudgetScreen(
                        state = uiState,
                        onBackClick = { activeTab = BottomTab.HOME },
                        onUpdateMonthlyBudget = { b -> viewModel.updateMonthlyBudget(b) },
                        onUpdateCategoryBudget = { cat, lim -> viewModel.updateCategoryBudget(cat, lim) }
                    )
                }
                BottomTab.MORE -> {
                    SettingsScreen(
                        state = uiState,
                        onBackClick = { activeTab = BottomTab.HOME },
                        onOpenProfileClick = { activeOverlay = OverlayScreen.PROFILE_SYNC },
                        onOpenFamilySharingClick = { activeOverlay = OverlayScreen.FAMILY_SHARING },
                        onOpenPrivacySecurityClick = { activeOverlay = OverlayScreen.PRIVACY_SECURITY },
                        onOpenExportBackupClick = { activeOverlay = OverlayScreen.EXPORT_BACKUP },
                        onOpenReportsClick = { activeOverlay = OverlayScreen.REPORTS },
                        onOpenBudgetClick = { activeTab = BottomTab.BUDGET },
                        onOpenRecurringClick = { activeOverlay = OverlayScreen.RECURRING_TRANSACTIONS },
                        onOpenSavingsGoalsClick = { activeOverlay = OverlayScreen.SAVINGS_GOALS },
                        onOpenPersonalizationClick = { activeOverlay = OverlayScreen.PERSONALIZATION },
                        onOpenReceiptsClick = { activeOverlay = OverlayScreen.RECEIPTS },
                        onOpenCalendarClick = { activeOverlay = OverlayScreen.CALENDAR },
                        onOpenEmiTrackerClick = { activeOverlay = OverlayScreen.EMI_TRACKER },
                        onOpenProUpgradeClick = { activeOverlay = OverlayScreen.PRO_UPGRADE },
                        onOpenOverviewClick = { activeOverlay = OverlayScreen.FINANCIAL_OVERVIEW },
                        onOpenNotificationsClick = { activeOverlay = OverlayScreen.SMART_NOTIFICATIONS },
                        onOpenGamificationClick = { activeOverlay = OverlayScreen.GAMIFICATION },
                        onOpenAxioSmsSyncClick = { activeOverlay = OverlayScreen.AXIO_SMS_SYNC },
                        onToggleAutoReminders = { enabled -> viewModel.toggleAutoReminders(enabled) },
                        onToggleOfflineMode = { enabled -> viewModel.toggleOfflineMode(enabled) }
                    )
                }
            }
        }
    }

    // App Lock PIN & Biometrics Overlay
    if (uiState.profile.isPinEnabled && !isAppUnlocked) {
        AppLockOverlay(
            isLocked = true,
            correctPin = uiState.profile.pinCode,
            isBiometricAvailable = uiState.profile.isBiometricEnabled,
            onUnlockSuccess = { isAppUnlocked = true }
        )
    }
}
