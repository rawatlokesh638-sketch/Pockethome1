package com.pockethome.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.FirebaseApp
import com.pockethome.app.ui.navigation.MainNavGraph
import com.pockethome.app.ui.theme.GrihaBudgetTheme
import com.pockethome.app.ui.viewmodel.GrihaBudgetViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: GrihaBudgetViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        try {
            FirebaseApp.initializeApp(this)
        } catch (e: Exception) {
            // Firebase already initialized
        }

        setContent {
            GrihaBudgetTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                    MainNavGraph(
                        viewModel = viewModel,
                        uiState = uiState
                    )
                }
            }
        }
    }
}
