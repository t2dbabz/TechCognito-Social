package com.tunde.techcognitosocial.model

import java.util.*

data class User(
    val userId: String,
    val userName: String,
    val fullName: String,
    val email: String,
    val profilePictureUrl: String? = null,
    val userBio: String ?= null,
    val location: String? = null,
    val createdAt: Date? = null,
    var followers: List<String> = listOf(),
    var following: List<String> = listOf(),

)
