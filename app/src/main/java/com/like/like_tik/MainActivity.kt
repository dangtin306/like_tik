//package com.like.like_tik
//
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//
//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        // Truyền URL muốn load vào WebApp
//        val webView = WebApp(this, "https://liketik.vn")
//        setContentView(webView)
//    }
//}

package com.like.like_tik

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import android.webkit.WebView
import android.webkit.WebViewClient

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Tạo WebView hiển thị web
        val webView = WebApp(this, "https://liketik.vn")

        // Nút thanh toán Google Pay (ẩn ban đầu)
        val payButton = Button(this).apply {
            text = "Thanh toán bằng Google Pay"
            textSize = 16f
            setPadding(20, 20, 20, 20)
            visibility = View.GONE // Ẩn trước, chỉ hiện khi web load xong

            setOnClickListener {
                val intent = Intent(this@MainActivity, GooglePayActivity::class.java)
                startActivity(intent)
            }
        }

        // Khi web load xong thì hiện nút
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                payButton.visibility = View.VISIBLE
            }
        }

        // Layout chứa web + nút
        val layout = FrameLayout(this)
        layout.addView(webView)

        val params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        params.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        params.bottomMargin = 60

        layout.addView(payButton, params)
        setContentView(layout)
    }
}
