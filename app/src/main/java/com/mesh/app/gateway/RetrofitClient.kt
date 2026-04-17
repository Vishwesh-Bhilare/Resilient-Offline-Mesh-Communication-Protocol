package com.mesh.app.gateway

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.mesh.app.util.Constants
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

object RetrofitClient {
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.GATEWAY_BASE_URL)
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(ApiService::class.java)
    }
}
