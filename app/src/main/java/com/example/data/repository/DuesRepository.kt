package com.example.data.repository

import com.example.data.local.DuesDao
import com.example.data.model.AppNotification
import com.example.data.model.ClusterLedgerEntry
import com.example.data.model.DuesBill
import com.example.data.model.Resident
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DuesRepository(private val duesDao: DuesDao) {

    val allResidents: Flow<List<Resident>> = duesDao.getAllResidents()
    val allLedgerEntries: Flow<List<ClusterLedgerEntry>> = duesDao.getAllLedgerEntries()
    val allNotifications: Flow<List<AppNotification>> = duesDao.getAllNotifications()
    val unreadNotificationCount: Flow<Int> = duesDao.getUnreadNotificationCount()
    val allBills: Flow<List<DuesBill>> = duesDao.getAllBills()

    fun getBillsForResident(residentId: String): Flow<List<DuesBill>> {
        return duesDao.getBillsForResident(residentId)
    }

    fun getUnpaidBillsForResident(residentId: String): Flow<List<DuesBill>> {
        return duesDao.getUnpaidBillsForResident(residentId)
    }

    fun getUnpaidCountForResident(residentId: String): Flow<Int> {
        return duesDao.getUnpaidCountForResident(residentId)
    }

    suspend fun getResidentById(id: String): Resident? {
        return duesDao.getResidentById(id)
    }

    suspend fun markNotificationAsRead(id: Long) {
        duesDao.markNotificationAsRead(id)
    }

    suspend fun markAllNotificationsAsRead() {
        duesDao.markAllNotificationsAsRead()
    }

    suspend fun payBill(billId: Long, paymentMethod: String): DuesBill? {
        val bill = duesDao.getBillById(billId) ?: return null
        if (bill.isPaid) return bill

        val now = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
        val timeFormat = SimpleDateFormat("HH:mm", Locale("id", "ID"))
        val dateStr = dateFormat.format(Date(now))
        val timeStr = timeFormat.format(Date(now))
        val randomNum = (1000..9999).random()
        val txRef = "GDN-${bill.periodIndex}-$randomNum"

        val updatedBill = bill.copy(
            isPaid = true,
            paidAt = now,
            paymentMethod = paymentMethod,
            transactionRef = txRef
        )
        duesDao.updateBill(updatedBill)

        // Insert into cluster transparency ledger (PEMASUKAN)
        val ledgerEntry = ClusterLedgerEntry(
            dateStr = dateStr,
            timestamp = now,
            type = "PEMASUKAN",
            category = "Iuran Warga",
            description = "Iuran ${bill.monthYear} (${bill.blockNumber} - ${bill.residentName})",
            amount = bill.totalAmount,
            recordedBy = "Sistem Kasir Otomatis",
            receiptNumber = txRef
        )
        duesDao.insertLedgerEntry(ledgerEntry)

        // Add payment confirmation notification
        val notification = AppNotification(
            title = "Pembayaran Iuran Berhasil ✓",
            message = "Terima kasih! Iuran ${bill.monthYear} untuk ${bill.blockNumber} senilai Rp ${formatRupiah(bill.totalAmount)} via $paymentMethod telah lunas dan masuk kas warga.",
            dateStr = "$dateStr, $timeStr WIB",
            timestamp = now,
            type = "PAYMENT_CONFIRMED",
            targetResidentId = bill.residentId
        )
        duesDao.insertNotification(notification)

        return updatedBill
    }

    suspend fun triggerMonthlyReminder(residentId: String, residentName: String, blockNumber: String, unpaidCount: Int, totalDue: Long) {
        val now = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
        val dateStr = "${dateFormat.format(Date(now))} WIB"

        val notification = AppNotification(
            title = "Pengingat Iuran Bulanan 🔔",
            message = "Halo $residentName ($blockNumber), Anda memiliki $unpaidCount tagihan iuran belum dibayar sebesar Rp ${formatRupiah(totalDue)}. Mohon selesaikan sebelum tanggal 10.",
            dateStr = dateStr,
            timestamp = now,
            type = "DUE_REMINDER",
            targetResidentId = residentId
        )
        duesDao.insertNotification(notification)
    }

    suspend fun seedInitialData() {
        // Seed Residents
        val residents = listOf(
            Resident(
                id = "A1-05",
                blockNumber = "Blok A1 No. 05",
                residentName = "Bpk. Hendra Gunawan",
                phone = "0812-8921-3341",
                occupancyStatus = "Pemilik Tetap"
            ),
            Resident(
                id = "A1-06",
                blockNumber = "Blok A1 No. 06",
                residentName = "Ibu Siti Rahmawati",
                phone = "0813-7722-1092",
                occupancyStatus = "Pemilik Tetap"
            ),
            Resident(
                id = "A2-01",
                blockNumber = "Blok A2 No. 01",
                residentName = "Bpk. Bambang Wijaya",
                phone = "0856-4433-2211",
                occupancyStatus = "Pemilik Tetap"
            ),
            Resident(
                id = "B1-03",
                blockNumber = "Blok B1 No. 03",
                residentName = "Bpk. Doni Pratama",
                phone = "0819-0123-4567",
                occupancyStatus = "Penyewa"
            ),
            Resident(
                id = "B2-10",
                blockNumber = "Blok B2 No. 10",
                residentName = "Bpk. Surya Kusuma",
                phone = "0812-9988-7766",
                occupancyStatus = "Pemilik Tetap"
            ),
            Resident(
                id = "B2-12",
                blockNumber = "Blok B2 No. 12",
                residentName = "Bpk. Ahmad Fauzi",
                phone = "0817-6655-4433",
                occupancyStatus = "Pemilik Tetap"
            )
        )
        duesDao.insertResidents(residents)

        // Seed Bills
        val bills = mutableListOf<DuesBill>()
        val currentTime = System.currentTimeMillis()
        val oneDayMillis = 86400000L

        residents.forEach { r ->
            // Juli 2026 - Lunas
            bills.add(
                DuesBill(
                    residentId = r.id,
                    residentName = r.residentName,
                    blockNumber = r.blockNumber,
                    monthYear = "Juli 2026",
                    periodIndex = 202607,
                    dueDate = "10 Juli 2026",
                    isPaid = true,
                    paidAt = currentTime - (70 * oneDayMillis),
                    paymentMethod = if (r.id == "A1-05") "QRIS Cluster Gandana" else "BCA Virtual Account",
                    transactionRef = "GDN-202607-${(1000..9999).random()}"
                )
            )
            // Agustus 2026 - Lunas
            bills.add(
                DuesBill(
                    residentId = r.id,
                    residentName = r.residentName,
                    blockNumber = r.blockNumber,
                    monthYear = "Agustus 2026",
                    periodIndex = 202608,
                    dueDate = "10 Agustus 2026",
                    isPaid = true,
                    paidAt = currentTime - (38 * oneDayMillis),
                    paymentMethod = "BCA Virtual Account",
                    transactionRef = "GDN-202608-${(1000..9999).random()}"
                )
            )
            // September 2026 - Tagihan Berjalan
            // For A1-05 (default active resident), let it be UNPAID so user can test paying it!
            val isSepPaid = (r.id != "A1-05" && r.id != "B1-03")
            bills.add(
                DuesBill(
                    residentId = r.id,
                    residentName = r.residentName,
                    blockNumber = r.blockNumber,
                    monthYear = "September 2026",
                    periodIndex = 202609,
                    dueDate = "10 September 2026",
                    isPaid = isSepPaid,
                    paidAt = if (isSepPaid) currentTime - (12 * oneDayMillis) else null,
                    paymentMethod = if (isSepPaid) "QRIS Cluster Gandana" else null,
                    transactionRef = if (isSepPaid) "GDN-202609-${(1000..9999).random()}" else null
                )
            )
            // Oktober 2026 - Tagihan Mendatang (Unpaid)
            bills.add(
                DuesBill(
                    residentId = r.id,
                    residentName = r.residentName,
                    blockNumber = r.blockNumber,
                    monthYear = "Oktober 2026",
                    periodIndex = 202610,
                    dueDate = "10 Oktober 2026",
                    isPaid = false,
                    paidAt = null,
                    paymentMethod = null,
                    transactionRef = null
                )
            )
        }
        duesDao.insertBills(bills)

        // Seed Cluster Ledger (Community Financial Transparency)
        val ledger = listOf(
            ClusterLedgerEntry(
                dateStr = "15 Sep 2026",
                timestamp = currentTime - (6 * oneDayMillis),
                type = "PENGELUARAN",
                category = "Gaji Keamanan",
                description = "Gaji 3 Petugas Satpam Cluster Gandana Shift Siang & Malam",
                amount = 7500000,
                recordedBy = "Bendahara Kas RW",
                receiptNumber = "KW-OUT-202609-01"
            ),
            ClusterLedgerEntry(
                dateStr = "12 Sep 2026",
                timestamp = currentTime - (9 * oneDayMillis),
                type = "PEMASUKAN",
                category = "Iuran Warga",
                description = "Pembayaran Iuran September 2026 (Blok A1-06 Ibu Siti Rahmawati)",
                amount = 250000,
                recordedBy = "Sistem Kasir Otomatis",
                receiptNumber = "GDN-202609-4821"
            ),
            ClusterLedgerEntry(
                dateStr = "11 Sep 2026",
                timestamp = currentTime - (10 * oneDayMillis),
                type = "PEMASUKAN",
                category = "Iuran Warga",
                description = "Pembayaran Iuran September 2026 (Blok A2-01 Bpk. Bambang Wijaya)",
                amount = 250000,
                recordedBy = "Sistem Kasir Otomatis",
                receiptNumber = "GDN-202609-3329"
            ),
            ClusterLedgerEntry(
                dateStr = "08 Sep 2026",
                timestamp = currentTime - (13 * oneDayMillis),
                type = "PENGELUARAN",
                category = "Kebersihan",
                description = "Biaya Truk Pengangkutan Sampah Cluster & Pembersihan Saluran Got",
                amount = 1800000,
                recordedBy = "Bendahara Kas RW",
                receiptNumber = "KW-OUT-202609-02"
            ),
            ClusterLedgerEntry(
                dateStr = "05 Sep 2026",
                timestamp = currentTime - (16 * oneDayMillis),
                type = "PENGELUARAN",
                category = "Fasilitas Umum",
                description = "Penggantian 4 Unit Bohlam Lampu PJU LED & Perawatan Otomasi Gerbang",
                amount = 650000,
                recordedBy = "Seksi Sarpras",
                receiptNumber = "KW-OUT-202609-03"
            ),
            ClusterLedgerEntry(
                dateStr = "01 Sep 2026",
                timestamp = currentTime - (20 * oneDayMillis),
                type = "PEMASUKAN",
                category = "Saldo Kas Awal",
                description = "Sisa Saldo Kas Cluster Gandana Buku Kas Bulan Agustus 2026",
                amount = 21450000,
                recordedBy = "Laporan Pertanggungjawaban",
                receiptNumber = "SALDO-AWAL-SEP"
            )
        )
        duesDao.insertLedgerEntries(ledger)

        // Seed Notifications
        val notifications = listOf(
            AppNotification(
                title = "Tagihan Iuran September 2026 Telah Terbit",
                message = "Tagihan iuran keamanan, kebersihan, dan fasilitas umum bulan September telah diterbitkan. Batas akhir pembayaran tgl 10 September 2026.",
                dateStr = "01 Sep 2026, 08:00 WIB",
                timestamp = currentTime - (20 * oneDayMillis),
                type = "BILL_ISSUED",
                isRead = true
            ),
            AppNotification(
                title = "Pengingat Batas Jatuh Tempo ⚠️",
                message = "Pengingat bagi warga yang belum melakukan pembayaran iuran bulan September 2026, mohon segera menyelesaikan sebelum dikenakan denda administrasi.",
                dateStr = "08 Sep 2026, 09:30 WIB",
                timestamp = currentTime - (13 * oneDayMillis),
                type = "DUE_REMINDER",
                isRead = false,
                targetResidentId = "A1-05"
            ),
            AppNotification(
                title = "Transparansi Kas Cluster Gandana Rilis",
                message = "Laporan buku kas dan bukti pengeluaran cluster periode Agustus telah dipublikasikan di menu Riwayat Transparan. Saldo sehat dan terverifikasi.",
                dateStr = "05 Sep 2026, 14:15 WIB",
                timestamp = currentTime - (16 * oneDayMillis),
                type = "REPORT_PUBLISHED",
                isRead = false
            )
        )
        duesDao.insertNotifications(notifications)
    }

    private fun formatRupiah(amount: Long): String {
        return "%,d".format(Locale("id", "ID"), amount).replace(',', '.')
    }
}
