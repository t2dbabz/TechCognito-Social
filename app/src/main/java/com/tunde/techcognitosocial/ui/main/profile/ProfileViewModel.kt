package com.tunde.techcognitosocial.ui.main.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tunde.techcognitosocial.data.MainRepository
import com.tunde.techcognitosocial.model.User
import com.tunde.techcognitosocial.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(val mainRepository: MainRepository): ViewModel() {


    private val _currentUserData = MutableLiveData<Resource<User>>()
    val currentUserData : LiveData<Resource<User>> = _currentUserData



    fun getUserData(userId: String) {
        _currentUserData.value = Resource.Loading()
        viewModelScope.launch {
            val result = mainRepository.getCurrentUser(userId)
            _currentUserData.value = result
        }
    }
}

