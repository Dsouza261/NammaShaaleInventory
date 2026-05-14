package com.nammashaalee.inventory.ui.assets

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nammashaalee.inventory.R
import com.nammashaalee.inventory.data.entity.Asset
import com.nammashaalee.inventory.data.entity.AssetCondition
import com.nammashaalee.inventory.databinding.ItemAssetBinding

class AssetAdapter(
    private val onItemClick: (Asset) -> Unit
) : ListAdapter<Asset, AssetAdapter.AssetViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssetViewHolder {
        val binding = ItemAssetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AssetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AssetViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AssetViewHolder(private val binding: ItemAssetBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(asset: Asset) {
            binding.tvAssetName.text = asset.name
            binding.tvAssetMeta.text = "${asset.room} · ${asset.serialNumber}"

            // Emoji icon based on name
            binding.tvAssetIcon.text = when {
                asset.name.contains("microscope", true) -> "🔬"
                asset.name.contains("tablet", true) -> "💻"
                asset.name.contains("football", true) -> "⚽"
                asset.name.contains("basketball", true) -> "🏀"
                asset.name.contains("projector", true) -> "🖥️"
                asset.name.contains("book", true) || asset.name.contains("library", true) -> "📚"
                asset.name.contains("lab", true) || asset.name.contains("beaker", true) -> "🧪"
                else -> "📦"
            }

            // Condition badge
            when (asset.condition) {
                AssetCondition.WORKING -> {
                    binding.tvConditionBadge.text = "Working"
                    binding.tvConditionBadge.setBackgroundResource(R.drawable.badge_green)
                    binding.tvConditionBadge.setTextColor(binding.root.context.getColor(R.color.green))
                }
                AssetCondition.NEEDS_REPAIR -> {
                    binding.tvConditionBadge.text = "Needs Repair"
                    binding.tvConditionBadge.setBackgroundResource(R.drawable.badge_amber)
                    binding.tvConditionBadge.setTextColor(binding.root.context.getColor(R.color.amber))
                }
                AssetCondition.BROKEN -> {
                    binding.tvConditionBadge.text = "Broken"
                    binding.tvConditionBadge.setBackgroundResource(R.drawable.badge_red)
                    binding.tvConditionBadge.setTextColor(binding.root.context.getColor(R.color.red))
                }
            }

            binding.root.setOnClickListener { onItemClick(asset) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Asset>() {
        override fun areItemsTheSame(oldItem: Asset, newItem: Asset) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Asset, newItem: Asset) = oldItem == newItem
    }
}