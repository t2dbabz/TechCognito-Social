package com.tunde.techcognitosocial.util

import java.math.BigInteger
import java.security.MessageDigest

object Constants {

    //References
    const val USERS_REF = "users"
    const val POST_REF = "posts"
    const val COMMENT_REF = "comments"


    //Fields
    const val USER_ID = "userId"
    const val USERNAME = "username"
    const val FULL_NAME = "fullName"
    const val EMAIL = "email"
    const val PHOTO_URL = "photoUrl"
    const val USER_BIO = "userBio"
    const val LOCATION = "location"
    const val CREATED_AT = "createdAt"
    const val FOLLOWERS = "followers"
    const val FOLLOWING = "following"

    //Post Fields
    const val POST_ID = "postId"
    const val AUTHOR_ID = "authorId"
    const val  NUM_COMMENTS = "numComments"
    const val NUM_LIKES = "numLikes"
    const val LIKED_BY = "likedBy"
    const val DATE_CREATED = "dateCreated"
    const val POST_TEXT = "postText"

    fun getProfileImageUrl(username: String): String {
        val digest = MessageDigest.getInstance("MD5")
        val hash = digest.digest(username.toByteArray())
        val bigInt = BigInteger(hash)
        val hex = bigInt.abs().toString(16)
        return "https://www.gravatar.com/avatar/$hex?d=identicon"
    }



}