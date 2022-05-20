package com.tunde.techcognitosocial.ui.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.firebase.auth.FirebaseAuth
import com.tunde.techcognitosocial.R
import com.tunde.techcognitosocial.databinding.CommentListItemBinding
import com.tunde.techcognitosocial.databinding.PostListItemBinding
import com.tunde.techcognitosocial.model.Comment
import com.tunde.techcognitosocial.model.Post
import com.tunde.techcognitosocial.util.Constants

class CommentAdapter(private val onLikeCLicked: (Comment) -> Unit): ListAdapter<Comment, CommentAdapter.CommentViewHolder>(DiffCallback) {

   inner class CommentViewHolder(val binding: CommentListItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: Comment) {
            binding.commentFullNameTextView.text = comment.author?.fullName
            binding.commentUserNameTextView.text = comment.author?.username
            if (comment.author?.photoUrl != null) {
                binding.userProfilePicImageView.load(comment.author.photoUrl)
            } else {

                binding.userProfilePicImageView.load(Constants.getProfileImageUrl(comment.authorId!!))
            }
            binding.commentTextView.text = comment.commentText
            binding.commentNumLikesTextView.text = comment.numLikes.toString()

            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
            if (comment.likedBy?.contains(currentUserId) == true) {
                binding.likeCommentImageView.setImageResource(R.drawable.ic_heart_fill)
            } else {
                binding.likeCommentImageView.setImageResource(R.drawable.ic_heart_line)
            }

            binding.likeCommentImageView.setOnClickListener {
                onLikeCLicked(comment)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CommentListItemBinding.inflate(inflater, parent, false)
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Comment>() {
        override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem.documentId == newItem.documentId
        }
    }
}