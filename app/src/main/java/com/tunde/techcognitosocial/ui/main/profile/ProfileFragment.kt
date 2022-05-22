package com.tunde.techcognitosocial.ui.main.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.google.android.material.tabs.TabLayoutMediator
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
    private val viewModel: ProfileViewModel by activityViewModels()
    @Inject lateinit var firebaseAuth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        val fragmentList = arrayListOf<Fragment>(
            PostFragment(),
        )

        val adapter = ProfileViewPagerAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )

        binding.viewPager.adapter = adapter

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabLayout = binding.tabLayout
        val viewPager2 = binding.viewPager
        val tabListName = arrayListOf<String>("Posts")

        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            tab.text = tabListName[position]
        }.attach()

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


        binding.toolbar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.editProfile -> {
                    findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
                    true
                }

                R.id.signOut -> {
                    viewModel.signOutUser()
                    true
                }
                else -> false
            }
        }

        viewModel.signOutStatus.observe(viewLifecycleOwner){ result ->
            when(result) {
               is Resource.Success -> {
                   Toast.makeText(requireContext(), "Logged Out Successfully", Toast.LENGTH_SHORT).show()
                   findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToAuthActivity())
                   requireActivity().finish()
                }

                else -> {}
            }
        }

        binding.addNewPostFab.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToAddPostFragment())
        }
    }

    private fun bindUserData(user: User) {
        if (user.photoUrl != null) {
            binding.userProfilePicImageView.load(user.photoUrl)
        } else {

            binding.userProfilePicImageView.load(Constants.getProfileImageUrl(user.userId!!))
        }

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

            userProfilePicImageView.visibility = View.VISIBLE
            profileFullNameTextView.visibility =  View.VISIBLE
            profileUserName.visibility =  View.VISIBLE
            dateIconImageView.visibility = View.VISIBLE
            profileUserDateCreated.visibility = View.VISIBLE
            profileUserBio.visibility =  View.VISIBLE
            dateIconImageView.visibility = View.VISIBLE
            profileUserDateCreated.visibility = View.VISIBLE
            locationIconImageView.visibility = View.VISIBLE
            profileUserLocation.visibility = View.VISIBLE
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