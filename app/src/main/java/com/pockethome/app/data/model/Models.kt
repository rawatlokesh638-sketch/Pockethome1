package com.pockethome.app.data.model

import androidx.annotation.DrawableRes

enum class TransactionType {
    EXPENSE,
    INCOME
}

enum class IncomeCategory(
    val displayName: String,
    val hexColor: String
) {
    SALARY("Salary", "#22C55E"),
    FREELANCE("Freelance", "#3B82F6"),
    BUSINESS("Business Income", "#8B5CF6"),
    POCKET_MONEY("Pocket Money", "#F97316"),
    RENTAL("Rental Income", "#06B6D4"),
    OTHER("Other Income", "#6366F1");

    companion object {
        fun fromString(name: String?): IncomeCategory {
            return entries.find { it.name.equals(name, ignoreCase = true) || it.displayName.equals(name, ignoreCase = true) }
                ?: OTHER
        }
    }
}

enum class ExpenseCategory(
    val displayName: String,
    val iconName: String,
    val hexColor: String
) {
    GROCERIES("Groceries", "ic_groceries", "#22C55E"),      // Vibrant Green
    BILLS("Bills & Utilities", "ic_bills", "#3B82F6"),        // Blue
    FOOD("Food & Dining", "ic_food", "#F97316"),            // Orange
    TRANSPORT("Transport", "ic_transport", "#8B5CF6"),      // Purple
    HEALTH("Health & Medical", "ic_health", "#EF4444"),      // Red
    SHOPPING("Shopping", "ic_shopping", "#EC4899"),          // Pink
    EDUCATION("Education", "ic_education", "#06B6D4"),      // Cyan / Teal
    HOME("Home", "ic_home", "#10B981"),                      // Emerald
    MOBILE("Mobile & Recharge", "ic_mobile", "#6366F1"),    // Indigo
    FUEL("Fuel", "ic_fuel", "#F59E0B"),                      // Amber
    EMI("EMI & Loans", "ic_emi", "#D97706"),                  // Amber Dark
    ENTERTAINMENT("Entertainment", "ic_entertainment", "#A855F7"), // Purple
    CLOTHING("Clothing", "ic_clothing", "#E11D48"),          // Rose
    PETS("Pets", "ic_pets", "#14B8A6"),                      // Teal
    OTHERS("Others", "ic_others", "#64748B");                // Slate Muted

    companion object {
        fun fromString(name: String?): ExpenseCategory {
            return entries.find { it.name.equals(name, ignoreCase = true) || it.displayName.equals(name, ignoreCase = true) }
                ?: OTHERS
        }
    }
}

enum class PaymentMethod(val displayName: String) {
    CASH("Cash"),
    UPI("UPI"),
    DEBIT_CARD("Debit Card"),
    CREDIT_CARD("Credit Card"),
    BANK_TRANSFER("Bank Transfer"),
    WALLET("Wallet"),
    NET_BANKING("Net Banking"),
    EMI("EMI"),
    OTHER("Other");

    companion object {
        fun fromString(name: String?): PaymentMethod {
            return entries.find { 
                it.name.equals(name, ignoreCase = true) || 
                it.displayName.equals(name, ignoreCase = true) ||
                (name != null && it.displayName.contains(name, ignoreCase = true))
            } ?: CASH
        }
    }
}

enum class RecurringPreset(
    val title: String,
    val defaultCategory: String,
    val defaultType: TransactionType,
    val frequency: String,
    val defaultAmount: Double
) {
    SALARY("Monthly Salary", "Salary", TransactionType.INCOME, "Monthly", 35000.0),
    RENT("Monthly House Rent", "Home", TransactionType.EXPENSE, "Monthly", 12000.0),
    EMI("Loan / Appliance EMI", "EMI & Loans", TransactionType.EXPENSE, "Monthly", 4500.0),
    SUBSCRIPTION("Subscriptions (Netflix/Prime)", "Entertainment", TransactionType.EXPENSE, "Monthly", 649.0),
    INTERNET("Broadband / Internet Bill", "Bills & Utilities", TransactionType.EXPENSE, "Monthly", 799.0),
    MOBILE("Mobile Recharge Plan", "Mobile & Recharge", TransactionType.EXPENSE, "Monthly", 299.0),
    INSURANCE("Insurance (Health / Term)", "Health & Medical", TransactionType.EXPENSE, "Yearly", 8500.0),
    CUSTOM("Custom Recurring Payment", "Others", TransactionType.EXPENSE, "Monthly", 1000.0)
}

