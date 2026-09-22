package com.herd.circle.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Provides a singleton Retrofit instance pointed at the Firestore REST API.
 * This is what satisfies the "use a RESTful API in an Android app" requirement:
 * rather than using the Firestore Android SDK for data access, all post and
 * friendship data goes through real HTTPS REST calls built with Retrofit.
 *
 * Firebase Authentication (see auth/AuthRepository.kt) is used separately as
 * the required "appropriate SDK" — the two are intentionally kept distinct.
 */
object RetrofitClient {

    // Replace with your own Firebase project ID once google-services.json is added.
    private const val PROJECT_ID = "circle-app-<your-project-id>"
    private const val BASE_URL =
        "https://firestore.googleapis.com/v1/projects/$PROJECT_ID/databases/(default)/documents/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    val api: FirestoreApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FirestoreApi::class.java)
    }
}
