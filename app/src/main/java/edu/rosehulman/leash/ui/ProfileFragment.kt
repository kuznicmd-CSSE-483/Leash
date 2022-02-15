package edu.rosehulman.leash.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import coil.load
import coil.transform.RoundedCornersTransformation
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import edu.rosehulman.leash.R
import edu.rosehulman.leash.databinding.FragmentProfileBinding
import edu.rosehulman.leash.models.User
import edu.rosehulman.leash.models.UserViewModel

class ProfileFragment : Fragment() {

    private lateinit var model: UserViewModel
    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        model = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        setupButtons()
        updateView()
        return binding.root
    }

    fun setupButtons() {
        binding.logoutButton.setOnClickListener {
            model.user = null
            Firebase.auth.signOut()
        }
        binding.editImageView.setOnClickListener {
            findNavController().navigate(R.id.navigation_profile_edit)
        }
    }

    fun updateView() {
        binding.profileName.text = model.user?.name
        binding.accountCreationDateText.text = "Created: ${timestampToString(model.user?.created!!)}"
        if (model.user!!.storageUriString.isNotEmpty()) {
            binding.profileImage.load(model.user!!.storageUriString) {
                crossfade(true)
                transformations(RoundedCornersTransformation(0F))
            }
        }
    }

    fun timestampToString(time: Timestamp) : String{
        return "%02d/%02d/%d".format(time.toDate().month+1, time.toDate().date, time.toDate().year+1900)
    }
}