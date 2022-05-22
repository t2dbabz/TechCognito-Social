package com.tunde.techcognitosocial.ui.main.addPost

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
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.tunde.techcognitosocial.R
import com.tunde.techcognitosocial.databinding.FragmentAddPostBinding
import com.tunde.techcognitosocial.ui.main.addComment.AddCommentFragmentArgs
import com.tunde.techcognitosocial.util.Constants
import com.tunde.techcognitosocial.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class AddPostFragment : Fragment() {

    private lateinit var binding: FragmentAddPostBinding
    private val viewModel: AddPostViewModel by viewModels()
    private val args: AddPostFragmentArgs by navArgs()
    @Inject lateinit var firebaseAuth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAddPostBinding.inflate(inflater, container, false)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentUserId = firebaseAuth.currentUser?.uid as String
        val photoUrl = args.photoUrl


        if (photoUrl != null) {
            binding.userProfilePicImageView.load(photoUrl)
        } else {
            binding.userProfilePicImageView.load(Constants.getProfileImageUrl(currentUserId))
        }


        binding.addCommentButton.setOnClickListener {
            val postText = binding.addPostEditTextView.text.toString().trim()
            if (postText.isNotEmpty()) {
                viewModel.createPost(postText)
            } else {
                Toast.makeText(requireActivity(), "The post field is empty", Toast.LENGTH_SHORT).show()
            }
        }


        viewModel.postStatus.observe(viewLifecycleOwner) { result ->

            when(result) {
                is Resource.Success -> {

                    Snackbar.make(requireView(), "Post Sent Successfully", Snackbar.LENGTH_SHORT)
                        .show()
                    findNavController().navigateUp()
                }

                is Resource.Error -> {
                    Snackbar.make(requireView(), result.message.toString(), Snackbar.LENGTH_SHORT).show()
                }
                else -> {}
            }

        }

    }


}