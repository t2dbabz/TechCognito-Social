package com.tunde.techcognitosocial.ui.main.search

import androidx.lifecycle.ViewModel
import com.tunde.techcognitosocial.data.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(val mainRepository: MainRepository): ViewModel() {


    fun getSearchResult(searchText: String) = mainRepository.searchPosts(searchText)

}