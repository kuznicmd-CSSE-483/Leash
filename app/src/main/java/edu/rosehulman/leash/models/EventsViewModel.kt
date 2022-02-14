package edu.rosehulman.leash.models

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.rosehulman.leash.Constants

class EventsViewModel  : ViewModel() {
    // Array of Events
    var events = ArrayList<Event>()
    var currentPos = 0

    fun getEventAt(pos: Int) = events[pos]
    fun getCurrentEvent() = getEventAt(currentPos)

    lateinit var ref: CollectionReference
    val subscriptions = HashMap<String, ListenerRegistration>()

    fun addListener(fragmentName: String, observer: () -> Unit) {
        val uid = Firebase.auth.currentUser!!.uid
        ref = Firebase.firestore.collection(User.COLLECTION_PATH).document(uid).collection((Event.COLLECTION_PATH))

        val subscription = ref
            .orderBy(Event.CREATED_KEY, Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot: QuerySnapshot?, error: FirebaseFirestoreException? ->
                error?.let {
                    Log.d(Constants.TAG, "Error:$error")
                    return@addSnapshotListener
                }
                events.clear()
                snapshot?.documents?.forEach {
                    events.add(Event.from(it))
                }
                observer()
            }
        subscriptions[fragmentName] = subscription
    }

    fun removeListener(fragmentName: String) {
        subscriptions[fragmentName]?.remove()
        subscriptions.remove(fragmentName)
    }

    fun addEvent(reminder: String, name: String, time: Timestamp, alert: String, recurrence: String, pet: String) {
        val newEvent = Event(reminder, name, time, alert, recurrence, pet)
        ref.add(newEvent)
    }

    fun updateCurrentEvent(reminder: String, name: String, time: Timestamp, alert: String, recurrence: String, pet: String){
        events[currentPos].reminder = reminder
        events[currentPos].name = name
        events[currentPos].time = time
        events[currentPos].alert = alert
        events[currentPos].recurrence = recurrence
        events[currentPos].pet = pet
        ref.document(getCurrentEvent().id).set(getCurrentEvent())
    }

    fun removeCurrentEvent() {
        ref.document(getCurrentEvent().id).delete()
        currentPos = 0
    }

    fun updatePos(pos: Int) {
        currentPos = pos
    }

    fun size() = events.size

}