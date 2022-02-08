package edu.rosehulman.leash.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import edu.rosehulman.leash.R
import edu.rosehulman.leash.adapters.PetsAdapter
import edu.rosehulman.leash.databinding.FragmentPetsBinding
import edu.rosehulman.leash.models.UserViewModel

class PetsFragment : Fragment() {

    // The file that will displaying the list; normal binding conventions here
    private lateinit var binding: FragmentPetsBinding
    private lateinit var adapter: PetsAdapter
    private lateinit var model: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPetsBinding.inflate(inflater, container, false)
        model = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        // Make Recycler; Connect with Adapter
        adapter = PetsAdapter(this)
        // Set Recyclerview and Adapter properties below
        binding.recyclerView.adapter = adapter
        adapter.addListener(fragmentName)
        // Make the items show up in a grid, with 3 items in each row
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.setHasFixedSize(true)
//        binding.recyclerView.addItemDecoration(
//            DividerItemDecoration(
//                requireContext(),
//                DividerItemDecoration.VERTICAL
//            )
//        )

        // Header of Pets List Fragment
        val names = model.user?.name?.split("\\s".toRegex())
        binding.petsTextView.text = "${names?.get(0)}'s Pets"

        // Set up FAB, which allows the user to create a new pet
        binding.fab.setOnClickListener {
            this.findNavController().navigate(R.id.navigation_pets_create, null,
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
