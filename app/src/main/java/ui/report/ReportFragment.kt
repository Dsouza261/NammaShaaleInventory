package com.nammashaalee.inventory.ui.report

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.nammashaalee.inventory.databinding.FragmentReportBinding
import com.nammashaalee.inventory.viewmodel.AssetViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class ReportFragment : Fragment() {

    private var _binding: FragmentReportBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AssetViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val month = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date())
        binding.tvReportMonth.text = month

        viewModel.workingCount.observe(viewLifecycleOwner) { binding.tvReportWorking.text = it.toString() }
        viewModel.repairCount.observe(viewLifecycleOwner) { binding.tvReportRepair.text = it.toString() }
        viewModel.brokenCount.observe(viewLifecycleOwner) { binding.tvReportBroken.text = it.toString() }

        viewModel.filteredAssets.observe(viewLifecycleOwner) { assets ->
            binding.btnGenerateReport.setOnClickListener {
                binding.tvAiReport.text = "Generating AI report..."
                viewModel.generateReport(assets, "GHPS School")
            }
        }

        viewModel.aiReport.observe(viewLifecycleOwner) { report ->
            report?.let { binding.tvAiReport.text = it }
        }

        viewModel.isGeneratingReport.observe(viewLifecycleOwner) { loading ->
            binding.btnGenerateReport.isEnabled = !loading
            binding.btnGenerateReport.text = if (loading) "Generating..." else "Generate AI Report"
        }

        binding.btnShareReport.setOnClickListener {
            val reportText = binding.tvAiReport.text.toString()
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "Namma-Shaale Inventory Report\n$month\n\n$reportText")
                putExtra(Intent.EXTRA_SUBJECT, "School Asset Report - $month")
            }
            startActivity(Intent.createChooser(shareIntent, "Share Report"))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}