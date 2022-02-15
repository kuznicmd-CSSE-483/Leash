package edu.rosehulman.leash.models

import com.google.firebase.Timestamp

data class User(
    var name: String = "",
    var created: Timestamp? = null,
    var storageUriString: String = ""
) {
    companion object {
        const val COLLECTION_PATH = "users"
    }
}
