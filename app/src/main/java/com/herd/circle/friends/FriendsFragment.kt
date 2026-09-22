package com.herd.circle.friends

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.herd.circle.databinding.FragmentFriendsBinding
import com.herd.circle.repository.FriendRepository
import kotlinx.coroutines.launch

/**
 * Friends — see Part 1 Design Document, Section 4.6.
 * Manages the reciprocal (two-way) friend network described in the
 * research and design documents.
 */
class FriendsFragment : Fragment() {

    private var _binding: FragmentFriendsBinding? = null
    private val binding get() = _binding!!

    private val friendRepository = FriendRepository()
    private lateinit var adapter: FriendAdapter

    companion object {
        private const val TAG = "FriendsFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = FriendAdapter { friendship ->
            lifecycleScope.launch {
                val result = friendRepository.acceptRequest(friendship.friendshipId)
                result.onSuccess {
                    Log.i(TAG, "Accepted friend request ${friendship.friendshipId}")
                    loadFriends()
                }.onFailure { error ->
                    Toast.makeText(requireContext(), "Couldn't accept: ${error.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
        binding.rvFriends.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFriends.adapter = adapter
        loadFriends()
    }

    private fun loadFriends() {
        lifecycleScope.launch {
            val result = friendRepository.getFriendships()
            result.onSuccess { friendships ->
                Log.i(TAG, "Loaded ${friendships.size} friendships")
                adapter.submitList(friendships)
            }.onFailure { error ->
                Log.e(TAG, "Failed to load friendships", error)
                Toast.makeText(requireContext(), "Couldn't load friends: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
