package com.herd.circle.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * REST endpoints for Circle's data layer, matching the API design table
 * in the Part 1 Design Document (Section 5 — API Design):
 *
 *   POST /posts            -> createDocument("posts", ...)
 *   GET  /posts/feed        -> listDocuments("posts", ...)
 *   POST /friends/request   -> createDocument("friendships", ...)
 *   PUT  /friends/{id}/accept -> patchDocument("friendships/{id}", ...)
 *
 * Firestore's REST API represents documents as {"fields": {...}} where each
 * field is typed (stringValue, integerValue, etc.) — see FirestoreConverter.kt
 * for the mapping to/from our plain Kotlin data classes.
 */
interface FirestoreApi {

    @GET("{collection}")
    suspend fun listDocuments(
        @Path("collection") collection: String,
        @Header("Authorization") authHeader: String,
        @Query("pageSize") pageSize: Int = 50
    ): Response<FirestoreListResponse>

    @POST("{collection}")
    suspend fun createDocument(
        @Path("collection") collection: String,
        @Header("Authorization") authHeader: String,
        @Body body: FirestoreDocument
    ): Response<FirestoreDocument>

    @PATCH("{collection}/{documentId}")
    suspend fun patchDocument(
        @Path("collection") collection: String,
        @Path("documentId") documentId: String,
        @Header("Authorization") authHeader: String,
        @Query("updateMask.fieldPaths") updateMask: String,
        @Body body: FirestoreDocument
    ): Response<FirestoreDocument>
}

/** Raw Firestore REST document shape: { "fields": { "key": { "stringValue": "..." } } } */
data class FirestoreDocument(
    val name: String? = null,
    val fields: Map<String, FirestoreValue> = emptyMap()
)

data class FirestoreValue(
    val stringValue: String? = null,
    val integerValue: String? = null,
    val arrayValue: FirestoreArray? = null
)

data class FirestoreArray(
    val values: List<FirestoreValue> = emptyList()
)

data class FirestoreListResponse(
    val documents: List<FirestoreDocument> = emptyList()
)
