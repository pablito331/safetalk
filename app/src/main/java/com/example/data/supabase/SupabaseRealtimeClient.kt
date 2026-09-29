package com.example.data.supabase

import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface SupabaseRealtimeClient {

    // ---------- Perfil (tabela legada public.profiles) ----------
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

    // ---------- Fila de mensagens (public.device_messages) ----------
    @POST("/rest/v1/device_messages")
    suspend fun insertDeviceMessage(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Body payload: Map<String, Any?>
    ): Map<String, Any?>

    @GET("/rest/v1/device_messages")
    suspend fun fetchDeviceMessages(
        @Header("apikey") apiKey: String,
        @retrofit2.http.Header("Authorization") authHeader2: String,
        @Query("recipient_id") recipient: String,
        @Query("select") select: String = "*",
        @Query("order") order: String = "created_at.asc"
    ): List<Map<String, Any?>>

    @DELETE("/rest/v1/device_messages")
    suspend fun deleteDeviceMessages(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Query("id") idFilter: String
    ): List<Map<String, Any?>>

    // ---------- Identidade do dispositivo (public.device_identities) ----------
    @PUT("/rest/v1/device_identities")
    suspend fun upsertIdentity(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String,
        @Body payload: Map<String, Any?>
    ): List<Map<String, Any?>>

    @GET("/auth/v1/user")
    suspend fun fetchAuthUser(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authHeader: String
    ): Map<String, Any?>
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
