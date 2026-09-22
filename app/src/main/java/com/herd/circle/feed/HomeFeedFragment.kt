package com.herd.circle.feed

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.herd.circle.databinding.FragmentHomeFeedBinding
import com.herd.circle.repository.PostRepository
import kotlinx.coroutines.launch

/**
 * Home Feed — see Part 1 Design Document, Section 4.3.
 * The private, friend-only visual feed at the heart of Circle.
 */
class HomeFeedFragment : Fragment() {

    private var _binding: FragmentHomeFeedBinding? = null
    private val binding get() = _binding!!

    private val postRepository = PostRepository()
    private val adapter = PostAdapter()

    companion object {
        private const val TAG = "HomeFeedFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvFeed.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFeed.adapter = adapter
        binding.swipeRefresh.setOnRefreshListener { loadFeed() }
        loadFeed()
    }

    private fun loadFeed() {
        binding.swipeRefresh.isRefreshing = true
        lifecycleScope.launch {
            val result = postRepository.getFeed()
            binding.swipeRefresh.isRefreshing = false
            result.onSuccess { posts ->
                Log.i(TAG, "Loaded ${posts.size} posts into feed")
                adapter.submitList(posts)
            }.onFailure { error ->
                Log.e(TAG, "Failed to load feed", error)
                Toast.makeText(requireContext(), "Couldn't load feed: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
