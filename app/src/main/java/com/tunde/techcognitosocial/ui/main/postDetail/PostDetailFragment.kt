package com.tunde.techcognitosocial.ui.main.postDetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.google.firebase.auth.FirebaseAuth
import com.tunde.techcognitosocial.R
import com.tunde.techcognitosocial.databinding.FragmentPostDetailBinding
import com.tunde.techcognitosocial.model.Post
import com.tunde.techcognitosocial.ui.main.adapter.CommentAdapter
import com.tunde.techcognitosocial.ui.main.addComment.AddCommentFragmentArgs
import com.tunde.techcognitosocial.util.Constants
import com.tunde.techcognitosocial.util.Constants.AUTHOR_ID
import com.tunde.techcognitosocial.util.Constants.FULL_NAME
import com.tunde.techcognitosocial.util.Constants.NUM_COMMENTS
import com.tunde.techcognitosocial.util.Constants.NUM_LIKES
import com.tunde.techcognitosocial.util.Constants.POST_ID
import com.tunde.techcognitosocial.util.Constants.POST_TEXT
import com.tunde.techcognitosocial.util.Constants.USERNAME
import com.tunde.techcognitosocial.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PostDetailFragment : Fragment() {

    private lateinit var binding: FragmentPostDetailBinding
    private val viewModel: PostDetailViewModel by viewModels()
    private val args: PostDetailFragmentArgs by navArgs()
    private lateinit var commentAdapter: CommentAdapter
    private var selectedPostId = ""
    private lateinit var selectedPost: Post

    @Inject lateinit var firebaseAuth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPostDetailBinding.inflate(inflater, container, false)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_postDetailFragment_to_homeFragment)
        }
        val postId = args.postId
        println(postId)
        viewModel.getPostDetails(postId)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {
            val postId = arguments?.getString(POST_ID) as String
            binding.postFullNameTextView.text = arguments?.getString(FULL_NAME)
            binding.postUserNameTextView.text = arguments?.getString(USERNAME)
            binding.postDetailTextView.text = arguments?.getString(POST_TEXT)
            binding.numCommentsTextView.text = arguments?.getLong(NUM_COMMENTS).toString()
            binding.numLikesTextView.text = arguments?.getLong(NUM_LIKES).toString()
            binding.userProfilePicImageView.load(arguments?.getString(AUTHOR_ID)?.let { Constants.getProfileImageUrl(it) })

            selectedPostId = postId

            viewModel.getPostDetails(postId)

            getPostComments(postId)

        } else {

            val postId = args.postId
            selectedPostId = postId
            println(postId)
            viewModel.getPostDetails(postId)
            getPostComments(postId)
        }

        commentAdapter = CommentAdapter { comment ->

            viewModel.toggleLikeComment(comment)
            Toast.makeText(requireContext(), "Like Clicked", Toast.LENGTH_SHORT).show()
        }

        binding.commentsRecyclerView.apply {
            adapter = commentAdapter
            layoutManager = LinearLayoutManager(requireActivity())
        }




        viewModel.postDetail.observe(viewLifecycleOwner) { result ->

            when(result) {
                is Resource.Loading -> {

                }

                is Resource.Success -> {
                    bindPostDetails(result.data!!)
                    selectedPost = result.data
                    val currentUserId = firebaseAuth.currentUser?.uid
                    if (selectedPost.likedBy?.contains(currentUserId) == true) {
                        binding.likePostImageView.setImageResource(R.drawable.ic_heart_fill)
                    } else {
                        binding.likePostImageView.setImageResource(R.drawable.ic_heart_line)
                    }
                }

                is Resource.Error -> {

                }

            }
        }

        viewModel.likePostStatus.observe(viewLifecycleOwner){ result ->
            if (result.data == true){
                viewModel.getPostDetails(selectedPostId)
            }
        }

        binding.commentPostImageView.setOnClickListener {

            findNavController().navigate(PostDetailFragmentDirections.actionPostDetailFragmentToAddCommentFragment(
                selectedPost.authorId,
                selectedPost.author?.username,
                selectedPost.documentId
            ))
        }





        binding.likePostImageView.setOnClickListener {

            viewModel.toggleLike(selectedPost)
        }



    }

    private fun bindPostDetails(post: Post) {
        binding.postFullNameTextView.text = post.author?.fullName
        binding.postUserNameTextView.text = post.author?.username
        binding.postDetailTextView.text = post.postText
        binding.numCommentsTextView.text = post.numComments.toString()
        binding.numLikesTextView.text = post.numLikes.toString()
        binding.userProfilePicImageView.load(Constants.getProfileImageUrl(post.authorId!!))

    }

    private fun getPostComments(postId: String) {
        viewModel.getPostComments(postId).observe(viewLifecycleOwner) { result ->
            if (result != null) {
                commentAdapter.submitList(result)
            }

        }
    }


}