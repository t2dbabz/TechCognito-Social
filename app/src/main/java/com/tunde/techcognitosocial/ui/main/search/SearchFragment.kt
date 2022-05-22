package com.tunde.techcognitosocial.ui.main.search

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.tunde.techcognitosocial.R
import com.tunde.techcognitosocial.databinding.FragmentSearchBinding
import com.tunde.techcognitosocial.ui.main.adapter.PostAdapter
import com.tunde.techcognitosocial.ui.main.profile.ProfileFragmentDirections
import com.tunde.techcognitosocial.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModels()
    private lateinit var adapter: PostAdapter
    @Inject lateinit var firebaseAuth: FirebaseAuth

    private lateinit var binding: FragmentSearchBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = PostAdapter { post ->
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

            findNavController().navigate(R.id.action_searchFragment_to_postDetailFragment, bundle)
        }

        binding.searchRecyclerView.adapter = adapter
        binding.searchRecyclerView.layoutManager = LinearLayoutManager(requireActivity())

        adapter.setOnLikeClickListener { post, position ->
            adapter.notifyItemChanged(position)
            viewModel.toggleLike(post)
        }

        adapter.setOnCommentClickListener { post, photoUrl ->
            val postId = post.documentId
            val postAuthorUserName = post.author?.username
            val userID = firebaseAuth.currentUser?.uid

            val action = SearchFragmentDirections.actionSearchFragmentToAddCommentFragment(
                userID,
                postAuthorUserName,
                postId,
                photoUrl
            )
            findNavController().navigate(action)
        }


        binding.searchView.queryHint = getString(R.string.search_post)

        binding.searchView.setOnQueryTextListener( object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    binding.searchRecyclerView.visibility = View.VISIBLE
                    binding.queryResultTextView.visibility = View.INVISIBLE
                    binding.progressBar.visibility = View.VISIBLE


                    viewModel.getSearchResult(query).observe(viewLifecycleOwner){ result ->
                        if (result != null && result.isNotEmpty()) {
                            binding.searchRecyclerView.visibility = View.VISIBLE
                            binding.progressBar.visibility = View.INVISIBLE
                            println(result)
                            adapter.submitList(result)
                        } else {
                            binding.progressBar.visibility = View.INVISIBLE
                            binding.queryResultTextView.visibility = View.VISIBLE
                            binding.queryResultTextView.text = getString(R.string.no_results_found)

                        }

                    }
                    hideKeyboard()
                }
              return  true
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                return  true
            }

        })

        binding.searchView.setOnCloseListener {
            binding.queryResultTextView.visibility = View.VISIBLE
            binding.queryResultTextView.text = getString(R.string.search_text)
            true
        }


        binding.searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            val bottomNav =  (activity as AppCompatActivity).findViewById<BottomNavigationView>(R.id.bottom_nav)
            if (hasFocus) {
                bottomNav.visibility = View.GONE
                binding.queryResultTextView.visibility = View.VISIBLE
                binding.queryResultTextView.text = getString(R.string.search_text)
            } else {
                bottomNav.visibility = View.VISIBLE
            }
        }




  }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

}