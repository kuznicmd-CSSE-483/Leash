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
import edu.rosehulman.leash.R
import edu.rosehulman.leash.databinding.FragmentPetsBinding
import edu.rosehulman.leash.models.PetsViewModel

class PetsFragment : Fragment() {

    private lateinit var binding: FragmentPetsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPetsBinding.inflate(inflater, container, false)

       setupButtons()

        return binding.root
    }


    fun setupButtons() {
        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.navigation_pets_edit)
        }
    }

}