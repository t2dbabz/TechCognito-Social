package com.tunde.techcognitosocial.ui.main.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tunde.techcognitosocial.R
import com.tunde.techcognitosocial.databinding.FragmentHomeBinding
import com.tunde.techcognitosocial.ui.main.adapter.PostAdapter
import com.tunde.techcognitosocial.util.Resource
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var adapter: PostAdapter


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

       // viewModel.getPosts()

        adapter = PostAdapter()

        binding.postRecyclerView.adapter = adapter
        binding.postRecyclerView.layoutManager = LinearLayoutManager(requireActivity())

        binding.addPostFAB.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addPostFragment)
        }

        Log.e("Home Fragment", "OnViewCreated called")


        viewModel.post.observe(viewLifecycleOwner) { result ->
            adapter.submitList(result)
        }



        adapter.setOnLikeClickListener {post, position ->
            adapter.notifyItemChanged(position)
            viewModel.toggleLike(post)
        }

    }

}