package com.sekolah.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.sekolah.mobile.data.storage.AndroidPlatformContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidPlatformContext.init(applicationContext)
        enableEdgeToEdge()
        setContent {
            App()
        }
    }
}

