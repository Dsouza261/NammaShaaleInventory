package com.nammashaalee.inventory.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.nammashaalee.inventory.data.entity.AssetCondition
import com.nammashaalee.inventory.databinding.FragmentDetailBinding
import com.nammashaalee.inventory.viewmodel.AssetViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AssetViewModel by activityViewModels()
    private val args: DetailFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getAssetById(args.assetId).observe(viewLifecycleOwner) { asset ->
            asset?.let {
                binding.tvDetailName.text = it.name
                binding.tvDetailSerial.text = it.serialNumber
                binding.tvDetailRoom.text = it.room
                binding.tvDetailCondition.text = when (it.condition) {
                    AssetCondition.WORKING -> "✅ Working"
                    AssetCondition.NEEDS_REPAIR -> "⚠️ Needs Repair"
                    AssetCondition.BROKEN -> "❌ Broken"
                }
                binding.tvAssetEmoji.text = when {
                    it.name.contains("microscope", true) -> "🔬"
                    it.name.contains("tablet", true) -> "💻"
                    it.name.contains("football", true) -> "⚽"
                    it.name.contains("projector", true) -> "🖥️"
                    else -> "📦"
                }
            }
        }

        binding.btnUpdateCondition.setOnClickListener {
            findNavController().navigate(com.nammashaalee.inventory.R.id.addAssetFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}