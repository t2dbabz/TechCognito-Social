package com.tunde.techcognitosocial.util

/**
 * used for :
 * verifying network calls
 * determining state of data : T?
 *
 * @SealedClass
 * impose restriction on inheritance, only classes in here can inherit
 */


sealed class Resource<T> (val data: T? = null, val message: String? = null){
    class Success<T> (data: T) : Resource<T>(data)

    class Error<T> (message: String, data: T? = null) : Resource<T>(data, message)

    class Loading<T> (data: T? = null) : Resource<T>(data)
}