package com.notsatria.videoscroll.utils

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.notsatria.videoscroll.model.ChatChannel
import com.notsatria.videoscroll.model.ImageMessage
import com.notsatria.videoscroll.model.Message
import com.notsatria.videoscroll.model.MessageType
import com.notsatria.videoscroll.model.TextMessage
import com.notsatria.videoscroll.model.User
import com.notsatria.videoscroll.recyclerview.ImageMessageItem
import com.notsatria.videoscroll.recyclerview.PersonItem
import com.notsatria.videoscroll.recyclerview.TextMessageItem
import com.xwray.groupie.kotlinandroidextensions.Item

object FirestoreUtil {
    private val instance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val currentUserDocRef: DocumentReference
        get() = instance.document(
            "users/${
                FirebaseAuth.getInstance().uid
                    ?: NullPointerException("UID is null")
            }"
        )

    private val chatChannelsCollectionRef = instance.collection("chatChannels")

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

    fun updateCurrentUser(name: String = "", photoUrl: String? = null) {
        val userFieldMap = mutableMapOf<String, Any>()
        if (name.isNotBlank()) userFieldMap["name"] = name
        if (photoUrl != null) userFieldMap["photoUrl"] = photoUrl
        currentUserDocRef.update(userFieldMap)
    }

    fun getCurrentUser(onComplete: (User) -> Unit) {
        currentUserDocRef.get()
            .addOnSuccessListener {
                onComplete(it.toObject(User::class.java)!!)
            }
    }

    fun addUserListener(context: Context, onListen: (List<PersonItem>) -> Unit): ListenerRegistration {
        return instance.collection("users")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.e("Firestore", "Users listener error.", firebaseFirestoreException)
                    return@addSnapshotListener
                }

                val items = mutableListOf<PersonItem>()
                querySnapshot?.documents?.forEach {
                    if (it.id != FirebaseAuth.getInstance().currentUser?.uid) {
                        items.add(PersonItem(it.toObject(User::class.java)!!, context))
                    }
                }

                onListen(items)

            }
    }

    fun removeListener(registration: ListenerRegistration) = registration.remove()

    fun getOrCreateChannel(otherUserId: String, onComplete: (channelId: String) -> Unit) {
        currentUserDocRef.collection("engagedChatChannels")
            .document(otherUserId).get().addOnSuccessListener {
                if (it.exists()) {
                    onComplete(it["channelId"] as String)
                    return@addOnSuccessListener
                }

                val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

                val newChannel = chatChannelsCollectionRef.document()
                newChannel.set(ChatChannel(mutableListOf(currentUserId, otherUserId)))

                currentUserDocRef.collection("engagedChatChannels")
                    .document(otherUserId)
                    .set(mapOf("channelId" to newChannel.id))

                instance.collection("users").document(otherUserId)
                    .collection("engagedChatChannels")
                    .document(currentUserId)
                    .set(mapOf("channelId" to newChannel.id))

                onComplete(newChannel.id)
            }
    }

    fun addChatMessageListener(channelId: String, context: Context, onListen: (List<Item>) -> Unit): ListenerRegistration {
        return chatChannelsCollectionRef.document(channelId)
            .collection("messages")
            .orderBy("time")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.e("Firestore", "ChatMessages listener error.", firebaseFirestoreException)
                    return@addSnapshotListener
                }

                val items = mutableListOf<Item>()
                querySnapshot?.documents?.forEach {
                   if (it["type"] == MessageType.TEXT)
                       items.add(TextMessageItem(it.toObject(TextMessage::class.java)!!, context))
                    else
                       items.add(ImageMessageItem(it.toObject(ImageMessage::class.java)!!, context))
                }

                onListen(items)
            }
    }

    fun sendMessage(message: Message, channelId: String) {
        chatChannelsCollectionRef.document(channelId)
            .collection("messages")
            .add(message)
    }
}