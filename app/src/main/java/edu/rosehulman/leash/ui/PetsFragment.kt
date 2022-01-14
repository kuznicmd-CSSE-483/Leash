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
import edu.rosehulman.leash.models.PetsViewModel

class PetsFragment : Fragment() {

    private lateinit var petsViewModel: PetsViewModel
    private var _binding: FragmentPetsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        petsViewModel =
            ViewModelProvider(this).get(PetsViewModel::class.java)

        _binding = FragmentPetsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textPets
        petsViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}