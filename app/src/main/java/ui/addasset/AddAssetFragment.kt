package com.nammashaalee.inventory.ui.addasset

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.nammashaalee.inventory.R
import com.nammashaalee.inventory.data.entity.Asset
import com.nammashaalee.inventory.data.entity.AssetCondition
import com.nammashaalee.inventory.databinding.FragmentAddAssetBinding
import com.nammashaalee.inventory.viewmodel.AssetViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddAssetFragment : Fragment() {

    private var _binding: FragmentAddAssetBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AssetViewModel by activityViewModels()

    private val rooms = listOf("Science Lab", "Computer Room", "Sports Room", "Library", "Classroom", "Staff Room")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddAssetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup room spinner
        val roomAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, rooms)
        roomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerRoom.adapter = roomAdapter

        // Open camera
        binding.btnTakePhoto.setOnClickListener {
            findNavController().navigate(R.id.cameraFragment)
        }

        // Save asset
        binding.btnSave.setOnClickListener {
            val name = binding.etItemName.text.toString().trim()
            val serial = binding.etSerialNumber.text.toString().trim()
            val room = rooms[binding.spinnerRoom.selectedItemPosition]
            val note = binding.etIssueNote.text.toString().trim()

            if (name.isEmpty()) {
                binding.etItemName.error = "Item name is required"
                return@setOnClickListener
            }

            val condition = when (binding.rgCondition.checkedRadioButtonId) {
                binding.rbRepair.id -> AssetCondition.NEEDS_REPAIR
                binding.rbBroken.id -> AssetCondition.BROKEN
                else -> AssetCondition.WORKING
            }

            val asset = Asset(
                name = name,
                serialNumber = serial.ifEmpty { "SN-${System.currentTimeMillis()}" },
                room = room,
                condition = condition,
                issueNote = note.ifEmpty { null }
            )

            viewModel.saveAsset(asset)
            Toast.makeText(requireContext(), "✓ Asset saved!", Toast.LENGTH_SHORT).show()
            clearForm()
        }

        // Observe AI photo analysis result
        viewModel.aiAnalysisResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                binding.tvAiPhotoResult.visibility = View.VISIBLE
                binding.tvAiPhotoResult.text = "AI: ${it.description} (${it.confidence}% confidence)"
                when (it.condition) {
                    AssetCondition.WORKING -> binding.rbWorking.isChecked = true
                    AssetCondition.NEEDS_REPAIR -> binding.rbRepair.isChecked = true
                    AssetCondition.BROKEN -> binding.rbBroken.isChecked = true
                }
            }
        }
    }

    private fun clearForm() {
        binding.etItemName.text?.clear()
        binding.etSerialNumber.text?.clear()
        binding.etIssueNote.text?.clear()
        binding.rbWorking.isChecked = true
        binding.tvAiPhotoResult.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}