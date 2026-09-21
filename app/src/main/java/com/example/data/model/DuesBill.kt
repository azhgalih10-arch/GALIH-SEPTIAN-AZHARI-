package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dues_bills")
data class DuesBill(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val residentId: String,          // e.g. "A1-05"
    val residentName: String,        // e.g. "Bpk. Hendra Gunawan"
    val blockNumber: String,         // e.g. "Blok A1 No. 05"
    val monthYear: String,           // e.g. "September 2026"
    val periodIndex: Int,            // e.g. 202609 for sorting
    val securityFee: Long = 150000,  // Keamanan & Satpam 24 Jam
    val wasteFee: Long = 50000,      // Kebersihan & Pengangkutan Sampah
    val facilityFee: Long = 50000,   // Fasum, Listrik PJU & Kas Sosial
    val totalAmount: Long = 250000,  // Rp 250.000
    val dueDate: String,             // e.g. "10 September 2026"
    val isPaid: Boolean = false,
    val paidAt: Long? = null,
    val paymentMethod: String? = null, // "QRIS Cluster Gandana", "BCA Virtual Account", "Mandiri Virtual Account", "Tunai ke Bendahara"
    val transactionRef: String? = null // e.g. "GDN-TRX-202609-0821"
)
