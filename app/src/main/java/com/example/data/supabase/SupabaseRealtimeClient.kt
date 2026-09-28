package com.example.data.supabase

import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface SupabaseRealtimeClient {
    @GET("/rest/v1/profiles")
    suspend fun fetchProfiles(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Query("select") select: String = "*"
    ): List<Map<String, Any?>>

    @POST("/rest/v1/profiles")
    suspend fun insertProfile(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Body payload: Map<String, Any?>
    ): Map<String, Any?>

    @GET("/rest/v1/chat_messages")
    suspend fun fetchMessages(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Query("select") select: String = "*",
        @Query("order") order: String = "created_at.desc"
    ): List<Map<String, Any?>>

    @POST("/rest/v1/chat_messages")
    suspend fun insertMessage(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Body payload: Map<String, Any?>
    ): Map<String, Any?>

    @PATCH("/rest/v1/chat_messages")
    suspend fun markDelivered(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Query("id") messageId: String,
        @Body payload: Map<String, Any?>
    ): List<Map<String, Any?>>
}

object SupabaseRealtimeHttp {
    fun createClient(baseUrl: String): SupabaseRealtimeClient {
        val client = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl.trimEnd('/') + "/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(Moshi.Builder().build()))
            .build()

        return retrofit.create(SupabaseRealtimeClient::class.java)
    }
}
