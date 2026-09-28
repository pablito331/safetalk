package com.example.data.supabase

import com.example.BuildConfig

object SupabaseConfig {
    const val PROJECT_ID: String = "nxmscabgbcwbifwhijwu"
    const val URL: String = "https://nxmscabgbcwbifwhijwu.supabase.co"
    const val REST_URL: String = "https://nxmscabgbcwbifwhijwu.supabase.co/rest/v1"

    // As chaves são injetadas em tempo de compilação a partir do arquivo .env (ignorado pelo git)
    val ANON_KEY: String
        get() = try {
            val key = BuildConfig::class.java.getField("SUPABASE_ANON_KEY").get(null) as? String
            if (!key.isNullOrBlank() && !key.contains("COLOQUE_SUA")) key else ""
        } catch (_: Exception) {
            ""
        }

    val IS_CONFIGURED: Boolean
        get() = ANON_KEY.isNotBlank()
}
