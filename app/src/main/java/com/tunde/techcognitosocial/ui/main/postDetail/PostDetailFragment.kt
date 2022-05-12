package com.tunde.techcognitosocial.ui.main.postDetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tunde.techcognitosocial.R
import com.tunde.techcognitosocial.databinding.FragmentPostDetailBinding


class PostDetailFragment : Fragment() {

    private lateinit var binding: FragmentPostDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPostDetailBinding.inflate(inflater, container, false)
        return binding.root
    }


}