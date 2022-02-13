package edu.rosehulman.leash.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.Timestamp
import edu.rosehulman.leash.Constants
import edu.rosehulman.leash.R
import edu.rosehulman.leash.databinding.FragmentEventsCreateBinding
import edu.rosehulman.leash.models.EventsViewModel
import java.lang.Integer.parseInt
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.Duration.Companion.milliseconds

class EventsCreateFragment : Fragment() {

    private lateinit var model: EventsViewModel

    private lateinit var binding: FragmentEventsCreateBinding

    private lateinit var date: String

    private lateinit var time: String

    private lateinit var timestamp: Date

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        model = ViewModelProvider(requireActivity()).get(EventsViewModel::class.java)
        binding = FragmentEventsCreateBinding.inflate(inflater, container, false)

        setupButtons()

        return binding.root
    }

    fun setupButtons() {
        // Logic for choosing a time
        binding.timeCreateEditText.setOnClickListener {
            val constraintsBuilder =
                CalendarConstraints.Builder()
                    .setValidator(DateValidatorPointBackward.now())
            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select date")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .setInputMode(MaterialDatePicker.INPUT_MODE_TEXT)
                    .setCalendarConstraints(constraintsBuilder.build())
                    .build()
            datePicker.addOnPositiveButtonClickListener {
                datePicker.dismiss()
                binding.timeCreateEditText.setText(datePicker.headerText)
                val formatter = SimpleDateFormat("dd/MM/yyyy")
                date = formatter.format(datePicker.selection?.let { it1 -> Date(it1) })
                var year = parseInt("${date.subSequence(6,10)}")
                var month = parseInt("${date.subSequence(3,5)}")
                var day = parseInt("${date.subSequence(0,2)}")

                val picker =
                    MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_24H)
                        .setTitleText("Select Event time")
                        .build()
                picker.addOnPositiveButtonClickListener{
                    timestamp = Date(year, month, day, picker.hour, picker.minute)
                }
                picker.show(parentFragmentManager, "tag")
            }
            datePicker.show(parentFragmentManager, "tag")
        }

        // Logic for saving event
        // TODO: Timestamp is set now; should come from selected date from above
        binding.saveEventCreateButton.setOnClickListener {
            model.addEvent(binding.eventTypeCreateSpinner.selectedItem.toString(), binding.nameCreateEditText.text.toString(),
                Timestamp(timestamp), binding.alertCreateSpinner.selectedItem.toString(), binding.recurrenceCreateSpinner.selectedItem.toString(),
                binding.petCreateEditText.text.toString()
            )
            this.findNavController().navigate(R.id.navigation_events)
        }
    }

}