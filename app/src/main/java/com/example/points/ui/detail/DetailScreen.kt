package com.example.points.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.points.R
import com.example.points.data.model.Brewery
import com.example.points.util.UiState

/** Actions the screen can't do by itself (navigation, opening other apps). */
data class DetailActions(
    val onBack: () -> Unit,
    val onCall: (String) -> Unit,
    val onOpenWebsite: (String) -> Unit
)

/**
 * Root of the detail screen. Turns the ViewModel LiveData into Compose state
 * (observeAsState): when a value changes, only the affected parts are redrawn.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(viewModel: DetailViewModel, actions: DetailActions) {
    val uiState by viewModel.uiState.observeAsState(UiState.Loading)
    val snackbarHostState = remember { SnackbarHostState() }
    MessageEffect(viewModel, snackbarHostState)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.detail_title)) },
                navigationIcon = {
                    IconButton(onClick = actions.onBack) {
                        Icon(painterResource(R.drawable.ic_arrow_back), stringResource(R.string.back))
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
            when (val state = uiState) {
                UiState.Loading -> CircularProgressIndicator()
                is UiState.Error -> ErrorContent(state.messageRes, onRetry = viewModel::retry)
                is UiState.Success -> DetailContent(state.data, viewModel, actions)
            }
        }
    }
}

/** Shows each new ViewModel message once in the Snackbar. */
@Composable
private fun MessageEffect(viewModel: DetailViewModel, snackbarHostState: SnackbarHostState) {
    val message by viewModel.message.observeAsState()
    val context = LocalContext.current
    LaunchedEffect(message) {
        message?.let {
            viewModel.onMessageShown()
            snackbarHostState.showSnackbar(context.getString(it))
        }
    }
}

@Composable
private fun DetailContent(brewery: Brewery, viewModel: DetailViewModel, actions: DetailActions) {
    val favoriteStatus by viewModel.favoriteStatus.observeAsState()
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        BreweryHeader(brewery)
        ContactCard(brewery, onCall = actions.onCall, onOpenWebsite = actions.onOpenWebsite)
        FavoriteSection(
            status = favoriteStatus,
            onStatusSelected = viewModel::onStatusSelected,
            onRemove = viewModel::onRemoveFavorite
        )
    }
}

@Composable
private fun ErrorContent(messageRes: Int, onRetry: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
        Text(stringResource(messageRes))
        Button(onClick = onRetry, modifier = Modifier.padding(top = 12.dp)) {
            Text(stringResource(R.string.retry))
        }
    }
}
