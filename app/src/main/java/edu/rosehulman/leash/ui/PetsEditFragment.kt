package edu.rosehulman.leash.ui

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import coil.transform.RoundedCornersTransformation
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.Timestamp
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import edu.rosehulman.leash.BuildConfig
import edu.rosehulman.leash.Constants
import edu.rosehulman.leash.R
import edu.rosehulman.leash.databinding.FragmentPetsCreateBinding
import edu.rosehulman.leash.databinding.FragmentPetsEditBinding
import edu.rosehulman.leash.models.PetsViewModel
import java.io.File
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs
import kotlin.random.Random

class PetsEditFragment : Fragment() {
    private val takeImageResult =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                latestTmpUri?.let { uri ->
                    binding.petEditImage.setImageURI(uri)
                    addPhotoFromUri(uri)
                }
            }
        }

    private val selectImageFromGalleryResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                binding.petEditImage.setImageURI(uri)
                addPhotoFromUri(uri)
            }
        }

    private var latestTmpUri: Uri? = null

    private val storageImagesRef = Firebase.storage
        .reference
        .child("images")
    private var storageUriStringInFragment: String = ""

    private lateinit var model: PetsViewModel
    private lateinit var binding: FragmentPetsEditBinding
    var date: String = ""
    var timestamp: Date? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        model = ViewModelProvider(requireActivity()).get(PetsViewModel::class.java)
        binding = FragmentPetsEditBinding.inflate(inflater, container, false)

        timestamp = model.getCurrentPet().birthdate.toDate()

        updateView()
        setupButtons()

        return binding.root
    }

    fun updateView() {
        binding.nameEditEditText.setText(model.getCurrentPet().name)
        binding.birthdateEditEditText.setText(model.getCurrentPet().birthdate.toDate().toString())
        binding.petTypeEditEditText.setText(model.getCurrentPet().type)
        if (model.getCurrentPet().storageUriString.isNotEmpty()) {
            binding.petEditImage.load(model.getCurrentPet().storageUriString) {
                crossfade(true)
                transformations(RoundedCornersTransformation(0F))
            }
        }
    }

    fun setupButtons() {
        // Logic for choosing a birthdate
        binding.birthdateEditEditText.setOnClickListener {
            val constraintsBuilder =
                CalendarConstraints.Builder()
                    .setValidator(DateValidatorPointBackward.now())

            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select date")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .setInputMode(MaterialDatePicker.INPUT_MODE_TEXT)
                    .setCalendarConstraints(constraintsBuilder.build())
                    .build()
            datePicker.addOnPositiveButtonClickListener {
                val formatter = SimpleDateFormat("dd/MM/yyyy")
                date = formatter.format(datePicker.selection?.let { it1 -> Date(it1) })
                var year = Integer.parseInt("${date.subSequence(6, 10)}")
                var month = Integer.parseInt("${date.subSequence(3, 5)}")
                var day = Integer.parseInt("${date.subSequence(0, 2)}")
                timestamp = Date(year-1900, month-1, day+1)
                binding.birthdateEditEditText.setText(Timestamp(timestamp!!).toDate().toString())
            }
            datePicker.show(parentFragmentManager, "tag");
        }

        binding.savePetEditButton.setOnClickListener{
            timestamp?.let { it1 -> Timestamp(it1) }?.let { it2 ->
                model.updateCurrentPet(binding.nameEditEditText.text.toString(),
                    it2,
                    binding.petTypeEditEditText.text.toString(),
                    storageUriStringInFragment)
            }
            this.findNavController().navigate(R.id.navigation_pets)
        }

        // TODO: Add modal/dialog to confirm deletion
        binding.deletePetEditButton.setOnClickListener {
            model.removeCurrentPet()
            this.findNavController().navigate(R.id.navigation_pets)
        }

        binding.petEditUploadPhotoButton.setOnClickListener {
            showPictureDialog()
        }
    }

    private fun showPictureDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Choose a photo source")
        builder.setMessage("Would you like to take a new picture?\nOr choose an existing one?")
        builder.setPositiveButton("Take Picture") { _, _ ->
            binding.savePetEditButton.isEnabled = false
            binding.savePetEditButton.text = "Loading image"
            takeImage()
        }

        builder.setNegativeButton("Choose Picture") { _, _ ->
            binding.savePetEditButton.isEnabled = false
            binding.savePetEditButton.text = "Loading image"
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
                    binding.savePetEditButton.text = "done"
                    binding.savePetEditButton.isEnabled = true
                } else {
                    // Handle failures
                    // ...
                }
            }


    }
}