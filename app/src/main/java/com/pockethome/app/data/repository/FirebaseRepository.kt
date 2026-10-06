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
import com.pockethome.app.data.model.SavingsGoal
import com.pockethome.app.data.model.TransactionItem
import com.pockethome.app.data.model.TransactionType
import com.pockethome.app.data.model.UserProfile
import com.pockethome.app.data.model.UtrPaymentRequest
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

    // Clean initial values - 100% real Firebase RTDB
    private val defaultInitTransactions = emptyList<TransactionItem>()
    private val defaultInitBills = emptyList<BillItem>()
    private val defaultInitBudgets = emptyList<CategoryBudget>()

    private val _userProfile = MutableStateFlow(UserProfile(name = "Lokesh", monthlyBudget = 25000.0))
    val userProfileFlow: Flow<UserProfile> = _userProfile.asStateFlow()

    private val _transactions = MutableStateFlow<List<TransactionItem>>(emptyList())
    val transactionsFlow: Flow<List<TransactionItem>> = _transactions.asStateFlow()

    private val _bills = MutableStateFlow<List<BillItem>>(emptyList())
    val billsFlow: Flow<List<BillItem>> = _bills.asStateFlow()

    private val _categoryBudgets = MutableStateFlow<List<CategoryBudget>>(emptyList())
    val categoryBudgetsFlow: Flow<List<CategoryBudget>> = _categoryBudgets.asStateFlow()

    private val _paymentRequests = MutableStateFlow<List<UtrPaymentRequest>>(emptyList())
    val paymentRequestsFlow: Flow<List<UtrPaymentRequest>> = _paymentRequests.asStateFlow()

    private val _savingsGoals = MutableStateFlow<List<SavingsGoal>>(emptyList())
    val savingsGoalsFlow: Flow<List<SavingsGoal>> = _savingsGoals.asStateFlow()

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
                    _transactions.value = emptyList()
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
                    _bills.value = emptyList()
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
                    _categoryBudgets.value = emptyList()
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

        // Global Payment Requests listener (for Admin & User updates)
        db.getReference("payment_requests").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<UtrPaymentRequest>()
                for (child in snapshot.children) {
                    child.getValue(UtrPaymentRequest::class.java)?.let { req ->
                        list.add(req)
                        // If this payment request is for the current user and was APPROVED by Admin, unlock PRO!
                        if (req.userId == getUserId() && req.status == "APPROVED" && !_userProfile.value.isProUser) {
                            val updated = _userProfile.value.copy(
                                isProUser = true,
                                subscriptionTier = req.planTier,
                                proExpiryDate = "Oct 2027"
                            )
                            _userProfile.value = updated
                            ref.child("profile").setValue(updated)
                        }
                    }
                }
                list.sortByDescending { it.timestamp }
                _paymentRequests.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Payment requests snapshot cancelled: ${error.message}")
            }
        })

        // Savings Goals listener
        ref.child("savingsGoals").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    _savingsGoals.value = emptyList()
                } else {
                    val list = mutableListOf<SavingsGoal>()
                    for (child in snapshot.children) {
                        child.getValue(SavingsGoal::class.java)?.let { list.add(it) }
                    }
                    _savingsGoals.value = list
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Savings goals snapshot cancelled: ${error.message}")
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

    fun submitUtrPaymentRequest(
        utrNumber: String,
        transactionRef: String,
        planTier: String,
        amount: Double,
        onResult: (Boolean, String?) -> Unit
    ) {
        val user = currentUser
        val reqId = "req_${System.currentTimeMillis()}"
        val request = UtrPaymentRequest(
            requestId = reqId,
            userId = user?.uid ?: getUserId(),
            userName = _userProfile.value.name,
            userEmail = user?.email ?: _userProfile.value.email,
            phonePeNumber = "9050884894",
            utrNumber = utrNumber.trim(),
            transactionRef = transactionRef.trim(),
            planTier = planTier,
            amount = amount,
            timestamp = System.currentTimeMillis(),
            status = "PENDING"
        )

        db.getReference("payment_requests").child(reqId).setValue(request)
            .addOnSuccessListener {
                getUserRef()?.child("payment_requests")?.child(reqId)?.setValue(request)
                onResult(true, null)
            }
            .addOnFailureListener {
                onResult(false, it.localizedMessage)
            }
    }

    fun approveUtrPaymentRequest(
        request: UtrPaymentRequest,
        onResult: (Boolean, String?) -> Unit
    ) {
        val reqRef = db.getReference("payment_requests").child(request.requestId)
        val userRef = db.getReference("users").child(request.userId)

        reqRef.child("status").setValue("APPROVED")
        userRef.child("payment_requests").child(request.requestId).child("status").setValue("APPROVED")

        val updates = mapOf<String, Any>(
            "isProUser" to true,
            "subscriptionTier" to request.planTier,
            "proExpiryDate" to "Oct 2027"
        )

        userRef.child("profile").updateChildren(updates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (request.userId == getUserId()) {
                    _userProfile.value = _userProfile.value.copy(
                        isProUser = true,
                        subscriptionTier = request.planTier,
                        proExpiryDate = "Oct 2027"
                    )
                }
                onResult(true, null)
            } else {
                onResult(false, task.exception?.localizedMessage)
            }
        }
    }

    fun rejectUtrPaymentRequest(requestId: String, userId: String, onResult: (Boolean, String?) -> Unit) {
        db.getReference("payment_requests").child(requestId).child("status").setValue("REJECTED")
        db.getReference("users").child(userId).child("payment_requests").child(requestId).child("status").setValue("REJECTED")
            .addOnCompleteListener {
                onResult(it.isSuccessful, null)
            }
    }

    fun addSavingsGoal(goal: SavingsGoal) {
        val ref = getUserRef() ?: return
        val goalId = if (goal.id.isBlank()) "goal_${System.currentTimeMillis()}" else goal.id
        val finalGoal = goal.copy(id = goalId)
        ref.child("savingsGoals").child(goalId).setValue(finalGoal)
    }

    fun updateSavingsGoal(goal: SavingsGoal) {
        val ref = getUserRef() ?: return
        if (goal.id.isNotBlank()) {
            ref.child("savingsGoals").child(goal.id).setValue(goal)
        }
    }

    fun deleteSavingsGoal(goalId: String) {
        val ref = getUserRef() ?: return
        ref.child("savingsGoals").child(goalId).removeValue()
    }

    fun togglePauseSavingsGoal(goalId: String) {
        val current = _savingsGoals.value.find { it.id == goalId } ?: return
        val updated = current.copy(isPaused = !current.isPaused)
        updateSavingsGoal(updated)
    }

    fun addMoneyToSavingsGoal(goalId: String, amount: Double) {
        val current = _savingsGoals.value.find { it.id == goalId } ?: return
        val newSaved = current.currentSaved + amount
        val isDone = newSaved >= current.targetAmount
        val updated = current.copy(currentSaved = newSaved, isCompleted = isDone)
        updateSavingsGoal(updated)
    }
}
