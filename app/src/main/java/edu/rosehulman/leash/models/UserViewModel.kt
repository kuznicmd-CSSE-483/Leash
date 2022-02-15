package edu.rosehulman.leash.models

import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UserViewModel : ViewModel() {
    var ref = Firebase.firestore.collection(User.COLLECTION_PATH).document(Firebase.auth.uid!!)

    var user: User? = null

    fun getOrMakeUser(observer: () -> Unit) {
        if (user != null) {
            observer()
        } else {
            ref.get().addOnSuccessListener { snapshot: DocumentSnapshot ->
                if (snapshot.exists()) {
                    user = snapshot.toObject(User::class.java)
                } else {
                    user = User(name = Firebase.auth.currentUser!!.displayName!!, created = Timestamp.now())
                    ref.set(user!!)
                }
                observer()
            }
        }
    }

    fun update(newName: String, newStorageUriString: String) {
        if (user != null) {
            with(user!!) {
                name = newName
                storageUriString = newStorageUriString
                ref.set(this)
            }
        }
    }
}