package com.pockethome.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pockethome.app.ui.viewmodel.GrihaUiState

data class AccentTheme(
    val name: String,
    val hex: String,
    val emoji: String
)

val AvailableAccentThemes = listOf(
    AccentTheme("Royal Indigo", "#4F46E5", "👑"),
    AccentTheme("Emerald Wealth", "#059669", "💎"),
    AccentTheme("Sunset Amber", "#D97706", "🌅"),
    AccentTheme("Velvet Rose", "#E11D48", "🌹"),
    AccentTheme("Electric Cyan", "#0891B2", "⚡"),
    AccentTheme("Mystic Purple", "#9333EA", "🔮")
)

val AvailableCurrencies = listOf(
    Pair("₹", "INR (Indian Rupee)"),
    Pair("$", "USD (US Dollar)"),
    Pair("€", "EUR (Euro)"),
    Pair("£", "GBP (British Pound)"),
    Pair("د.إ", "AED (UAE Dirham)"),
    Pair("﷼", "SAR (Saudi Riyal)"),
    Pair("د.ك", "KWD (Kuwaiti Dinar)"),
    Pair("S$", "SGD (Singapore Dollar)"),
    Pair("C$", "CAD (Canadian Dollar)"),
    Pair("A$", "AUD (Australian Dollar)"),
    Pair("¥", "JPY (Japanese Yen)")
)

val AvailableLanguages = listOf(
    Pair("English", "🇬🇧 English (Global)"),
    Pair("Hindi", "🇮🇳 हिन्दी (Hindi)"),
    Pair("Hinglish", "🇮🇳 Hinglish (Mix)"),
    Pair("Gujarati", "🇮🇳 ગુજરાતી (Gujarati)"),
    Pair("Marathi", "🇮🇳 मराठी (Marathi)"),
    Pair("Tamil", "🇮🇳 தமிழ் (Tamil)"),
    Pair("Bengali", "🇮🇳 বাংলা (Bengali)")
)

val AvatarEmojis = listOf("👑", "👨‍💼", "👩‍💼", "🧑‍💻", "🧕", "👨‍👩‍👧", "🚀", "🦁", "💎", "🎯", "🌟", "🛡️")

