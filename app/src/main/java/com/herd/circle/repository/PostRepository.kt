package com.herd.circle.repository

import android.util.Log
import com.herd.circle.auth.AuthRepository
import com.herd.circle.model.Post
import com.herd.circle.network.FirestoreConverter
import com.herd.circle.network.RetrofitClient

/**
 * Repository for post/feed data, implementing the "/posts" and "/posts/feed"
 * endpoints from the Part 1 Design Document's API table via Retrofit calls
 * to Firestore's REST API. Kept independent of any Android framework classes
 * so it can be unit tested directly (see PostRepositoryTest).
 */
class PostRepository(
    private val authRepository: AuthRepository = AuthRepository(),
    private val api: com.herd.circle.network.FirestoreApi = RetrofitClient.api
) {
    companion object {
        private const val TAG = "PostRepository"
        private const val COLLECTION = "posts"
    }

    /** Fetches the friend-only feed. In a full build this would filter server-side
     * by the caller's accepted-friends list; simplified here to list all posts. */
    suspend fun getFeed(): Result<List<Post>> {
        return try {
            val token = authRepository.getIdToken() ?: return Result.failure(IllegalStateException("Not authenticated"))
            val response = api.listDocuments(COLLECTION, "Bearer $token")
            if (response.isSuccessful) {
                val posts = response.body()?.documents?.map { FirestoreConverter.documentToPost(it) } ?: emptyList()
                Log.i(TAG, "Fetched ${posts.size} posts")
                Result.success(posts.sortedByDescending { it.createdAt })
            } else {
                Result.failure(Exception("Feed request failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "getFeed failed", e)
            Result.failure(e)
        }
    }

    suspend fun createPost(post: Post): Result<Post> {
        return try {
            val token = authRepository.getIdToken() ?: return Result.failure(IllegalStateException("Not authenticated"))
            val doc = FirestoreConverter.postToDocument(post)
            val response = api.createDocument(COLLECTION, "Bearer $token", doc)
            if (response.isSuccessful && response.body() != null) {
                Log.i(TAG, "Post created")
                Result.success(FirestoreConverter.documentToPost(response.body()!!))
            } else {
                Result.failure(Exception("Create post failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "createPost failed", e)
            Result.failure(e)
        }
    }
}
