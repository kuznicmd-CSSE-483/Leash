package edu.rosehulman.leash.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import edu.rosehulman.leash.R
import edu.rosehulman.leash.adapters.EventAdapter
import edu.rosehulman.leash.adapters.PetsAdapter
import edu.rosehulman.leash.databinding.FragmentEventsBinding
import edu.rosehulman.leash.databinding.FragmentPetsBinding

class EventsFragment : Fragment() {

    private lateinit var binding: FragmentEventsBinding
    private lateinit var adapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEventsBinding.inflate(inflater, container, false)
        // Make Recycler; Connect with Adapter
        adapter = EventAdapter(this)
        // Set Recyclerview and Adapter properties below
        binding.recyclerView.adapter = adapter
        adapter.addListener(EventsFragment.fragmentName)
        // Make the items show up in a grid, with 3 items in each row
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )

        // Set up FAB, which allows the user to create a new pet
        binding.fab.setOnClickListener {
            this.findNavController().navigate(R.id.navigation_events_create, null,
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

    override fun onDestroyView() {
        super.onDestroyView()
        adapter.removeListener(fragmentName)
    }

    companion object {
        const val fragmentName = "PetsFragment"
    }
}