package com.herd.circle

import com.herd.circle.auth.AuthRepository
import com.herd.circle.model.Post
import com.herd.circle.network.FirestoreApi
import com.herd.circle.network.FirestoreDocument
import com.herd.circle.network.FirestoreListResponse
import com.herd.circle.network.FirestoreValue
import com.herd.circle.repository.PostRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import retrofit2.Response

/**
 * Unit tests for PostRepository, mocking both AuthRepository (Firebase SDK
 * wrapper) and FirestoreApi (Retrofit REST interface) so the repository's
 * own logic — error handling, sorting, result mapping — is tested in
 * isolation from any real network or Firebase call.
 */
class PostRepositoryTest {

    private lateinit var authRepository: AuthRepository
    private lateinit var api: FirestoreApi
    private lateinit var repository: PostRepository

    @Before
    fun setUp() {
        authRepository = mock()
        api = mock()
        repository = PostRepository(authRepository, api)
    }

    @Test
    fun `getFeed returns failure when not authenticated`() = runTest {
        whenever(authRepository.getIdToken()).thenReturn(null)

        val result = repository.getFeed()

        assertTrue(result.isFailure)
    }

    @Test
    fun `getFeed returns posts sorted by newest first`() = runTest {
        whenever(authRepository.getIdToken()).thenReturn("fake-token")
        val doc1 = FirestoreDocument(
            name = "projects/x/databases/(default)/documents/posts/p1",
            fields = mapOf(
                "createdAt" to FirestoreValue(integerValue = "1000"),
                "userId" to FirestoreValue(stringValue = "u1")
            )
        )
        val doc2 = FirestoreDocument(
            name = "projects/x/databases/(default)/documents/posts/p2",
            fields = mapOf(
                "createdAt" to FirestoreValue(integerValue = "2000"),
                "userId" to FirestoreValue(stringValue = "u2")
            )
        )
        whenever(api.listDocuments("posts", "Bearer fake-token"))
            .thenReturn(Response.success(FirestoreListResponse(documents = listOf(doc1, doc2))))

        val result = repository.getFeed()

        assertTrue(result.isSuccess)
        val posts = result.getOrNull()!!
        assertEquals(2, posts.size)
        // newest first
        assertEquals("p2", posts[0].postId)
        assertEquals("p1", posts[1].postId)
    }

    @Test
    fun `createPost returns failure when not authenticated`() = runTest {
        whenever(authRepository.getIdToken()).thenReturn(null)

        val result = repository.createPost(Post(userId = "u1", caption = "hello"))

        assertTrue(result.isFailure)
    }

    @Test
    fun `createPost returns success on 200 response`() = runTest {
        whenever(authRepository.getIdToken()).thenReturn("fake-token")
        val returnedDoc = FirestoreDocument(
            name = "projects/x/databases/(default)/documents/posts/p9",
            fields = mapOf("caption" to FirestoreValue(stringValue = "hello"))
        )
        whenever(api.createDocument(eq("posts"), eq("Bearer fake-token"), org.mockito.kotlin.any()))
            .thenReturn(Response.success(returnedDoc))

        val result = repository.createPost(Post(userId = "u1", caption = "hello"))

        assertTrue(result.isSuccess)
        assertEquals("hello", result.getOrNull()?.caption)
    }

    private fun <T> eq(value: T): T = org.mockito.kotlin.eq(value)
}
