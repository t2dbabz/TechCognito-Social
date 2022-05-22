package com.tunde.techcognitosocial.data

import android.util.Log
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.tunde.techcognitosocial.model.User
import com.tunde.techcognitosocial.util.Constants.CREATED_AT
import com.tunde.techcognitosocial.util.Constants.EMAIL
import com.tunde.techcognitosocial.util.Constants.FOLLOWERS
import com.tunde.techcognitosocial.util.Constants.FOLLOWING
import com.tunde.techcognitosocial.util.Constants.FULL_NAME
import com.tunde.techcognitosocial.util.Constants.LOCATION
import com.tunde.techcognitosocial.util.Constants.PHOTO_URL
import com.tunde.techcognitosocial.util.Constants.USERNAME
import com.tunde.techcognitosocial.util.Constants.USERS_REF
import com.tunde.techcognitosocial.util.Constants.USER_BIO
import com.tunde.techcognitosocial.util.Constants.USER_ID
import com.tunde.techcognitosocial.util.Resource
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ViewModelScoped
class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val fireBaseFirestore: FirebaseFirestore
) {


    suspend fun registerNewUser(emailAddress: String, password: String) : Resource<AuthResult> = withContext(Dispatchers.IO) {
        return@withContext try {
            val result = firebaseAuth.createUserWithEmailAndPassword(emailAddress, password).await()

            Resource.Success(result)

        } catch (e: Exception) {
            Resource.Error(e.message ?: "An Error Occurred")
        }
    }

    suspend fun signInUser(emailAddress: String, password: String): Resource<AuthResult> = withContext(Dispatchers.IO) {
        return@withContext try {
            val result = firebaseAuth.signInWithEmailAndPassword(emailAddress, password).await()

            Resource.Success(result)

        } catch (e: Exception) {
            Resource.Error(e.message ?: "An Error Occurred")
        }
    }

    suspend fun signOutUser(): Resource<Boolean> = withContext(Dispatchers.IO) {
        return@withContext try {

              firebaseAuth.signOut()
            Resource.Success(true)

        } catch (e: Exception) {
            Resource.Error(e.message ?: "An Error Occured")
        }
    }



    suspend fun createUserInFireStore(user: User) {
        withContext(Dispatchers.IO){
            try {
                val data = mapOf(
                    USER_ID to user.userId,
                    USERNAME to user.username,
                    FULL_NAME to user.fullName,
                    EMAIL to user.email,
                    PHOTO_URL to user.photoUrl,
                    USER_BIO to user.userBio,
                    LOCATION to user.location,
                    CREATED_AT to FieldValue.serverTimestamp(),
                    FOLLOWING to user.following,
                    FOLLOWERS to user.followers
                )
                fireBaseFirestore.collection(USERS_REF).document(user.userId!!).set(data).await()
            } catch (e: Exception) {
                Log.e("Exception", "Could not create user in database:${e.localizedMessage}")
            }

        }
    }

}