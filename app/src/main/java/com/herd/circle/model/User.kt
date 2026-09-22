package com.herd.circle.model

/**
 * Represents a Circle user profile, as stored in Firestore under /users/{userId}.
 * Matches the Data Listing defined in the Part 1 design document.
 */
data class User(
    val userId: String = "",
    val fullName: String = "",
    val username: String = "",
    val email: String = "",
    val avatarUrl: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
