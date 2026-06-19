package com.example.bloodbridge

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Donor::class, DonationLog::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun donorDao(): DonorDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bloodbridge_database"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateSampleData(database.donorDao())
                }
            }
        }

        suspend fun populateSampleData(dao: DonorDao) {
            val today = System.currentTimeMillis()
            val day = 24 * 60 * 60 * 1000L
            dao.insertDonor(Donor(name = "Rahul Sharma", bloodGroup = "A+", phone = "9876543210", location = "Bangalore", lastDonationDate = today - (100 * day), isEligible = true))
            dao.insertDonor(Donor(name = "Priya Nair", bloodGroup = "B+", phone = "9123456780", location = "Mysore", lastDonationDate = today - (50 * day), isEligible = false))
            dao.insertDonor(Donor(name = "Arun Kumar", bloodGroup = "O+", phone = "9988776655", location = "Bangalore", lastDonationDate = today - (120 * day), isEligible = true))
            dao.insertDonor(Donor(name = "Sneha Reddy", bloodGroup = "AB+", phone = "9871234560", location = "Hubli", lastDonationDate = today - (95 * day), isEligible = true))
            dao.insertDonor(Donor(name = "Vikram Patil", bloodGroup = "O-", phone = "9000111222", location = "Dharwad", lastDonationDate = today - (30 * day), isEligible = false))
            dao.insertDonor(Donor(name = "Meena Joshi", bloodGroup = "A-", phone = "9111222333", location = "Bangalore", lastDonationDate = today - (200 * day), isEligible = true))
            dao.insertDonor(Donor(name = "Suresh Gowda", bloodGroup = "B-", phone = "9222333444", location = "Mangalore", lastDonationDate = today - (91 * day), isEligible = true))
            dao.insertDonor(Donor(name = "Kavitha Rao", bloodGroup = "AB-", phone = "9333444555", location = "Belgaum", lastDonationDate = today - (60 * day), isEligible = false))
        }
    }
}