package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.AppNotification
import com.example.data.model.ClusterLedgerEntry
import com.example.data.model.DuesBill
import com.example.data.model.Resident
import com.example.data.repository.DuesRepository
import com.example.ui.components.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CashSummary(
    val totalIncome: Long = 0,
    val totalExpense: Long = 0,
    val currentBalance: Long = 0
)

class DuesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DuesRepository

    val residents: StateFlow<List<Resident>>
    private val _selectedResident = MutableStateFlow<Resident?>(null)
    val selectedResident: StateFlow<Resident?> = _selectedResident.asStateFlow()

    val billsForResident: StateFlow<List<DuesBill>>
    val unpaidBillsForResident: StateFlow<List<DuesBill>>
    val allBills: StateFlow<List<DuesBill>>
    val ledgerEntries: StateFlow<List<ClusterLedgerEntry>>
    val notifications: StateFlow<List<AppNotification>>
    val unreadNotificationCount: StateFlow<Int>
    val cashSummary: StateFlow<CashSummary>

    // UI state for modals
    private val _payingBill = MutableStateFlow<DuesBill?>(null)
    val payingBill: StateFlow<DuesBill?> = _payingBill.asStateFlow()

    private val _activeReceiptBill = MutableStateFlow<DuesBill?>(null)
    val activeReceiptBill: StateFlow<DuesBill?> = _activeReceiptBill.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = DuesRepository(database.duesDao())

        // Initial seeding in background
        viewModelScope.launch {
            repository.seedInitialData()
        }

        residents = repository.allResidents.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        // Set default resident when residents list is loaded
        viewModelScope.launch {
            residents.collect { list ->
                if (_selectedResident.value == null && list.isNotEmpty()) {
                    _selectedResident.value = list.firstOrNull { it.id == "A1-05" } ?: list.first()
                }
            }
        }

        billsForResident = _selectedResident.flatMapLatest { resident ->
            if (resident != null) repository.getBillsForResident(resident.id) else flowOf(emptyList())
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        unpaidBillsForResident = _selectedResident.flatMapLatest { resident ->
            if (resident != null) repository.getUnpaidBillsForResident(resident.id) else flowOf(emptyList())
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        allBills = repository.allBills.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        ledgerEntries = repository.allLedgerEntries.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        notifications = repository.allNotifications.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        unreadNotificationCount = repository.unreadNotificationCount.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            0
        )

        cashSummary = ledgerEntries.combine(allBills) { entries, _ ->
            var income = 0L
            var expense = 0L
            entries.forEach { entry ->
                if (entry.type == "PEMASUKAN") {
                    income += entry.amount
                } else {
                    expense += entry.amount
                }
            }
            CashSummary(
                totalIncome = income,
                totalExpense = expense,
                currentBalance = income - expense
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            CashSummary()
        )
    }

    fun selectResident(resident: Resident) {
        _selectedResident.value = resident
    }

    fun openPayment(bill: DuesBill) {
        _payingBill.value = bill
    }

    fun dismissPayment() {
        _payingBill.value = null
    }

    fun showReceipt(bill: DuesBill) {
        _activeReceiptBill.value = bill
    }

    fun dismissReceipt() {
        _activeReceiptBill.value = null
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun payBill(billId: Long, method: String) {
        viewModelScope.launch {
            val updated = repository.payBill(billId, method)
            _payingBill.value = null
            if (updated != null) {
                _activeReceiptBill.value = updated
                _toastMessage.value = "Pembayaran iuran ${updated.monthYear} sebesar Rp ${formatCurrency(updated.totalAmount)} berhasil diverifikasi!"

                // Show Android system notification
                NotificationHelper.showSystemNotification(
                    getApplication(),
                    "Pembayaran Iuran Berhasil ✓",
                    "Kwitansi resmi terbit untuk ${updated.blockNumber} - ${updated.monthYear}. Terima kasih atas kepedulian Anda."
                )
            }
        }
    }

    fun triggerBillNotification() {
        viewModelScope.launch {
            val resident = _selectedResident.value ?: return@launch
            val unpaid = unpaidBillsForResident.value
            val totalDue = unpaid.sumOf { it.totalAmount }

            repository.triggerMonthlyReminder(
                residentId = resident.id,
                residentName = resident.residentName,
                blockNumber = resident.blockNumber,
                unpaidCount = if (unpaid.isEmpty()) 1 else unpaid.size,
                totalDue = if (totalDue == 0L) 250000L else totalDue
            )

            NotificationHelper.showSystemNotification(
                getApplication(),
                "Pengingat Tagihan Iuran Bulanan 🔔",
                "Tagihan Iuran Cluster Gandana untuk ${resident.blockNumber} sebesar Rp ${formatCurrency(if (totalDue == 0L) 250000L else totalDue)} jatuh tempo tanggal 10. Bayar sekarang via QRIS/VA."
            )

            _toastMessage.value = "Notifikasi pengingat iuran bulanan berhasil dikirim ke perangkat & pusat notifikasi!"
        }
    }

    fun markNotificationAsRead(id: Long) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    fun markAllNotificationsAsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsAsRead()
            _toastMessage.value = "Semua notifikasi telah ditandai dibaca"
        }
    }

    private fun formatCurrency(amount: Long): String {
        return "%,d".format(java.util.Locale("id", "ID"), amount).replace(',', '.')
    }
}
