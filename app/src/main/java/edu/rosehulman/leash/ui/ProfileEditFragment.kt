package edu.rosehulman.leash.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.Timestamp
import edu.rosehulman.leash.R
import edu.rosehulman.leash.databinding.FragmentProfileEditBinding
import edu.rosehulman.leash.models.UserViewModel

class ProfileEditFragment : Fragment() {

    private lateinit var binding: FragmentProfileEditBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        val userModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        binding = FragmentProfileEditBinding.inflate(inflater, container, false)

        binding.saveProfileButton.setOnClickListener {
            // Save user info into Firestore.
            userModel.update(
                newName=binding.profileNameEditText.text.toString(),
            )
            findNavController().navigate(R.id.navigation_profile)
        }

        userModel.getOrMakeUser {
            with(userModel.user!!) {
                binding.profileNameEditText.setText(name)
                binding.accountCreationDateText.setText("Created: ${timestampToString(created!!)}")
            }
        }

        return binding.root
    }

    fun timestampToString(time: Timestamp) : String{
        return "%02d/%02d/%d".format(time.toDate().month+1, time.toDate().date, time.toDate().year+1900)
    }
}