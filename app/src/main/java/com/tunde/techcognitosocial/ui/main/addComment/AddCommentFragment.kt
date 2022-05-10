package com.tunde.techcognitosocial.ui.main.addComment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.tunde.techcognitosocial.R
import com.tunde.techcognitosocial.databinding.FragmentAddCommentBinding


class AddCommentFragment : Fragment() {

    private lateinit var binding: FragmentAddCommentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAddCommentBinding.inflate(inflater, container, false)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        return binding.root
    }


}