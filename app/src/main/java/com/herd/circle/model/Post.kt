package com.herd.circle.model

/**
 * Represents a single post in a user's friend-only feed.
 * Matches the Post entity defined in the Part 1 design document's Data Listing.
 */
data class Post(
    val postId: String = "",
    val userId: String = "",
    val username: String = "",
    val mediaUrl: String = "",
    val caption: String = "",
    val hashtags: List<String> = emptyList(),
    val likeCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
