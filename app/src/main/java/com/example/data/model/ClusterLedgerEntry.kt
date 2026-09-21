package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cluster_ledger")
data class ClusterLedgerEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateStr: String,          // e.g. "15 Sep 2026"
    val timestamp: Long,
    val type: String,             // "PEMASUKAN" or "PENGELUARAN"
    val category: String,         // e.g. "Iuran Warga", "Gaji Satpam", "Operasional Sampah", "Fasilitas Umum"
    val description: String,      // e.g. "Iuran Keamanan & Kebersihan Blok A1 No. 05"
    val amount: Long,             // in Rupiah
    val recordedBy: String = "Bendahara Cluster Gandana",
    val receiptNumber: String     // e.g. "KW-GDN-202609-012"
)
