package com.notsatria.videoscroll

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.notsatria.videoscroll.auth.AuthActivity
import com.notsatria.videoscroll.databinding.ActivityPostBinding
import com.notsatria.videoscroll.model.User
import java.io.ByteArrayOutputStream

class PostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostBinding
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getUserData()
        setupView()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupView() {
        showSnackBar("Welcome, ${user.email}")

        binding.btnLogout.setOnClickListener {
            logout()
        }

        binding.ivShare.setOnClickListener {
            shareCard()
        }

        binding.btnGoToChat.setOnClickListener {
            navigate(ActiveUsersActivity::class.java)
        }

        binding.btnAccount.setOnClickListener {
            navigate(ProfileActivity::class.java)
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
        user = intent.getParcelableExtra(AuthActivity.EXTRA_USER)!!
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

}