package edu.rosehulman.leash.adapters


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.util.rangeTo
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.rosehulman.leash.R
import edu.rosehulman.leash.models.Pet
import edu.rosehulman.leash.models.PetsViewModel
import edu.rosehulman.leash.ui.PetsFragment

class PetsAdapter(val fragment: PetsFragment) : RecyclerView.Adapter<PetsAdapter.PetsViewHolder>() {

    val model = ViewModelProvider(fragment.requireActivity()).get(PetsViewModel::class.java)

    /** This gets fired every time an adapter is made
     * Just added to make sure that connection between app
     * and Firebase works **/
    init {
          // Add data to a Firestore collection
          // Firebase.firestore.collection("Pets").add(Pet("Rudolph", Timestamp.now(), "dog"))
    }

    fun addListener(fragmentName: String) {
        model.addListener(fragmentName) {
            notifyDataSetChanged()
        }
    }

    fun removeListener(fragmentName: String) {
        model.removeListener(fragmentName)
    }

    /** Called when RecyclerView needs a new [ViewHolder] of the given type to represent...*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.pet_item, parent, false)
        return PetsViewHolder(view)
    }

    /** Called by RecyclerView to display the data at the specified position. This method should...*/
    override fun onBindViewHolder(holder: PetsViewHolder, position: Int) {
        holder.bind(model.getPetAt(position))
    }

    /** Returns the total number of items in the data set held by the adapter...*/
    override fun getItemCount() = model.size()


    /*
    This is where we add a photo; Calls the add method located in the PhotosViewModel
     */
    fun addPet() {
//        model.addPet()
//        notifyDataSetChanged()
    }

    inner class PetsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val petNameTextView: TextView = itemView.findViewById(R.id.pet_name_textView)
        val petTypeTextView: TextView = itemView.findViewById(R.id.pet_type_textView)
        val petBirthdateTextView: TextView = itemView.findViewById(R.id.pet_birthdate_textView)

        init {
//            itemView.setOnClickListener {
//                model.updatePos(adapterPosition)
//                fragment.findNavController().navigate(R.id.nav_edit_photos,
//                    null,
//                    // Simple animation when sliding between pages
//                    navOptions {
//                        anim {
//                            enter = R.anim.slide_in_left
//                            exit = R.anim.slide_out_right
//                        }
//                    })
        }

//            itemView.setOnLongClickListener{
//                model.updatePos(adapterPosition)
//                model.toggleCurrentPhoto()
//                model.updateCurrentPhoto(model.getCurrentPhoto().url, model.getCurrentPhoto().caption, model.getCurrentPhoto().isSelected)
////                notifyDataSetChanged()
//                true
//            }

        // This method needs to be fast! It can get called many many many times over!!!
        fun bind(pet: Pet) {
            petNameTextView.text = pet.name
            petTypeTextView.text = "Pet Type: ${pet.type}"
            petBirthdateTextView.text = "Birthdate: ${parseDate(pet.birthdate)}"
        }
    }

    fun parseDate(time: Timestamp): String {
        return "${time.toDate().month.toString() + 1}/${time.toDate().date.toString()}/${time.toDate().year.toString().substring(1,3)}"
    }
}