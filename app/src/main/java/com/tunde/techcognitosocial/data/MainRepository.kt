package com.tunde.techcognitosocial.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tunde.techcognitosocial.model.Post
import com.tunde.techcognitosocial.model.User
import com.tunde.techcognitosocial.util.Constants.POST_REF
import com.tunde.techcognitosocial.util.Constants.USERS_REF
import com.tunde.techcognitosocial.util.Resource
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

@ViewModelScoped
class MainRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val fireBaseFirestore: FirebaseFirestore
) {

    private val postRef = fireBaseFirestore.collection(POST_REF)

    suspend fun getCurrentUser(userId:String): Resource<User> = withContext(Dispatchers.IO) {
        return@withContext try {
            val userRef = fireBaseFirestore.collection(USERS_REF)
            val user = userRef.document(userId).get().await().toObject(User::class.java)!!

           Resource.Success(user)
        } catch (e: Exception) {
            Log.e("Repository11", e.message.toString())
            Resource.Error(e.message ?: "Could not get user")
        }
    }

    suspend fun createPost(postText: String): Resource<Any> = withContext(Dispatchers.IO){
      return@withContext  try {
          val uid = firebaseAuth.currentUser?.uid as String
          val postId = UUID.randomUUID().toString()
          Log.e("Repository", postId)
          val currentUser = getCurrentUser(uid).data!!


          val post = Post(
              documentId = postId,
              authorId = uid,
              postText = postText,
              numComments = 0,
              numLikes = 0,
              author = currentUser,
              dateCreated = System.currentTimeMillis()
          )

          postRef.document(postId).set(post).await()
          Resource.Success(Any())

        } catch (e: Exception) {
          Resource.Error(e.message ?: "Could not send your post: ${e.localizedMessage}")

        }
    }

    suspend fun getPosts(): Resource<List<Post>> = withContext(Dispatchers.IO){
        return@withContext try {
            val posts = postRef.get().await().toObjects(Post::class.java)
            Resource.Success(posts)
        }catch (e: Exception) {
            Resource.Error(e.message ?: "Could not get posts: ${e.localizedMessage}")
        }
    }


}