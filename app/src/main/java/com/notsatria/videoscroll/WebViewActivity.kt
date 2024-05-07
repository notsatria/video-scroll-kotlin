package com.notsatria.videoscroll

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.notsatria.videoscroll.databinding.ActivityWebViewBinding
import com.notsatria.videoscroll.datastore.WebViewPreferences
import com.notsatria.videoscroll.viewmodel.ViewModelFactory
import com.notsatria.videoscroll.viewmodel.WebViewViewModel

class WebViewActivity : AppCompatActivity() {

    private val url: String =
        "https://www.linkedin.com/search/results/all/?keywords=flutter&origin=GLOBAL_SEARCH_HEADER&sid=wP2"
    private var currentUrl: String? = null
    private lateinit var binding: ActivityWebViewBinding

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.let {
            it.title = "WebView"
            it.setDisplayHomeAsUpEnabled(true)
        }

        val pref = WebViewPreferences.getInstance(context = this)
        val viewModel =
            ViewModelProvider(this, ViewModelFactory(pref))[WebViewViewModel::class.java]

        with(binding) {
            webView.settings.javaScriptEnabled = true
            webView.loadUrl(url)
            webView.webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    showLoading(true)
                }

                override fun onPageFinished(view: android.webkit.WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    showLoading(false)
                    Log.d("WebViewActivity", "currentUrl: $url")
                    currentUrl = url
                    viewModel.saveUrl(url ?: "No data found")
                }
            }

        }
    }

    override fun onSupportNavigateUp(): Boolean {
        Log.d("WebViewActivity", "onSupportNavigateUp: $currentUrl")
        finish()
        return true
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}