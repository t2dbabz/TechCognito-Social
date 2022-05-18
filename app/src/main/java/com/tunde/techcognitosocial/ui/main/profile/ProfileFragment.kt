package com.tunde.techcognitosocial.ui.main.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import coil.load
import com.google.firebase.auth.FirebaseAuth
import com.tunde.techcognitosocial.R
import com.tunde.techcognitosocial.databinding.FragmentProfileBinding
import com.tunde.techcognitosocial.model.User
import com.tunde.techcognitosocial.util.Constants
import com.tunde.techcognitosocial.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val viewModel: ProfileViewModel by viewModels()
    @Inject lateinit var firebaseAuth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)


        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentUserId = firebaseAuth.currentUser?.uid as String

        viewModel.getUserData(currentUserId)

        viewModel.currentUserData.observe(viewLifecycleOwner){ result ->
            when(result) {
                is Resource.Loading -> {
                    showProgressBar()
                }

                is Resource.Success -> {
                    bindUserData(result.data as User)
                    hideProgressBar()
                }

                is Resource.Error -> {
                    hideProgressBar()
                }
            }

        }




    }

    private fun bindUserData(user: User) {
        binding.userProfilePicImageView.load(Constants.getProfileImageUrl(user.userId!!))
        binding .profileFullNameTextView.text = user.fullName
        binding.profileUserName.text = getString(R.string.post_username, user.username)

        val dateFormatter = SimpleDateFormat("MMMM y", Locale.getDefault())
        val dateString = dateFormatter.format(user.createdAt!!)
        binding.profileUserDateCreated.text = getString(R.string.user_date_joined, dateString)

        if (user.location != null) {
            binding.profileUserLocation.text = user.location

        } else {
            binding.locationIconImageView.visibility = View.GONE
            binding.profileUserLocation.visibility = View.GONE
        }

        if (user.userBio != null) {
            binding.profileUserBio.text = user.userBio

        } else {
            binding.profileUserBio.visibility = View.GONE

        }

    }

    private fun hideProgressBar() {
        binding.apply {
            binding.profileProgressBar.visibility = View.GONE
            userProfilePicImageView.visibility = View.VISIBLE
            profileFullNameTextView.visibility =  View.VISIBLE
            profileUserName.visibility =  View.VISIBLE
            dateIconImageView.visibility = View.VISIBLE
            profileUserDateCreated.visibility = View.VISIBLE
        }
    }

    private fun showProgressBar() {
        binding.apply {
            userProfilePicImageView.visibility = View.INVISIBLE
            profileFullNameTextView.visibility =  View.INVISIBLE
            profileUserName.visibility =  View.INVISIBLE
            profileUserBio.visibility =  View.INVISIBLE
            dateIconImageView.visibility = View.INVISIBLE
            profileUserDateCreated.visibility = View.INVISIBLE
            locationIconImageView.visibility = View.INVISIBLE
            profileUserLocation.visibility = View.INVISIBLE
        }
    }


}