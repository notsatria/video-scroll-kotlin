package com.notsatria.videoscroll.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.notsatria.videoscroll.PostActivity
import com.notsatria.videoscroll.R
import com.notsatria.videoscroll.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setupBinding()
        setupFragment()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupBinding() {
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setupFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.flContainer, LoginFragment())
            .commit()
    }

    fun navigateToRegisterFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.flContainer, RegisterFragment())
            .addToBackStack(null)
            .commit()
    }

    fun navigateBack() {
        onBackPressed()
    }
}