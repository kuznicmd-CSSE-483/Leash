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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import edu.rosehulman.leash.R
import edu.rosehulman.leash.adapters.PetsAdapter
import edu.rosehulman.leash.databinding.FragmentPetsBinding
import edu.rosehulman.leash.models.PetsViewModel

class PetsFragment : Fragment() {

    // The file that will displaying the list; normal binding conventions here
    private lateinit var binding: FragmentPetsBinding
    private lateinit var adapter: PetsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPetsBinding.inflate(inflater, container, false)
        // Make Recycler; Connect with Adapter
        adapter = PetsAdapter(this)
        // Set Recyclerview and Adapter properties below
        binding.recyclerView.adapter = adapter
        adapter.addListener(fragmentName)
        // Make the items show up in a grid, with 3 items in each row
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )

        binding.fab.setOnClickListener {
            adapter.addPet()
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
