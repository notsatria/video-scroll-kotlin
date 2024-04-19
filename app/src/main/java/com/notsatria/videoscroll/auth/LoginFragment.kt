package com.notsatria.videoscroll.auth

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.notsatria.videoscroll.R
import com.notsatria.videoscroll.databinding.FragmentLoginBinding
import com.notsatria.videoscroll.model.User

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnToRegister.setOnClickListener {
            (activity as AuthActivity).navigateToRegisterFragment()
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.emailTextField.editText!!.text.toString()
            val password = binding.passwordTextField.editText!!.text.toString()
            signInWithEmailAndPassword(email, password)
        }
    }

    private fun signInWithEmailAndPassword(email: String, password: String) {
        showProgressBar(true)
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showProgressBar(false)
                    Log.d(TAG, "signInWithEmail:success")
                    val currentUser = auth.currentUser
                    val user = currentUser?.let {
                        User(
                            it.uid,
                            it.email!!,
                            it.displayName
                        )
                    }
                    (activity as AuthActivity).navigateToPostActivityWithData(user!!)
                } else {
                    showProgressBar(false)
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        requireContext(), "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun showProgressBar(show: Boolean) {
        if (show) binding.progressBar.visibility = View.VISIBLE
        else binding.progressBar.visibility = View.GONE
    }

    companion object {
        private const val TAG = "LoginFragment"
    }

}