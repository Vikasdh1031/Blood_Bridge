package com.example.bloodbridge

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bloodbridge.databinding.ActivityDonationLogBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DonationLogActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDonationLogBinding
    private lateinit var db: AppDatabase
    private lateinit var adapter: DonationLogAdapter

    private val bloodGroups = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
    private val CHANNEL_ID = "bloodbridge_channel"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDonationLogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = "Donation Log"
            setDisplayHomeAsUpEnabled(true)
        }

        db = AppDatabase.getDatabase(this)
        createNotificationChannel()

        adapter = DonationLogAdapter(emptyList())
        binding.recyclerLogs.layoutManager = LinearLayoutManager(this)
        binding.recyclerLogs.adapter = adapter

        lifecycleScope.launch {
            db.donorDao().getAllDonationLogs().collectLatest { logs ->
                adapter.updateList(logs)
            }
        }

        binding.btnLogDonation.setOnClickListener {
            showLogDonationDialog()
        }
    }

    private fun showLogDonationDialog() {
        val dialogView = LayoutInflater.from(this).inflate(android.R.layout.select_dialog_item, null)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 32, 48, 16)
        }

        val etName = EditText(this).apply { hint = "Donor Name" }
        val etLocation = EditText(this).apply { hint = "Location" }
        val spinnerGroup = Spinner(this)
        val spinnerAdapter = ArrayAdapter(this@DonationLogActivity, android.R.layout.simple_spinner_item, bloodGroups)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGroup.adapter = spinnerAdapter

        layout.addView(TextView(this).apply { text = "Donor Name" })
        layout.addView(etName)
        layout.addView(TextView(this).apply { text = "Blood Group"; setPadding(0, 16, 0, 0) })
        layout.addView(spinnerGroup)
        layout.addView(TextView(this).apply { text = "Location"; setPadding(0, 16, 0, 0) })
        layout.addView(etLocation)

        AlertDialog.Builder(this)
            .setTitle("🩸 Log New Donation")
            .setView(layout)
            .setPositiveButton("Save") { _, _ ->
                val name = etName.text.toString().trim()
                val location = etLocation.text.toString().trim()
                val bloodGroup = spinnerGroup.selectedItem.toString()

                if (name.isEmpty() || location.isEmpty()) {
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val log = DonationLog(
                    donorName = name,
                    bloodGroup = bloodGroup,
                    donationDate = System.currentTimeMillis(),
                    location = location,
                    donorId = 0
                )

                lifecycleScope.launch {
                    db.donorDao().insertDonationLog(log)
                    runOnUiThread {
                        sendThankYouNotification(name)
                        Toast.makeText(this@DonationLogActivity, "✅ Donation logged!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "BloodBridge Notifications",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Thank you notifications for donors"
        }
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    private fun sendThankYouNotification(donorName: String) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Thank You, $donorName! 🩸")
            .setContentText("Your donation has been logged. You are a hero today!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(System.currentTimeMillis().toInt(), notification)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}