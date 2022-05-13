package com.tunde.techcognitosocial.model

import com.google.firebase.firestore.Exclude

data class Post(
    val documentId: String? = null,
    val authorId: String? = null,
    val postText: String? = null,
    val numComments: Long? = null,
    val numLikes: Long? = null,
    val likedBy:List<String>? = null,
    val author: User? = null,
    val dateCreated: Long? = null,
)
