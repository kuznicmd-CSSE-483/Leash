package edu.rosehulman.leash.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.Timestamp
import edu.rosehulman.leash.R
import edu.rosehulman.leash.databinding.FragmentPetsCreateBinding
import edu.rosehulman.leash.databinding.FragmentPetsEditBinding
import edu.rosehulman.leash.models.PetsViewModel

class PetsEditFragment : Fragment() {


    private lateinit var model: PetsViewModel

    private lateinit var binding: FragmentPetsEditBinding

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
        binding.birthdateEditEditText.setText(model.getCurrentPet().birthdate.toString())
        binding.petTypeEditEditText.setText(model.getCurrentPet().type)
    }

    // TODO: Fix time parameter, for now uses timestamp.now()...
    fun setupButtons() {
        binding.savePetEditButton.setOnClickListener{
            model.updateCurrentPet(binding.nameEditEditText.text.toString(), Timestamp.now(),
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