package site.freelike.twa

import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.FrameLayout
import android.view.Gravity
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val webView = WebView(this)
        webView.settings.javaScriptEnabled = true
        webView.loadUrl("https://itok.pro")

        val payButton = Button(this).apply {
            text = "Thanh toán bằng Google Pay"
            textSize = 16f
            setPadding(20, 20, 20, 20)
            visibility = Button.GONE // Ẩn mặc định
            setOnClickListener {
                val intent = Intent(this@MainActivity, GooglePayActivity::class.java)
                startActivity(intent)
            }
        }

        val layout = FrameLayout(this)
        layout.addView(webView)

        val params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            bottomMargin = 60
        }
        layout.addView(payButton, params)
        setContentView(layout)

        // Lắng nghe thay đổi URL để hiển thị nút nạp xu
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (url?.contains("https://itok.pro/#NapXu") == true) {
                    payButton.visibility = Button.VISIBLE
                } else {
                    payButton.visibility = Button.GONE
                }
            }
        }
    }
}
