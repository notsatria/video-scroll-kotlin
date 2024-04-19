package com.notsatria.videoscroll

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.notsatria.videoscroll.auth.AuthActivity
import com.notsatria.videoscroll.databinding.ActivityPostBinding
import com.notsatria.videoscroll.model.User

class PostActivity : AppCompatActivity() {

    private lateinit var viewPager2: ViewPager2
    private lateinit var postAdapter: PostAdapter
    private lateinit var binding: ActivityPostBinding
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        getUserData()
        setupBinding()
        setupView()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onStart() {
        super.onStart()
        postAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        postAdapter.stopListening()
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

        showSnackBar("Welcome, ${user.email}")
    }

    private fun getUserData() {
        user = intent.getParcelableExtra(AuthActivity.EXTRA_USER)!!
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

}