package com.tunde.techcognitosocial.ui.main.profile

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.github.dhaval2404.imagepicker.ImagePicker
import com.tunde.techcognitosocial.R
import com.tunde.techcognitosocial.databinding.FragmentEditProfileBinding
import com.tunde.techcognitosocial.model.User
import com.tunde.techcognitosocial.util.Constants
import com.tunde.techcognitosocial.util.Resource
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class EditProfileFragment : Fragment() {

    private lateinit var binding: FragmentEditProfileBinding
    private val viewModel: ProfileViewModel by activityViewModels()

    private lateinit var mProfileUri: Uri



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.currentUserData.observe(viewLifecycleOwner){ result ->

            when(result) {
                is Resource.Success -> {
                    bindUserData(result.data!!)
                }
                else -> {

                }
            }

        }

        binding.addPhotoIcon.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .galleryOnly()
                .compress(1020)
                .maxResultSize(1080, 1080)
                .createIntent {intent ->
                    startForProfileImageResult.launch(intent)
                }
        }

        binding.saveProfileButton.setOnClickListener {
            updateUserProfile()
        }

        viewModel.userPhotoUploadStatus.observe(viewLifecycleOwner) { result ->

            when(result) {

                is Resource.Loading -> {

                }
                is Resource.Success ->{
                    binding.userProfilePicImageView.load(result.data)
                }


                else -> {

                }
            }

        }

        viewModel.userProfileUpdateStatus.observe(viewLifecycleOwner) { result ->

            when (result.data) {
                true -> {
                    Toast.makeText(requireContext(), "Profile Updated Successfully", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
                else -> {

                }
            }

        }




    }
    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            when (resultCode) {
                Activity.RESULT_OK -> {
                    //Image Uri will not be null for RESULT_OK
                    val fileUri = data?.data!!

                    mProfileUri = fileUri
                    // binding.userProfilePicImageView.setImageURI(fileUri)
                    viewModel.uploadProfilePhoto(fileUri)
                    Toast.makeText(requireContext(), "Task Done", Toast.LENGTH_SHORT).show()
                }
                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
                }
            }
        }





    private fun bindUserData(user: User) {
        if (user.photoUrl != null) {
            binding.userProfilePicImageView.load(user.photoUrl)
        } else {

            binding.userProfilePicImageView.load(Constants.getProfileImageUrl(user.userId!!))
        }

        binding.profileFullNameEditTextView.setText(user.fullName)

        if (user.userBio != null) {
            binding.profileBioEditTextView.setText(user.userBio)
        } else {
            binding.profileBioEditTextView.setText("")
        }


        if (user.location != null) {
            binding.profileLocationEditTextView.setText(user.location)
        } else {
            binding.profileLocationEditTextView.setText("")
        }


    }

    private fun updateUserProfile() {

        val fullName = binding.profileFullNameEditTextView.text.toString().trim()
        val bio = binding.profileBioEditTextView.text.toString().trim()
        val location = binding.profileLocationEditTextView.text.toString().trim()

        if (fullName.isNotEmpty() && bio.isNotEmpty() && location.isNotEmpty()) {
            viewModel.updateUserProfile(fullName, bio, location)
        } else {
            Toast.makeText(requireContext(), "Text Fields Should not be empty", Toast.LENGTH_SHORT).show()
        }
    }

}