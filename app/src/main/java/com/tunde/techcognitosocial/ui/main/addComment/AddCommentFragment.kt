package com.tunde.techcognitosocial.ui.main.addComment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.tunde.techcognitosocial.R
import com.tunde.techcognitosocial.databinding.FragmentAddCommentBinding
import com.tunde.techcognitosocial.util.Constants
import com.tunde.techcognitosocial.util.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddCommentFragment : Fragment() {

    private lateinit var binding: FragmentAddCommentBinding
    private val args: AddCommentFragmentArgs by navArgs()
    private val viewModel: AddCommentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAddCommentBinding.inflate(inflater, container, false)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val postUserName = args.postAuthorUsername
        val postId = args.postId as String
        val currentUserId = args.authorUid as String
        val photoUrl = args.userPhotoUrl

        binding.replyingTextView.text = getString(R.string.comment_reply, postUserName)

        if (photoUrl != null) {
            binding.userProfilePicImageView.load(photoUrl)
        } else {
            binding.userProfilePicImageView.load(Constants.getProfileImageUrl(currentUserId))
        }


        binding.addCommentButton.setOnClickListener {

            val commentText = binding.commentEditText.text.toString().trim()
            if (commentText.isNotEmpty()) {
                viewModel.postComment(postId, commentText)
            } else {
                Toast.makeText(requireActivity(), "The comment field is empty", Toast.LENGTH_SHORT).show()
            }
        }


        viewModel.commentStatus.observe(viewLifecycleOwner) { result ->

            when (result) {
                is Resource.Loading -> {

                }

                is Resource.Success ->  {
                    Toast.makeText(requireActivity(), "The comment added successfully", Toast.LENGTH_SHORT).show()

                    findNavController().navigate(AddCommentFragmentDirections.actionAddCommentFragmentToPostDetailFragment(postId))

                }
                else -> {}
            }
        }


    }


}