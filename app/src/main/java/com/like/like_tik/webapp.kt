package com.like.like_tik

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient

class WebApp(context: Context, private val url: String) : WebView(context) {
    init {
        settings.javaScriptEnabled = true

        webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: android.webkit.WebResourceRequest?): Boolean {
                val targetUrl = request?.url.toString()

                return if (targetUrl.contains("liketik")) {
                    // Nếu URL chứa "liketik", load trong WebView
                    false
                } else {
                    // Nếu không, mở ra trình duyệt ngoài
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(targetUrl))
                    context.startActivity(intent)
                    true
                }
            }
        }

        loadUrl(url)
    }
}
