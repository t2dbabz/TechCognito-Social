package com.tunde.techcognitosocial.ui.main.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import com.tunde.techcognitosocial.R
import com.tunde.techcognitosocial.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding


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
        val toolbar = binding.toolbar
        val navHostFragment = NavHostFragment.findNavController(this)
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.homeFragment,
            R.id.searchFragment,  // set all your top level destinations in here
            R.id.searchFragment)
        )
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration)

        binding.addPostFAB.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addPostFragment)
        }


    }

}