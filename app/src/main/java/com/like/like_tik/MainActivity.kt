package com.like.like_tik

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity

class MainActivity  : ComponentActivity() {
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val webView = WebView(this)
        setContentView(webView)

        // Cho phép JavaScript để web Google hiển thị đúng
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()

        // Mở trang Google
        webView.loadUrl("https://www.google.com")
    }
}
