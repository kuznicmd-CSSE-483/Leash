package edu.rosehulman.leash.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class Pet (var name: String="", var birthdate: Timestamp = Timestamp.now(), var type: String =""){
    override fun toString(): String {
        return if (name.isNotBlank()) "'$name', from $type" else ""
    }

    @get:Exclude
    var id = ""

    @ServerTimestamp
    var created : Timestamp? = null

    companion object {
        const val COLLECTION_PATH = "Pets"
        const val CREATED_KEY = "created"

        fun from(snapshot: DocumentSnapshot): Pet {
            val pet = snapshot.toObject(Pet::class.java)!!
            pet.id = snapshot.id
            return pet
        }
    }
}