data class TransactionItem(
    val id: String = "",
    val title: String = "",
    val amount: Double = 0.0,
    val category: String = ExpenseCategory.OTHERS.displayName,
    val type: String = TransactionType.EXPENSE.name,
    val dateString: String = "", // e.g. "4 Oct 2026"
    val timestamp: Long = System.currentTimeMillis(),
    val note: String = "",
    val paymentMethod: String = PaymentMethod.CASH.displayName,
    val isRecurring: Boolean = false,
    val recurringFrequency: String = "Monthly", // Daily, Weekly, Monthly, Yearly
    val recurringPreset: String = "",
    val receiptUri: String = "",
    val shopName: String = "",
    val ocrScanned: Boolean = false,
    val memberName: String = "Lokesh",
    val isShared: Boolean = true
)

data class FamilyMember(
    val id: String = "",
    val name: String = "",
    val role: String = "Member", // Admin, Member, Viewer
    val email: String = "",
    val totalSpent: Double = 0.0,
    val permissionLevel: String = "Full Access" // Full Access, View Only, Edit Expenses
)

data class FamilyGroup(
    val id: String = "",
    val name: String = "Sharma Family",
    val inviteCode: String = "SHARMA-8826",
    val members: List<FamilyMember> = listOf(
        FamilyMember("1", "Lokesh (You)", "Admin", "lokesh@sharma.in", 12450.0, "Full Access"),
        FamilyMember("2", "Priya (Wife)", "Member", "priya@sharma.in", 4800.0, "Edit Expenses"),
        FamilyMember("3", "Rohan (Son)", "Viewer", "rohan@sharma.in", 1500.0, "View Only")
    ),
    val sharedExpensesTotal: Double = 18750.0
)

data class BillItem(
    val id: String = "",
    val title: String = "",
    val amount: Double = 0.0,
    val dueDateString: String = "", // e.g. "10 Oct 2026"
    val dueDaysText: String = "", // e.g. "Due in 6 days"
    val category: String = ExpenseCategory.BILLS.displayName,
    val isPaid: Boolean = false,
    val billType: String = "electricity" // electricity, wifi, phone, gas, water
)

data class CategoryBudget(
    val categoryName: String = "",
    val limitAmount: Double = 0.0,
    val spentAmount: Double = 0.0
)

data class SavingsGoal(
    val id: String = "",
    val title: String = "",
    val category: String = "Custom Goal",
    val emoji: String = "🎯",
    val targetAmount: Double = 0.0,
    val currentSaved: Double = 0.0,
    val targetDateString: String = "Dec 2026",
    val monthlyTarget: Double = 0.0,
    val strategy: String = "Moderate", // Conservative, Moderate, Aggressive
    val investmentType: String = "SIP", // SIP, RD, FD, Gold SGB, Savings Account
    val isPaused: Boolean = false,
    val isCompleted: Boolean = false,
    val notes: String = "",
    val autoSaveReminder: Boolean = true,
    val colorHex: String = "#4F46E5"
)

