package com.notsatria.videoscroll.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.notsatria.videoscroll.model.User

object FirestoreUtil {
    private val instance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val currentUserDocRef: DocumentReference
        get() = instance.document(
            "users/${
                FirebaseAuth.getInstance().uid
                    ?: NullPointerException("UID is null")
            }"
        )

    fun initCurrentUserIfFirstTime(onComplete: () -> Unit, user: User) {
        currentUserDocRef.get().addOnSuccessListener { documentSnapshot ->
            if (!documentSnapshot.exists()) {
                currentUserDocRef.set(user).addOnSuccessListener {
                    onComplete()
                }
            }
            else {
                onComplete()
            }
        }
    }

    fun updateCurrentUser(name: String = "", profileUrl: String? = null) {
        val userFieldMap = mutableMapOf<String, Any>()
        if (name.isNotBlank()) userFieldMap["name"] = name
        if (profileUrl != null) userFieldMap["profileUrl"] = profileUrl
        currentUserDocRef.update(userFieldMap)
    }

    fun getCurrentUser(onComplete: (User) -> Unit) {
        currentUserDocRef.get()
            .addOnSuccessListener {
                onComplete(it.toObject(User::class.java)!!)
            }
    }
}