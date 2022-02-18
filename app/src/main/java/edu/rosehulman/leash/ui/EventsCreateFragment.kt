package edu.rosehulman.leash.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.Timestamp
import edu.rosehulman.leash.R
import edu.rosehulman.leash.databinding.FragmentEventsCreateBinding
import edu.rosehulman.leash.models.AlarmViewModel
import edu.rosehulman.leash.models.EventsViewModel
import edu.rosehulman.leash.utils.NotificationUtils
import java.lang.Integer.parseInt
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random


class EventsCreateFragment : Fragment() {

    private lateinit var model: EventsViewModel
    private lateinit var binding: FragmentEventsCreateBinding
    private lateinit var date: String
    private lateinit var timestamp: Date

    // Create Alarm information
    private lateinit var alarmModel: AlarmViewModel
    var code = getRandom()

    // Random Number needed
    fun getRandom() = Random.nextInt(1000)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        model = ViewModelProvider(requireActivity()).get(EventsViewModel::class.java)
        binding = FragmentEventsCreateBinding.inflate(inflater, container, false)
        alarmModel = ViewModelProvider(requireActivity())[AlarmViewModel::class.java]
        alarmModel.setCurrentTime()
        NotificationUtils.createChannel(requireContext())

        timestamp = Timestamp.now().toDate()

        // Set up Spinners
        val eventTypeSpinner: Spinner = binding.eventTypeCreateSpinner
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.event_type_array,
            R.layout.color_spinner_layout
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout)
            // Apply the adapter to the spinner
            eventTypeSpinner.adapter = adapter
        }

        val alertSpinner: Spinner = binding.alertCreateSpinner
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.alert_array,
            R.layout.color_spinner_layout
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout)
            // Apply the adapter to the spinner
            alertSpinner.adapter = adapter
        }

        val recurrenceSpinner: Spinner = binding.recurrenceCreateSpinner
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.recurrence_array,
            R.layout.color_spinner_layout
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout)
            // Apply the adapter to the spinner
            recurrenceSpinner.adapter = adapter
        }

        setupButtons()

        return binding.root
    }

    fun setupButtons() {
        // Logic for choosing a time
        binding.timeCreateEditText.setOnClickListener {
            if (binding.eventTypeCreateSpinner.selectedItem.toString() == "Event") {
                binding.recurrenceCreateSpinner.setEnabled(false)
                binding.recurrenceCreateSpinner.setSelection(0)
            } else {
                binding.recurrenceCreateSpinner.setEnabled(true)
            }
            val constraintsBuilder =
                CalendarConstraints.Builder()
                    .setValidator(DateValidatorPointForward.now())
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
                var year = parseInt("${date.subSequence(6, 10)}") - 1900
                var month = parseInt("${date.subSequence(3, 5)}")
                var day = parseInt("${date.subSequence(0, 2)}")

                val picker =
                    MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_24H)
                        .setTitleText("Select Event time")
                        .build()
                picker.addOnPositiveButtonClickListener {
                    timestamp = Date(year, month - 1, day + 1, picker.hour, picker.minute)
                    binding.timeCreateEditText.setText(Timestamp(timestamp).toDate().toString())
                }
                picker.show(parentFragmentManager, "tag")
            }
            datePicker.show(parentFragmentManager, "tag")
        }
        // The following click listeners are meant to disable the reoccurrence spinner
        // if the event type is "Event", as this will not reoccur.
        binding.nameCreateEditText.setOnClickListener {
            if (binding.eventTypeCreateSpinner.selectedItem.toString() == "Event") {
                binding.recurrenceCreateSpinner.setEnabled(false)
                binding.recurrenceCreateSpinner.setSelection(0)
            } else {
                binding.recurrenceCreateSpinner.setEnabled(true)
            }
        }

        binding.petCreateEditText.setOnClickListener {
            if (binding.eventTypeCreateSpinner.selectedItem.toString() == "Event") {
                binding.recurrenceCreateSpinner.setEnabled(false)
                binding.recurrenceCreateSpinner.setSelection(0)
            } else {
                binding.recurrenceCreateSpinner.setEnabled(true)
            }
        }

        // Logic for saving event // If they select yes for an alert, do that here;
        binding.saveEventCreateButton.setOnClickListener {
            if (binding.eventTypeCreateSpinner.selectedItem.toString() == "Event") {
                binding.recurrenceCreateSpinner.setEnabled(false)
                binding.recurrenceCreateSpinner.setSelection(0)
            }
            // TODO: CREATING ALERTS & REOCCURRENCES (EVENT AND/OR REMINDER)
            if (binding.alertCreateSpinner.selectedItem.toString() != "None") {
                if (binding.alertCreateSpinner.selectedItem.toString() == "Time of event") {
                    alarmModel.setAlarmTime(
                        timestamp,
                        0
                    )
                } else if (binding.alertCreateSpinner.selectedItem.toString() == "5 mins before") {
                    alarmModel.setAlarmTime(
                        timestamp,
                        5
                    )
                } else if (binding.alertCreateSpinner.selectedItem.toString() == "15 mins before") {
                    alarmModel.setAlarmTime(
                        timestamp,
                        15
                    )
                } else if (binding.alertCreateSpinner.selectedItem.toString() == "30 mins before") {
                    alarmModel.setAlarmTime(
                        timestamp,
                        30
                    )
                } else if (binding.alertCreateSpinner.selectedItem.toString() == "1 hour before") {
                    alarmModel.setAlarmTime(
                        timestamp,
                        60
                    )
                } else if (binding.alertCreateSpinner.selectedItem.toString() == "2 hours before") {
                    alarmModel.setAlarmTime(
                        timestamp,
                        120
                    )
                }

                if (binding.recurrenceCreateSpinner.selectedItem.toString() == "None") {
                    alarmModel.setAlarmScheduled(binding.nameCreateEditText.text.toString(), code)
                } else {
                    if (binding.recurrenceCreateSpinner.selectedItem.toString() == "Daily") {
                        alarmModel.setAlarmRecurring(
                            86400000,
                            binding.nameCreateEditText.text.toString(),
                            code
                        )
                    } else if (binding.recurrenceCreateSpinner.selectedItem.toString() == "Weekly") {
                        alarmModel.setAlarmRecurring(
                            604800000,
                            binding.nameCreateEditText.text.toString(),
                            code
                        )
                    } else if (binding.recurrenceCreateSpinner.selectedItem.toString() == "Monthly") {
                        alarmModel.setAlarmRecurring(
                            2629746000,
                            binding.nameCreateEditText.text.toString(),
                            code
                        )
                    } else if (binding.recurrenceCreateSpinner.selectedItem.toString() == "Annually") {
                        alarmModel.setAlarmRecurring(
                            31556952000,
                            binding.nameCreateEditText.text.toString(),
                            code
                        )
                    }
                }
            }

            model.addEvent(
                binding.eventTypeCreateSpinner.selectedItem.toString(),
                binding.nameCreateEditText.text.toString(),
                Timestamp(timestamp),
                binding.alertCreateSpinner.selectedItem.toString(),
                binding.recurrenceCreateSpinner.selectedItem.toString(),
                binding.petCreateEditText.text.toString(),
                code
            )
            this.findNavController().navigate(R.id.navigation_events)
        }
    }
}