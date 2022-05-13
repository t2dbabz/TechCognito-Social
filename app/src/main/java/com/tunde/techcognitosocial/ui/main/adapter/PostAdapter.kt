package com.tunde.techcognitosocial.ui.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.tunde.techcognitosocial.R
import com.tunde.techcognitosocial.databinding.PostListItemBinding
import com.tunde.techcognitosocial.model.Post
import java.math.BigInteger
import java.security.MessageDigest

class PostAdapter(): ListAdapter<Post, PostAdapter.PostViewHolder>(DiffCallback) {

    private var onLikeClickListener: ((Post, Int) -> Unit)? = null
    private var onCommentClickListener: ((Post) -> Unit)? = null
    private var onShareClickListener: ((Post )-> Unit)? = null

   inner class PostViewHolder(val binding: PostListItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post, position: Int) {
            binding.postTextView.text = post.postText
            binding.commentFullNameTextView.text = post.author?.fullName
            binding.commentUserNameTextView.text = itemView.context.getString(R.string.post_username, post.author?.username)
            binding.userProfilePicImageView.load(getProfileImageUrl(post.authorId!!))
            binding.numLikesTextView.text = post.numLikes.toString()



            if (post.likedBy?.contains(post.authorId) == true) {
                binding.likePostImageView.setImageResource(R.drawable.ic_heart_fill)
            } else {
                binding.likePostImageView.setImageResource(R.drawable.ic_heart_line)
            }


            binding.likePostImageView.setOnClickListener {
                onLikeClickListener?.let { onClick ->
                    onClick(post, position)
                }
            }

            binding.commentImageView.setOnClickListener {
                onCommentClickListener?.let { onClick ->
                    onClick(post)
                }
            }

            binding.sharePostImageView.setOnClickListener {
                onShareClickListener?.let { onClick ->
                    onClick(post)
                }
            }
        }

        private fun getProfileImageUrl(username: String): String {
            val digest = MessageDigest.getInstance("MD5")
            val hash = digest.digest(username.toByteArray())
            val bigInt = BigInteger(hash)
            val hex = bigInt.abs().toString(16)
            return "https://www.gravatar.com/avatar/$hex?d=identicon"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PostListItemBinding.inflate(inflater, parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    fun setOnLikeClickListener(listener: (Post, Int) -> Unit) {
        onLikeClickListener = listener
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