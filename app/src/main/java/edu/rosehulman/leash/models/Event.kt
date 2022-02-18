package edu.rosehulman.leash.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp

class Event(
    var reminder: String = "",
    var name: String = "",
    var time: Timestamp = Timestamp.now(),
    var alert: String = "",
    var recurrence: String = "",
    var pet: String = "",
    var code: Int = 0
){
//    override fun toString(): String {
//        return if (name.isNotBlank()) "'$name', from $type" else ""
//    }

    @get:Exclude
    var id = ""

    @ServerTimestamp
    var created : Timestamp? = null

    companion object {
        const val COLLECTION_PATH = "Events"
        const val CREATED_KEY = "created"

        fun from(snapshot: DocumentSnapshot): Event {
            val event = snapshot.toObject(Event::class.java)!!
            event.id = snapshot.id
            return event
        }
    }
}