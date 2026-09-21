package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.DuesBill
import com.example.data.model.Resident
import com.example.ui.CashSummary
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.AmberGold
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldMedium
import com.example.ui.theme.GreenIncome
import com.example.ui.theme.RedExpense
import com.example.ui.theme.SlateLight
import com.example.ui.theme.SlateMuted
import com.example.ui.theme.SlateNavy
import java.util.Locale

@Composable
fun HomeScreen(
    selectedResident: Resident?,
    residents: List<Resident>,
    bills: List<DuesBill>,
    unpaidBills: List<DuesBill>,
    cashSummary: CashSummary,
    unreadNotificationCount: Int,
    onSelectResident: (Resident) -> Unit,
    onPayBill: (DuesBill) -> Unit,
    onViewReceipt: (DuesBill) -> Unit,
    onNavigateToBills: () -> Unit,
    onNavigateToTransparency: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onTriggerNotificationTest: () -> Unit
) {
    var showResidentPicker by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // App Top Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = EmeraldDark,
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Cluster Gandana",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SlateNavy
                        )
                        Text(
                            text = "Portal Iuran Warga & Kas RT",
                            style = MaterialTheme.typography.bodySmall,
                            color = SlateMuted
                        )
                    }
                }

                // Notification Bell with badge
                IconButton(
                    onClick = onNavigateToNotifications,
                    modifier = Modifier.testTag("notification_bell_button")
                ) {
                    BadgedBox(
                        badge = {
                            if (unreadNotificationCount > 0) {
                                Badge(
                                    containerColor = AmberGold,
                                    contentColor = Color.White
                                ) {
                                    Text(text = unreadNotificationCount.toString())
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Pusat Notifikasi",
                            tint = SlateNavy
                        )
                    }
                }
            }
        }

        // Hero Banner with neighborhood gate illustration
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .height(140.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_cluster_hero),
                    contentDescription = "Gerbang Cluster Gandana",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0xCC0A3622)),
                                startY = 40f
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Surface(
                        color = AmberGold,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "Cluster Siaga & Terbuka",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Iuran Transparan, Lingkungan Aman",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Resident Selector Card
        item {
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedCard(
                colors = CardDefaults.outlinedCardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clickable { showResidentPicker = !showResidentPicker }
                    .testTag("resident_selector_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(EmeraldLight, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = null,
                                tint = EmeraldDark,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = selectedResident?.blockNumber ?: "Pilih Unit",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = SlateNavy
                            )
                            Text(
                                text = "${selectedResident?.residentName ?: "-"} (${selectedResident?.occupancyStatus ?: ""})",
                                style = MaterialTheme.typography.bodySmall,
                                color = SlateMuted
                            )
                        }
                    }
                    Surface(
                        color = SlateLight,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (showResidentPicker) "Tutup" else "Ganti Unit",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = EmeraldDark,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Expandable Resident list for easy demo testing
            if (showResidentPicker) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SlateLight),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Pilih Unit Warga (Simulasi Pengguna):",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = SlateNavy
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        residents.forEach { res ->
                            val isCurrent = res.id == selectedResident?.id
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isCurrent) EmeraldLight else Color.White)
                                    .clickable {
                                        onSelectResident(res)
                                        showResidentPicker = false
                                    }
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "${res.blockNumber} - ${res.residentName}",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isCurrent) EmeraldDark else SlateNavy
                                    )
                                    Text(
                                        text = res.occupancyStatus,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SlateMuted
                                    )
                                }
                                if (isCurrent) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Aktif",
                                        tint = EmeraldDark,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Active Monthly Bill Card
        item {
            Spacer(modifier = Modifier.height(16.dp))
            val currentBill = unpaidBills.firstOrNull() ?: bills.firstOrNull()

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (unpaidBills.isNotEmpty()) Color(0xFFFFFBEB) else EmeraldContainer
                ),
                shape = RoundedCornerShape(20.dp),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(
                        if (unpaidBills.isNotEmpty()) AmberGold.copy(alpha = 0.5f) else EmeraldMedium.copy(alpha = 0.3f)
                    )
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .testTag("current_bill_card")
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (unpaidBills.isNotEmpty()) AmberGold else EmeraldMedium
                        ) {
                            Text(
                                text = if (unpaidBills.isNotEmpty()) "MENUNGGU PEMBAYARAN" else "SEMUA TAGIHAN LUNAS ✓",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        Text(
                            text = currentBill?.monthYear ?: "September 2026",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = SlateNavy
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (unpaidBills.isNotEmpty()) {
                        Text(
                            text = "Total Tagihan Berjalan:",
                            style = MaterialTheme.typography.bodySmall,
                            color = SlateMuted
                        )
                        val totalUnpaid = unpaidBills.sumOf { it.totalAmount }
                        Text(
                            text = "Rp %,d".format(Locale("id", "ID"), totalUnpaid).replace(',', '.'),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = SlateNavy
                        )
                        Text(
                            text = "Jatuh tempo: ${currentBill?.dueDate ?: "10 September 2026"} (${unpaidBills.size} bulan)",
                            style = MaterialTheme.typography.bodySmall,
                            color = RedExpense,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Fee breakdowns preview
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            FeeItemRow("Keamanan & Satpam 24 Jam", 150000)
                            FeeItemRow("Kebersihan & Truk Sampah", 50000)
                            FeeItemRow("Fasum, Lampu PJU & Sosial", 50000)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { currentBill?.let { onPayBill(it) } },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("pay_dues_button")
                            ) {
                                Icon(imageVector = Icons.Default.Payment, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Bayar Iuran", color = Color.White, fontWeight = FontWeight.Bold)
                            }

                            FilledTonalButton(
                                onClick = onTriggerNotificationTest,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .height(48.dp)
                                    .testTag("remind_me_button")
                            ) {
                                Icon(imageVector = Icons.Default.NotificationsActive, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Ingatkan")
                            }
                        }
                    } else {
                        // All Paid state
                        Text(
                            text = "Tidak Ada Tagihan Tertunggak",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Terima kasih telah membayar iuran tepat waktu untuk lingkungan yang lebih aman dan nyaman.",
                            style = MaterialTheme.typography.bodySmall,
                            color = SlateNavy
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        currentBill?.let { paidBill ->
                            OutlinedButton(
                                onClick = { onViewReceipt(paidBill) },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("view_last_receipt_button")
                            ) {
                                Icon(imageVector = Icons.Default.ReceiptLong, contentDescription = null, tint = EmeraldDark)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Lihat Kwitansi Terakhir (${paidBill.monthYear})", color = EmeraldDark)
                            }
                        }
                    }
                }
            }
        }

        // Quick Transparency Highlights Section
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Transparansi Kas Cluster",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SlateNavy
                    )
                    Text(
                        text = "Laporan real-time kas warga terbuka",
                        style = MaterialTheme.typography.bodySmall,
                        color = SlateMuted
                    )
                }

                Text(
                    text = "Lihat Buku Kas",
                    color = EmeraldDark,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable(onClick = onNavigateToTransparency)
                        .testTag("see_all_transparency_button")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Cash overview mini card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(SlateLight)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clickable(onClick = onNavigateToTransparency)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Saldo Kas Cluster Gandana:",
                            style = MaterialTheme.typography.bodySmall,
                            color = SlateMuted
                        )
                        Text(
                            text = "Rp %,d".format(Locale("id", "ID"), cashSummary.currentBalance).replace(',', '.'),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = "Masuk: Rp %,d".format(Locale("id", "ID"), cashSummary.totalIncome).replace(',', '.'),
                                style = MaterialTheme.typography.labelSmall,
                                color = GreenIncome
                            )
                            Text(
                                text = "Keluar: Rp %,d".format(Locale("id", "ID"), cashSummary.totalExpense).replace(',', '.'),
                                style = MaterialTheme.typography.labelSmall,
                                color = RedExpense
                            )
                        }
                    }

                    Surface(
                        shape = CircleShape,
                        color = EmeraldLight,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = EmeraldDark,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }

        // Quick feature navigation banners
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Menu & Layanan Warga",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = SlateNavy,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Feature 1: Tagihan & Riwayat Saya
                ActionFeatureCard(
                    title = "Tagihan & Riwayat",
                    subtitle = "Cek kwitansi & status",
                    icon = Icons.Default.ReceiptLong,
                    iconBg = EmeraldLight,
                    iconTint = EmeraldDark,
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onNavigateToBills)
                        .testTag("quick_bills_menu")
                )

                // Feature 2: Transparansi Kas & Warga
                ActionFeatureCard(
                    title = "Transparansi Kas",
                    subtitle = "Audit kas & status warga",
                    icon = Icons.Default.VerifiedUser,
                    iconBg = AmberContainer,
                    iconTint = AmberGold,
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onNavigateToTransparency)
                        .testTag("quick_transparency_menu")
                )
            }
        }

        // Notification shortcut button
        item {
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedCard(
                colors = CardDefaults.outlinedCardColors(containerColor = SlateLight.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clickable(onClick = onTriggerNotificationTest)
                    .testTag("send_test_notification_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = EmeraldDark,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Simulasi Notifikasi Tagihan Bulanan",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = SlateNavy
                            )
                            Text(
                                text = "Klik untuk uji coba kirim pengingat tagihan ke perangkat",
                                style = MaterialTheme.typography.bodySmall,
                                color = SlateMuted
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = SlateMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun FeeItemRow(name: String, amount: Long) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = name, style = MaterialTheme.typography.bodySmall, color = SlateNavy)
        Text(
            text = "Rp %,d".format(Locale("id", "ID"), amount).replace(',', '.'),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = SlateNavy
        )
    }
}

@Composable
fun ActionFeatureCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(SlateLight)
        ),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(iconBg, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = SlateNavy
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = SlateMuted
            )
        }
    }
}
