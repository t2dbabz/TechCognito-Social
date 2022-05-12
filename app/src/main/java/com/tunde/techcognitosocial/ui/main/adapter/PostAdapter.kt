package com.tunde.techcognitosocial.ui.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tunde.techcognitosocial.databinding.PostListItemBinding
import com.tunde.techcognitosocial.model.Post

class PostAdapter(): ListAdapter<Post, PostAdapter.PostViewHolder>(DiffCallback) {

    class PostViewHolder(val binding: PostListItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            binding.postTextView.text = post.postText
            binding.commentFullNameTextView.text = post.author?.fullName
            binding.commentUserNameTextView.text = post.author?.username
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PostListItemBinding.inflate(inflater, parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.documentId == newItem.documentId
        }
    }


}