package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DuesBill
import com.example.ui.theme.AmberGold
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldMedium
import com.example.ui.theme.SlateLight
import com.example.ui.theme.SlateMuted
import com.example.ui.theme.SlateNavy
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentBottomSheet(
    bill: DuesBill,
    onDismiss: () -> Unit,
    onConfirmPayment: (billId: Long, method: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    var selectedMethod by remember { mutableStateOf("QRIS Cluster Gandana") }

    val bcaVaNumber = "8809 1005 2609 05"
    val mandiriVaNumber = "8912 0052 6090 5"

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("payment_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Pembayaran Iuran Warga",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = SlateNavy
                    )
                    Text(
                        text = "${bill.blockNumber} • Periode ${bill.monthYear}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SlateMuted
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Amount summary card
            Card(
                colors = CardDefaults.cardColors(containerColor = EmeraldLight.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
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
                            text = "Total Pembayaran",
                            style = MaterialTheme.typography.bodySmall,
                            color = SlateMuted
                        )
                        Text(
                            text = "Rp %,d".format(Locale("id", "ID"), bill.totalAmount).replace(',', '.'),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldDark
                        )
                    }
                    Surface(
                        color = EmeraldDark,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Tagihan Resmi",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Pilih Metode Pembayaran:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = SlateNavy
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Method 1: QRIS
            PaymentMethodItem(
                title = "QRIS Cluster Gandana (Instan)",
                description = "Scan melalui GoPay, OVO, Dana, BCA, Livin, dll",
                icon = Icons.Default.QrCode2,
                isSelected = selectedMethod == "QRIS Cluster Gandana",
                onClick = { selectedMethod = "QRIS Cluster Gandana" }
            )

            // Method 2: BCA Virtual Account
            PaymentMethodItem(
                title = "BCA Virtual Account",
                description = "No. VA: $bcaVaNumber",
                icon = Icons.Default.AccountBalance,
                isSelected = selectedMethod == "BCA Virtual Account",
                onClick = { selectedMethod = "BCA Virtual Account" }
            )

            // Method 3: Mandiri Virtual Account
            PaymentMethodItem(
                title = "Mandiri Virtual Account",
                description = "No. VA: $mandiriVaNumber",
                icon = Icons.Default.AccountBalance,
                isSelected = selectedMethod == "Mandiri Virtual Account",
                onClick = { selectedMethod = "Mandiri Virtual Account" }
            )

            // Method 4: Tunai
            PaymentMethodItem(
                title = "Tunai ke Bendahara / Pos Satpam",
                description = "Pembayaran manual langsung ke pengurus RT/RW",
                icon = Icons.Default.Payments,
                isSelected = selectedMethod == "Tunai ke Bendahara",
                onClick = { selectedMethod = "Tunai ke Bendahara" }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Dynamic detail based on selected method
            when (selectedMethod) {
                "QRIS Cluster Gandana" -> {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SlateLight),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "QRIS Standar Pembayaran Nasional",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = SlateNavy
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            // Custom QRIS Pattern Canvas
                            QrisCanvas(bill = bill)

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "NMID: ID1020083912049 • Cluster Gandana RT 04",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = SlateMuted
                            )
                            Text(
                                text = "Otomatis terverifikasi & kwitansi langsung terbit",
                                style = MaterialTheme.typography.bodySmall,
                                color = EmeraldDark,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
                "BCA Virtual Account" -> {
                    VirtualAccountDetailBox(
                        bankName = "BCA",
                        vaNumber = bcaVaNumber,
                        context = context
                    )
                }
                "Mandiri Virtual Account" -> {
                    VirtualAccountDetailBox(
                        bankName = "Mandiri",
                        vaNumber = mandiriVaNumber,
                        context = context
                    )
                }
                "Tunai ke Bendahara" -> {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SlateLight),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Pembayaran Manual:",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = SlateNavy
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Silakan setorkan uang tunai Rp 250.000 ke Pos Satpam Gerbang Utama atau kediaman Bpk. Bambang (Blok A2-01). Klik konfirmasi setelah menyetor untuk menerbitkan kwitansi.",
                                style = MaterialTheme.typography.bodySmall,
                                color = SlateMuted
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Confirmation Button
            Button(
                onClick = { onConfirmPayment(bill.id, selectedMethod) },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("confirm_pay_button")
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Konfirmasi & Bayar Sekarang",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Composable
fun PaymentMethodItem(
    title: String,
    description: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) EmeraldLight.copy(alpha = 0.25f) else Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(if (isSelected) EmeraldDark else SlateLight)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick)
            .testTag("payment_method_${title.take(8)}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(if (isSelected) EmeraldDark else SlateLight, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) Color.White else SlateNavy,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = SlateNavy
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = SlateMuted
                )
            }
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = EmeraldDark)
            )
        }
    }
}

@Composable
fun VirtualAccountDetailBox(
    bankName: String,
    vaNumber: String,
    context: Context
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SlateLight),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Nomor Virtual Account $bankName:",
                style = MaterialTheme.typography.bodySmall,
                color = SlateMuted
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = vaNumber,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = SlateNavy
                )
                IconButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("No. VA $bankName", vaNumber.replace(" ", "")))
                        Toast.makeText(context, "No. VA $bankName disalin!", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Salin Nomor VA",
                        tint = EmeraldDark
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Nama Akun: IURAN GANDANA - BLOK A1/05",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = SlateNavy
            )
            Text(
                text = "Transfer dapat dilakukan melalui m-Banking atau ATM mana saja.",
                style = MaterialTheme.typography.bodySmall,
                fontSize = 11.sp,
                color = SlateMuted
            )
        }
    }
}

@Composable
fun QrisCanvas(bill: DuesBill) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        modifier = Modifier.size(170.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(140.dp)) {
                val step = size.width / 21f
                val darkColor = Color(0xFF1E293B)

                // Outer boundary markers (3 squares)
                fun drawFinder(x: Float, y: Float) {
                    drawRect(color = darkColor, topLeft = Offset(x, y), size = Size(7 * step, 7 * step))
                    drawRect(color = Color.White, topLeft = Offset(x + step, y + step), size = Size(5 * step, 5 * step))
                    drawRect(color = darkColor, topLeft = Offset(x + 2 * step, y + 2 * step), size = Size(3 * step, 3 * step))
                }

                drawFinder(0f, 0f)
                drawFinder(14 * step, 0f)
                drawFinder(0f, 14 * step)

                // Pattern blocks based on bill ID
                for (r in 0..20) {
                    for (c in 0..20) {
                        val inFinder1 = r < 8 && c < 8
                        val inFinder2 = r < 8 && c > 13
                        val inFinder3 = r > 13 && c < 8
                        val inCenter = r in 9..11 && c in 9..11
                        if (!inFinder1 && !inFinder2 && !inFinder3 && !inCenter) {
                            val hash = (r * 17 + c * 31 + bill.id.toInt() * 13) % 7
                            if (hash in 0..2) {
                                drawRect(
                                    color = darkColor,
                                    topLeft = Offset(c * step, r * step),
                                    size = Size(step * 0.9f, step * 0.9f)
                                )
                            }
                        }
                    }
                }
            }

            // Center Badge
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = EmeraldDark,
                modifier = Modifier.size(26.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "CG",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
