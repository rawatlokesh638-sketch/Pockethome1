package com.pockethome.app.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pockethome.app.ui.viewmodel.GrihaUiState

val HouseholdRoleList = listOf(
    Pair("Head of Family", "👑"),
    Pair("Primary Earner", "💼"),
    Pair("Homemaker", "🏡"),
    Pair("Student", "🎓")
)

@Composable
fun WelcomeAuthScreen(
    state: GrihaUiState,
    onSignInWithEmail: (email: String, pass: String, onResult: (Boolean, String?) -> Unit) -> Unit,
    onSignUpWithEmail: (email: String, pass: String, name: String, role: String, monthlyBudget: Double, onResult: (Boolean, String?) -> Unit) -> Unit,
    onQuickStart: (name: String, email: String, onResult: (Boolean, String?) -> Unit) -> Unit,
    onAuthSuccess: () -> Unit
) {
    val context = LocalContext.current
    var isSignUpMode by remember { mutableStateOf(true) }

    var fullName by remember { mutableStateOf(state.profile.name.ifBlank { "Lokesh Sharma" }) }
    var emailText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var selectedRole by remember { mutableStateOf("Head of Family") }
    var budgetTargetText by remember { mutableStateOf("25000") }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val textDark = Color(0xFF0F172A)
    val textMuted = Color(0xFF64748B)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(28.dp))

        // Hero Brand Header
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF4F46E5), Color(0xFF3730A3))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "🏠", fontSize = 36.sp)
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Pocket Home",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                color = textDark
            )
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Apna Ghar, Smart Budget & Family Finance",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                color = textMuted
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFDCFCE7)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.CloudDone, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Firebase Cloud Sync Active ⚡",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF15803D),
                        fontSize = 11.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Mode Switcher (Sign Up vs Log In)
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFFEEF2FF),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                // Sign Up Tab
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSignUpMode) Color(0xFF4F46E5) else Color.Transparent,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            isSignUpMode = true
                            errorMessage = null
                        }
                ) {
                    Text(
                        text = "Sign Up 📝",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isSignUpMode) Color.White else textDark,
                        modifier = Modifier.padding(vertical = 10.dp),
                        textAlign = TextAlign.Center
                    )
                }

                // Log In Tab
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (!isSignUpMode) Color(0xFF4F46E5) else Color.Transparent,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            isSignUpMode = false
                            errorMessage = null
                        }
                ) {
                    Text(
                        text = "Log In 🔑",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (!isSignUpMode) Color.White else textDark,
                        modifier = Modifier.padding(vertical = 10.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Error message banner
        AnimatedVisibility(visible = errorMessage != null) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFFEE2E2),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCA5A5)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Text(
                    text = errorMessage ?: "",
                    color = Color(0xFFB91C1C),
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        // Auth Form Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (isSignUpMode) {
                    // Full Name
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Full Name / Aapka Naam") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = textMuted) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_name_input")
                    )
                }

                // Email Address
                OutlinedTextField(
                    value = emailText,
                    onValueChange = { emailText = it },
                    label = { Text("Email Address") },
                    placeholder = { Text("lokesh@sharma.in") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = textMuted) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_email_input")
                )

                // Password
                OutlinedTextField(
                    value = passwordText,
                    onValueChange = { passwordText = it },
                    label = { Text("Password (Min 6 characters)") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = textMuted) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                tint = textMuted
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_pass_input")
                )

                if (isSignUpMode) {
                    // Household Role Picker
                    Text(
                        text = "Aapka Household Role:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = textDark
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        HouseholdRoleList.forEach { (role, emoji) ->
                            val isSelected = selectedRole == role
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) Color(0xFF4F46E5) else Color(0xFFF1F5F9),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedRole = role }
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 2.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(text = emoji, fontSize = 16.sp)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = role.split(" ").first(),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 9.sp
                                        ),
                                        color = if (isSelected) Color.White else textDark
                                    )
                                }
                            }
                        }
                    }

                    // Monthly Budget Target
                    OutlinedTextField(
                        value = budgetTargetText,
                        onValueChange = { if (it.all { c -> c.isDigit() }) budgetTargetText = it },
                        label = { Text("Monthly Budget Target (₹)") },
                        leadingIcon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = textMuted) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Submit Action Button
                Button(
                    onClick = {
                        if (emailText.isBlank() || !emailText.contains("@")) {
                            errorMessage = "Kripya valid email address enter karein."
                            return@Button
                        }
                        if (passwordText.length < 6) {
                            errorMessage = "Password kam se kam 6 characters ka hona chahiye."
                            return@Button
                        }

                        isLoading = true
                        errorMessage = null

                        if (isSignUpMode) {
                            val budget = budgetTargetText.toDoubleOrNull() ?: 25000.0
                            val name = fullName.ifBlank { "Lokesh Sharma" }
                            onSignUpWithEmail(emailText.trim(), passwordText, name, selectedRole, budget) { success, err ->
                                isLoading = false
                                if (success) {
                                    Toast.makeText(context, "Account Created! Welcome to Pocket Home 🏠", Toast.LENGTH_LONG).show()
                                    onAuthSuccess()
                                } else {
                                    errorMessage = err ?: "Sign up failed. Kripya check karein."
                                }
                            }
                        } else {
                            onSignInWithEmail(emailText.trim(), passwordText) { success, err ->
                                isLoading = false
                                if (success) {
                                    Toast.makeText(context, "Welcome Back! Logged in successfully 🔐", Toast.LENGTH_LONG).show()
                                    onAuthSuccess()
                                } else {
                                    errorMessage = err ?: "Login failed. Kripya password check karein."
                                }
                            }
                        }
                    },
                    enabled = !isLoading,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("auth_submit_btn")
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                    } else {
                        Text(
                            text = if (isSignUpMode) "Create Account with Firebase 🚀" else "Log In to My Account 🔐",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Guest / 1-Tap Start Option
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    isLoading = true
                    onQuickStart("Lokesh Sharma", "lokesh@sharma.in") { success, _ ->
                        isLoading = false
                        Toast.makeText(context, "Welcome! Instant Setup Ready ⚡", Toast.LENGTH_SHORT).show()
                        onAuthSuccess()
                    }
                }
                .testTag("auth_quick_start_btn")
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFEF3C7)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.FlashOn, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(20.dp))
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "1-Tap Quick Start (Guest / Offline Sync)",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = textDark
                    )
                    Text(
                        text = "Bina type kiye turant shuru karein • Sync anytime",
                        style = MaterialTheme.typography.bodySmall,
                        color = textMuted,
                        fontSize = 11.sp
                    )
                }

                Text(
                    text = "Start ➔",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF4F46E5)
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
