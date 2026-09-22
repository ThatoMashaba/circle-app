package com.herd.circle.repository

import android.util.Log
import com.herd.circle.auth.AuthRepository
import com.herd.circle.model.Friendship
import com.herd.circle.model.FriendshipStatus
import com.herd.circle.network.FirestoreDocument
import com.herd.circle.network.FirestoreValue
import com.herd.circle.network.RetrofitClient

/**
 * Repository for the reciprocal friend network, implementing the
 * "/friends/request" and "/friends/request/{id}/accept" endpoints from the
 * Part 1 Design Document's API table.
 */
class FriendRepository(
    private val authRepository: AuthRepository = AuthRepository(),
    private val api: com.herd.circle.network.FirestoreApi = RetrofitClient.api
) {
    companion object {
        private const val TAG = "FriendRepository"
        private const val COLLECTION = "friendships"
    }

    suspend fun getFriendships(): Result<List<Friendship>> {
        return try {
            val token = authRepository.getIdToken() ?: return Result.failure(IllegalStateException("Not authenticated"))
            val response = api.listDocuments(COLLECTION, "Bearer $token")
            if (response.isSuccessful) {
                val friendships = response.body()?.documents?.map { doc ->
                    val f = doc.fields
                    Friendship(
                        friendshipId = doc.name?.substringAfterLast("/") ?: "",
                        userIdA = f["userIdA"]?.stringValue ?: "",
                        userIdB = f["userIdB"]?.stringValue ?: "",
                        otherUserName = f["otherUserName"]?.stringValue ?: "",
                        status = FriendshipStatus.valueOf(f["status"]?.stringValue ?: "PENDING")
                    )
                } ?: emptyList()
                Result.success(friendships)
            } else {
                Result.failure(Exception("Friendships request failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "getFriendships failed", e)
            Result.failure(e)
        }
    }

    suspend fun acceptRequest(friendshipId: String): Result<Unit> {
        return try {
            val token = authRepository.getIdToken() ?: return Result.failure(IllegalStateException("Not authenticated"))
            val body = FirestoreDocument(fields = mapOf("status" to FirestoreValue(stringValue = "ACCEPTED")))
            val response = api.patchDocument(COLLECTION, friendshipId, "Bearer $token", "status", body)
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Accept failed: ${response.code()}"))
        } catch (e: Exception) {
            Log.e(TAG, "acceptRequest failed", e)
            Result.failure(e)
        }
    }
}
