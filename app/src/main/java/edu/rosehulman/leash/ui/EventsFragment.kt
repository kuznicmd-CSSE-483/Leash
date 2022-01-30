package edu.rosehulman.leash.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import edu.rosehulman.leash.R
import edu.rosehulman.leash.databinding.FragmentEventsBinding

class EventsFragment : Fragment() {

    private lateinit var binding: FragmentEventsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEventsBinding.inflate(inflater, container, false)

        binding.fab.setOnClickListener {
            this.findNavController().navigate(R.id.navigation_events_edit, null,
                // Simple animation when sliding between pages
                navOptions {
                    anim {
                        enter = android.R.anim.slide_in_left
                        exit = android.R.anim.slide_out_right
                    }
                })
        }

        return binding.root
    }
}