package com.example.points.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.points.R
import com.example.points.data.model.Brewery
import com.example.points.databinding.ItemBreweryBinding
import com.example.points.util.faviconUrl
import com.squareup.picasso.Picasso

/**
 * Adapter pattern: turns a List<Brewery> into rows of the RecyclerView.
 * ListAdapter + DiffUtil only redraws the rows that actually changed.
 * Reused by the main list and the favorites list.
 *
 * @param onClick what to do when a row is tapped (the Fragment decides).
 */
class BreweryAdapter(
    private val onClick: (Brewery) -> Unit
) : ListAdapter<Brewery, BreweryAdapter.BreweryViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BreweryViewHolder {
        val binding = ItemBreweryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BreweryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BreweryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /** Holds the views of one row so findViewById isn't repeated on every scroll. */
    inner class BreweryViewHolder(
        private val binding: ItemBreweryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(brewery: Brewery) {
            binding.textName.text = brewery.name
            binding.textType.text = brewery.type?.replaceFirstChar { it.uppercase() }
            binding.textCity.text = listOfNotNull(brewery.city, brewery.state).joinToString(", ")
            loadFavicon(brewery)
            binding.root.setOnClickListener { onClick(brewery) }
        }

        /** A null url makes Picasso show the placeholder; a failed download shows error(). */
        private fun loadFavicon(brewery: Brewery) {
            Picasso.get()
                .load(brewery.faviconUrl())
                .placeholder(R.drawable.ic_brewery)
                .error(R.drawable.ic_brewery)
                .into(binding.imageFavicon)
        }
    }

    /** Tells DiffUtil when two rows are the same brewery and when their content changed. */
    private object DiffCallback : DiffUtil.ItemCallback<Brewery>() {
        override fun areItemsTheSame(oldItem: Brewery, newItem: Brewery) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Brewery, newItem: Brewery) = oldItem == newItem
    }
}
