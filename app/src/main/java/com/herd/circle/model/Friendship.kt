package com.herd.circle.model

/**
 * Represents a friend connection between two users, matching the two-way
 * (reciprocal) friend model described in the Part 1 research and design documents.
 */
data class Friendship(
    val friendshipId: String = "",
    val userIdA: String = "",
    val userIdB: String = "",
    val otherUserName: String = "", // convenience field for display in RecyclerViews
    val status: FriendshipStatus = FriendshipStatus.PENDING
)

enum class FriendshipStatus {
    PENDING, ACCEPTED, DECLINED
}
