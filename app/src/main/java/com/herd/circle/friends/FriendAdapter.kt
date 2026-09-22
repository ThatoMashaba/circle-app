package com.herd.circle.friends

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.herd.circle.databinding.ItemFriendBinding
import com.herd.circle.model.Friendship
import com.herd.circle.model.FriendshipStatus

class FriendAdapter(
    private val onAcceptClicked: (Friendship) -> Unit
) : ListAdapter<Friendship, FriendAdapter.FriendViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val binding = ItemFriendBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FriendViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        holder.bind(getItem(position), onAcceptClicked)
    }

    class FriendViewHolder(private val binding: ItemFriendBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(friendship: Friendship, onAcceptClicked: (Friendship) -> Unit) {
            binding.tvName.text = friendship.otherUserName
            binding.tvStatus.text = friendship.status.name.lowercase()
            binding.btnAccept.visibility = if (friendship.status == FriendshipStatus.PENDING) View.VISIBLE else View.GONE
            binding.btnAccept.setOnClickListener { onAcceptClicked(friendship) }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Friendship>() {
            override fun areItemsTheSame(oldItem: Friendship, newItem: Friendship) =
                oldItem.friendshipId == newItem.friendshipId
            override fun areContentsTheSame(oldItem: Friendship, newItem: Friendship) = oldItem == newItem
        }
    }
}