@Composable
fun PersonalizationScreen(
    state: GrihaUiState,
    onBackClick: () -> Unit,
    onUpdateTheme: (themeMode: String, accentHex: String) -> Unit,
    onUpdateCurrency: (code: String, name: String) -> Unit,
    onUpdateLanguage: (language: String) -> Unit,
    onUpdateDashboardPreferences: (showDaily: Boolean, showGoals: Boolean, showInsights: Boolean, showDonut: Boolean) -> Unit,
    onUpdateCustomProfile: (name: String, emoji: String, role: String, monthlyBudget: Double) -> Unit
) {
    val context = LocalContext.current

    var selectedThemeMode by remember { mutableStateOf(state.profile.themeMode) }
    var selectedAccentHex by remember { mutableStateOf(state.profile.accentThemeHex) }
    var selectedCurrencyCode by remember { mutableStateOf(state.profile.currencyCode) }
    var selectedCurrencyName by remember { mutableStateOf(state.profile.currencyName) }
    var selectedLanguage by remember { mutableStateOf(state.profile.language) }

    // Dashboard Switches
    var showDailySpent by remember { mutableStateOf(state.profile.showDailySpentBanner) }
    var showSavingsGoals by remember { mutableStateOf(state.profile.showSavingsGoalsWidget) }
    var showSmartInsights by remember { mutableStateOf(state.profile.showSmartInsightsWidget) }
    var showCategoryDonut by remember { mutableStateOf(state.profile.showCategoryDonut) }

    // Profile Fields
    var profileName by remember { mutableStateOf(state.profile.name) }
    var profileAvatar by remember { mutableStateOf(state.profile.avatarEmoji) }
    var profileRole by remember { mutableStateOf(state.profile.householdRole) }
    var profileBudget by remember { mutableStateOf(state.profile.monthlyBudget.toInt().toString()) }

    val activeAccentColor = try {
        Color(android.graphics.Color.parseColor(selectedAccentHex))
    } catch (e: Exception) {
        Color(0xFF4F46E5)
    }

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
                modifier = Modifier.testTag("personalization_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = textDark
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "UI & Personalization 🎨",
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Hero Live Preview Card
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = activeAccentColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(activeAccentColor, activeAccentColor.copy(alpha = 0.85f))
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(50.dp)
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.22f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = profileAvatar, fontSize = 26.sp)
                                    }
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column {
                                        Text(
                                            text = profileName,
                                            style = MaterialTheme.typography.titleLarge.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        )
                                        Text(
                                            text = "$profileRole • $selectedLanguage",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.White.copy(alpha = 0.85f)
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color.White.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "$selectedCurrencyCode Active",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Monthly Budget: $selectedCurrencyCode${String.format("%,d", profileBudget.toIntOrNull() ?: 25000)}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Section 1: Theme & Display Mode
            item {
                Text(
                    text = "Display Mode & Theme 🌓",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = textDark
                )
            }

            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Light / Dark / System Segmented Switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                Triple("Light", "☀️ Light", Icons.Default.LightMode),
                                Triple("Dark", "🌙 Dark", Icons.Default.DarkMode),
                                Triple("System", "⚙️ Auto", Icons.Default.ColorLens)
                            ).forEach { (mode, label, icon) ->
                                val isSelected = selectedThemeMode == mode
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) activeAccentColor else Color(0xFFF1F5F9),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            selectedThemeMode = mode
                                            onUpdateTheme(mode, selectedAccentHex)
                                            Toast.makeText(context, "$mode Theme Activated", Toast.LENGTH_SHORT).show()
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 10.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                            ),
                                            color = if (isSelected) Color.White else textDark
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Custom Accent Colors
                        Text(
                            text = "Accent Color Palette:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = textMuted
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            AvailableAccentThemes.forEach { theme ->
                                val color = Color(android.graphics.Color.parseColor(theme.hex))
                                val isSelected = selectedAccentHex == theme.hex

                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = Color(0xFFF8FAFC),
                                    border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, color) else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                    modifier = Modifier.clickable {
                                        selectedAccentHex = theme.hex
                                        onUpdateTheme(selectedThemeMode, theme.hex)
                                        Toast.makeText(context, "${theme.name} Applied", Toast.LENGTH_SHORT).show()
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(16.dp)
                                                .clip(CircleShape)
                                                .background(color)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = theme.name,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                            ),
                                            color = textDark
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Section 2: Currency Selection 🌐
            item {
                Text(
                    text = "Currency Format 🌐",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = textDark
                )
            }

            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AvailableCurrencies.forEach { (code, name) ->
                            val isSelected = selectedCurrencyCode == code
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) activeAccentColor.copy(alpha = 0.12f) else Color(0xFFF8FAFC),
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, activeAccentColor) else null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedCurrencyCode = code
                                        selectedCurrencyName = name
                                        onUpdateCurrency(code, name)
                                        Toast.makeText(context, "Currency set to $name", Toast.LENGTH_SHORT).show()
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = code, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = activeAccentColor)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(text = name, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = textDark)
                                    }
                                    if (isSelected) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = activeAccentColor, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Quick Currency Conversion & Format Preview Card
            item {
                var convAmount by remember { mutableStateOf("100") }
                val inputVal = convAmount.toDoubleOrNull() ?: 100.0

                // Reference exchange rates (approx relative to 1 USD)
                val inrVal = inputVal * 84.0
                val aedVal = inputVal * 3.67
                val eurVal = inputVal * 0.92
                val gbpVal = inputVal * 0.77

                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "💱 Live Multi-Currency Converter", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = textDark)
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = convAmount,
                            onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) convAmount = it },
                            label = { Text("Base USD ($) Amount") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                Triple("₹ INR", "₹${String.format("%,.0f", inrVal)}", Color(0xFF16A34A)),
                                Triple("د.إ AED", "${String.format("%,.1f", aedVal)} AED", Color(0xFFD97706)),
                                Triple("€ EUR", "€${String.format("%,.1f", eurVal)}", Color(0xFF2563EB)),
                                Triple("£ GBP", "£${String.format("%,.1f", gbpVal)}", Color(0xFF7C3AED))
                            ).forEach { (cur, result, color) ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color.White,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(text = cur, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = color, fontSize = 10.sp)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(text = result, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = textDark, fontSize = 11.sp, maxLines = 1)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Section 3: App Language 🗣️
            item {
                Text(
                    text = "App Language 🗣️",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = textDark
                )
            }

            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AvailableLanguages.forEach { (langKey, label) ->
                            val isSelected = selectedLanguage == langKey
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) activeAccentColor.copy(alpha = 0.12f) else Color(0xFFF8FAFC),
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, activeAccentColor) else null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedLanguage = langKey
                                        onUpdateLanguage(langKey)
                                        Toast.makeText(context, "Language changed to $langKey", Toast.LENGTH_SHORT).show()
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = label, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal), color = textDark)
                                    if (isSelected) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = activeAccentColor, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Section 4: Dashboard Layout Customization 📱
            item {
                Text(
                    text = "Dashboard Layout & Widgets 📱",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = textDark
                )
            }

            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Daily Spent Banner
                        DashboardToggleRow(
                            title = "Daily Spending Banner",
                            subtitle = "Show 'Aaj ka kharcha' on top of Home",
                            checked = showDailySpent,
                            onCheckedChange = {
                                showDailySpent = it
                                onUpdateDashboardPreferences(it, showSavingsGoals, showSmartInsights, showCategoryDonut)
                            }
                        )

                        // Savings Goals Widget
                        DashboardToggleRow(
                            title = "Savings Goals Quick Widget",
                            subtitle = "Display phone, bike, vacation progress",
                            checked = showSavingsGoals,
                            onCheckedChange = {
                                showSavingsGoals = it
                                onUpdateDashboardPreferences(showDailySpent, it, showSmartInsights, showCategoryDonut)
                            }
                        )

                        // Smart Insights
                        DashboardToggleRow(
                            title = "Smart AI Spending Insights",
                            subtitle = "AI suggestions & projected month-end forecast",
                            checked = showSmartInsights,
                            onCheckedChange = {
                                showSmartInsights = it
                                onUpdateDashboardPreferences(showDailySpent, showSavingsGoals, it, showCategoryDonut)
                            }
                        )

                        // Category Donut
                        DashboardToggleRow(
                            title = "Category Breakdown Chart",
                            subtitle = "Visual distribution donut on Dashboard",
                            checked = showCategoryDonut,
                            onCheckedChange = {
                                showCategoryDonut = it
                                onUpdateDashboardPreferences(showDailySpent, showSavingsGoals, showSmartInsights, it)
                            }
                        )
                    }
                }
            }

            // Section 5: Custom Profile & Avatar 👑
            item {
                Text(
                    text = "Custom Profile & Avatar 👑",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = textDark
                )
            }

            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Select Profile Avatar Emoji:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = textMuted)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AvatarEmojis.forEach { em ->
                                val isSelected = profileAvatar == em
                                Surface(
                                    shape = CircleShape,
                                    color = if (isSelected) activeAccentColor.copy(alpha = 0.15f) else Color(0xFFF1F5F9),
                                    border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, activeAccentColor) else null,
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .clickable { profileAvatar = em }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(text = em, fontSize = 22.sp)
                                    }
                                }
                            }
                        }

                        OutlinedTextField(
                            value = profileName,
                            onValueChange = { profileName = it },
                            label = { Text("Display Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = profileRole,
                            onValueChange = { profileRole = it },
                            label = { Text("Household Role (e.g. Head of Family / Admin)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = profileBudget,
                            onValueChange = { if (it.all { c -> c.isDigit() }) profileBudget = it },
                            label = { Text("Default Monthly Budget (₹)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                val b = profileBudget.toDoubleOrNull() ?: 25000.0
                                onUpdateCustomProfile(profileName, profileAvatar, profileRole, b)
                                Toast.makeText(context, "Profile Updated Successfully!", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = activeAccentColor),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Save Profile Changes", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun DashboardToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = Color(0xFF0F172A))
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF4F46E5)
            )
        )
    }
}
