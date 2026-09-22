package com.herd.circle.post

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.herd.circle.auth.AuthRepository
import com.herd.circle.databinding.ActivityCreatePostBinding
import com.herd.circle.model.Post
import com.herd.circle.repository.PostRepository
import kotlinx.coroutines.launch

/**
 * Create Post — see Part 1 Design Document, Section 4.5.
 * Note: actual media upload to Firebase Storage (to obtain a real mediaUrl)
 * is left as a follow-up integration; this screen demonstrates the full
 * post-creation flow against the REST API with a placeholder URL, which is
 * consistent with the brief's allowance that final assets are not required
 * for the Part 2 prototype.
 */
class CreatePostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreatePostBinding
    private val postRepository = PostRepository()
    private val authRepository = AuthRepository()
    private var selectedMediaUri: Uri? = null

    companion object {
        private const val TAG = "CreatePostActivity"
    }

    private val pickMediaLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedMediaUri = uri
            Glide.with(this).load(uri).centerCrop().into(binding.ivPreview)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnPickMedia.setOnClickListener {
            pickMediaLauncher.launch("image/*")
        }

        binding.btnPost.setOnClickListener { submitPost() }
    }

    private fun submitPost() {
        val caption = binding.etCaption.text?.toString()?.trim().orEmpty()
        val userId = authRepository.currentUserId()

        if (userId == null) {
            Toast.makeText(this, "You must be logged in to post", Toast.LENGTH_SHORT).show()
            return
        }
        if (caption.isEmpty() && selectedMediaUri == null) {
            Toast.makeText(this, "Add a photo or write a caption first", Toast.LENGTH_SHORT).show()
            return
        }

        val hashtags = Regex("#\\w+").findAll(caption).map { it.value }.toList()

        val post = Post(
            userId = userId,
            username = "me", // in a full build, resolved from the user's Firestore profile
            mediaUrl = selectedMediaUri?.toString().orEmpty(),
            caption = caption,
            hashtags = hashtags
        )

        setLoading(true)
        lifecycleScope.launch {
            val result = postRepository.createPost(post)
            setLoading(false)
            result.onSuccess {
                Log.i(TAG, "Post created successfully")
                Toast.makeText(this@CreatePostActivity, "Posted to Circle!", Toast.LENGTH_SHORT).show()
                finish()
            }.onFailure { error ->
                Log.e(TAG, "Failed to create post", error)
                Toast.makeText(this@CreatePostActivity, "Couldn't post: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) android.view.View.VISIBLE else android.view.View.GONE
        binding.btnPost.isEnabled = !loading
    }
}
