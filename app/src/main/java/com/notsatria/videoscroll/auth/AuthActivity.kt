package com.notsatria.videoscroll.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.notsatria.videoscroll.PostActivity
import com.notsatria.videoscroll.R
import com.notsatria.videoscroll.databinding.ActivityAuthBinding
import com.notsatria.videoscroll.model.User

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private lateinit var auth: FirebaseAuth
    private val REQ_ONE_TAP = 2
    private var showOneTapUI = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setupBinding()
        setupFragment()
        initFirebaseAuth()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onStart() {
        super.onStart()
        checkUserSignedIn()
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

    private fun initFirebaseAuth() {
        auth = Firebase.auth
    }

    private fun checkUserSignedIn() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val user = currentUser.let {
                User(
                    it.uid,
                    it.email!!,
                    it.displayName
                )
            }
            navigateToPostActivityWithData(user)
        }
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

    fun navigateToPostActivity() {
        startActivity(Intent(this, PostActivity::class.java))
        finish()
    }

    fun navigateToPostActivityWithData(user: User) {
        val intent = Intent(this, PostActivity::class.java)
        intent.putExtra(EXTRA_USER, user)
        startActivity(intent)
        finish()
    }

    companion object {
        private const val TAG = "AuthActivity"
        const val EXTRA_USER = "extra_user"
    }
}