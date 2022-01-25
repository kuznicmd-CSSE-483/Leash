package edu.rosehulman.leash.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import edu.rosehulman.leash.databinding.FragmentPetsBinding
import edu.rosehulman.leash.databinding.FragmentPetsDetailBinding
import edu.rosehulman.leash.models.PetsViewModel

class PetsDetailFragment : Fragment() {

    private lateinit var binding: FragmentPetsDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPetsDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

}