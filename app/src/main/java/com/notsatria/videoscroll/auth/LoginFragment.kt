package com.notsatria.videoscroll.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.notsatria.videoscroll.R
import com.notsatria.videoscroll.databinding.FragmentLoginBinding
import com.notsatria.videoscroll.model.User
import com.notsatria.videoscroll.utils.FirestoreUtil


@Suppress("DEPRECATION")
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var mGoogleApiClient: GoogleApiClient
    private val GOOGLE_SIGN_IN_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        initFBGoogleSignIn()
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
        setupView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SIGN_IN_REQUEST_CODE) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data!!)
            if (result!!.isSuccess) {
                val account = result.signInAccount
                firebaseAuthWithGoogle(account!!)
            } else {
                showSnackBar("Google Sign In failed")
            }
        }
    }

    private fun setupView() {
        binding.btnToRegister.setOnClickListener {
            (activity as AuthActivity).navigateToRegisterFragment()
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.emailTextField.editText!!.text.toString()
            val password = binding.passwordTextField.editText!!.text.toString()
            if (email.isEmpty() || password.isEmpty()) {
                showToast(getString(R.string.email_and_password_must_be_filled))
                return@setOnClickListener
            }
            signInWithEmailAndPassword(email, password)
        }

        binding.btnGoogle.setOnClickListener {
            signInWithGoogleSignIn()
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

    // [START auth_with_google]
    private fun initFBGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()
        mGoogleApiClient = GoogleApiClient.Builder(requireContext())
            .enableAutoManage(
                requireActivity()
            ) { connectionResult -> showSnackBar(connectionResult.errorMessage!!) }
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso).build()
    }

    private fun signInWithGoogleSignIn() {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN_REQUEST_CODE)
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(AuthActivity(),
                OnCompleteListener<AuthResult?> { task ->
                    showProgressBar(true)
                    if (task.isSuccessful) {
                        Log.d(TAG, "signInWithCredential:success")
                        val currentUser = auth.currentUser
                        val user = currentUser?.let {
                            User(
                                it.uid,
                                it.email!!,
                                it.displayName
                            )
                        }
                        FirestoreUtil.initCurrentUserIfFirstTime({
                            showProgressBar(false)
                            Log.d(TAG, "onComplete: User created in firestore")
                            (activity as AuthActivity).navigateToPostActivityWithData(user!!)
                        }, user!!)
                    } else {
                        showProgressBar(false)
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        showSnackBar("Authentication failed.")
                    }
                }).addOnFailureListener(OnFailureListener { e ->
                showSnackBar(e.message!!)
                e.printStackTrace()
            })
    }
    // [END auth_with_google]

    private fun showProgressBar(show: Boolean) {
        if (show) binding.progressBar.visibility = View.VISIBLE
        else binding.progressBar.visibility = View.GONE
    }

    private fun showSnackBar(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "LoginFragment"
    }

}