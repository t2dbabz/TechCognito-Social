package com.tunde.techcognitosocial.ui.main.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.tunde.techcognitosocial.R
import com.tunde.techcognitosocial.databinding.FragmentPostBinding
import com.tunde.techcognitosocial.ui.main.adapter.PostAdapter
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


         postAdapter = PostAdapter{

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