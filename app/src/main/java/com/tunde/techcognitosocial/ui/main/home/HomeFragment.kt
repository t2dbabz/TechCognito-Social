package com.tunde.techcognitosocial.ui.main.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tunde.techcognitosocial.R
import com.tunde.techcognitosocial.databinding.FragmentHomeBinding
import com.tunde.techcognitosocial.ui.main.adapter.PostAdapter
import com.tunde.techcognitosocial.util.Constants
import com.tunde.techcognitosocial.util.Constants.AUTHOR_ID
import com.tunde.techcognitosocial.util.Constants.DATE_CREATED
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
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var adapter: PostAdapter

    private var photoUrl: String? = null

    @Inject lateinit var firebaseAuth: FirebaseAuth

    @Inject lateinit var firestore: FirebaseFirestore


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userID = firebaseAuth.currentUser?.uid
        adapter = PostAdapter{ post ->  
            val bundle = bundleOf(
                POST_ID to post.documentId,
                AUTHOR_ID to post.authorId,
                FULL_NAME to post.author?.fullName,
                USERNAME to post.author?.username,
                POST_TEXT to post.postText,
                DATE_CREATED to post.dateCreated,
                NUM_COMMENTS to post.numComments,
                NUM_LIKES to post.numLikes
            )

            findNavController().navigate(R.id.action_homeFragment_to_postDetailFragment, bundle)
        }

        binding.postRecyclerView.adapter = adapter
        binding.postRecyclerView.layoutManager = LinearLayoutManager(requireActivity())

        binding.addPostFAB.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToAddPostFragment(photoUrl) )
        }

        Log.e("Home Fragment", "OnViewCreated called")


        viewModel.post.observe(viewLifecycleOwner) { result ->
            adapter.submitList(result)
        }
        
        

        adapter.setOnLikeClickListener { post, position ->
            adapter.notifyItemChanged(position)
            viewModel.toggleLike(post)
        }

        adapter.setOnCommentClickListener { post, photoUrl ->
            val postId = post.documentId
            val postAuthorUserName = post.author?.username



            val action = HomeFragmentDirections.actionHomeFragmentToAddCommentFragment(
                userID,
                postAuthorUserName,
                postId,
                photoUrl
            )
            findNavController().navigate(action)
        }

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver(){
            override fun onChanged() {
                super.onChanged()
                binding.postRecyclerView.scrollToPosition(0)
            }
            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                binding.postRecyclerView.scrollToPosition(0)
            }
            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                binding.postRecyclerView.scrollToPosition(0)
            }
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                binding.postRecyclerView.scrollToPosition(0)
            }
            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                binding.postRecyclerView.scrollToPosition(0)
            }
            override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
                binding.postRecyclerView.scrollToPosition(0)
            }
        })

        if (userID != null) {
            FirebaseFirestore.getInstance().collection(Constants.USERS_REF)
                .document(userID).addSnapshotListener { document, exception ->

                    if (exception != null) {
                        Log.e("Exception", "Could not retrieve Document : ${exception.localizedMessage}")
                    }

                    photoUrl = document?.getString(Constants.PHOTO_URL)
                }
        }
    }

}