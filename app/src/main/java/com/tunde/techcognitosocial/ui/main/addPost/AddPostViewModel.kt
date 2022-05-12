package com.tunde.techcognitosocial.ui.main.addPost

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tunde.techcognitosocial.data.MainRepository
import com.tunde.techcognitosocial.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddPostViewModel @Inject constructor(val mainRepository: MainRepository): ViewModel() {
    private val _postStatus = MutableLiveData<Resource<Any>>()
    val postStatus : LiveData<Resource<Any>> = _postStatus


    fun createPost(postText: String) {
        viewModelScope.launch {
            val result = mainRepository.createPost(postText)
            _postStatus.value = result
        }
    }
}