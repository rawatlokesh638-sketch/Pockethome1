package com.pockethome.app.util

import android.content.Context
import android.content.Intent
import com.pockethome.app.data.model.BillItem
import com.pockethome.app.data.model.CategoryBudget
import com.pockethome.app.data.model.TransactionItem
import com.pockethome.app.data.model.UserProfile
import com.pockethome.app.ui.viewmodel.GrihaUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ReportExportHelper {

    fun generateCsv(transactions: List<TransactionItem>): String {
        val sb = StringBuilder()
        sb.append("ID,Title,Amount,Category,Type,Date,PaymentMethod,Note,IsRecurring\n")
        transactions.forEach { tx ->
            val safeTitle = tx.title.replace(",", " ")
            val safeNote = tx.note.replace(",", " ")
            sb.append("${tx.id},$safeTitle,${tx.amount},${tx.category},${tx.type},${tx.dateString},${tx.paymentMethod},$safeNote,${tx.isRecurring}\n")
        }
        return sb.toString()
    }

    fun generateExcelFormat(state: GrihaUiState): String {
        val sb = StringBuilder()
        sb.append("=== POCKET HOME FINANCIAL REPORT (EXCEL SPREADSHEET EXPORT) ===\n")
        sb.append("Generated On: ${SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH).format(Date())}\n")
        sb.append("User: ${state.profile.name} (${state.profile.email})\n\n")

        sb.append("--- SUMMARY SHEET ---\n")
        sb.append("Metric\tValue (INR)\n")
        sb.append("Total Income\t₹${state.totalIncome}\n")
        sb.append("Total Expenses\t₹${state.totalExpenses}\n")
        sb.append("Net Balance\t₹${state.totalIncome - state.totalExpenses}\n")
        sb.append("Monthly Budget\t₹${state.monthlyBudget}\n\n")

        sb.append("--- TRANSACTIONS SHEET ---\n")
        sb.append("ID\tDate\tTitle\tCategory\tType\tAmount (INR)\tPayment Method\tNote\n")
        state.transactions.forEach { tx ->
            sb.append("${tx.id}\t${tx.dateString}\t${tx.title}\t${tx.category}\t${tx.type}\t${tx.amount}\t${tx.paymentMethod}\t${tx.note}\n")
        }

        sb.append("\n--- BILLS & REMINDERS SHEET ---\n")
        sb.append("ID\tBill Name\tCategory\tAmount (INR)\tDue Date\tStatus\n")
        state.bills.forEach { b ->
            val status = if (b.isPaid) "PAID" else "PENDING (${b.dueDaysText})"
            sb.append("${b.id}\t${b.title}\t${b.category}\t${b.amount}\t${b.dueDateString}\t$status\n")
        }
        return sb.toString()
    }

    fun generatePdfReportText(state: GrihaUiState, type: String = "Monthly"): String {
        val sb = StringBuilder()
        sb.append("═══════════════════════════════════════════════════════\n")
        sb.append("           POCKET HOME • FINANCIAL REPORT ($type)      \n")
        sb.append("═══════════════════════════════════════════════════════\n")
        sb.append("Generated for: ${state.profile.name}\n")
        sb.append("Date: ${SimpleDateFormat("d MMMM yyyy, h:mm a", Locale.ENGLISH).format(Date())}\n")
        sb.append("Firebase Cloud ID: ${state.profile.uid.take(10)}...\n")
        sb.append("───────────────────────────────────────────────────────\n")
        sb.append(" FINANCIAL OVERVIEW                                   \n")
        sb.append("───────────────────────────────────────────────────────\n")
        sb.append(" • Total Income      : ₹${String.format("%,.2f", state.totalIncome)}\n")
        sb.append(" • Total Expenses    : ₹${String.format("%,.2f", state.totalExpenses)}\n")
        sb.append(" • Net Savings       : ₹${String.format("%,.2f", state.totalIncome - state.totalExpenses)}\n")
        sb.append(" • Monthly Budget    : ₹${String.format("%,.2f", state.monthlyBudget)}\n")
        val budgetPct = if (state.monthlyBudget > 0) ((state.totalExpenses / state.monthlyBudget) * 100).toInt() else 0
        sb.append(" • Budget Consumed   : $budgetPct%\n")
        sb.append("───────────────────────────────────────────────────────\n")
        sb.append(" CATEGORY BREAKDOWN                                   \n")
        sb.append("───────────────────────────────────────────────────────\n")
        state.categoryBreakdowns.forEach { b ->
            sb.append(" • %-18s : ₹%,8.0f (%2d%%)\n".format(b.category.displayName, b.amount, b.percentage))
        }
        sb.append("───────────────────────────────────────────────────────\n")
        sb.append(" RECENT TRANSACTIONS (Top 10)                         \n")
        sb.append("───────────────────────────────────────────────────────\n")
        state.transactions.take(10).forEach { tx ->
            val sign = if (tx.type == "INCOME") "(+) " else "(-) "
            sb.append(" • %-10s | %-16s | %s₹%,.0f (%s)\n".format(
                tx.dateString,
                tx.title.take(16),
                sign,
                tx.amount,
                tx.paymentMethod
            ))
        }
        sb.append("───────────────────────────────────────────────────────\n")
        sb.append(" PENDING BILLS                                        \n")
        sb.append("───────────────────────────────────────────────────────\n")
        val pendingBills = state.bills.filter { !it.isPaid }
        if (pendingBills.isEmpty()) {
            sb.append(" • All bills are fully paid! 🎉\n")
        } else {
            pendingBills.forEach { b ->
                sb.append(" • %-18s : ₹%,8.0f (Due: %s)\n".format(b.title, b.amount, b.dueDateString))
            }
        }
        sb.append("═══════════════════════════════════════════════════════\n")
        sb.append("      Thank you for budgeting with Pocket Home!        \n")
        sb.append("═══════════════════════════════════════════════════════\n")
        return sb.toString()
    }

    fun generateBackupJson(state: GrihaUiState): String {
        val sb = StringBuilder()
        sb.append("{\n")
        sb.append("  \"version\": 1,\n")
        sb.append("  \"timestamp\": ${System.currentTimeMillis()},\n")
        sb.append("  \"user\": {\n")
        sb.append("    \"uid\": \"${state.profile.uid}\",\n")
        sb.append("    \"name\": \"${state.profile.name}\",\n")
        sb.append("    \"email\": \"${state.profile.email}\",\n")
        sb.append("    \"monthlyBudget\": ${state.profile.monthlyBudget}\n")
        sb.append("  },\n")
        sb.append("  \"transactionsCount\": ${state.transactions.size},\n")
        sb.append("  \"billsCount\": ${state.bills.size},\n")
        sb.append("  \"totalExpenses\": ${state.totalExpenses},\n")
        sb.append("  \"totalIncome\": ${state.totalIncome}\n")
        sb.append("}\n")
        return sb.toString()
    }

    fun shareText(context: Context, title: String, content: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, content)
        }
        context.startActivity(Intent.createChooser(intent, "Share $title via"))
    }
}
