package com.nammashaalee.inventory.util

import android.content.Context
import com.nammashaalee.inventory.data.entity.Asset
import com.nammashaalee.inventory.data.entity.AssetCondition
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

data class ConditionAnalysisResult(
    val condition: AssetCondition,
    val confidence: Int,
    val description: String,
    val repairRecommendation: String? = null
)

@Singleton
class GeminiHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val apiKey = "AIzaSyAqxxy7i2t_9Ww87KNUONm5oT8d8Cgg8gM"
    private val apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent"
    suspend fun analyzeAssetPhoto(imagePath: String): ConditionAnalysisResult? = null

    suspend fun generateSummaryReport(
        assets: List<Asset>,
        schoolName: String,
        month: String
    ): String = withContext(Dispatchers.IO) {
        try {
            val working = assets.count { it.condition == AssetCondition.WORKING }
            val repair = assets.count { it.condition == AssetCondition.NEEDS_REPAIR }
            val broken = assets.count { it.condition == AssetCondition.BROKEN }
            val repairItems = assets.filter { it.condition == AssetCondition.NEEDS_REPAIR }
                .joinToString { it.name }.ifEmpty { "None" }
            val brokenItems = assets.filter { it.condition == AssetCondition.BROKEN }
                .joinToString { it.name }.ifEmpty { "None" }

            val prompt = """
                Write a professional asset report for SDMC (School Development and Monitoring Committee).
                School: $schoolName
                Month: $month
                Total Assets: ${assets.size}
                Working: $working
                Needs Repair: $repair - $repairItems
                Broken: $broken - $brokenItems
                Write 3 short paragraphs covering health status, urgent items, and recommendations.
                Keep under 150 words. Professional tone.
            """.trimIndent()

            val requestBody = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply { put("text", prompt) })
                        })
                    })
                })
            }.toString()

            val url = URL("$apiUrl?key=$apiKey")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true
            connection.connectTimeout = 30000
            connection.readTimeout = 30000

            connection.outputStream.use { it.write(requestBody.toByteArray()) }

            val responseCode = connection.responseCode
            if (responseCode == 200) {
                val response = connection.inputStream.bufferedReader().readText()
                val json = JSONObject(response)
                json.getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text")
            } else {
                val error = connection.errorStream?.bufferedReader()?.readText()
                "Failed: $error"
            }
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }
}