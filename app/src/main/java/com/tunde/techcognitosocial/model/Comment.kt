package com.tunde.techcognitosocial.model


data class Comment(
    val documentId: String? = null,
    val postId: String? = null,
    val authorId: String? = null,
    val commentText: String? = null,
    val numLikes: Long? = null,
    var likedBy:List<String>? = null,
    val author: User? = null,
    val dateCreated: Long? = null
)