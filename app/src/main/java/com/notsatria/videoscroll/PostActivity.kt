package com.notsatria.videoscroll

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.notsatria.videoscroll.auth.AuthActivity
import com.notsatria.videoscroll.databinding.ActivityPostBinding
import com.notsatria.videoscroll.datastore.WebViewPreferences
import com.notsatria.videoscroll.viewmodel.ViewModelFactory
import com.notsatria.videoscroll.viewmodel.WebViewViewModel
import java.io.ByteArrayOutputStream

class PostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostBinding
    private lateinit var userEmail: String
    private var dataFromWebView: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getUserData()
        setupView()

        val pref = WebViewPreferences.getInstance(context = this)
        val viewModel =
            ViewModelProvider(this, ViewModelFactory(pref)).get(WebViewViewModel::class.java)

        viewModel.getUrl().observe(this) {
            binding.tvDataFromWebView.text =
                getString(R.string.data_from_webview, if (!it.isBlank()) it else "No data found")
        }
    }


    private fun setupView() {
        if (intent != null) showSnackBar("Welcome, $userEmail")

        with(binding) {
            btnLogout.setOnClickListener {
                logout()
            }

            ivShare.setOnClickListener {
                shareCard()
            }

            btnGoToChat.setOnClickListener {
                navigate(ActiveUsersActivity::class.java)
            }

            btnAccount.setOnClickListener {
                navigate(ProfileActivity::class.java)
            }

            btnGoToWebView.setOnClickListener {
                navigate(WebViewActivity::class.java)
            }

            btnGoToDropdown.setOnClickListener {
                navigate(DropDownActivity::class.java)
            }
        }
    }

    private fun shareCard() {
        val cardView = findViewById<View>(R.id.cardBaseItem)
        val bitmap = createBitmapFromView(cardView)
        val uri = getImageUri(bitmap)

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, "Halo ges ini tes ya ")
            type = "image/jpeg"
        }

        startActivity(Intent.createChooser(shareIntent, "Share Card"))
    }

    private fun createBitmapFromView(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private fun getImageUri(image: Bitmap): Uri {
        val bytes: ByteArrayOutputStream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path: String =
            MediaStore.Images.Media.insertImage(contentResolver, image, "Title", null)
        return Uri.parse(path)
    }


    private fun getUserData() {
        userEmail = FirebaseAuth.getInstance().currentUser?.email.toString()
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun showProgressBar(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun navigateToAuthActivity() {
        startActivity(
            Intent(this, AuthActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        )
        finish()
    }

    private fun navigate(activity: Class<*>) {
        startActivity(Intent(this, activity))
    }

    private fun logout() {
        showProgressBar(true)
        Firebase.auth.signOut()
        navigateToAuthActivity()
    }

    companion object {
        const val EXTRA_DATA = "extra_data"
    }
}