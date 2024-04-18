package com.notsatria.videoscroll

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase
import com.notsatria.videoscroll.databinding.ActivityPostBinding

class PostActivity : AppCompatActivity() {

    private lateinit var viewPager2: ViewPager2
    private lateinit var postAdapter: PostAdapter
    private lateinit var binding: ActivityPostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setupBinding()
        setupView()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupBinding() {
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setupView() {
        viewPager2 = binding.viewPager2

        val query = FirebaseDatabase.getInstance().getReference().child("posts")

        val options = FirebaseRecyclerOptions.Builder<Post>()
            .setQuery(query, Post::class.java)
            .build()

        postAdapter = PostAdapter(options)
        viewPager2.adapter = postAdapter
    }

    override fun onStart() {
        super.onStart()
        postAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        postAdapter.stopListening()
    }
}