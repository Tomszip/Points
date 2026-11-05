package com.example.points.ui.detail

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.net.toUri
import com.example.points.R
import com.example.points.ui.theme.BreweryTheme
import com.example.points.util.ViewModelFactory

/**
 * Hosts the Jetpack Compose detail screen.
 * Receives only the brewery id; the ViewModel loads the rest.
 */
class DetailActivity : ComponentActivity() {

    private val viewModel: DetailViewModel by viewModels { ViewModelFactory(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val breweryId = intent.getStringExtra(EXTRA_BREWERY_ID)
        if (breweryId == null) { // should never happen, but never crash because of it
            finish()
            return
        }
        viewModel.load(breweryId)

        setContent {
            BreweryTheme {
                DetailScreen(viewModel, DetailActions(::finish, ::dialPhone, ::openWebsite))
            }
        }
    }

    /** ACTION_DIAL opens the dialer with the number typed; it doesn't call by itself. */
    private fun dialPhone(phone: String) = startSafely(Intent(Intent.ACTION_DIAL, "tel:$phone".toUri()))

    private fun openWebsite(url: String) = startSafely(Intent(Intent.ACTION_VIEW, url.toUri()))

    /** If no app can handle the intent (e.g. no browser), show a message instead of crashing. */
    private fun startSafely(intent: Intent) {
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, R.string.error_no_app, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val EXTRA_BREWERY_ID = "extra_brewery_id"

        /** The only way to open this screen, so nobody forgets the required extra. */
        fun newIntent(context: Context, breweryId: String): Intent =
            Intent(context, DetailActivity::class.java).putExtra(EXTRA_BREWERY_ID, breweryId)
    }
}
