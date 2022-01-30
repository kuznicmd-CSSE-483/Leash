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
import com.google.firebase.Timestamp
import edu.rosehulman.leash.R
import edu.rosehulman.leash.databinding.FragmentPetsCreateBinding
import edu.rosehulman.leash.models.PetsViewModel
import java.util.*

class PetsCreateFragment : Fragment() {

    private lateinit var model: PetsViewModel

    private lateinit var binding: FragmentPetsCreateBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        model = ViewModelProvider(requireActivity()).get(PetsViewModel::class.java)
        binding = FragmentPetsCreateBinding.inflate(inflater, container, false)

        setupButtons()

        return binding.root
    }

    fun setupButtons() {
        // Logic for choosing a birthdate
        binding.birthdateEditText.setOnClickListener {
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
                // Respond to positive button click.
                // TODO: Figure out how to get correct value of date selected
                binding.birthdateEditText.setText(datePicker.toString())
            }
            datePicker.show(parentFragmentManager, "tag");
        }

        // Logic for saving pet
        // TODO: Timestamp is set now; should come from selected date from above
        binding.savePetButton.setOnClickListener {
              model.addPet(binding.nameEditText.text.toString(), Timestamp.now(),
                  binding.petTypeEditText.text.toString().lowercase(Locale.getDefault())
              )

            // Navigate back to the Pets List Fragment
            this.findNavController().navigate(R.id.navigation_pets, null,
                // Simple animation when sliding between pages
                navOptions {
                    anim {
                        enter = android.R.anim.slide_in_left
                        exit = android.R.anim.slide_out_right
                    }
                })
        }
    }
}