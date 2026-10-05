package com.pockethome.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pockethome.app.data.model.BillItem
import com.pockethome.app.data.model.CategoryBudget
import com.pockethome.app.data.model.ExpenseCategory
import com.pockethome.app.data.model.TransactionItem
import com.pockethome.app.data.model.TransactionType
import com.pockethome.app.data.model.UserProfile
import com.pockethome.app.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import com.pockethome.app.data.model.FamilyGroup
import com.pockethome.app.data.model.FamilyMember
import com.pockethome.app.data.model.SavingsGoal
import com.pockethome.app.data.model.EmiLoanItem
import com.pockethome.app.data.model.SmartNotificationItem
import com.pockethome.app.data.model.GamificationBadge
import com.pockethome.app.data.model.SpendingChallenge
import com.pockethome.app.data.model.GamificationData
import kotlinx.coroutines.flow.MutableStateFlow

data class CategoryBreakdown(
    val category: ExpenseCategory,
    val amount: Double,
    val percentage: Int
)

data class AdvancedFinancialOverview(
    val netBalance: Double = 0.0,
    val totalAssets: Double = 0.0,
    val totalLiabilities: Double = 0.0,
    val monthlySavings: Double = 0.0,
    val savingsRatePct: Int = 0,
    val monthlyFixedExpenses: Double = 0.0,
    val variableExpenses: Double = 0.0,
    val essentialExpenses: Double = 0.0,
    val nonEssentialExpenses: Double = 0.0
)

data class GrihaUiState(
    val profile: UserProfile = UserProfile(),
    val transactions: List<TransactionItem> = emptyList(),
    val bills: List<BillItem> = emptyList(),
    val categoryBudgets: List<CategoryBudget> = emptyList(),
    val totalExpenses: Double = 0.0,
    val totalIncome: Double = 0.0,
    val todayExpenses: Double = 0.0,
    val monthlyBudget: Double = 25000.0,
    val categoryBreakdowns: List<CategoryBreakdown> = emptyList(),
    val familyGroup: FamilyGroup = FamilyGroup(),
    val isConnectedToCloud: Boolean = true,
    val savingsGoals: List<SavingsGoal> = emptyList(),
    val emiLoans: List<EmiLoanItem> = emptyList(),
    val notifications: List<SmartNotificationItem> = emptyList(),
    val gamification: GamificationData = GamificationData(),
    val financialOverview: AdvancedFinancialOverview = AdvancedFinancialOverview(),
    val isOfflineMode: Boolean = false,
    val pendingOfflineSyncCount: Int = 0
)

private data class ExtraViewModelState(
    val savingsGoals: List<SavingsGoal>,
    val emiLoans: List<EmiLoanItem>,
    val notifications: List<SmartNotificationItem>,
    val gamification: GamificationData
)

