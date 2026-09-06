package ru.artem_torpedo.memorandum.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import ru.artem_torpedo.memorandum.presentation.navigation.NavGraph
import ru.artem_torpedo.memorandum.presentation.ui.theme.MemorandumTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MemorandumTheme {
                NavGraph()
            }
        }
    }
}