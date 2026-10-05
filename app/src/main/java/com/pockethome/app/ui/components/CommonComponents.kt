package com.pockethome.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocalGroceryStore
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pockethome.app.data.model.BillItem
import com.pockethome.app.data.model.ExpenseCategory

private val TextDark = Color(0xFF0F172A)
private val TextMuted = Color(0xFF475569)

@Composable
fun getCategoryIcon(categoryName: String): ImageVector {
    val cat = ExpenseCategory.fromString(categoryName)
    return when (cat) {
        ExpenseCategory.GROCERIES -> Icons.Default.LocalGroceryStore
        ExpenseCategory.BILLS -> Icons.Default.Receipt
        ExpenseCategory.FOOD -> Icons.Default.Restaurant
        ExpenseCategory.TRANSPORT -> Icons.Default.DirectionsCar
        ExpenseCategory.HEALTH -> Icons.Default.Favorite
        ExpenseCategory.SHOPPING -> Icons.Default.ShoppingBag
        ExpenseCategory.EDUCATION -> Icons.Default.School
        ExpenseCategory.HOME -> Icons.Default.Home
        ExpenseCategory.MOBILE -> Icons.Default.PhoneAndroid
        ExpenseCategory.FUEL -> Icons.Default.LocalGasStation
        ExpenseCategory.EMI -> Icons.Default.CreditCard
        ExpenseCategory.ENTERTAINMENT -> Icons.Default.Movie
        ExpenseCategory.CLOTHING -> Icons.Default.Checkroom
        ExpenseCategory.PETS -> Icons.Default.Favorite
        ExpenseCategory.OTHERS -> Icons.Default.MoreHoriz
    }
}

@Composable
fun getCategoryColor(categoryName: String): Color {
    val cat = ExpenseCategory.fromString(categoryName)
    return try {
        Color(android.graphics.Color.parseColor(cat.hexColor))
    } catch (e: Exception) {
        Color(0xFF4F46E5)
    }
}

@Composable
fun CategoryIconBadge(
    categoryName: String,
    modifier: Modifier = Modifier
) {
    val icon = getCategoryIcon(categoryName)
    val color = getCategoryColor(categoryName)

    Box(
        modifier = modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = categoryName,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun QuickActionButton(
    title: String,
    icon: ImageVector,
    iconBgColor: Color,
    iconColor: Color = Color.White,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .testTag(testTag)
            .clickable { onClick() }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(vertical = 16.dp, horizontal = 12.dp)
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp),
                color = TextDark
            )
        }
    }
}

@Composable
fun ExpenseOverviewRow(
    categoryName: String,
    percentage: Int,
    amount: Double,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CategoryIconBadge(categoryName = categoryName)
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = categoryName,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = TextDark,
            modifier = Modifier.weight(1f)
        )
        if (percentage > 0) {
            Text(
                text = "$percentage%",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = TextMuted,
                modifier = Modifier.padding(end = 12.dp)
            )
        }
        Text(
            text = "₹${String.format("%,.0f", amount)}",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = TextDark
        )
    }
}

@Composable
fun getBillIcon(billType: String): ImageVector {
    return when (billType.lowercase()) {
        "electricity" -> Icons.Default.ElectricBolt
        "wifi", "internet" -> Icons.Default.Wifi
        "phone", "recharge", "mobile" -> Icons.Default.PhoneAndroid
        "gas", "cylinder", "lpg" -> Icons.Default.LocalGasStation
        "water" -> Icons.Default.WaterDrop
        "rent" -> Icons.Default.Home
        "insurance" -> Icons.Default.Security
        "emi" -> Icons.Default.CreditCard
        "school", "college", "fees", "education" -> Icons.Default.School
        "subscription", "subscriptions" -> Icons.Default.Subscriptions
        else -> Icons.Default.Receipt
    }
}

@Composable
fun BillCardItem(
    bill: BillItem,
    onTogglePaid: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
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
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (bill.isPaid) Color(0xFFE2E8F0)
                        else Color(0xFFEEF2FF)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getBillIcon(bill.billType),
                    contentDescription = bill.title,
                    tint = if (bill.isPaid) Color(0xFF94A3B8) else Color(0xFF4F46E5),
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = bill.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (bill.isPaid) Color(0xFF94A3B8) else TextDark
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = bill.dueDateString,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "₹${String.format("%,.0f", bill.amount)}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (bill.isPaid) Color(0xFF94A3B8) else TextDark
                )
                Spacer(modifier = Modifier.height(2.dp))
                if (bill.isPaid) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFDCFCE7)
                    ) {
                        Text(
                            text = "Paid",
                            style = MaterialTheme.typography.labelLarge.copy(fontSize = 11.sp),
                            color = Color(0xFF166534),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                } else {
                    Text(
                        text = bill.dueDaysText.ifEmpty { "Due soon" },
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color(0xFFEF4444)
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = { onTogglePaid(!bill.isPaid) },
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        if (bill.isPaid) Color(0xFF22C55E) else Color(0xFFF1F5F9)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Toggle Paid",
                    tint = if (bill.isPaid) Color.White else Color(0xFF64748B)
                )
            }
        }
    }
}
