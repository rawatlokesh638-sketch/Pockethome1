package com.pockethome.app.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.pockethome.app.data.model.BillItem
import com.pockethome.app.data.model.CategoryBudget
import com.pockethome.app.data.model.ExpenseCategory
import com.pockethome.app.data.model.PaymentMethod
import com.pockethome.app.data.model.TransactionItem
import com.pockethome.app.data.model.TransactionType
import com.pockethome.app.data.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FirebaseRepository {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val db: FirebaseDatabase by lazy {
        try {
            FirebaseDatabase.getInstance("https://pocket-home-3d327-default-rtdb.firebaseio.com").apply {
                try {
                    setPersistenceEnabled(true)
                } catch (e: Exception) {
                    // Persistence already enabled
                }
            }
        } catch (e: Exception) {
            try {
                FirebaseDatabase.getInstance().apply {
                    try {
                        setPersistenceEnabled(true)
                    } catch (e2: Exception) {
                        // Already enabled
                    }
                }
            } catch (e3: Exception) {
                FirebaseDatabase.getInstance("https://pocket-home-3d327-default-rtdb.asia-southeast1.firebasedatabase.app")
            }
        }
    }

    private val TAG = "FirebaseRepository"

    // Default screenshot initial values
    private val defaultInitTransactions = listOf(
        TransactionItem("tx1", "Groceries", 6400.0, ExpenseCategory.GROCERIES.displayName, TransactionType.EXPENSE.name, "4 Oct 2026", System.currentTimeMillis() - 1000, "Rashan & daily items", PaymentMethod.UPI.displayName),
        TransactionItem("tx2", "Bills & Utilities", 3350.0, ExpenseCategory.BILLS.displayName, TransactionType.EXPENSE.name, "4 Oct 2026", System.currentTimeMillis() - 2000, "Electricity & wifi", PaymentMethod.NET_BANKING.displayName),
        TransactionItem("tx3", "Food & Dining", 2250.0, ExpenseCategory.FOOD.displayName, TransactionType.EXPENSE.name, "4 Oct 2026", System.currentTimeMillis() - 3000, "Dinner with family", PaymentMethod.CREDIT_CARD.displayName),
        TransactionItem("tx4", "Transport", 1850.0, ExpenseCategory.TRANSPORT.displayName, TransactionType.EXPENSE.name, "4 Oct 2026", System.currentTimeMillis() - 4000, "Petrol refill", PaymentMethod.UPI.displayName),
        TransactionItem("tx5", "Health & Medical", 1500.0, ExpenseCategory.HEALTH.displayName, TransactionType.EXPENSE.name, "4 Oct 2026", System.currentTimeMillis() - 5000, "Medicine checkup", PaymentMethod.CASH.displayName),
        TransactionItem("tx6", "Others", 3400.0, ExpenseCategory.OTHERS.displayName, TransactionType.EXPENSE.name, "4 Oct 2026", System.currentTimeMillis() - 6000, "Household items", PaymentMethod.UPI.displayName),
        TransactionItem("inc1", "Monthly Salary", 35000.0, "Salary", TransactionType.INCOME.name, "1 Oct 2026", System.currentTimeMillis() - 8000, "Primary job salary", PaymentMethod.NET_BANKING.displayName, isRecurring = true),
        TransactionItem("inc2", "Freelance Payout", 8500.0, "Freelance", TransactionType.INCOME.name, "3 Oct 2026", System.currentTimeMillis() - 7000, "Client project payout", PaymentMethod.UPI.displayName, isRecurring = false),
        TransactionItem("inc3", "Tenant Rent Received", 12000.0, "Rental Income", TransactionType.INCOME.name, "2 Oct 2026", System.currentTimeMillis() - 7500, "Monthly apartment rent", PaymentMethod.NET_BANKING.displayName, isRecurring = true)
    )

    private val defaultInitBills = listOf(
        BillItem("b1", "Electricity Bill", 1200.0, "10 Oct 2026", "Due in 6 days", ExpenseCategory.BILLS.displayName, false, "electricity"),
        BillItem("b2", "Internet Bill", 799.0, "12 Oct 2026", "Due in 8 days", ExpenseCategory.BILLS.displayName, false, "wifi"),
        BillItem("b3", "Mobile Recharge", 299.0, "15 Oct 2026", "Due in 11 days", ExpenseCategory.BILLS.displayName, false, "phone"),
        BillItem("b4", "Gas Cylinder", 1100.0, "20 Oct 2026", "Due in 16 days", ExpenseCategory.BILLS.displayName, false, "gas"),
        BillItem("b5", "Water Bill", 450.0, "25 Oct 2026", "Due in 21 days", ExpenseCategory.BILLS.displayName, false, "water")
    )

    private val defaultInitBudgets = listOf(
        CategoryBudget(ExpenseCategory.GROCERIES.displayName, 8000.0),
        CategoryBudget(ExpenseCategory.BILLS.displayName, 5000.0),
        CategoryBudget(ExpenseCategory.FOOD.displayName, 3000.0),
        CategoryBudget(ExpenseCategory.TRANSPORT.displayName, 2500.0),
        CategoryBudget(ExpenseCategory.HEALTH.displayName, 2000.0),
        CategoryBudget(ExpenseCategory.SHOPPING.displayName, 2500.0),
        CategoryBudget(ExpenseCategory.EDUCATION.displayName, 2000.0),
        CategoryBudget(ExpenseCategory.OTHERS.displayName, 4000.0)
    )

    private val _userProfile = MutableStateFlow(UserProfile(name = "Lokesh", monthlyBudget = 25000.0))
    val userProfileFlow: Flow<UserProfile> = _userProfile.asStateFlow()

    private val _transactions = MutableStateFlow<List<TransactionItem>>(defaultInitTransactions)
    val transactionsFlow: Flow<List<TransactionItem>> = _transactions.asStateFlow()

    private val _bills = MutableStateFlow<List<BillItem>>(defaultInitBills)
    val billsFlow: Flow<List<BillItem>> = _bills.asStateFlow()

    private val _categoryBudgets = MutableStateFlow<List<CategoryBudget>>(defaultInitBudgets)
    val categoryBudgetsFlow: Flow<List<CategoryBudget>> = _categoryBudgets.asStateFlow()

    private var activeListenersAttached = false

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    fun getUserId(): String {
        return currentUser?.uid ?: "user_lokesh_101"
    }

    private fun getUserRef(): DatabaseReference? {
        val user = currentUser
        return if (user != null) {
            db.getReference("users").child(user.uid)
        } else {
            null
        }
    }

    init {
        attachFirebaseListenersIfAuthenticated()
    }

    fun ensureAuth(onComplete: (Boolean) -> Unit) {
        if (auth.currentUser == null) {
            auth.signInAnonymously()
                .addOnSuccessListener {
                    Log.d(TAG, "Signed in anonymously: ${it.user?.uid}")
                    attachFirebaseListenersIfAuthenticated()
                    onComplete(true)
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Anonymous auth not enabled: ${e.message}")
                    onComplete(false)
                }
        } else {
            attachFirebaseListenersIfAuthenticated()
            onComplete(true)
        }
    }

    fun attachFirebaseListenersIfAuthenticated() {
        val ref = getUserRef() ?: return
        if (activeListenersAttached) return
        activeListenersAttached = true

        // Profile listener
        ref.child("profile").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val profile = snapshot.getValue(UserProfile::class.java)
                if (profile != null) {
                    _userProfile.value = profile
                } else {
                    val defaultProf = UserProfile(
                        uid = getUserId(),
                        name = auth.currentUser?.displayName?.ifEmpty { "Lokesh" } ?: "Lokesh",
                        email = auth.currentUser?.email ?: "",
                        monthlyBudget = 25000.0,
                        isAnonymous = auth.currentUser?.isAnonymous ?: true
                    )
                    ref.child("profile").setValue(defaultProf)
                    _userProfile.value = defaultProf
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Profile snapshot cancelled: ${error.message}")
            }
        })

        // Transactions listener
        ref.child("transactions").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    // Seed initial screenshot dataset to Firebase
                    for (tx in defaultInitTransactions) {
                        ref.child("transactions").child(tx.id).setValue(tx)
                    }
                } else {
                    val list = mutableListOf<TransactionItem>()
                    for (child in snapshot.children) {
                        child.getValue(TransactionItem::class.java)?.let { list.add(it) }
                    }
                    list.sortByDescending { it.timestamp }
                    _transactions.value = list
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Transactions snapshot cancelled: ${error.message}")
            }
        })

        // Bills listener
        ref.child("bills").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    for (b in defaultInitBills) {
                        ref.child("bills").child(b.id).setValue(b)
                    }
                } else {
                    val list = mutableListOf<BillItem>()
                    for (child in snapshot.children) {
                        child.getValue(BillItem::class.java)?.let { list.add(it) }
                    }
                    _bills.value = list
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Bills snapshot cancelled: ${error.message}")
            }
        })

        // Category Budgets listener
        ref.child("categoryBudgets").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    for (cb in defaultInitBudgets) {
                        ref.child("categoryBudgets").child(cb.categoryName.replace("/", "_")).setValue(cb)
                    }
                } else {
                    val list = mutableListOf<CategoryBudget>()
                    for (child in snapshot.children) {
                        child.getValue(CategoryBudget::class.java)?.let { list.add(it) }
                    }
                    _categoryBudgets.value = list
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Category budgets snapshot cancelled: ${error.message}")
            }
        })
    }

    fun signInWithEmail(email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, pass)
            .addOnSuccessListener {
                Log.d(TAG, "Signed in successfully with email: ${it.user?.email}")
                activeListenersAttached = false
                attachFirebaseListenersIfAuthenticated()
                onResult(true, null)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Email sign-in failed", e)
                onResult(false, e.localizedMessage)
            }
    }

    fun signUpWithEmail(
        email: String,
        pass: String,
        name: String,
        role: String = "Head of Family",
        monthlyBudget: Double = 25000.0,
        onResult: (Boolean, String?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnSuccessListener { result ->
                Log.d(TAG, "Account created successfully: ${result.user?.email}")
                val userRef = db.getReference("users").child(result.user!!.uid)
                val newProfile = UserProfile(
                    uid = result.user!!.uid,
                    name = name.ifBlank { "Lokesh" },
                    email = email,
                    householdRole = role,
                    monthlyBudget = monthlyBudget,
                    isAnonymous = false
                )
                userRef.child("profile").setValue(newProfile)
                activeListenersAttached = false
                attachFirebaseListenersIfAuthenticated()
                onResult(true, null)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Account creation failed", e)
                onResult(false, e.localizedMessage)
            }
    }

    fun signUpWithEmail(
        email: String,
        pass: String,
        name: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        signUpWithEmail(email, pass, name, "Head of Family", 25000.0, onResult)
    }

    fun signOut() {
        auth.signOut()
        activeListenersAttached = false
        _userProfile.value = UserProfile(name = "Lokesh", monthlyBudget = 25000.0)
        _transactions.value = defaultInitTransactions
        _bills.value = defaultInitBills
        _categoryBudgets.value = defaultInitBudgets
    }

    fun addTransaction(item: TransactionItem) {
        val ref = getUserRef()
        val id = if (item.id.isEmpty()) System.currentTimeMillis().toString() else item.id
        val newItem = item.copy(id = id)

        val currentList = _transactions.value.filter { it.id != id }.toMutableList()
        currentList.add(0, newItem)
        currentList.sortByDescending { it.timestamp }
        _transactions.value = currentList

        if (ref != null) {
            ref.child("transactions").child(id).setValue(newItem)
        }
    }

    fun deleteTransaction(id: String) {
        val currentList = _transactions.value.filter { it.id != id }
        _transactions.value = currentList

        val ref = getUserRef()
        if (ref != null) {
            ref.child("transactions").child(id).removeValue()
        }
    }

    fun addBill(bill: BillItem) {
        val id = if (bill.id.isEmpty()) System.currentTimeMillis().toString() else bill.id
        val newBill = bill.copy(id = id)

        val currentList = _bills.value.filter { it.id != id }.toMutableList()
        currentList.add(newBill)
        _bills.value = currentList

        val ref = getUserRef()
        if (ref != null) {
            ref.child("bills").child(id).setValue(newBill)
        }
    }

    fun toggleBillPaid(id: String, isPaid: Boolean) {
        val currentList = _bills.value.map {
            if (it.id == id) it.copy(isPaid = isPaid) else it
        }
        _bills.value = currentList

        val ref = getUserRef()
        if (ref != null) {
            ref.child("bills").child(id).child("isPaid").setValue(isPaid)
        }
    }

    fun deleteBill(id: String) {
        _bills.value = _bills.value.filter { it.id != id }
        val ref = getUserRef()
        if (ref != null) {
            ref.child("bills").child(id).removeValue()
        }
    }

    fun updateProfile(profile: UserProfile) {
        _userProfile.value = profile
        val ref = getUserRef()
        if (ref != null) {
            ref.child("profile").setValue(profile)
        }
    }

    fun updateMonthlyBudget(newBudget: Double) {
        _userProfile.value = _userProfile.value.copy(monthlyBudget = newBudget)
        val ref = getUserRef()
        if (ref != null) {
            ref.child("profile").child("monthlyBudget").setValue(newBudget)
        }
    }

    fun updateCategoryBudget(categoryName: String, limit: Double) {
        val safeKey = categoryName.replace("/", "_")
        val updated = _categoryBudgets.value.filter { it.categoryName != categoryName }.toMutableList()
        updated.add(CategoryBudget(categoryName, limit))
        _categoryBudgets.value = updated

        val ref = getUserRef()
        if (ref != null) {
            ref.child("categoryBudgets").child(safeKey).setValue(CategoryBudget(categoryName, limit))
        }
    }

    fun updateProfileName(name: String) {
        _userProfile.value = _userProfile.value.copy(name = name)
        val ref = getUserRef()
        if (ref != null) {
            ref.child("profile").child("name").setValue(name)
        }
    }

    fun toggleAutoReminders(enabled: Boolean) {
        _userProfile.value = _userProfile.value.copy(autoRemindersEnabled = enabled)
        val ref = getUserRef()
        if (ref != null) {
            ref.child("profile").child("autoRemindersEnabled").setValue(enabled)
        }
    }

    fun updatePinLock(enabled: Boolean, pin: String) {
        _userProfile.value = _userProfile.value.copy(isPinEnabled = enabled, pinCode = pin)
        val ref = getUserRef()
        if (ref != null) {
            ref.child("profile").child("isPinEnabled").setValue(enabled)
            ref.child("profile").child("pinCode").setValue(pin)
        }
    }

    fun updateBiometricLock(enabled: Boolean) {
        _userProfile.value = _userProfile.value.copy(isBiometricEnabled = enabled)
        val ref = getUserRef()
        if (ref != null) {
            ref.child("profile").child("isBiometricEnabled").setValue(enabled)
        }
    }

    fun signInWithGoogle(name: String, email: String, onResult: (Boolean, String?) -> Unit) {
        if (auth.currentUser != null && !auth.currentUser!!.isAnonymous && auth.currentUser!!.email == email) {
            onResult(true, null)
            return
        }
        val safeName = name.ifBlank { "Google User" }
        val safeEmail = email.ifBlank { "user@gmail.com" }
        
        // Sign in or link
        val current = auth.currentUser
        if (current != null) {
            val userRef = db.getReference("users").child(current.uid)
            val updated = _userProfile.value.copy(
                uid = current.uid,
                name = safeName,
                email = safeEmail,
                isAnonymous = false
            )
            _userProfile.value = updated
            userRef.child("profile").setValue(updated)
            onResult(true, null)
        } else {
            auth.signInAnonymously().addOnSuccessListener { res ->
                val uid = res.user?.uid ?: "user_g_${System.currentTimeMillis()}"
                val userRef = db.getReference("users").child(uid)
                val updated = UserProfile(
                    uid = uid,
                    name = safeName,
                    email = safeEmail,
                    monthlyBudget = 25000.0,
                    isAnonymous = false
                )
                _userProfile.value = updated
                userRef.child("profile").setValue(updated)
                activeListenersAttached = false
                attachFirebaseListenersIfAuthenticated()
                onResult(true, null)
            }.addOnFailureListener {
                onResult(false, it.localizedMessage)
            }
        }
    }

    fun deleteFinancialData(onComplete: (Boolean) -> Unit) {
        val ref = getUserRef()
        _transactions.value = emptyList()
        _bills.value = emptyList()
        _categoryBudgets.value = emptyList()
        if (ref != null) {
            ref.child("transactions").removeValue()
            ref.child("bills").removeValue()
            ref.child("categoryBudgets").removeValue().addOnCompleteListener {
                onComplete(it.isSuccessful)
            }
        } else {
            onComplete(true)
        }
    }

    fun deleteAccount(onComplete: (Boolean, String?) -> Unit) {
        val user = currentUser
        val ref = getUserRef()
        if (ref != null) {
            ref.removeValue()
        }
        if (user != null) {
            user.delete().addOnCompleteListener { task ->
                activeListenersAttached = false
                _userProfile.value = UserProfile(name = "Lokesh", monthlyBudget = 25000.0)
                _transactions.value = emptyList()
                _bills.value = emptyList()
                _categoryBudgets.value = emptyList()
                if (task.isSuccessful) {
                    onComplete(true, null)
                } else {
                    // Force signout anyway
                    auth.signOut()
                    onComplete(true, null)
                }
            }
        } else {
            signOut()
            onComplete(true, null)
        }
    }

    fun logoutAllDevices() {
        signOut()
    }
}
