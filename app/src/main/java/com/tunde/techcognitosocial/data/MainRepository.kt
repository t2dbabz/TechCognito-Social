package com.tunde.techcognitosocial.data

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.tunde.techcognitosocial.model.Comment
import com.tunde.techcognitosocial.model.Post
import com.tunde.techcognitosocial.model.User
import com.tunde.techcognitosocial.util.Constants.AUTHOR_ID
import com.tunde.techcognitosocial.util.Constants.COMMENT_REF
import com.tunde.techcognitosocial.util.Constants.DATE_CREATED
import com.tunde.techcognitosocial.util.Constants.FULL_NAME
import com.tunde.techcognitosocial.util.Constants.LIKED_BY
import com.tunde.techcognitosocial.util.Constants.LOCATION
import com.tunde.techcognitosocial.util.Constants.NUM_COMMENTS
import com.tunde.techcognitosocial.util.Constants.NUM_LIKES
import com.tunde.techcognitosocial.util.Constants.PHOTO_URL
import com.tunde.techcognitosocial.util.Constants.POST_ID
import com.tunde.techcognitosocial.util.Constants.POST_REF
import com.tunde.techcognitosocial.util.Constants.POST_TEXT
import com.tunde.techcognitosocial.util.Constants.USERS_REF
import com.tunde.techcognitosocial.util.Constants.USER_BIO
import com.tunde.techcognitosocial.util.Constants.USER_ID
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
    private val fireBaseFirestore: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage
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

    suspend fun createComment(postId: String, commentText: String): Resource<Any> = withContext(Dispatchers.IO){
        return@withContext  try {

            val uid = firebaseAuth.currentUser?.uid as String
            val commentId = UUID.randomUUID().toString()
            Log.e("Repository", postId)
            val currentUser = getCurrentUser(uid).data!!
            fireBaseFirestore.runTransaction { transaction ->
                val postRef = fireBaseFirestore.collection(POST_REF).document(postId)

                val numComments = transaction.get(postRef).getLong(NUM_COMMENTS)?.plus(1)
                transaction.update(postRef, NUM_COMMENTS, numComments)

                val comment = Comment (
                    documentId = commentId,
                    authorId = uid,
                    postId = postId,
                    commentText = commentText,
                    numLikes = 0,
                    dateCreated = System.currentTimeMillis(),
                    author = currentUser
                )

                val commentRef = fireBaseFirestore.collection(POST_REF).document(postId).collection(COMMENT_REF)
                    .document(commentId)

                transaction.set(commentRef, comment)

            }.await()


            Resource.Success(Any())

        } catch (e: Exception) {
            Resource.Error(e.message ?: "Could not send your post: ${e.localizedMessage}")

        }
    }

     fun getPosts() =
         fireBaseFirestore.collection(POST_REF)
             .orderBy(DATE_CREATED, Query.Direction.DESCENDING)
             .livedata(Post::class.java)

    suspend fun getPostDetails(postId: String): Resource<Post> = withContext(Dispatchers.IO) {
        return@withContext try {
            val post = postRef.document(postId).get().await().toObject(Post::class.java) as Post
            Resource.Success(post)
        } catch (e: Exception) {
            Resource.Error(e.message?: "Could get ")
        }
    }

    fun getPostComment(postId: String) =
        postRef.document(postId).collection(COMMENT_REF)
            .orderBy(DATE_CREATED, Query.Direction.DESCENDING)
            .livedata(Comment::class.java)


    suspend fun toggleLikePost(post: Post) : Resource<Boolean> = withContext(Dispatchers.IO) {
        return@withContext try {
            var isLiked = false
            fireBaseFirestore.runTransaction { transaction ->
                val userId = firebaseAuth.currentUser?.uid
                val postRef = fireBaseFirestore.collection(POST_REF).document(post.documentId!!)
                val currentLikes =  transaction.get(postRef).toObject(Post::class.java)?.likedBy ?: listOf()

                if (currentLikes.contains(userId)) {
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

    suspend fun toggleLikeComment(comment: Comment) : Resource<Boolean> = withContext(Dispatchers.IO) {
        return@withContext try {
            var isLiked = false
            fireBaseFirestore.runTransaction { transaction ->
                val userId = firebaseAuth.currentUser?.uid
                val commentRef = fireBaseFirestore.collection(POST_REF).document(comment.postId!!).collection(COMMENT_REF)
                    .document(comment.documentId!!)

                val currentLikes =  transaction.get(commentRef).toObject(Comment::class.java)?.likedBy ?: listOf()

                if (currentLikes.contains(userId)) {
                    val numLikes = transaction.get(commentRef).getLong(NUM_LIKES)?.minus(1)

                    transaction.update(
                        commentRef,
                        LIKED_BY,
                        FieldValue.arrayRemove(userId),
                        NUM_LIKES,
                        numLikes
                    )
                } else {
                    val numLikes = transaction.get(commentRef).getLong(NUM_LIKES)?.plus(1)
                    transaction.update(
                        commentRef,
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

    fun searchPosts(searchText: String) =
        fireBaseFirestore.collection(POST_REF)
            .whereGreaterThanOrEqualTo(POST_TEXT, searchText)
            .livedata(Post::class.java)


    fun getUserPosts(userId: String) =
        fireBaseFirestore.collection(POST_REF)
            .orderBy(DATE_CREATED, Query.Direction.DESCENDING)
            .whereEqualTo(AUTHOR_ID, userId)
            .livedata(Post::class.java)

    suspend fun uploadUserProfilePic(imageUri: Uri) : Resource<String> = withContext(Dispatchers.IO) {
        return@withContext try {


            val currentUser =  firebaseAuth.currentUser
            val photoUploadResult = firebaseStorage.getReference(currentUser?.uid!!).putFile(imageUri).await()

            val photoUrl = photoUploadResult.metadata?.reference?.downloadUrl?.await().toString()

            val profileUpdates = userProfileChangeRequest {
                photoUri = Uri.parse(photoUrl)
            }

            currentUser.updateProfile(profileUpdates).await()

            Log.e("PhotoUrl", photoUrl)

            fireBaseFirestore.runTransaction { transaction->
                val userRef = fireBaseFirestore.collection(USERS_REF).document(currentUser.uid)

                transaction.update(userRef, PHOTO_URL, photoUrl)

            }.await()

            Resource.Success(photoUrl)

        } catch (e: Exception) {
            Log.e("Repository111", e.message.toString())
            Resource.Error(e.message ?: "Could not upload photo : ${e.localizedMessage}")
        }
    }

    suspend fun updateUserProfile(fullName: String, bio: String, location: String): Resource<Boolean> = withContext(Dispatchers.IO) {
        return@withContext try {

            val currentUserId = firebaseAuth.currentUser?.uid as String
            fireBaseFirestore.runTransaction { transaction ->
                val userRef = fireBaseFirestore.collection(USERS_REF).document(currentUserId)
                transaction.update(
                    userRef,
                    FULL_NAME,
                    fullName,
                    USER_BIO,
                    bio,
                    LOCATION,
                    location
                )
            }.await()

            Resource.Success(true)
        }catch (e: Exception) {
            Resource.Error(e.message ?: "Profile Not updated${e.localizedMessage}")
        }
    }


}