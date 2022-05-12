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

    private val _posts = MutableLiveData<Resource<List<Post>>>()
    val post : LiveData<Resource<List<Post>>> = _posts


     fun getPosts() {
         _posts.value = Resource.Loading()
        viewModelScope.launch {
            val posts = mainRepository.getPosts()
            _posts.value = posts
        }
    }
}