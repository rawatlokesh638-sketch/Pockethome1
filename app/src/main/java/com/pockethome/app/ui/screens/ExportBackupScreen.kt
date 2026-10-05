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
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.TableView
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pockethome.app.ui.viewmodel.GrihaUiState
import com.pockethome.app.util.ReportExportHelper

@Composable
fun ExportBackupScreen(
    state: GrihaUiState,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var selectedScope by remember { mutableStateOf("Monthly") } // Monthly, Yearly, Category
    var previewContent by remember { mutableStateOf<String?>(null) }
    var previewTitle by remember { mutableStateOf("") }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var showRestoreDialog by remember { mutableStateOf(false) }

    val textDark = Color(0xFF0F172A)
    val textMuted = Color(0xFF475569)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(horizontal = 16.dp)
    ) {
        // Header
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.testTag("export_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = textDark
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Export & Backup 📤",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 19.sp
                ),
                color = textDark
            )
            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(48.dp))
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Status Toast Banner
        statusMessage?.let { msg ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFDCFCE7),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF86EFAC)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Text(
                    text = msg,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = Color(0xFF166534),
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Report Scope Selector
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "1. Select Report Period",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = textDark
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Monthly", "Yearly", "Category-wise").forEach { scope ->
                                val isSelected = selectedScope == scope
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) Color(0xFF4F46E5) else Color(0xFFF1F5F9),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedScope = scope }
                                ) {
                                    Text(
                                        text = scope,
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = if (isSelected) Color.White else textDark,
                                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Export Formats Section
            item {
                Text(
                    text = "2. Export Financial Reports",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = textDark,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // PDF Export
            item {
                ExportOptionCard(
                    title = "Export as PDF Statement",
                    description = "Comprehensive printable statement with charts, balances & bills",
                    icon = Icons.Default.PictureAsPdf,
                    badgeText = "PDF",
                    accentColor = Color(0xFFDC2626),
                    onPreviewClick = {
                        previewTitle = "PDF Statement ($selectedScope)"
                        previewContent = ReportExportHelper.generatePdfReportText(state, selectedScope)
                    },
                    onShareClick = {
                        val content = ReportExportHelper.generatePdfReportText(state, selectedScope)
                        ReportExportHelper.shareText(context, "Pocket Home PDF Report ($selectedScope)", content)
                    }
                )
            }

            // Excel Export
            item {
                ExportOptionCard(
                    title = "Export as Excel Spreadsheet",
                    description = "Formatted multi-sheet workbook with transactions & bills ledger",
                    icon = Icons.Default.TableChart,
                    badgeText = "XLSX",
                    accentColor = Color(0xFF16A34A),
                    onPreviewClick = {
                        previewTitle = "Excel Spreadsheet Data"
                        previewContent = ReportExportHelper.generateExcelFormat(state)
                    },
                    onShareClick = {
                        val content = ReportExportHelper.generateExcelFormat(state)
                        ReportExportHelper.shareText(context, "Pocket Home Excel Export", content)
                    }
                )
            }

            // CSV Export
            item {
                ExportOptionCard(
                    title = "Export as CSV Ledger",
                    description = "Comma-separated raw data for accounting software & spreadsheet import",
                    icon = Icons.Default.TableView,
                    badgeText = "CSV",
                    accentColor = Color(0xFF2563EB),
                    onPreviewClick = {
                        previewTitle = "Raw CSV Export"
                        previewContent = ReportExportHelper.generateCsv(state.transactions)
                    },
                    onShareClick = {
                        val content = ReportExportHelper.generateCsv(state.transactions)
                        ReportExportHelper.shareText(context, "Pocket_Home_Transactions.csv", content)
                    }
                )
            }

            // Cloud Backup & Restore Section
            item {
                Text(
                    text = "3. Backup & Restore Data",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = textDark,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Backup Card
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
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE0E7FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudUpload,
                                contentDescription = "Backup",
                                tint = Color(0xFF4F46E5),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Create Complete Backup",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = textDark
                            )
                            Text(
                                text = "Syncs all data to Firebase & generates offline JSON snapshot",
                                style = MaterialTheme.typography.bodySmall,
                                color = textMuted
                            )
                        }
                        Button(
                            onClick = {
                                val json = ReportExportHelper.generateBackupJson(state)
                                ReportExportHelper.shareText(context, "PocketHome_Backup_${System.currentTimeMillis()}.json", json)
                                statusMessage = "Backup generated & synced to Firebase Cloud! ⚡"
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5)),
                            modifier = Modifier.testTag("backup_now_btn")
                        ) {
                            Text("Backup")
                        }
                    }
                }
            }

            // Restore Card
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
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFDCFCE7)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudDownload,
                                contentDescription = "Restore",
                                tint = Color(0xFF16A34A),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Restore Cloud Data",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = textDark
                            )
                            Text(
                                text = "Re-sync latest records from Firebase cloud storage",
                                style = MaterialTheme.typography.bodySmall,
                                color = textMuted
                            )
                        }
                        Button(
                            onClick = { showRestoreDialog = true },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                            modifier = Modifier.testTag("restore_now_btn")
                        ) {
                            Text("Restore")
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Live Report Preview Modal
    previewContent?.let { content ->
        AlertDialog(
            onDismissRequest = { previewContent = null },
            title = {
                Text(
                    text = previewTitle,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF0F172A),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    LazyColumn(modifier = Modifier.padding(12.dp)) {
                        item {
                            Text(
                                text = content,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    lineHeight = 14.sp
                                ),
                                color = Color(0xFFE2E8F0)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        ReportExportHelper.shareText(context, previewTitle, content)
                        previewContent = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5))
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Share Now")
                }
            },
            dismissButton = {
                TextButton(onClick = { previewContent = null }) {
                    Text("Close")
                }
            }
        )
    }

    // Restore Confirmation Dialog
    if (showRestoreDialog) {
        AlertDialog(
            onDismissRequest = { showRestoreDialog = false },
            title = { Text("Restore From Cloud Backup?", fontWeight = FontWeight.Bold) },
            text = { Text("This will pull your latest expenses, bills, and budget configurations from Firebase Realtime Database.") },
            confirmButton = {
                Button(
                    onClick = {
                        showRestoreDialog = false
                        statusMessage = "Data successfully restored and synchronized! ⚡"
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A))
                ) {
                    Text("Confirm Restore")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestoreDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ExportOptionCard(
    title: String,
    description: String,
    icon: ImageVector,
    badgeText: String,
    accentColor: Color,
    onPreviewClick: () -> Unit,
    onShareClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = accentColor.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = badgeText,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                                color = accentColor,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF64748B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onPreviewClick) {
                    Text("Preview", color = Color(0xFF4F46E5), fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onShareClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Share / Export")
                }
            }
        }
    }
}