class GrihaBudgetViewModel(
    private val repository: FirebaseRepository = FirebaseRepository()
) : ViewModel() {

    private val _savingsGoals = MutableStateFlow<List<SavingsGoal>>(
        listOf(
            SavingsGoal("1", "New Phone", "📱", 20000.0, 12000.0, "Dec 2026", 2000.0, false, "#3B82F6"),
            SavingsGoal("2", "Bike", "🏍️", 80000.0, 45000.0, "Feb 2027", 7000.0, false, "#F59E0B"),
            SavingsGoal("3", "Vacation", "✈️", 30000.0, 30000.0, "Oct 2026", 0.0, true, "#10B981"),
            SavingsGoal("4", "Home Down Payment", "🏠", 500000.0, 180000.0, "Dec 2028", 15000.0, false, "#8B5CF6")
        )
    )

    private val _emiLoans = MutableStateFlow<List<EmiLoanItem>>(
        listOf(
            EmiLoanItem(
                id = "emi1",
                title = "HDFC Home Loan",
                loanType = "Home Loan",
                lender = "HDFC Bank",
                loanAmount = 2500000.0,
                emiAmount = 21500.0,
                interestRate = 8.5,
                remainingAmount = 1820000.0,
                dueDayOfMonth = 5,
                dueDateString = "5th of every month",
                totalInstallments = 180,
                paidInstallments = 42,
                remindersEnabled = true,
                colorHex = "#3B82F6"
            ),
            EmiLoanItem(
                id = "emi2",
                title = "Hyundai Car Loan",
                loanType = "Car Loan",
                lender = "Hyundai Finance",
                loanAmount = 650000.0,
                emiAmount = 13200.0,
                interestRate = 9.2,
                remainingAmount = 240000.0,
                dueDayOfMonth = 10,
                dueDateString = "10th of every month",
                totalInstallments = 60,
                paidInstallments = 38,
                remindersEnabled = true,
                colorHex = "#F59E0B"
            ),
            EmiLoanItem(
                id = "emi3",
                title = "iPhone 16 Pro No-Cost EMI",
                loanType = "Electronics EMI",
                lender = "Bajaj Finserv",
                loanAmount = 130000.0,
                emiAmount = 10833.0,
                interestRate = 0.0,
                remainingAmount = 32500.0,
                dueDayOfMonth = 15,
                dueDateString = "15th of every month",
                totalInstallments = 12,
                paidInstallments = 9,
                remindersEnabled = true,
                colorHex = "#10B981"
            ),
            EmiLoanItem(
                id = "emi4",
                title = "SBI Personal Loan",
                loanType = "Personal Loan",
                lender = "State Bank of India",
                loanAmount = 300000.0,
                emiAmount = 6800.0,
                interestRate = 10.5,
                remainingAmount = 92000.0,
                dueDayOfMonth = 1,
                dueDateString = "1st of every month",
                totalInstallments = 48,
                paidInstallments = 34,
                remindersEnabled = true,
                colorHex = "#8B5CF6"
            )
        )
    )

    private val _notifications = MutableStateFlow<List<SmartNotificationItem>>(
        listOf(
            SmartNotificationItem(
                id = "notif_1",
                title = "⚡ Electricity Bill Due in 2 Days",
                body = "BSES Yamuna bill of ₹3,350 is due on 10 Oct. Pay before due date to avoid late fee.",
                type = "BILL_DUE",
                timeAgo = "10m ago",
                isRead = false,
                iconEmoji = "⚡",
                accentColorHex = "#D97706"
            ),
            SmartNotificationItem(
                id = "notif_2",
                title = "⚠️ Dining Budget Exhausted 85%",
                body = "You've spent ₹2,250 of your ₹2,500 Dining budget. Only ₹250 remains for this month.",
                type = "BUDGET_WARNING",
                timeAgo = "2h ago",
                isRead = false,
                iconEmoji = "⚠️",
                accentColorHex = "#EF4444"
            ),
            SmartNotificationItem(
                id = "notif_3",
                title = "💰 Monthly Salary Credited",
                body = "Salary of ₹35,000 was credited to your Primary Bank Account on 1 Oct.",
                type = "SALARY",
                timeAgo = "3d ago",
                isRead = true,
                iconEmoji = "💰",
                accentColorHex = "#16A34A"
            ),
            SmartNotificationItem(
                id = "notif_4",
                title = "🔄 HDFC Home Loan Auto-Debit Scheduled",
                body = "Monthly EMI installment of ₹21,500 will auto-debit on 5 Oct.",
                type = "RECURRING",
                timeAgo = "1d ago",
                isRead = false,
                iconEmoji = "🔄",
                accentColorHex = "#3B82F6"
            ),
            SmartNotificationItem(
                id = "notif_5",
                title = "🎯 Savings Goal Target Reminder",
                body = "Deposit ₹2,000 this week to keep 'New Phone' savings goal on track for Dec.",
                type = "SAVINGS_GOAL",
                timeAgo = "1d ago",
                isRead = true,
                iconEmoji = "🎯",
                accentColorHex = "#8B5CF6"
            ),
            SmartNotificationItem(
                id = "notif_6",
                title = "🚨 Daily Overspending Alert",
                body = "You spent ₹1,400 above your recommended daily safe spending limit today.",
                type = "OVERSPENDING",
                timeAgo = "4h ago",
                isRead = false,
                iconEmoji = "🚨",
                accentColorHex = "#DC2626"
            ),
            SmartNotificationItem(
                id = "notif_7",
                title = "📊 Weekly Financial Summary",
                body = "You spent ₹8,450 this week (-12% vs last week). Great discipline in groceries!",
                type = "WEEKLY_DIGEST",
                timeAgo = "3d ago",
                isRead = true,
                iconEmoji = "📊",
                accentColorHex = "#0891B2"
            ),
            SmartNotificationItem(
                id = "notif_8",
                title = "🗓️ September Financial Health Report",
                body = "September savings total: ₹14,200 (31% of total income). Score: 84/100 EXCELLENT.",
                type = "MONTHLY_SUMMARY",
                timeAgo = "4d ago",
                isRead = true,
                iconEmoji = "🗓️",
                accentColorHex = "#059669"
            )
        )
    )

    private val _gamification = MutableStateFlow<GamificationData>(
        GamificationData(
            streakDays = 14,
            noSpendDaysThisMonth = 5,
            monthlySavingTarget = 15000.0,
            financialScore = 84,
            scoreGrade = "EXCELLENT",
            activeChallenges = listOf(
                SpendingChallenge(
                    id = "chal_1",
                    title = "No-Dining Weekend Challenge",
                    description = "Cook home meals on Saturday & Sunday to save ~₹1,500.",
                    targetDays = 2,
                    currentDays = 1,
                    rewardPoints = 150,
                    isCompleted = false,
                    iconEmoji = "🍲"
                ),
                SpendingChallenge(
                    id = "chal_2",
                    title = "7-Day Zero-Zomato Challenge",
                    description = "Avoid online food delivery orders for a full week.",
                    targetDays = 7,
                    currentDays = 4,
                    rewardPoints = 250,
                    isCompleted = false,
                    iconEmoji = "🚴"
                ),
                SpendingChallenge(
                    id = "chal_3",
                    title = "Save ₹500 on Fuel Refill",
                    description = "Use metro/carpool for 3 commutes to save fuel costs.",
                    targetDays = 3,
                    currentDays = 3,
                    rewardPoints = 100,
                    isCompleted = true,
                    iconEmoji = "⛽"
                )
            ),
            badges = listOf(
                GamificationBadge("b1", "Budget Master", "Stayed under monthly budget 3 consecutive months", "🛡️", true, "1 Oct 2026"),
                GamificationBadge("b2", "Bill Ninja", "100% bills paid before due date", "⚡", true, "3 Oct 2026"),
                GamificationBadge("b3", "Goal Crusher", "Completed 'Vacation' savings goal", "🎯", true, "28 Sep 2026"),
                GamificationBadge("b4", "Super Saver", "Achieved over 30% monthly savings rate", "💰", true, "30 Sep 2026"),
                GamificationBadge("b5", "Streak Legend", "Maintained 14-day expense tracking streak", "🔥", true, "Today"),
                GamificationBadge("b6", "Debt Slayer", "Paid 42 home loan installments on time", "💎", true, "4 Oct 2026"),
                GamificationBadge("b7", "Century Saver", "Accumulated ₹1,00,000+ in family savings", "👑", false, ""),
                GamificationBadge("b8", "Zen Spender", "Logged 10 'No-Spend Days' in a single month", "🧘", false, "")
            )
        )
    )

    private val _isOfflineMode = MutableStateFlow(false)
    private val _pendingOfflineSyncCount = MutableStateFlow(0)

    init {
        repository.ensureAuth {
            // Auth ready
        }
    }

    val userProfile: StateFlow<UserProfile> = repository.userProfileFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserProfile(name = "Lokesh")
        )

    val transactions: StateFlow<List<TransactionItem>> = repository.transactionsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val bills: StateFlow<List<BillItem>> = repository.billsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val categoryBudgets: StateFlow<List<CategoryBudget>> = repository.categoryBudgetsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val baseDataFlow = combine(
        userProfile,
        transactions,
        bills,
        categoryBudgets
    ) { profile, txList, billList, catBudgetList ->
        val expenses = txList.filter { it.type == TransactionType.EXPENSE.name }
        val income = txList.filter { it.type == TransactionType.INCOME.name }

        val totalExpenses = expenses.sumOf { it.amount }
        val totalIncome = income.sumOf { it.amount }

        val todayStr = SimpleDateFormat("d MMM yyyy", Locale.ENGLISH).format(Date())
        val todayExpenses = expenses.filter { it.dateString == todayStr }.sumOf { it.amount }

        // Category breakdown calculation
        val breakdownList = ExpenseCategory.entries.map { cat ->
            val catAmount = expenses.filter { it.category == cat.displayName }.sumOf { it.amount }
            val pct = if (totalExpenses > 0) ((catAmount / totalExpenses) * 100).toInt() else 0
            CategoryBreakdown(cat, catAmount, pct)
        }.filter { it.amount > 0 }.sortedByDescending { it.amount }

        GrihaUiState(
            profile = profile,
            transactions = txList,
            bills = billList,
            categoryBudgets = catBudgetList,
            totalExpenses = totalExpenses,
            totalIncome = totalIncome,
            todayExpenses = if (todayExpenses > 0) todayExpenses else 320.0,
            monthlyBudget = profile.monthlyBudget,
            categoryBreakdowns = breakdownList
        )
    }

    private val extraStateFlow = combine(
        _savingsGoals,
        _emiLoans,
        _notifications,
        _gamification
    ) { goals, emiList, notifs, gami ->
        ExtraViewModelState(goals, emiList, notifs, gami)
    }

    val uiState: StateFlow<GrihaUiState> = combine(
        baseDataFlow,
        extraStateFlow,
        _isOfflineMode,
        _pendingOfflineSyncCount
    ) { baseState, extra, isOffline, pendingCount ->
        // Advanced Financial Overview calculations
        val totalAssets = 75000.0 + extra.savingsGoals.sumOf { it.currentSaved } + (baseState.totalIncome - baseState.totalExpenses).coerceAtLeast(0.0)
        val totalLiabilities = extra.emiLoans.sumOf { it.remainingAmount }
        val netBalance = totalAssets - totalLiabilities
        val monthlySavings = (baseState.totalIncome - baseState.totalExpenses).coerceAtLeast(0.0)
        val savingsRatePct = if (baseState.totalIncome > 0) ((monthlySavings / baseState.totalIncome) * 100).toInt() else 0
        val monthlyFixedExpenses = extra.emiLoans.filter { !it.isCompleted }.sumOf { it.emiAmount } + baseState.bills.sumOf { it.amount } + 649.0
        val variableExpenses = (baseState.totalExpenses - monthlyFixedExpenses).coerceAtLeast(0.0)

        val essentialCategories = listOf("Groceries", "Bills & Utilities", "Health & Medical", "Transport", "Fuel", "EMI", "Education")
        val essentialExpenses = baseState.transactions.filter {
            it.type == TransactionType.EXPENSE.name && it.category in essentialCategories
        }.sumOf { it.amount }
        val nonEssentialExpenses = (baseState.totalExpenses - essentialExpenses).coerceAtLeast(0.0)

        val overview = AdvancedFinancialOverview(
            netBalance = netBalance,
            totalAssets = totalAssets,
            totalLiabilities = totalLiabilities,
            monthlySavings = monthlySavings,
            savingsRatePct = savingsRatePct,
            monthlyFixedExpenses = monthlyFixedExpenses,
            variableExpenses = variableExpenses,
            essentialExpenses = essentialExpenses,
            nonEssentialExpenses = nonEssentialExpenses
        )

        baseState.copy(
            savingsGoals = extra.savingsGoals,
            emiLoans = extra.emiLoans,
            notifications = extra.notifications,
            gamification = extra.gamification,
            financialOverview = overview,
            isOfflineMode = isOffline,
            isConnectedToCloud = !isOffline,
            pendingOfflineSyncCount = pendingCount
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = GrihaUiState()
    )

    fun addTransaction(
        title: String,
        amount: Double,
        category: String,
        type: TransactionType,
        note: String,
        paymentMethod: String,
        dateString: String,
        isRecurring: Boolean = false,
        receiptUri: String = "",
        shopName: String = "",
        ocrScanned: Boolean = false
    ) {
        val tx = TransactionItem(
            title = title.ifBlank { category },
            amount = amount,
            category = category,
            type = type.name,
            dateString = dateString.ifBlank { SimpleDateFormat("d MMM yyyy", Locale.ENGLISH).format(Date()) },
            note = note,
            paymentMethod = paymentMethod,
            isRecurring = isRecurring,
            receiptUri = receiptUri,
            shopName = shopName,
            ocrScanned = ocrScanned
        )
        repository.addTransaction(tx)
    }

    fun duplicateTransaction(item: TransactionItem) {
        val dup = item.copy(
            id = System.currentTimeMillis().toString(),
            title = "${item.title} (Copy)",
            timestamp = System.currentTimeMillis()
        )
        repository.addTransaction(dup)
    }

    fun deleteTransaction(id: String) {
        repository.deleteTransaction(id)
    }

    fun addBill(title: String, amount: Double, dueDateString: String, dueDaysText: String, category: String, billType: String) {
        val bill = BillItem(
            title = title,
            amount = amount,
            dueDateString = dueDateString,
            dueDaysText = dueDaysText,
            category = category,
            isPaid = false,
            billType = billType
        )
        repository.addBill(bill)
    }

    fun toggleBillPaid(id: String, isPaid: Boolean) {
        repository.toggleBillPaid(id, isPaid)
    }

    fun deleteBill(id: String) {
        repository.deleteBill(id)
    }

    fun updateMonthlyBudget(amount: Double) {
        repository.updateMonthlyBudget(amount)
    }

    fun updateCategoryBudget(categoryName: String, limit: Double) {
        repository.updateCategoryBudget(categoryName, limit)
    }

    fun updateProfileName(name: String) {
        repository.updateProfileName(name)
    }

    fun toggleAutoReminders(enabled: Boolean) {
        repository.toggleAutoReminders(enabled)
    }

    fun signInWithEmail(email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        repository.signInWithEmail(email, pass, onResult)
    }

    fun signUpWithEmail(
        email: String,
        pass: String,
        name: String,
        role: String = "Head of Family",
        monthlyBudget: Double = 25000.0,
        onResult: (Boolean, String?) -> Unit
    ) {
        repository.signUpWithEmail(email, pass, name, role, monthlyBudget, onResult)
    }

    fun signUpWithEmail(
        email: String,
        pass: String,
        name: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        signUpWithEmail(email, pass, name, "Head of Family", 25000.0, onResult)
    }

    fun signInWithGoogle(name: String, email: String, onResult: (Boolean, String?) -> Unit) {
        repository.signInWithGoogle(name, email, onResult)
    }

    fun updatePinLock(enabled: Boolean, pin: String) {
        repository.updatePinLock(enabled, pin)
    }

    fun updateBiometricLock(enabled: Boolean) {
        repository.updateBiometricLock(enabled)
    }

    fun deleteFinancialData(onComplete: (Boolean) -> Unit) {
        repository.deleteFinancialData(onComplete)
    }

    fun deleteAccount(onComplete: (Boolean, String?) -> Unit) {
        repository.deleteAccount(onComplete)
    }

    fun logoutAllDevices() {
        repository.logoutAllDevices()
    }

    fun addSavingsGoal(title: String, emoji: String, targetAmount: Double, targetDateString: String, monthlyTarget: Double, colorHex: String) {
        val newGoal = SavingsGoal(
            id = System.currentTimeMillis().toString(),
            title = title,
            emoji = emoji,
            targetAmount = targetAmount,
            currentSaved = 0.0,
            targetDateString = targetDateString,
            monthlyTarget = monthlyTarget,
            isCompleted = false,
            colorHex = colorHex
        )
        _savingsGoals.value = _savingsGoals.value + newGoal
    }

    fun depositToSavingsGoal(goalId: String, amount: Double) {
        _savingsGoals.value = _savingsGoals.value.map { goal ->
            if (goal.id == goalId) {
                val newSaved = goal.currentSaved + amount
                val completed = newSaved >= goal.targetAmount
                goal.copy(currentSaved = newSaved, isCompleted = completed)
            } else {
                goal
            }
        }
    }

    fun deleteSavingsGoal(goalId: String) {
        _savingsGoals.value = _savingsGoals.value.filter { it.id != goalId }
    }

    fun toggleOfflineMode(enabled: Boolean) {
        _isOfflineMode.value = enabled
        if (!enabled) {
            syncOfflineData()
        }
    }

    fun syncOfflineData() {
        _pendingOfflineSyncCount.value = 0
    }

    fun updateTheme(themeMode: String, accentHex: String) {
        val updated = userProfile.value.copy(themeMode = themeMode, accentThemeHex = accentHex)
        repository.updateProfile(updated)
    }

    fun updateCurrency(code: String, name: String) {
        val updated = userProfile.value.copy(currencyCode = code, currencyName = name)
        repository.updateProfile(updated)
    }

    fun updateLanguage(language: String) {
        val updated = userProfile.value.copy(language = language)
        repository.updateProfile(updated)
    }

    fun updateDashboardPreferences(showDaily: Boolean, showGoals: Boolean, showInsights: Boolean, showDonut: Boolean) {
        val updated = userProfile.value.copy(
            showDailySpentBanner = showDaily,
            showSavingsGoalsWidget = showGoals,
            showSmartInsightsWidget = showInsights,
            showCategoryDonut = showDonut
        )
        repository.updateProfile(updated)
    }

    fun updateCustomProfile(name: String, emoji: String, role: String, monthlyBudget: Double) {
        val updated = userProfile.value.copy(
            name = name,
            avatarEmoji = emoji,
            householdRole = role,
            monthlyBudget = monthlyBudget
        )
        repository.updateProfile(updated)
    }

    fun addEmiLoan(
        title: String,
        loanType: String,
        lender: String,
        loanAmount: Double,
        emiAmount: Double,
        interestRate: Double,
        dueDay: Int,
        totalInstallments: Int,
        colorHex: String
    ) {
        val newLoan = EmiLoanItem(
            id = "emi_${System.currentTimeMillis()}",
            title = title,
            loanType = loanType,
            lender = lender,
            loanAmount = loanAmount,
            emiAmount = emiAmount,
            interestRate = interestRate,
            remainingAmount = loanAmount,
            dueDayOfMonth = dueDay,
            dueDateString = "${dueDay}th of every month",
            totalInstallments = totalInstallments,
            paidInstallments = 0,
            remindersEnabled = true,
            colorHex = colorHex
        )
        _emiLoans.value = _emiLoans.value + newLoan
    }

    fun payEmiInstallment(loanId: String) {
        val targetLoan = _emiLoans.value.find { it.id == loanId } ?: return
        val updatedPaid = targetLoan.paidInstallments + 1
        val updatedRemaining = (targetLoan.remainingAmount - targetLoan.emiAmount).coerceAtLeast(0.0)

        _emiLoans.value = _emiLoans.value.map { loan ->
            if (loan.id == loanId) {
                loan.copy(
                    paidInstallments = updatedPaid,
                    remainingAmount = updatedRemaining
                )
            } else {
                loan
            }
        }

        // Automatically record this EMI payment in transactions
        val todayStr = SimpleDateFormat("d MMM yyyy", Locale.ENGLISH).format(Date())
        addTransaction(
            title = "${targetLoan.title} (Installment $updatedPaid/${targetLoan.totalInstallments})",
            amount = targetLoan.emiAmount,
            category = ExpenseCategory.EMI.displayName,
            type = TransactionType.EXPENSE,
            note = "EMI Payment to ${targetLoan.lender}",
            paymentMethod = "Auto-Debit",
            dateString = todayStr
        )
    }

    fun toggleEmiReminder(loanId: String, enabled: Boolean) {
        _emiLoans.value = _emiLoans.value.map { loan ->
            if (loan.id == loanId) {
                loan.copy(remindersEnabled = enabled)
            } else {
                loan
            }
        }
    }

    fun deleteEmiLoan(loanId: String) {
        _emiLoans.value = _emiLoans.value.filter { it.id != loanId }
    }

    fun upgradeToPro(tier: String = "PRO_MONTHLY") {
        val updated = userProfile.value.copy(
            isProUser = true,
            subscriptionTier = tier,
            proExpiryDate = if (tier == "PRO_ANNUAL") "Oct 2027" else "Nov 2026"
        )
        repository.updateProfile(updated)
    }

    fun downgradeToFree() {
        val updated = userProfile.value.copy(
            isProUser = false,
            subscriptionTier = "FREE"
        )
        repository.updateProfile(updated)
    }

    fun markNotificationAsRead(id: String) {
        _notifications.value = _notifications.value.map {
            if (it.id == id) it.copy(isRead = true) else it
        }
    }

    fun markAllNotificationsAsRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
    }

    fun deleteNotification(id: String) {
        _notifications.value = _notifications.value.filter { it.id != id }
    }

    fun triggerSimulatedNotification(type: String) {
        val newNotif = when (type) {
            "BILL_DUE" -> SmartNotificationItem(
                id = "sim_${System.currentTimeMillis()}",
                title = "⚡ Bill Due Tomorrow: Jio Fiber",
                body = "Bill of ₹1,180 is due tomorrow. Auto-pay will process at 10 AM.",
                type = "BILL_DUE",
                iconEmoji = "⚡",
                accentColorHex = "#D97706"
            )
            "OVERSPENDING" -> SmartNotificationItem(
                id = "sim_${System.currentTimeMillis()}",
                title = "🚨 Budget Alert: Shopping Limit Hit",
                body = "You have reached 92% of your monthly shopping allowance.",
                type = "OVERSPENDING",
                iconEmoji = "🚨",
                accentColorHex = "#DC2626"
            )
            "SALARY" -> SmartNotificationItem(
                id = "sim_${System.currentTimeMillis()}",
                title = "💰 Income Received: Freelance Project",
                body = "₹8,500 payout credited via UPI.",
                type = "SALARY",
                iconEmoji = "💰",
                accentColorHex = "#16A34A"
            )
            else -> SmartNotificationItem(
                id = "sim_${System.currentTimeMillis()}",
                title = "🎯 Savings Target Hit!",
                body = "You saved ₹1,500 extra this week. Score boosted to 86/100!",
                type = "SAVINGS_GOAL",
                iconEmoji = "🏆",
                accentColorHex = "#8B5CF6"
            )
        }
        _notifications.value = listOf(newNotif) + _notifications.value
    }

    fun joinChallenge(id: String) {
        _gamification.value = _gamification.value.copy(
            activeChallenges = _gamification.value.activeChallenges.map {
                if (it.id == id) it.copy(currentDays = (it.currentDays + 1).coerceAtMost(it.targetDays)) else it
            }
        )
    }

    fun logNoSpendDay() {
        _gamification.value = _gamification.value.copy(
            noSpendDaysThisMonth = _gamification.value.noSpendDaysThisMonth + 1,
            streakDays = _gamification.value.streakDays + 1,
            financialScore = (_gamification.value.financialScore + 2).coerceAtMost(100)
        )
    }

    fun signOut() {
        repository.signOut()
    }
}
