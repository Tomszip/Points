package com.example.points.ui.detail

import android.widget.ImageView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.points.R
import com.example.points.data.model.Brewery
import com.example.points.data.model.FavoriteStatus
import com.example.points.util.faviconUrl
import com.squareup.picasso.Picasso

/** Small, reusable pieces of the detail screen. */

/**
 * Picasso works with ImageView, so we embed one inside Compose with AndroidView.
 * update{} runs again if the url changes.
 */
@Composable
fun BreweryLogo(url: String?, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context -> ImageView(context).apply { scaleType = ImageView.ScaleType.FIT_CENTER } },
        update = { imageView ->
            Picasso.get()
                .load(url)
                .placeholder(R.drawable.ic_brewery)
                .error(R.drawable.ic_brewery)
                .into(imageView)
        }
    )
}

@Composable
fun BreweryHeader(brewery: Brewery) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BreweryLogo(url = brewery.faviconUrl(), modifier = Modifier.size(96.dp))
        Text(
            text = brewery.name.orEmpty(),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(top = 12.dp)
        )
        Text(
            text = brewery.type?.replaceFirstChar { it.uppercase() }.orEmpty(),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

/** Address, phone and website. Phone and website are clickable when they exist. */
@Composable
fun ContactCard(brewery: Brewery, onCall: (String) -> Unit, onOpenWebsite: (String) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        InfoRow(label = stringResource(R.string.detail_address), value = brewery.fullAddress)
        HorizontalDivider()
        InfoRow(label = stringResource(R.string.detail_phone), value = brewery.phone, onClick = onCall)
        HorizontalDivider()
        InfoRow(label = stringResource(R.string.detail_website), value = brewery.websiteUrl, onClick = onOpenWebsite)
    }
}

/** A label + value line. Shows "Not available" when the API sent no data. */
@Composable
private fun InfoRow(label: String, value: String?, onClick: ((String) -> Unit)? = null) {
    val hasValue = !value.isNullOrBlank()
    val clickModifier = if (hasValue && onClick != null) Modifier.clickable { onClick(value!!) } else Modifier

    Column(modifier = clickModifier.fillMaxWidth().padding(16.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelMedium)
        Text(
            text = if (hasValue) value!! else stringResource(R.string.not_available),
            style = MaterialTheme.typography.bodyLarge,
            color = if (hasValue && onClick != null) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface
        )
    }
}

/** Lets the user mark the brewery as Visited or Pending, or remove it. */
@Composable
fun FavoriteSection(
    status: FavoriteStatus?,
    onStatusSelected: (FavoriteStatus) -> Unit,
    onRemove: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = stringResource(R.string.my_lists), style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatusChip(R.string.status_visited, FavoriteStatus.VISITED, status, onStatusSelected)
            StatusChip(R.string.status_pending, FavoriteStatus.PENDING, status, onStatusSelected)
        }
        if (status != null) {
            TextButton(onClick = onRemove) { Text(stringResource(R.string.remove_from_lists)) }
        }
    }
}

@Composable
private fun StatusChip(
    labelRes: Int,
    chipStatus: FavoriteStatus,
    currentStatus: FavoriteStatus?,
    onSelected: (FavoriteStatus) -> Unit
) {
    FilterChip(
        selected = chipStatus == currentStatus,
        onClick = { onSelected(chipStatus) },
        label = { Text(stringResource(labelRes)) }
    )
}
