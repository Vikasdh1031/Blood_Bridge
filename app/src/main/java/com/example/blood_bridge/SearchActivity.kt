package com.example.bloodbridge

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bloodbridge.databinding.ActivitySearchBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var db: AppDatabase
    private lateinit var adapter: DonorAdapter

    private val bloodGroups = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = "Emergency Search"
            setDisplayHomeAsUpEnabled(true)
        }

        db = AppDatabase.getDatabase(this)

        // Setup spinner
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, bloodGroups)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerBloodGroup.adapter = spinnerAdapter

        // Setup RecyclerView
        adapter = DonorAdapter(emptyList())
        binding.recyclerDonors.layoutManager = LinearLayoutManager(this)
        binding.recyclerDonors.adapter = adapter

        binding.btnSearch.setOnClickListener {
            val selectedGroup = binding.spinnerBloodGroup.selectedItem.toString()
            searchDonors(selectedGroup)
        }

        // Auto search on open with first blood group
        searchDonors(bloodGroups[0])
    }

    private fun searchDonors(bloodGroup: String) {
        lifecycleScope.launch {
            db.donorDao().getEligibleDonorsByGroup(bloodGroup).collectLatest { donors ->
                adapter.updateList(donors)
                val count = donors.size
                binding.tvResultCount.text = if (count == 0)
                    "No eligible donors found for $bloodGroup"
                else
                    "Found $count eligible donor(s) for $bloodGroup"
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}