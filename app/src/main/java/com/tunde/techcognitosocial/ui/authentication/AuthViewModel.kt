package com.tunde.techcognitosocial.ui.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.FieldValue
import com.tunde.techcognitosocial.data.AuthRepository
import com.tunde.techcognitosocial.model.User
import com.tunde.techcognitosocial.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {


    private val _userRegistrationStatus = MutableLiveData<Resource<AuthResult>>()
    val userRegistrationStatus: LiveData<Resource<AuthResult>> = _userRegistrationStatus

    private val _userSignInStatus = MutableLiveData<Resource<AuthResult>>()
    val userSignInStatus: LiveData<Resource<AuthResult>> = _userSignInStatus


    fun registerNewUser(emailAddress: String, password: String) {

        viewModelScope.launch {
            _userRegistrationStatus.value = Resource.Loading()
            try {
                val result = authRepository.registerNewUser(emailAddress, password)

                _userRegistrationStatus.value = result
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Unable to Create account", data = null)
            }
            
        }
    }

    fun signInUser(emailAddress: String, password: String) {
        viewModelScope.launch {
            _userSignInStatus.value = Resource.Loading()
            try {

                val result = authRepository.signInUser(emailAddress, password)
                _userSignInStatus.value = result

            } catch (e: Exception) {
                Resource.Error(e.message ?: "Unable to Sign In", data = null)
            }
        }
    }

  fun createUserInDatabase(userId: String, fullName: String, userName: String, emailAddress: String) {
      viewModelScope.launch {

          val user = User(
              userId = userId,
              userName = userName,
              fullName = fullName,
              email = emailAddress,
          )
          authRepository.createUserInFireStore(user)
      }

  }
}