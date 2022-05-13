package com.tunde.techcognitosocial.util

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.*


fun <T> Query.livedata(modelClass: Class<T>): LiveData<List<T>> {
    return QuerySnapshotLiveData(
        this,
        modelClass,
    )
}


class QuerySnapshotLiveData<T>(private val query: Query, private val modelClass: Class<T>) : LiveData<List<T>>(){

    private var registration: ListenerRegistration? = null



    override fun onActive() {
        super.onActive()
        registration = query.addSnapshotListener { querySnapshot, exception ->
            if (exception == null) {
                value = querySnapshot?.documents?.map { it.toObject(modelClass)!! }
            } else {
                Log.e("FireStoreException", exception.message.toString())
            }
        }

        }

    override fun onInactive() {
        super.onInactive()

        registration?.also {
            it.remove()
            registration = null
        }
    }
}


