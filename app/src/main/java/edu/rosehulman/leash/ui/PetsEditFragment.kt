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
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*

class PetsEditFragment : Fragment() {

    private lateinit var model: PetsViewModel
    private lateinit var binding: FragmentPetsEditBinding
    var date: String = ""
    var timestamp: Date? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        model = ViewModelProvider(requireActivity()).get(PetsViewModel::class.java)
        binding = FragmentPetsEditBinding.inflate(inflater, container, false)

        timestamp = model.getCurrentPet().birthdate.toDate()

        updateView()
        setupButtons()

        return binding.root
    }

    fun updateView() {
        binding.nameEditEditText.setText(model.getCurrentPet().name)
        binding.birthdateEditEditText.setText(model.getCurrentPet().birthdate.toDate().toString())
        binding.petTypeEditEditText.setText(model.getCurrentPet().type)
    }

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
                val formatter = SimpleDateFormat("dd/MM/yyyy")
                date = formatter.format(datePicker.selection?.let { it1 -> Date(it1) })
                var year = Integer.parseInt("${date.subSequence(6, 10)}")
                var month = Integer.parseInt("${date.subSequence(3, 5)}")
                var day = Integer.parseInt("${date.subSequence(0, 2)}")
                timestamp = Date(year, month-1, day+1)
                binding.birthdateEditEditText.setText(Timestamp(timestamp!!).toDate().toString())
            }
            datePicker.show(parentFragmentManager, "tag");
        }

        binding.savePetEditButton.setOnClickListener{
            timestamp?.let { it1 -> Timestamp(it1) }?.let { it2 ->
                model.updateCurrentPet(binding.nameEditEditText.text.toString(),
                    it2,
                    binding.petTypeEditEditText.text.toString())
            }
            this.findNavController().navigate(R.id.navigation_pets)
        }

        // TODO: Add modal/dialog to confirm deletion
        binding.deletePetEditButton.setOnClickListener {
            model.removeCurrentPet()
            this.findNavController().navigate(R.id.navigation_pets)
        }
    }
}