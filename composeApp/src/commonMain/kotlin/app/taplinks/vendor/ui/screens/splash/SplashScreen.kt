package app.taplinks.vendor.ui.screens.splash

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SplashScreen(
    viewModel: SplashViewModel,
    onNavigateToSignIn: () -> Unit,
    onNavigateToPaymentRequests: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.checkLoginStatus()
    }

    LaunchedEffect(uiState.navigationEvent) {
        when (uiState.navigationEvent) {
            SplashNavigationEvent.NavigateToSignIn -> onNavigateToSignIn()
            SplashNavigationEvent.NavigateToPaymentRequests -> onNavigateToPaymentRequests()
            null -> { 
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "TapLinks",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 24.dp)
            )

            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(top = 32.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

