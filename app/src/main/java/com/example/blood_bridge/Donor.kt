package com.example.bloodbridge

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "donors")
data class Donor(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val bloodGroup: String,
    val phone: String,
    val location: String,
    val lastDonationDate: Long,
    val isEligible: Boolean = true
)