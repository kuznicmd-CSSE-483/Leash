package edu.rosehulman.leash.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import edu.rosehulman.leash.R
import edu.rosehulman.leash.models.Event
import edu.rosehulman.leash.models.EventsViewModel
import edu.rosehulman.leash.ui.EventsFragment
import edu.rosehulman.leash.ui.PetsFragment

class EventAdapter (val fragment: EventsFragment) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {
    val model = ViewModelProvider(fragment.requireActivity()).get(EventsViewModel::class.java)

    fun addListener(fragmentName: String) {
        model.addListener(fragmentName) {
            notifyDataSetChanged()
        }
    }

    fun removeListener(fragmentName: String) {
        model.removeListener(fragmentName)
    }

    /** Called when RecyclerView needs a new [ViewHolder] of the given type to represent...*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventAdapter.EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.event_item, parent, false)
        return EventViewHolder(view)
    }

    /** Called by RecyclerView to display the data at the specified position. This method should...*/
    override fun onBindViewHolder(holder: EventAdapter.EventViewHolder, position: Int) {
        holder.bind(model.getEventAt(position))
    }

    /** Returns the total number of items in the data set held by the adapter...*/
    override fun getItemCount() = model.size()

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eventNameTextView: TextView = itemView.findViewById(R.id.event_textView)
        val eventPetTextView: TextView = itemView.findViewById(R.id.pet_name_textView)
        val eventTimeTextView: TextView = itemView.findViewById(R.id.time_textView)
        val eventEditImageView: ImageView = itemView.findViewById(R.id.edit_imageView)
        val eventDateTextView: TextView = itemView.findViewById(R.id.date_textView)

        init {
            eventEditImageView.setOnClickListener {
                model.updatePos(adapterPosition)
                fragment.findNavController().navigate(R.id.navigation_events_edit,
                    null,
                    // Simple animation when sliding between pages
                    navOptions {
                        anim {
                            enter = R.anim.fui_slide_in_right
                            exit = R.anim.fui_slide_out_left
                        }
                    }
                )
            }

//            itemView.setOnLongClickListener{
//                model.updatePos(adapterPosition)
//                model.toggleCurrentPhoto()
//                model.updateCurrentPhoto(model.getCurrentPhoto().url, model.getCurrentPhoto().caption, model.getCurrentPhoto().isSelected)
////                notifyDataSetChanged()
//                true
        }

        // This method needs to be fast! It can get called many many many times over!!!
        fun bind(event: Event) {
            eventNameTextView.text = event.name
            eventPetTextView.text = "Pet: ${event.pet}"
            eventTimeTextView.text = "Time: ${parseTime(event.time)}"
            eventDateTextView.text = "${parseDate(event.time)}"
        }
    }

    fun parseDate(time: Timestamp): String {
        return "${time.toDate().month}/${time.toDate().date.toString()}/${time.toDate().year.toString().substring(1,3)}"
    }

    fun parseTime(time: Timestamp): String {
        return "${time.toDate().hours.toString()}:${time.toDate().minutes.toString()}"
    }
}