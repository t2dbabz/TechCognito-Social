package com.tunde.techcognitosocial.ui.main.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.tunde.techcognitosocial.R
import com.tunde.techcognitosocial.databinding.FragmentPostBinding
import com.tunde.techcognitosocial.ui.main.adapter.PostAdapter
import com.tunde.techcognitosocial.ui.main.home.HomeFragmentDirections
import com.tunde.techcognitosocial.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class PostFragment : Fragment() {
    private lateinit var binding: FragmentPostBinding
    private val viewModel: ProfileViewModel by activityViewModels()
    @Inject lateinit var firebaseAuth: FirebaseAuth
    private lateinit var postAdapter: PostAdapter



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPostBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


         postAdapter = PostAdapter { post ->
             val bundle = bundleOf(
                 Constants.POST_ID to post.documentId,
                 Constants.AUTHOR_ID to post.authorId,
                 Constants.FULL_NAME to post.author?.fullName,
                 Constants.USERNAME to post.author?.username,
                 Constants.POST_TEXT to post.postText,
                 Constants.DATE_CREATED to post.dateCreated,
                 Constants.NUM_COMMENTS to post.numComments,
                 Constants.NUM_LIKES to post.numLikes
             )

             findNavController().navigate(R.id.action_profileFragment_to_postDetailFragment, bundle)
        }

        postAdapter.setOnLikeClickListener { post, position ->
            postAdapter.notifyItemChanged(position)
            viewModel.toggleLike(post)
        }

        postAdapter.setOnCommentClickListener { post, photoUrl ->
            val postId = post.documentId
            val postAuthorUserName = post.author?.username
            val userID = firebaseAuth.currentUser?.uid

            val action = ProfileFragmentDirections.actionProfileFragmentToAddCommentFragment(
                userID,
                postAuthorUserName,
                postId,
                photoUrl
            )
            findNavController().navigate(action)
        }

        val currentUserId = firebaseAuth.currentUser?.uid
        viewModel.getUserPosts(currentUserId as String)

        Log.e("PostFragment", currentUserId)

        binding.postRecyclerView.adapter = postAdapter

        binding.postRecyclerView.layoutManager = LinearLayoutManager(requireActivity())

        viewModel.getUserPosts(currentUserId).observe(viewLifecycleOwner){ result ->
            Log.e("PostFragment", result.size.toString())
            if (result != null) {
                binding.postProgressBar.visibility = View.GONE

                postAdapter.submitList(result)
            }

        }



    }


}