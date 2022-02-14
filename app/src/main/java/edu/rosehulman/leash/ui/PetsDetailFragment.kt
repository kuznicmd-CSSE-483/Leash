package edu.rosehulman.leash.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import edu.rosehulman.leash.R
import edu.rosehulman.leash.databinding.FragmentPetsBinding
import edu.rosehulman.leash.databinding.FragmentPetsDetailBinding
import edu.rosehulman.leash.models.EventsViewModel
import edu.rosehulman.leash.models.PetsViewModel

class PetsDetailFragment : Fragment() {

    private lateinit var binding: FragmentPetsDetailBinding
    private lateinit var model: PetsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPetsDetailBinding.inflate(inflater, container, false)
        model = ViewModelProvider(requireActivity()).get(PetsViewModel::class.java)

        updateView()
        return binding.root
    }

    fun updateView() {
        binding.petNameTextView.text = model.getCurrentPet().name
        binding.birthdateTextView.text = "Birthdate: ${parseDate(model.getCurrentPet().birthdate)}"
    }

    fun parseDate(time: Timestamp): String {
        return "${time.toDate().month + 1}/${time.toDate().date.toString()}/${time.toDate().year.toString()}"
    }

}