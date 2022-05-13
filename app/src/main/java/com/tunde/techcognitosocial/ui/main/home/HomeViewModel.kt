package com.tunde.techcognitosocial.ui.main.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tunde.techcognitosocial.data.MainRepository
import com.tunde.techcognitosocial.model.Post
import com.tunde.techcognitosocial.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mainRepository: MainRepository
): ViewModel() {

    val post : LiveData<List<Post>> = mainRepository.getPosts()


    fun toggleLike(post: Post) {
        viewModelScope.launch {
            mainRepository.toggleLikePost(post)
        }
    }
}