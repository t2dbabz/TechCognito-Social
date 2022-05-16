package com.tunde.techcognitosocial.ui.main.postDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tunde.techcognitosocial.data.MainRepository
import com.tunde.techcognitosocial.model.Comment
import com.tunde.techcognitosocial.model.Post
import com.tunde.techcognitosocial.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(val mainRepository: MainRepository): ViewModel() {

    private val _postDetail = MutableLiveData<Resource<Post>>()
            val postDetail : LiveData<Resource<Post>> = _postDetail

    private val _likePostStatus = MutableLiveData<Resource<Boolean>>()
    val likePostStatus : LiveData<Resource<Boolean>> = _likePostStatus

    fun getPostDetails(postId: String) {
        _postDetail.value = Resource.Loading()
        viewModelScope.launch {
           val result = mainRepository.getPostDetails(postId)
            _postDetail.value = result
        }

    }

    fun getPostComments(postId: String) = mainRepository.getPostComment(postId)

    fun toggleLikeComment(comment: Comment) {
        viewModelScope.launch {
            mainRepository.toggleLikeComment(comment)
        }
    }

    fun toggleLike(post: Post) {
        viewModelScope.launch {
        val result =  mainRepository.toggleLikePost(post)
            _likePostStatus.value = result
        }
    }
}