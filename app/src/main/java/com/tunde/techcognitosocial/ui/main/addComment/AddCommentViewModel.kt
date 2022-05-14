package com.tunde.techcognitosocial.ui.main.addComment

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
class AddCommentViewModel @Inject constructor(
    private val mainRepository: MainRepository
): ViewModel() {


    private val _commentStatus = MutableLiveData<Resource<Any>>()
    val commentStatus : LiveData<Resource<Any>> = _commentStatus



    fun postComment(postId: String, commentText: String) {
        _commentStatus.value = Resource.Loading()
        viewModelScope.launch {
           val result = mainRepository.createComment(postId, commentText)
            _commentStatus.value = result
        }
    }
}