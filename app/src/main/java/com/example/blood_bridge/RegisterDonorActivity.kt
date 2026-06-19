package com.example.bloodbridge

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.bloodbridge.databinding.ActivityRegisterDonorBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class RegisterDonorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterDonorBinding
    private lateinit var db: AppDatabase
    private var selectedDateMillis: Long = System.currentTimeMillis()
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    private val bloodGroups = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterDonorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = "Register Donor"
            setDisplayHomeAsUpEnabled(true)
        }

        db = AppDatabase.getDatabase(this)

        // Setup blood group spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, bloodGroups)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerBloodGroup.adapter = adapter

        // Date picker
        binding.btnPickDate.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, day ->
                cal.set(year, month, day)
                selectedDateMillis = cal.timeInMillis
                binding.tvSelectedDate.text = "Selected: ${dateFormat.format(cal.time)}"
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        // Register button
        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()
            val location = binding.etLocation.text.toString().trim()
            val bloodGroup = binding.spinnerBloodGroup.selectedItem.toString()

            if (name.isEmpty() || phone.isEmpty() || location.isEmpty()) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (phone.length != 10) {
                Toast.makeText(this, "Please enter a valid 10-digit phone number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Calculate eligibility (90 days rule)
            val daysSinceDonation = (System.currentTimeMillis() - selectedDateMillis) / (24 * 60 * 60 * 1000)
            val isEligible = daysSinceDonation >= 90

            val donor = Donor(
                name = name,
                bloodGroup = bloodGroup,
                phone = phone,
                location = location,
                lastDonationDate = selectedDateMillis,
                isEligible = isEligible
            )

            lifecycleScope.launch {
                db.donorDao().insertDonor(donor)
                runOnUiThread {
                    Toast.makeText(this@RegisterDonorActivity, "✅ Donor registered successfully!", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}