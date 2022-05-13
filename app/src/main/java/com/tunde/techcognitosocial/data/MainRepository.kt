package com.tunde.techcognitosocial.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.tunde.techcognitosocial.model.Post
import com.tunde.techcognitosocial.model.User
import com.tunde.techcognitosocial.util.Constants.DATE_CREATED
import com.tunde.techcognitosocial.util.Constants.LIKED_BY
import com.tunde.techcognitosocial.util.Constants.NUM_LIKES
import com.tunde.techcognitosocial.util.Constants.POST_REF
import com.tunde.techcognitosocial.util.Constants.USERS_REF
import com.tunde.techcognitosocial.util.QuerySnapshotLiveData
import com.tunde.techcognitosocial.util.Resource
import com.tunde.techcognitosocial.util.livedata
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

     fun getPosts() =
         fireBaseFirestore.collection(POST_REF)
             .orderBy(DATE_CREATED, Query.Direction.DESCENDING)
             .livedata(Post::class.java)



    suspend fun toggleLikePost(post: Post) : Resource<Boolean> = withContext(Dispatchers.IO) {
        return@withContext try {
            var isLiked = false
            fireBaseFirestore.runTransaction { transaction ->
                val userId = firebaseAuth.currentUser?.uid
                val postRef = fireBaseFirestore.collection(POST_REF).document(post.documentId!!)
                val currentLikes =  transaction.get(postRef).toObject(Post::class.java)?.likedBy ?: listOf()

                if (currentLikes.contains(post.authorId)) {
                    val numLikes = transaction.get(postRef).getLong(NUM_LIKES)?.minus(1)

                    transaction.update(
                        postRef,
                        LIKED_BY,
                        FieldValue.arrayRemove(userId),
                        NUM_LIKES,
                        numLikes
                    )
                } else {
                    val numLikes = transaction.get(postRef).getLong(NUM_LIKES)?.plus(1)
                    transaction.update(
                        postRef,
                        LIKED_BY,
                        FieldValue.arrayUnion(userId),
                        NUM_LIKES,
                        numLikes
                    )
                    isLiked = true
                }
            }.await()
            Resource.Success(isLiked)
        } catch (e: Exception) {
            Log.e("Repository111", e.message.toString())
            Resource.Error(e.message ?: "Post could not be liked : ${e.localizedMessage}")
        }
    }


}