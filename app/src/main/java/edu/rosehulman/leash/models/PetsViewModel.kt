package edu.rosehulman.leash.models

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.rosehulman.leash.Constants
import kotlin.random.Random

/*
 Includes CRUD Operations
*/
class PetsViewModel : ViewModel() {
    // Array of Pets
    var pets = ArrayList<Pet>()
    var currentPos = 0

    fun getPetAt(pos: Int) = pets[pos]
    fun getCurrentPet() = getPetAt(currentPos)

    lateinit var ref: CollectionReference
    val subscriptions = HashMap<String, ListenerRegistration>()

    fun addListener(fragmentName: String, observer: () -> Unit) {
        val uid = Firebase.auth.currentUser!!.uid
        ref = Firebase.firestore.collection(User.COLLECTION_PATH).document(uid).collection((Pet.COLLECTION_PATH))

        val subscription = ref
            .orderBy(Pet.CREATED_KEY, Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot: QuerySnapshot?, error: FirebaseFirestoreException? ->
                error?.let {
                    Log.d(Constants.TAG, "Error:$error")
                    return@addSnapshotListener
                }
                pets.clear()
                snapshot?.documents?.forEach {
                    pets.add(Pet.from(it))
                }
                observer()
            }
        subscriptions[fragmentName] = subscription
    }

    fun removeListener(fragmentName: String) {
        subscriptions[fragmentName]?.remove()
        subscriptions.remove(fragmentName)
    }

    /*
    This method adds a random photo and a random caption.
    // TODO: Come back and complete this method
     */
    fun addPet(name: String, birthdate: Timestamp, type: String) {
        val newPet = Pet(name, birthdate, type)
        ref.add(newPet)
    }

    fun updateCurrentPet(name: String, birthdate: Timestamp, type: String) {
        pets[currentPos].name = name
        pets[currentPos].birthdate = birthdate
        pets[currentPos].type = type
        ref.document(getCurrentPet().id).set(getCurrentPet())
    }

    fun removeCurrentPet() {
        ref.document(getCurrentPet().id).delete()
        currentPos = 0
    }

    fun updatePos(pos: Int) {
        currentPos = pos
    }

    fun size() = pets.size

    // Until is exclusive -> From 0 to 99
    fun getRandom() = Random.nextInt(5)


}