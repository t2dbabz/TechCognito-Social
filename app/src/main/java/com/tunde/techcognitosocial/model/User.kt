package com.tunde.techcognitosocial.model

import java.util.*

data class User (
    var userId: String? = null,
    var username: String? = null,
    var fullName: String? = null,
    var email: String? = null,
    var photoUrl: String? = null,
    var userBio: String ?= null,
    var location: String? = null,
    val createdAt: Date? = null,
    var followers: List<String>? = null,
    var following: List<String>? = null,

)
