package com.example.bloodbridge

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DonorDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDonor(donor: Donor)

    @Update
    suspend fun updateDonor(donor: Donor)

    @Delete
    suspend fun deleteDonor(donor: Donor)

    @Query("SELECT * FROM donors ORDER BY name ASC")
    fun getAllDonors(): Flow<List<Donor>>

    @Query("SELECT * FROM donors WHERE bloodGroup = :bloodGroup AND isEligible = 1 ORDER BY name ASC")
    fun getEligibleDonorsByGroup(bloodGroup: String): Flow<List<Donor>>

    @Query("SELECT * FROM donors WHERE id = :id")
    suspend fun getDonorById(id: Int): Donor?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDonationLog(log: DonationLog)

    @Query("SELECT * FROM donation_logs ORDER BY donationDate DESC")
    fun getAllDonationLogs(): Flow<List<DonationLog>>
}