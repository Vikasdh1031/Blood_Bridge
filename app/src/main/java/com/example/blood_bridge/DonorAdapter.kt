package com.example.bloodbridge

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bloodbridge.databinding.ItemDonorBinding
import java.text.SimpleDateFormat
import java.util.*

class DonorAdapter(private var donors: List<Donor>) :
    RecyclerView.Adapter<DonorAdapter.DonorViewHolder>() {

    inner class DonorViewHolder(val binding: ItemDonorBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonorViewHolder {
        val binding = ItemDonorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DonorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DonorViewHolder, position: Int) {
        val donor = donors[position]
        val binding = holder.binding

        binding.tvBloodGroup.text = donor.bloodGroup
        binding.tvDonorName.text = donor.name
        binding.tvLocation.text = "📍 ${donor.location}"

        val daysSince = (System.currentTimeMillis() - donor.lastDonationDate) / (24 * 60 * 60 * 1000)

        if (donor.isEligible) {
            binding.tvEligibilityStatus.text = "✓ Eligible to Donate"
            binding.tvEligibilityStatus.setTextColor(
                holder.itemView.context.getColor(R.color.green_eligible)
            )
        } else {
            val daysLeft = 90 - daysSince
            binding.tvEligibilityStatus.text = "✗ Eligible in $daysLeft days"
            binding.tvEligibilityStatus.setTextColor(
                holder.itemView.context.getColor(R.color.red_ineligible)
            )
        }

        // Call button — uses Intent so number is not shown on screen
        binding.btnCall.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${donor.phone}"))
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount() = donors.size

    fun updateList(newList: List<Donor>) {
        donors = newList
        notifyDataSetChanged()
    }
}