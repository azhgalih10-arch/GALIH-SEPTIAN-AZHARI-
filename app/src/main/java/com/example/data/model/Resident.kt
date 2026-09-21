package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "residents")
data class Resident(
    @PrimaryKey val id: String, // e.g. "A1-05"
    val blockNumber: String,    // e.g. "Blok A1 No. 05"
    val residentName: String,   // e.g. "Bpk. Hendra Gunawan"
    val phone: String,          // e.g. "0812-8921-3341"
    val occupancyStatus: String // "Pemilik Tetap" / "Penyewa"
)
