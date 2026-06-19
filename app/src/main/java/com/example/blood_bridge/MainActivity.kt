package com.example.bloodbridge

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.bloodbridge.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)

        // Load stats
        lifecycleScope.launch {
            db.donorDao().getAllDonors().collectLatest { donors ->
                binding.tvTotalDonors.text = donors.size.toString()
                binding.tvEligibleDonors.text = donors.count { it.isEligible }.toString()
            }
        }

        binding.cardSearch.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        binding.cardRegister.setOnClickListener {
            startActivity(Intent(this, RegisterDonorActivity::class.java))
        }

        binding.cardLog.setOnClickListener {
            startActivity(Intent(this, DonationLogActivity::class.java))
        }
    }
}