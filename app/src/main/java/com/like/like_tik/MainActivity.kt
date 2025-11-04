package com.like.like_tik

import android.os.Bundle
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Truyền URL muốn load vào WebApp
        val webView = WebApp(this, "https://liketik.vn")
        setContentView(webView)
    }
}
