package com.notsatria.videoscroll

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.notsatria.videoscroll.databinding.ActivityChatBinding
import com.notsatria.videoscroll.model.ImageMessage
import com.notsatria.videoscroll.model.MessageType
import com.notsatria.videoscroll.model.TextMessage
import com.notsatria.videoscroll.utils.FirestoreUtil
import com.notsatria.videoscroll.utils.StorageUtil
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import java.io.ByteArrayOutputStream
import java.util.Calendar

private const val REQUEST_CODE_SELECT_IMAGE = 301

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var messagesListenerRegistration: ListenerRegistration
    private lateinit var currentChannelId: String
    private var shouldInitRecyclerView = true
    private lateinit var messagesSection: Section

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = intent.getStringExtra(AppConstants.USER_NAME)
        }

        val otherUserId = intent.getStringExtra(AppConstants.USER_ID)

        FirestoreUtil.getOrCreateChannel(otherUserId ?: "") { channelId ->
            currentChannelId = channelId
            messagesListenerRegistration =
                FirestoreUtil.addChatMessageListener(channelId, this, this::updateRecyclerView)

            binding.ivSend.setOnClickListener {
                val messageToSend = TextMessage(
                    binding.etMessage.text.toString(),
                    Calendar.getInstance().time,
                    FirebaseAuth.getInstance().currentUser?.uid!!,
                    MessageType.TEXT
                )
                binding.etMessage.setText("")
                FirestoreUtil.sendMessage(messageToSend, channelId)
            }

            binding.fabOpenGallery.setOnClickListener {
                val intent = Intent().apply {
                    type = "image/*"
                    action = Intent.ACTION_GET_CONTENT
                    putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
                }
                startActivityForResult(
                    Intent.createChooser(intent, "Select Image"),
                    REQUEST_CODE_SELECT_IMAGE
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK
            && data != null && data.data != null) {
            val selectedImagePath = data.data
            val selectedImageBmp = MediaStore.Images.Media.getBitmap(contentResolver, selectedImagePath)
            val outputStream = ByteArrayOutputStream()
            selectedImageBmp.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            val selectedImageBytes = outputStream.toByteArray()

            StorageUtil.uploadMessageImage(selectedImageBytes) { imagePath ->
                val messageToSend = ImageMessage(
                    imagePath,
                    Calendar.getInstance().time,
                    FirebaseAuth.getInstance().currentUser?.uid!!,
                    MessageType.IMAGE
                )
                FirestoreUtil.sendMessage(messageToSend, currentChannelId)
            }
        }
    }

    private fun updateRecyclerView(messages: List<Item>) {
        fun init() {
            binding.rvChat.apply {
                adapter = GroupAdapter<GroupieViewHolder>().apply {
                    messagesSection = Section(messages)
                    this.add(messagesSection)
                }
                layoutManager = LinearLayoutManager(this@ChatActivity)
            }
            shouldInitRecyclerView = false
        }

        fun updateItem() {
            messagesSection.update(messages)
        }

        if (shouldInitRecyclerView)
            init()
        else
            updateItem()

        binding.rvChat.scrollToPosition(binding.rvChat.adapter!!.itemCount - 1)
    }
}