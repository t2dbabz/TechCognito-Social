package com.tunde.techcognitosocial.ui.main.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tunde.techcognitosocial.data.AuthRepository
import com.tunde.techcognitosocial.data.MainRepository
import com.tunde.techcognitosocial.model.Post
import com.tunde.techcognitosocial.model.User
import com.tunde.techcognitosocial.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(val mainRepository: MainRepository, val authRepository: AuthRepository): ViewModel() {


    private val _currentUserData = MutableLiveData<Resource<User>>()
    val currentUserData : LiveData<Resource<User>> = _currentUserData

    private val _userPhotoUploadStatus = MutableLiveData<Resource<String>>()
    val userPhotoUploadStatus : LiveData<Resource<String>> = _userPhotoUploadStatus

    private val _userProfileUpdateStatus = MutableLiveData<Resource<Boolean>>()
    val userProfileUpdateStatus : LiveData<Resource<Boolean>> = _userProfileUpdateStatus

    private val _signOutStatus = MutableLiveData<Resource<Boolean>>()
    val signOutStatus : LiveData<Resource<Boolean>> = _signOutStatus


    fun getUserData(userId: String) {
        _currentUserData.value = Resource.Loading()
        viewModelScope.launch {
            val result = mainRepository.getCurrentUser(userId)
            _currentUserData.value = result
        }
    }

    fun getUserPosts(userId: String) = mainRepository.getUserPosts(userId)

    fun uploadProfilePhoto(imageUri: Uri) {
        _userPhotoUploadStatus.value = Resource.Loading()
        viewModelScope.launch {
            val result = mainRepository.uploadUserProfilePic(imageUri)
            _userPhotoUploadStatus.value = result
        }
    }



    fun toggleLike(post: Post) {
        viewModelScope.launch {
            mainRepository.toggleLikePost(post)
        }
    }


    fun updateUserProfile(fullName: String, bio: String, location: String) {
        _userProfileUpdateStatus.value = Resource.Loading()
        viewModelScope.launch {
            val result = mainRepository.updateUserProfile(fullName, bio, location)
            _userProfileUpdateStatus.value = result
        }
    }

    fun signOutUser() {
        _signOutStatus.value = Resource.Loading()
        viewModelScope.launch {
            val result = authRepository.signOutUser()
            _signOutStatus.value = result
        }
    }


}

