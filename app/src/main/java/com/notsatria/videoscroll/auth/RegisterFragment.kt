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
import com.google.firebase.auth.auth
import com.notsatria.videoscroll.R
import com.notsatria.videoscroll.databinding.FragmentLoginBinding
import com.notsatria.videoscroll.databinding.FragmentRegisterBinding
import com.notsatria.videoscroll.model.User
import com.notsatria.videoscroll.utils.FirestoreUtil

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
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
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnToLogin.setOnClickListener {
            (activity as AuthActivity).navigateBack()
        }

        binding.btnRegister.setOnClickListener {
            val email = binding.emailTextField.editText!!.text.toString()
            val password = binding.passwordTextField.editText!!.text.toString()
            if (email.isEmpty() || password.isEmpty()) {
                showToast(getString(R.string.email_and_password_must_be_filled))
                return@setOnClickListener
            }
            registerWithEmailAndPassword(email, password)
        }
    }

    private fun registerWithEmailAndPassword(email: String, password: String) {
        showProgressBar(true)
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    val currentUser = auth.currentUser
                    val user = currentUser?.let {
                        User(
                            it.uid,
                            it.email!!,
                            binding.nameTextField.editText!!.text.toString(),
                        )
                    }
                    FirestoreUtil.initCurrentUserIfFirstTime({
                        showProgressBar(false)
                        Log.d(TAG, "onComplete: User created in firestore")
                        (activity as AuthActivity).navigateToPostActivityWithData(user!!)
                    }, user!!)
                } else {
                    showProgressBar(false)
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        requireContext(),
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    private fun showProgressBar(show: Boolean) {
        if (show) binding.progressBar.visibility = View.VISIBLE
        else binding.progressBar.visibility = View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "RegisterFragment"
    }
}