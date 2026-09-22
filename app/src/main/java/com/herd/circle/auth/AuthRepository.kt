package com.herd.circle.auth

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

/**
 * Wraps the Firebase Authentication SDK — this satisfies the "connect an app
 * to an appropriate SDK" requirement, kept deliberately separate from the
 * Retrofit/Firestore REST layer used for post and friend data.
 */
class AuthRepository(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    companion object {
        private const val TAG = "AuthRepository"
    }

    suspend fun register(fullName: String, email: String, password: String): Result<String> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: throw IllegalStateException("No UID returned")
            Log.i(TAG, "Registered new user: $uid")
            Result.success(uid)
        } catch (e: Exception) {
            Log.e(TAG, "Registration failed", e)
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<String> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: throw IllegalStateException("No UID returned")
            Log.i(TAG, "Logged in user: $uid")
            Result.success(uid)
        } catch (e: Exception) {
            Log.e(TAG, "Login failed", e)
            Result.failure(e)
        }
    }

    /** Returns a fresh Firebase ID token, used as the Bearer token for REST calls to Firestore. */
    suspend fun getIdToken(): String? {
        return try {
            firebaseAuth.currentUser?.getIdToken(false)?.await()?.token
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch ID token", e)
            null
        }
    }

    fun currentUserId(): String? = firebaseAuth.currentUser?.uid

    fun logout() = firebaseAuth.signOut()
}
