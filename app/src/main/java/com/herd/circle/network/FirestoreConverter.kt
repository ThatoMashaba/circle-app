package com.herd.circle.network

import com.herd.circle.model.Post

/**
 * Converts between plain Kotlin models and Firestore's typed REST document
 * format. Kept separate from the repository layer so the REST/JSON mapping
 * logic can be unit tested in isolation (see PostRepositoryTest).
 */
object FirestoreConverter {

    fun postToDocument(post: Post): FirestoreDocument {
        val fields = mutableMapOf(
            "userId" to FirestoreValue(stringValue = post.userId),
            "username" to FirestoreValue(stringValue = post.username),
            "mediaUrl" to FirestoreValue(stringValue = post.mediaUrl),
            "caption" to FirestoreValue(stringValue = post.caption),
            "likeCount" to FirestoreValue(integerValue = post.likeCount.toString()),
            "createdAt" to FirestoreValue(integerValue = post.createdAt.toString()),
            "hashtags" to FirestoreValue(
                arrayValue = FirestoreArray(
                    values = post.hashtags.map { FirestoreValue(stringValue = it) }
                )
            )
        )
        return FirestoreDocument(fields = fields)
    }

    fun documentToPost(doc: FirestoreDocument): Post {
        val f = doc.fields
        val id = doc.name?.substringAfterLast("/") ?: ""
        return Post(
            postId = id,
            userId = f["userId"]?.stringValue ?: "",
            username = f["username"]?.stringValue ?: "",
            mediaUrl = f["mediaUrl"]?.stringValue ?: "",
            caption = f["caption"]?.stringValue ?: "",
            likeCount = f["likeCount"]?.integerValue?.toIntOrNull() ?: 0,
            createdAt = f["createdAt"]?.integerValue?.toLongOrNull() ?: 0L,
            hashtags = f["hashtags"]?.arrayValue?.values?.mapNotNull { it.stringValue } ?: emptyList()
        )
    }
}
