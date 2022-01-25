package edu.rosehulman.leash.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import edu.rosehulman.leash.R
import edu.rosehulman.leash.databinding.FragmentProfileEditBinding

class ProfileEditFragment : Fragment() {

    private lateinit var binding: FragmentProfileEditBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileEditBinding.inflate(inflater, container, false)

        setupButtons()

        return binding.root
    }

    fun setupButtons() {
        // TODO: For demonstrating navigation
        binding.saveProfileButton.setOnClickListener {
            findNavController().navigate(R.id.navigation_profile)
        }
    }
}