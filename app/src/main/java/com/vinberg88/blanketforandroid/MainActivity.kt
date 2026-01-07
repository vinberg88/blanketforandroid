package com.vinberg88.blanketforandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.vinberg88.blanketforandroid.ui.screens.MainScreen
import com.vinberg88.blanketforandroid.ui.theme.BlanketForAndroidTheme
import com.vinberg88.blanketforandroid.viewmodel.BlanketViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: BlanketViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BlanketForAndroidTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(viewModel = viewModel)
                }
            }
        }
    }
}
