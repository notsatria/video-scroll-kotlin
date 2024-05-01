package com.notsatria.videoscroll

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.notsatria.videoscroll.databinding.ActivityProfileBinding
import com.notsatria.videoscroll.utils.FirestoreUtil
import com.notsatria.videoscroll.utils.StorageUtil
import java.io.ByteArrayOutputStream

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var selectedImageBytes: ByteArray
    private var pictureJustChanged: Boolean = false

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val selectedImageBitmap = MediaStore.Images.Media
                .getBitmap(contentResolver, uri)
            val outputStream = ByteArrayOutputStream()
            selectedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            selectedImageBytes = outputStream.toByteArray()

            Glide.with(this)
                .load(selectedImageBytes)
                .into(binding.ivProfilePicture)

            pictureJustChanged = true
        } else {
            showToast(getString(R.string.image_not_found))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            ivProfilePicture.setOnClickListener {
                startGallery()
            }

            btnSaveChanges.setOnClickListener {
                if (::selectedImageBytes.isInitialized) {
                    StorageUtil.uploadProfilePhoto(selectedImageBytes) { imagePath ->
                        FirestoreUtil.updateCurrentUser(
                            name = nameTextField.editText?.text.toString() ?: "",
                            photoUrl = imagePath
                        )
                    }
                    navigateToPostActivity()
                } else {
                    FirestoreUtil.updateCurrentUser(
                        name = nameTextField.editText?.text.toString() ?: "",
                        photoUrl = null
                    )
                    navigateToPostActivity()

                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        FirestoreUtil.getCurrentUser { user ->
            binding.nameEditText.setText(user.name)
            if (!pictureJustChanged && user.photoUrl != null)
                Glide.with(this)
                    .load(StorageUtil.pathToReference(user.photoUrl))
                    .placeholder(R.drawable.ic_account)
                    .into(binding.ivProfilePicture)
        }
    }

    private fun startGallery() {
        launcherGallery.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToPostActivity() {
        finish()
    }
}