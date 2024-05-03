package com.notsatria.videoscroll

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ListenerRegistration
import com.notsatria.videoscroll.databinding.ActivityActiveUsersBinding
import com.notsatria.videoscroll.databinding.ActivityChatBinding
import com.notsatria.videoscroll.recyclerview.PersonItem
import com.notsatria.videoscroll.utils.FirestoreUtil
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder

class ActiveUsersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityActiveUsersBinding
    private lateinit var userListenerRegistration: ListenerRegistration
    private var shouldInitRecyclerView = true
    private lateinit var peopleSection: Section

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActiveUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userListenerRegistration = FirestoreUtil.addUserListener(this, this::updateRecyclerView)

    }

    override fun onDestroy() {
        super.onDestroy()
        FirestoreUtil.removeListener(userListenerRegistration)
        shouldInitRecyclerView = true
    }

    private fun updateRecyclerView(items: List<PersonItem>) {
        fun init() {
            binding.rvPeople.apply {
                layoutManager = LinearLayoutManager(this@ActiveUsersActivity)
                adapter = GroupAdapter<GroupieViewHolder>().apply {
                    peopleSection = Section(items)
                    add(peopleSection)
                    setOnItemClickListener(onItemClick())
                }
            }
            shouldInitRecyclerView = false
        }

        fun updateItems() = peopleSection.update(items)

        if (shouldInitRecyclerView) {
            init()
        } else {
            updateItems()
        }
    }

    private fun onItemClick() = OnItemClickListener { item, view ->
        if (item is PersonItem) {
            val intent = Intent(this, ChatActivity::class.java).apply {
                putExtra(AppConstants.USER_NAME, item.user.name)
                putExtra(AppConstants.USER_ID, item.user.uid)
            }
            startActivity(intent)
        }
    }

}