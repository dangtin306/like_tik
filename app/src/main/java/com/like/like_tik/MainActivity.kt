package com.like.like_tik

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import android.view.Gravity
import androidx.activity.ComponentActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val webApp = WebApp(this, "https://itok.pro")

        val payButton = Button(this).apply {
            text = "Thanh toán bằng Google Pay"
            textSize = 16f
            setPadding(20, 20, 20, 20)
            visibility = Button.GONE
            setOnClickListener {
                val intent = Intent(this@MainActivity, GooglePayActivity::class.java)
                startActivity(intent)
            }
        }

        webApp.setPayButton(payButton)

        // ✅ Khi kéo xuống, load lại URL đang lưu trong file con
        val swipeRefresh = SwipeRefreshLayout(this).apply {
            addView(webApp)
            setOnRefreshListener {
                val currentUrl = webApp.currentUrl
                webApp.loadUrl(currentUrl)
                isRefreshing = false
            }
        }

        val layout = FrameLayout(this)
        layout.addView(swipeRefresh)

        val params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            bottomMargin = 60
        }
        layout.addView(payButton, params)

        setContentView(layout)
    }
}