data class EmiLoanItem(
    val id: String = "",
    val title: String = "",
    val loanType: String = "Home Loan", // Home Loan, Car Loan, Personal Loan, Electronics EMI, Education
    val lender: String = "HDFC Bank",
    val loanAmount: Double = 0.0,
    val emiAmount: Double = 0.0,
    val interestRate: Double = 8.5,
    val remainingAmount: Double = 0.0,
    val dueDayOfMonth: Int = 5,
    val dueDateString: String = "5th of every month",
    val totalInstallments: Int = 12,
    val paidInstallments: Int = 0,
    val remindersEnabled: Boolean = true,
    val colorHex: String = "#4F46E5"
) {
    val remainingInstallments: Int
        get() = (totalInstallments - paidInstallments).coerceAtLeast(0)

    val progressPct: Int
        get() = if (totalInstallments > 0) ((paidInstallments.toDouble() / totalInstallments) * 100).toInt().coerceIn(0, 100) else 0

    val isCompleted: Boolean
        get() = paidInstallments >= totalInstallments || remainingAmount <= 0.0
}

data class UserProfile(
    val uid: String = "",
    val name: String = "Lokesh",
    val email: String = "",
    val avatarEmoji: String = "👑",
    val householdRole: String = "Head of Family",
    val monthlyBudget: Double = 25000.0,
    val isAnonymous: Boolean = true,
    val autoRemindersEnabled: Boolean = true,
    val isPinEnabled: Boolean = false,
    val pinCode: String = "",
    val isBiometricEnabled: Boolean = false,
    val themeMode: String = "Light", // Light, Dark, System
    val accentThemeHex: String = "#4F46E5", // Indigo, Emerald, Amber, Rose, Cyan
    val currencyCode: String = "₹",
    val currencyName: String = "INR (Indian Rupee)",
    val language: String = "English", // English, Hindi, Hinglish, Gujarati, Marathi
    val showDailySpentBanner: Boolean = true,
    val showSavingsGoalsWidget: Boolean = true,
    val showSmartInsightsWidget: Boolean = true,
    val showCategoryDonut: Boolean = true,
    val isProUser: Boolean = false,
    val subscriptionTier: String = "FREE", // FREE, PRO_MONTHLY, PRO_ANNUAL
    val proExpiryDate: String = "Nov 2026",
    val lastSyncTimestamp: Long = System.currentTimeMillis()
)

data class SmartNotificationItem(
    val id: String = "",
    val title: String = "",
    val body: String = "",
    val type: String = "BILL_DUE", // BILL_DUE, BUDGET_WARNING, OVERSPENDING, RECURRING, SALARY, SAVINGS_GOAL, WEEKLY_DIGEST, MONTHLY_SUMMARY
    val timestamp: Long = System.currentTimeMillis(),
    val timeAgo: String = "Just now",
    val isRead: Boolean = false,
    val iconEmoji: String = "🔔",
    val accentColorHex: String = "#4F46E5"
)

data class SpendingChallenge(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val targetDays: Int = 3,
    val currentDays: Int = 0,
    val rewardPoints: Int = 100,
    val isCompleted: Boolean = false,
    val iconEmoji: String = "🎯"
)

data class GamificationBadge(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val iconEmoji: String = "🏆",
    val unlocked: Boolean = false,
    val unlockedDate: String = ""
)

data class GamificationData(
    val streakDays: Int = 14,
    val noSpendDaysThisMonth: Int = 5,
    val monthlySavingTarget: Double = 15000.0,
    val financialScore: Int = 84,
    val scoreGrade: String = "EXCELLENT",
    val activeChallenges: List<SpendingChallenge> = emptyList(),
    val badges: List<GamificationBadge> = emptyList()
)

data class UtrPaymentRequest(
    val requestId: String = "",
    val userId: String = "",
    val userName: String = "",
    val userEmail: String = "",
    val phonePeNumber: String = "9050884894",
    val utrNumber: String = "",
    val transactionRef: String = "",
    val planTier: String = "PRO_MONTHLY", // PRO_MONTHLY (₹199) or PRO_ANNUAL (₹1499)
    val amount: Double = 199.0,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "PENDING", // PENDING, APPROVED, REJECTED
    val adminNote: String = ""
)
