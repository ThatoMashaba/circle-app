package com.herd.circle

import com.herd.circle.model.Post
import com.herd.circle.network.FirestoreConverter
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for FirestoreConverter — the logic that maps our plain Post
 * model to and from Firestore's typed REST document format. This is the
 * core piece of "using a RESTful API" worth testing in isolation, since it
 * has no Android framework dependencies.
 */
class FirestoreConverterTest {

    @Test
    fun `postToDocument maps all fields correctly`() {
        val post = Post(
            postId = "p1",
            userId = "u1",
            username = "thato",
            mediaUrl = "https://example.com/img.jpg",
            caption = "Test caption #fun",
            hashtags = listOf("#fun"),
            likeCount = 5,
            createdAt = 1000L
        )

        val doc = FirestoreConverter.postToDocument(post)

        assertEquals("u1", doc.fields["userId"]?.stringValue)
        assertEquals("thato", doc.fields["username"]?.stringValue)
        assertEquals("5", doc.fields["likeCount"]?.integerValue)
        assertEquals("1000", doc.fields["createdAt"]?.integerValue)
        assertEquals(1, doc.fields["hashtags"]?.arrayValue?.values?.size)
        assertEquals("#fun", doc.fields["hashtags"]?.arrayValue?.values?.get(0)?.stringValue)
    }

    @Test
    fun `documentToPost round-trips correctly`() {
        val original = Post(
            userId = "u2",
            username = "alex",
            mediaUrl = "https://example.com/a.jpg",
            caption = "Round trip test",
            hashtags = listOf("#roundtrip", "#test"),
            likeCount = 12,
            createdAt = 2000L
        )

        val doc = FirestoreConverter.postToDocument(original)
        val result = FirestoreConverter.documentToPost(doc)

        assertEquals(original.userId, result.userId)
        assertEquals(original.username, result.username)
        assertEquals(original.caption, result.caption)
        assertEquals(original.likeCount, result.likeCount)
        assertEquals(original.createdAt, result.createdAt)
        assertEquals(original.hashtags, result.hashtags)
    }

    @Test
    fun `documentToPost handles missing fields gracefully`() {
        val doc = com.herd.circle.network.FirestoreDocument(fields = emptyMap())
        val post = FirestoreConverter.documentToPost(doc)

        assertEquals("", post.userId)
        assertEquals(0, post.likeCount)
        assertEquals(emptyList<String>(), post.hashtags)
    }
}
