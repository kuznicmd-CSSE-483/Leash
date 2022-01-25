package edu.rosehulman.leash.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import edu.rosehulman.leash.R
import edu.rosehulman.leash.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        setupButtons()

        return binding.root
    }

    fun setupButtons() {
        // TODO: For demonstrating navigation
        binding.editImageView.setOnClickListener {
            findNavController().navigate(R.id.navigation_profile_edit)
        }
    }
}