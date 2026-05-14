package com.nammashaalee.inventory.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.nammashaalee.inventory.data.entity.Asset
import com.nammashaalee.inventory.data.entity.AssetCondition
import com.nammashaalee.inventory.data.entity.HealthCheckEntry
import com.nammashaalee.inventory.data.repository.AssetRepository
import com.nammashaalee.inventory.util.ConditionAnalysisResult
import com.nammashaalee.inventory.util.GeminiHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AssetViewModel @Inject constructor(
    private val repository: AssetRepository,
    private val geminiHelper: GeminiHelper
) : ViewModel() {

    // --- Dashboard stats ---
    val totalCount = repository.totalCount.asLiveData()
    val workingCount = repository.workingCount.asLiveData()
    val repairCount = repository.repairCount.asLiveData()
    val brokenCount = repository.brokenCount.asLiveData()
    val checksThisMonth = repository.getChecksThisMonth().asLiveData()

    // --- Asset list with filter ---
    private val _filterCondition = MutableStateFlow<AssetCondition?>(null)
    val filteredAssets = _filterCondition.flatMapLatest { condition ->
        if (condition == null) repository.allAssets
        else repository.getAssetsByCondition(condition)
    }.asLiveData()

    // --- Search ---
    private val _searchQuery = MutableStateFlow("")
    val searchResults = _searchQuery.flatMapLatest { q ->
        if (q.isBlank()) repository.allAssets else repository.searchAssets(q)
    }.asLiveData()

    // --- Selected asset detail ---
    private val _selectedAssetId = MutableLiveData<Long>()
    fun selectAsset(id: Long) { _selectedAssetId.value = id }
    fun getAssetById(id: Long) = repository.getAssetById(id).asLiveData()
    fun getHealthHistory(assetId: Long) = repository.getHealthHistory(assetId).asLiveData()

    // --- AI photo analysis ---
    private val _aiAnalysisResult = MutableLiveData<ConditionAnalysisResult?>()
    val aiAnalysisResult: LiveData<ConditionAnalysisResult?> = _aiAnalysisResult

    private val _isAnalyzing = MutableLiveData(false)
    val isAnalyzing: LiveData<Boolean> = _isAnalyzing

    fun analyzePhoto(imagePath: String) {
        viewModelScope.launch {
            _isAnalyzing.value = true
            _aiAnalysisResult.value = geminiHelper.analyzeAssetPhoto(imagePath)
            _isAnalyzing.value = false
        }
    }

    // --- AI report generation ---
    private val _aiReport = MutableLiveData<String?>()
    val aiReport: LiveData<String?> = _aiReport

    private val _isGeneratingReport = MutableLiveData(false)
    val isGeneratingReport: LiveData<Boolean> = _isGeneratingReport

    fun generateReport(assets: List<Asset>, schoolName: String) {
        viewModelScope.launch {
            _isGeneratingReport.value = true
            val month = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date())
            _aiReport.value = geminiHelper.generateSummaryReport(assets, schoolName, month)
            _isGeneratingReport.value = false
        }
    }

    // --- CRUD operations ---
    fun setFilter(condition: AssetCondition?) { _filterCondition.value = condition }
    fun setSearchQuery(query: String) { _searchQuery.value = query }

    fun saveAsset(asset: Asset) {
        viewModelScope.launch { repository.insertAsset(asset) }
    }

    fun updateAsset(asset: Asset) {
        viewModelScope.launch { repository.updateAsset(asset) }
    }

    fun deleteAsset(asset: Asset) {
        viewModelScope.launch { repository.deleteAsset(asset) }
    }
}