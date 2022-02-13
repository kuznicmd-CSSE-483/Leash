package edu.rosehulman.leash.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.Timestamp
import edu.rosehulman.leash.R
import edu.rosehulman.leash.databinding.FragmentPetsCreateBinding
import edu.rosehulman.leash.databinding.FragmentPetsEditBinding
import edu.rosehulman.leash.models.PetsViewModel
import java.util.*

class PetsEditFragment : Fragment() {


    private lateinit var model: PetsViewModel

    private lateinit var binding: FragmentPetsEditBinding

    private lateinit var timestamp: Date

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        model = ViewModelProvider(requireActivity()).get(PetsViewModel::class.java)
        binding = FragmentPetsEditBinding.inflate(inflater, container, false)

        updateView()
        setupButtons()

        return binding.root
    }

    fun updateView() {
        binding.nameEditEditText.setText(model.getCurrentPet().name)
        binding.birthdateEditEditText.setText(model.getCurrentPet().birthdate.toDate().toString())
        binding.petTypeEditEditText.setText(model.getCurrentPet().type)
    }

    // TODO: Fix time parameter, for now uses timestamp.now()...
    fun setupButtons() {
        // Logic for choosing a birthdate
        binding.birthdateEditEditText.setOnClickListener {
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

                timestamp = datePicker.selection?.let { it1 -> Date(it1) }!!
                binding.birthdateEditEditText.setText(datePicker.headerText)
            }
            datePicker.show(parentFragmentManager, "tag");
        }

        binding.savePetEditButton.setOnClickListener{
            model.updateCurrentPet(binding.nameEditEditText.text.toString(), Timestamp(timestamp),
                    binding.petTypeEditEditText.text.toString())
            this.findNavController().navigate(R.id.navigation_pets)
        }

        // TODO: Add modal/dialog to confirm deletion
        binding.deletePetEditButton.setOnClickListener {
            model.removeCurrentPet()
            this.findNavController().navigate(R.id.navigation_pets)
        }
    }
}