package com.tunde.techcognitosocial.ui.main.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tunde.techcognitosocial.R
import com.tunde.techcognitosocial.databinding.PostListItemBinding
import com.tunde.techcognitosocial.model.Post
import com.tunde.techcognitosocial.util.Constants
import java.math.BigInteger
import java.security.MessageDigest

class PostAdapter(val onPostClicked: (Post) -> Unit): ListAdapter<Post, PostAdapter.PostViewHolder>(DiffCallback) {

    private var onLikeClickListener: ((Post, Int) -> Unit)? = null
    private var onCommentClickListener: ((Post, String?) -> Unit)? = null
    private var onShareClickListener: ((Post )-> Unit)? = null


   inner class PostViewHolder(val binding: PostListItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post, position: Int) {
            var photoUrl: String? = null

            binding.postTextView.text = post.postText
            binding.commentFullNameTextView.text = post.author?.fullName
            binding.commentUserNameTextView.text = itemView.context.getString(R.string.post_username, post.author?.username)

            FirebaseFirestore.getInstance().collection(Constants.USERS_REF)
                .document(post.authorId!!).addSnapshotListener { document, exception ->

                    if (exception != null) {
                        Log.e("Exception", "Could not retrieve Document : ${exception.localizedMessage}")
                    }

                     photoUrl = document?.getString(Constants.PHOTO_URL)

                    if (photoUrl != null) {
                        binding.userProfilePicImageView.load(photoUrl)
                    } else {
                        binding.userProfilePicImageView.load(Constants.getProfileImageUrl(post.authorId))
                    }
                }


            binding.numLikesTextView.text = post.numLikes.toString()


            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

            if (post.likedBy?.contains(currentUserId) == true) {
                binding.likePostImageView.setImageResource(R.drawable.ic_heart_fill)
            } else {
                binding.likePostImageView.setImageResource(R.drawable.ic_heart_line)
            }

            itemView.setOnClickListener {
                onPostClicked(post)
            }


            binding.likePostImageView.setOnClickListener {
                onLikeClickListener?.let { onClick ->
                    onClick(post, position)
                }
            }

            binding.commentPostImageView.setOnClickListener {
                onCommentClickListener?.let { onClick ->
                    onClick(post, photoUrl)
                }
            }

            binding.sharePostImageView.setOnClickListener {
                onShareClickListener?.let { onClick ->
                    onClick(post)
                }
            }
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

    fun setOnCommentClickListener(listener: (Post, String?) -> Unit) {
        onCommentClickListener = listener
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