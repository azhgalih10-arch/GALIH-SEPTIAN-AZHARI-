package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_notifications")
data class AppNotification(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val message: String,
    val dateStr: String,
    val timestamp: Long,
    val type: String, // "BILL_ISSUED", "DUE_REMINDER", "PAYMENT_CONFIRMED", "REPORT_PUBLISHED"
    val isRead: Boolean = false,
    val targetResidentId: String? = null // null means broadcast to all residents
)
