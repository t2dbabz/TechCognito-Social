package com.tunde.techcognitosocial.ui.main.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tunde.techcognitosocial.data.MainRepository
import com.tunde.techcognitosocial.model.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(val mainRepository: MainRepository): ViewModel() {


    fun getSearchResult(searchText: String) = mainRepository.searchPosts(searchText)

    fun toggleLike(post: Post) {
        viewModelScope.launch {
            mainRepository.toggleLikePost(post)
        }
    }

}