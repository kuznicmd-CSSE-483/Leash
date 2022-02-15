package edu.rosehulman.leash.ui

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import coil.transform.RoundedCornersTransformation
import com.google.firebase.Timestamp
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import edu.rosehulman.leash.BuildConfig
import edu.rosehulman.leash.Constants
import edu.rosehulman.leash.R
import edu.rosehulman.leash.databinding.FragmentProfileEditBinding
import edu.rosehulman.leash.models.UserViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs
import kotlin.random.Random

class ProfileEditFragment : Fragment() {

    private val takeImageResult =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                latestTmpUri?.let { uri ->
                    binding.profileImage.setImageURI(uri)
                    addPhotoFromUri(uri)
                }
            }
        }

    private val selectImageFromGalleryResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                binding.profileImage.setImageURI(uri)
                addPhotoFromUri(uri)
            }
        }

    private var latestTmpUri: Uri? = null

    private val storageImagesRef = Firebase.storage
        .reference
        .child("images")
    private var storageUriStringInFragment: String = ""

    private lateinit var binding: FragmentProfileEditBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        val userModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        binding = FragmentProfileEditBinding.inflate(inflater, container, false)

        if (userModel.user!!.storageUriString.isNotEmpty()) {
            binding.profileImage.load(userModel.user!!.storageUriString) {
                crossfade(true)
                transformations(RoundedCornersTransformation(0F))
            }
        }

        binding.saveProfileButton.setOnClickListener {
            // Save user info into Firestore.
            userModel.update(
                newName=binding.profileNameEditText.text.toString(),
                newStorageUriString = storageUriStringInFragment
            )
            findNavController().navigate(R.id.navigation_profile)
        }

        binding.profileEditUploadPhotoButton.setOnClickListener {
            showPictureDialog()
        }

        userModel.getOrMakeUser {
            with(userModel.user!!) {
                binding.profileNameEditText.setText(name)
                binding.accountCreationDateText.setText("Created: ${timestampToString(created!!)}")
                storageUriStringInFragment = storageUriString
            }
        }

        return binding.root
    }

    fun timestampToString(time: Timestamp) : String{
        return "%02d/%02d/%d".format(time.toDate().month+1, time.toDate().date, time.toDate().year+1900)
    }

    private fun showPictureDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Choose a photo source")
        builder.setMessage("Would you like to take a new picture?\nOr choose an existing one?")
        builder.setPositiveButton("Take Picture") { _, _ ->
            binding.saveProfileButton.isEnabled = false
            binding.saveProfileButton.text = "Loading image"
            takeImage()
        }

        builder.setNegativeButton("Choose Picture") { _, _ ->
            binding.saveProfileButton.isEnabled = false
            binding.saveProfileButton.text = "Loading image"
            selectImageFromGallery()
        }
        builder.create().show()
    }

    private fun takeImage() {
        lifecycleScope.launchWhenStarted {
            getTmpFileUri().let { uri ->
                latestTmpUri = uri
                takeImageResult.launch(uri)
            }
        }
    }

    private fun getTmpFileUri(): Uri {
        val storageDir: File =
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val tmpFile = File.createTempFile("JPEG_${timeStamp}_", ".png", storageDir).apply {
            createNewFile()
            deleteOnExit()
        }
        return FileProvider.getUriForFile(
            requireContext(),
            "${BuildConfig.APPLICATION_ID}.provider",
            tmpFile
        )
    }

    private fun selectImageFromGallery() = selectImageFromGalleryResult.launch("image/*")

    private fun addPhotoFromUri(uri: Uri?) {
        if (uri == null) {
            Log.e(Constants.TAG, "Uri is null. Not saving to storage")
            return
        }
// https://stackoverflow.com/a/5657557
        val stream = requireActivity().contentResolver.openInputStream(uri)
        if (stream == null) {
            Log.e(Constants.TAG, "Stream is null. Not saving to storage")
            return
        }

        // TODO: Add to storage
        val imageId = abs(Random.nextLong()).toString()

        storageImagesRef.child(imageId).putStream(stream)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                storageImagesRef.child(imageId).downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    storageUriStringInFragment = task.result.toString()
                    Log.d(Constants.TAG, "Got download uri: $storageUriStringInFragment")
                    binding.saveProfileButton.text = "done"
                    binding.saveProfileButton.isEnabled = true
                } else {
                    // Handle failures
                    // ...
                }
            }


    }
}