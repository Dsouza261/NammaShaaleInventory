package com.nammashaalee.inventory.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.nammashaalee.inventory.databinding.FragmentDashboardBinding
import com.nammashaalee.inventory.ui.assets.AssetAdapter
import com.nammashaalee.inventory.ui.auth.SplashActivity
import com.nammashaalee.inventory.viewmodel.AssetViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardFragment : Fragment() {
    // Dashboard screen — shows live asset health stats and AI insights

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AssetViewModel by activityViewModels()
    private lateinit var adapter: AssetAdapter
    private val auth = FirebaseAuth.getInstance()

    private var totalAssets = 0
    private var repairCount = 0
    private var brokenCount = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Show real logged in user email
        val user = auth.currentUser
        binding.tvSchoolSubtitle.text = "Total Assets: 0 | Needs Repair: 0"

        // Show user email in header
        val userEmail = user?.email ?: user?.displayName ?: "Teacher"
        binding.tvUserEmail?.text = userEmail

        // Logout button
        binding.btnLogout?.setOnClickListener {
            auth.signOut()
            startActivity(Intent(requireContext(), SplashActivity::class.java))
            requireActivity().finishAffinity()
        }

        adapter = AssetAdapter { asset -> viewModel.selectAsset(asset.id) }
        binding.rvRecentAssets.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRecentAssets.adapter = adapter

        viewModel.totalCount.observe(viewLifecycleOwner) { total ->
            totalAssets = total
            updateDashboardTitle()
            binding.tvTotalCount.text = "Total: $total assets tracked"
        }

        viewModel.workingCount.observe(viewLifecycleOwner) {
            binding.tvWorkingCount.text = it.toString()
        }

        viewModel.repairCount.observe(viewLifecycleOwner) { repair ->
            repairCount = repair
            binding.tvRepairCount.text = repair.toString()
            updateDashboardTitle()
        }

        viewModel.brokenCount.observe(viewLifecycleOwner) { broken ->
            brokenCount = broken
            binding.tvBrokenCount.text = broken.toString()
            updateAiInsight()
        }

        viewModel.filteredAssets.observe(viewLifecycleOwner) { assets ->
            adapter.submitList(assets.take(5))
            updateAiInsight()
        }
    }

    private fun updateDashboardTitle() {
        binding.tvSchoolSubtitle.text =
            "Total Assets: $totalAssets | Needs Repair: $repairCount"
    }

    private fun updateAiInsight() {
        binding.tvAiInsight.text = when {
            brokenCount > 0 || repairCount > 0 ->
                "$brokenCount broken and $repairCount items need repair. Recommend raising a request to SDMC this week."
            totalAssets == 0 ->
                "Add assets to get AI-powered insights about your school inventory."
            else ->
                "All $totalAssets assets are in good condition. Great work!"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}