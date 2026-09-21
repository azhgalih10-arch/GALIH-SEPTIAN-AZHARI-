package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DuesBill
import com.example.data.model.Resident
import com.example.ui.theme.AmberGold
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldMedium
import com.example.ui.theme.RedExpense
import com.example.ui.theme.SlateLight
import com.example.ui.theme.SlateMuted
import com.example.ui.theme.SlateNavy
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BillsScreen(
    resident: Resident?,
    bills: List<DuesBill>,
    onPayBill: (DuesBill) -> Unit,
    onViewReceipt: (DuesBill) -> Unit
) {
    var selectedFilter by remember { mutableStateOf("Semua") }

    val filteredBills = when (selectedFilter) {
        "Belum Lunas" -> bills.filter { !it.isPaid }
        "Lunas" -> bills.filter { it.isPaid }
        else -> bills
    }

    val totalPending = bills.filter { !it.isPaid }.sumOf { it.totalAmount }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("bills_screen"),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "Tagihan & Riwayat Iuran",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = SlateNavy
                )
                Text(
                    text = "${resident?.blockNumber ?: "Unit Rumah"} • ${resident?.residentName ?: ""}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SlateMuted
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Summary Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = EmeraldDark),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Total Tagihan Belum Dibayar",
                        style = MaterialTheme.typography.bodySmall,
                        color = EmeraldLight
                    )
                    Text(
                        text = "Rp %,d".format(Locale("id", "ID"), totalPending).replace(',', '.'),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Besaran Iuran: Rp 250.000 / bulan",
                            style = MaterialTheme.typography.bodySmall,
                            color = EmeraldLight.copy(alpha = 0.9f)
                        )
                        Surface(
                            color = if (totalPending > 0) AmberGold else EmeraldMedium,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = if (totalPending > 0) "${bills.count { !it.isPaid }} Bulan Tertunda" else "Lunas Bebas Tunggakan",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Filter chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Semua", "Belum Lunas", "Lunas").forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldDark,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Bills List
        if (filteredBills.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = EmeraldMedium,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tidak ada tagihan dalam filter ini",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SlateMuted
                        )
                    }
                }
            }
        } else {
            items(filteredBills, key = { it.id }) { bill ->
                BillCardItem(
                    bill = bill,
                    onPay = { onPayBill(bill) },
                    onViewReceipt = { onViewReceipt(bill) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun BillCardItem(
    bill: DuesBill,
    onPay: () -> Unit,
    onViewReceipt: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (bill.isPaid) Color.White else Color(0xFFFFFDF5)
        ),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                if (bill.isPaid) SlateLight else AmberGold.copy(alpha = 0.4f)
            )
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("bill_card_${bill.periodIndex}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Month & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = if (bill.isPaid) EmeraldLight else Color(0xFFFEF3C7),
                        modifier = Modifier.size(34.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (bill.isPaid) Icons.Default.CheckCircle else Icons.Default.Schedule,
                                contentDescription = null,
                                tint = if (bill.isPaid) EmeraldDark else AmberGold,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = bill.monthYear,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SlateNavy
                        )
                        Text(
                            text = if (bill.isPaid) "Lunas pada ${formatPaidDate(bill.paidAt)}" else "Jatuh Tempo: ${bill.dueDate}",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (bill.isPaid) EmeraldDark else RedExpense
                        )
                    }
                }

                Surface(
                    color = if (bill.isPaid) EmeraldContainer else Color(0xFFFEE2E2),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (bill.isPaid) "LUNAS ✓" else "BELUM BAYAR",
                        color = if (bill.isPaid) EmeraldDark else RedExpense,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = SlateLight)
            Spacer(modifier = Modifier.height(10.dp))

            // Breakdown of fees
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Keamanan & Satpam", style = MaterialTheme.typography.bodySmall, color = SlateMuted)
                Text(text = "Rp 150.000", style = MaterialTheme.typography.bodySmall, color = SlateNavy)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Kebersihan & Sampah", style = MaterialTheme.typography.bodySmall, color = SlateMuted)
                Text(text = "Rp 50.000", style = MaterialTheme.typography.bodySmall, color = SlateNavy)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Fasum & Kas Sosial", style = MaterialTheme.typography.bodySmall, color = SlateMuted)
                Text(text = "Rp 50.000", style = MaterialTheme.typography.bodySmall, color = SlateNavy)
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = SlateLight)
            Spacer(modifier = Modifier.height(8.dp))

            // Total & Action Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Total Iuran", style = MaterialTheme.typography.bodySmall, color = SlateMuted)
                    Text(
                        text = "Rp %,d".format(Locale("id", "ID"), bill.totalAmount).replace(',', '.'),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SlateNavy
                    )
                }

                if (bill.isPaid) {
                    OutlinedButton(
                        onClick = onViewReceipt,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("view_receipt_${bill.periodIndex}")
                    ) {
                        Icon(imageVector = Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Kwitansi")
                    }
                } else {
                    Button(
                        onClick = onPay,
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("pay_bill_${bill.periodIndex}")
                    ) {
                        Icon(imageVector = Icons.Default.Payment, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Bayar", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

private fun formatPaidDate(timestamp: Long?): String {
    if (timestamp == null) return "-"
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
    return sdf.format(Date(timestamp))
}
