package com.example.bloodbridge

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "donation_logs")
data class DonationLog(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val donorId: Int,
    val donorName: String,
    val bloodGroup: String,
    val donationDate: Long,
    val location: String
)