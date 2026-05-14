package com.nammashaalee.inventory.ui.assets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nammashaalee.inventory.R
import com.nammashaalee.inventory.data.entity.AssetCondition
import com.nammashaalee.inventory.databinding.FragmentAssetsBinding
import com.nammashaalee.inventory.viewmodel.AssetViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AssetsFragment : Fragment() {

    private var _binding: FragmentAssetsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AssetViewModel by activityViewModels()
    private lateinit var adapter: AssetAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAssetsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = AssetAdapter { asset ->
            viewModel.selectAsset(asset.id)
            findNavController().navigate(R.id.action_assets_to_detail)
        }

        binding.rvAssets.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAssets.adapter = adapter

        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.addAssetFragment)
        }

        binding.etSearch.addTextChangedListener { text ->
            viewModel.setSearchQuery(text.toString())
        }

        binding.chipGroupFilter.setOnCheckedStateChangeListener { _, checkedIds ->
            when (checkedIds.firstOrNull()) {
                R.id.chip_working -> viewModel.setFilter(AssetCondition.WORKING)
                R.id.chip_repair -> viewModel.setFilter(AssetCondition.NEEDS_REPAIR)
                R.id.chip_broken -> viewModel.setFilter(AssetCondition.BROKEN)
                else -> viewModel.setFilter(null)
            }
        }

        viewModel.filteredAssets.observe(viewLifecycleOwner) { assets ->
            adapter.submitList(assets)
            binding.tvAssetCount.text = "${assets.size} items"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}