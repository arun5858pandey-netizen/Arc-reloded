package com.example.remote

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiHunterService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"

    suspend fun consultHunterSystem(
        userPrompt: String,
        enableHighThinking: Boolean = false,
        streakDays: Int = 10,
        habitSummary: String = "Wake up at 05:00, Running, Studies, No Junk Food, Water intake, Cold Shower, Discipline"
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Throwable) {
            ""
        }

        val modelName = if (enableHighThinking) "gemini-4-argon" else "gemini-3.8-flash"

        val systemPersona = """
            You are the "HUNTER SYSTEM" from Solo Leveling, speaking directly to the Player/Hunter.
            Tone: Stern, authoritative, cold yet motivating, relentless, immersive anime HUD style.
            Current Player Stats:
            - Streak: $streakDays Days Clean / Pure Momentum
            - Active Daily Habits: $habitSummary
            - Focus Arc: Winter/Summer Arc Personal Growth & Academic Supremacy (JEE/NEET / Studies)
            
            Address the player as [PLAYER] or [HUNTER]. Give sharp, actionable breakdown, tactical protocol, or stern evaluation.
        """.trimIndent()

        val fullPrompt = "$systemPersona\n\nPlayer Inquiry: $userPrompt"

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            // Intelligent pre-built Hunter System response if API key is not configured
            val fallback = if (enableHighThinking) {
                """
                [SYSTEM NOTIFICATION: DEEP TACTICAL ANALYSIS]
                
                Hunter, your current streak of $streakDays days has activated the Arc Potential Protocol.
                
                ■ STATUS EVALUATION:
                1. Routine Discipline: 7 Daily Quests active. Every missed node reduces your vitality multiplier.
                2. Academic Matrix: Syllabus coverage requires rigorous problem solving (DPPs and PYQs) immediately following lecture completion.
                3. Dopamine Fasting: Remain vigilant. The Shadow Monarch does not negotiate with fatigue.
                
                ■ MANDATORY DIRECTIVE:
                - Clear all daily quests before 23:00.
                - Complete 2 uninterrupted 90-minute deep study blocks today.
                - Arise and conquer.
                """.trimIndent()
            } else {
                """
                [HUNTER SYSTEM WARNING]
                
                You have logged $streakDays days of pure focus. Weakness is a choice; power is cultivated through relentless consistency.
                
                Directive: Complete your daily quests now. Do not delay your awakening.
                """.trimIndent()
            }
            return@withContext Result.success(fallback)
        }

        try {
            val jsonRoot = JSONObject()

            val contentsArray = JSONArray()
            val contentObj = JSONObject()
            val partsArray = JSONArray()
            val partObj = JSONObject()
            partObj.put("text", fullPrompt)
            partsArray.put(partObj)
            contentObj.put("parts", partsArray)
            contentsArray.put(contentObj)
            jsonRoot.put("contents", contentsArray)

            val genConfig = JSONObject()
            if (enableHighThinking) {
                val thinkingConfig = JSONObject()
                thinkingConfig.put("thinkingLevel", "HIGH")
                genConfig.put("thinkingConfig", thinkingConfig)
            } else {
                genConfig.put("temperature", 0.7)
            }
            jsonRoot.put("generationConfig", genConfig)

            val requestBody = jsonRoot.toString().toRequestBody("application/json".toMediaType())
            val url = "$BASE_URL/$modelName:generateContent?key=$apiKey"

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                return@withContext Result.failure(
                    Exception("Hunter System error (${response.code}): $responseBody")
                )
            }

            val parsedJson = JSONObject(responseBody)
            val candidates = parsedJson.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val firstCandidate = candidates.getJSONObject(0)
                val content = firstCandidate.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    val text = parts.getJSONObject(0).optString("text", "")
                    if (text.isNotBlank()) {
                        return@withContext Result.success(text)
                    }
                }
            }

            Result.success("[SYSTEM: No response text received from the System.]")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
