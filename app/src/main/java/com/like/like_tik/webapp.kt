package com.like.like_tik

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button

class WebApp(context: Context, private val startUrl: String) : WebView(context) {
    private var payButton: Button? = null

    // ✅ Biến để lưu URL hiện tại
    var currentUrl: String = startUrl
        private set

    init {
        settings.javaScriptEnabled = true

        webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: android.webkit.WebResourceRequest?): Boolean {
                val targetUrl = request?.url.toString()

                // ✅ Cập nhật URL khi điều hướng
                currentUrl = targetUrl

                return if (targetUrl.contains("liketik") || targetUrl.contains("itok")) {
                    false
                } else {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(targetUrl))
                    context.startActivity(intent)
                    true
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (url != null) {
                    currentUrl = url // ✅ Cập nhật lại khi load xong (phòng trường hợp redirect)
                }

                if (url?.contains("https://itok.pro/#NapXu") == true) {
                    payButton?.visibility = View.VISIBLE
                } else {
                    payButton?.visibility = View.GONE
                }
            }
        }

        loadUrl(startUrl)
    }

    fun setPayButton(button: Button) {
        this.payButton = button
    }
}
