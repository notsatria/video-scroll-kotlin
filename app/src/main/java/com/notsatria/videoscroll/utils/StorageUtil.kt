package com.notsatria.videoscroll.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.UUID

object StorageUtil {
    private val instance: FirebaseStorage by lazy { FirebaseStorage.getInstance() }

    private val currentUserRef: StorageReference
        get() = instance.reference
            .child(
                FirebaseAuth.getInstance().uid
                    ?: throw NullPointerException("UID is null")
            )

    fun uploadProfilePhoto(imageBytes: ByteArray, onSuccess: (imagePath: String) -> Unit) {
        val ref = currentUserRef.child(
            "profile_picture/${UUID.nameUUIDFromBytes(imageBytes)}"
        )
        ref.putBytes(imageBytes)
            .addOnSuccessListener {
                onSuccess(ref.path)
            }
    }

    fun pathToReference(path: String) = instance.getReference(path)
}