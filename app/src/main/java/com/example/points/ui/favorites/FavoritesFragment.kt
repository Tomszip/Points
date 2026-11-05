package com.example.points.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.points.R
import com.example.points.data.model.Brewery
import com.example.points.data.model.FavoriteStatus
import com.example.points.databinding.FragmentFavoritesBinding
import com.example.points.ui.detail.DetailActivity
import com.example.points.ui.list.BreweryAdapter
import com.example.points.util.UiState
import com.example.points.util.ViewModelFactory

/**
 * Favorites tab (View in MVVM). Reuses the same BreweryAdapter as the main list.
 */
class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavoritesViewModel by viewModels { ViewModelFactory(requireContext()) }
    private val adapter = BreweryAdapter { brewery -> openDetail(brewery) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerFavorites.adapter = adapter
        setupStatusChips()
        viewModel.uiState.observe(viewLifecycleOwner) { state -> render(state) }
    }

    /** Reload when coming back from the detail screen (the user may have changed a status). */
    override fun onResume() {
        super.onResume()
        viewModel.loadFavorites()
    }

    /** Reload when the tab is shown again from the bottom navigation. */
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) viewModel.loadFavorites()
    }

    private fun setupStatusChips() {
        val savedChip = if (viewModel.selectedStatus == FavoriteStatus.PENDING) R.id.chipPending else R.id.chipVisited
        binding.chipGroupStatus.check(savedChip)

        binding.chipGroupStatus.setOnCheckedStateChangeListener { _, checkedIds ->
            val status = if (checkedIds.firstOrNull() == R.id.chipPending) FavoriteStatus.PENDING else FavoriteStatus.VISITED
            viewModel.onStatusSelected(status)
        }
    }

    /** Local data loads instantly, so there is no Loading state to draw here. */
    private fun render(state: UiState<List<Brewery>>) {
        binding.recyclerFavorites.isVisible = state is UiState.Success
        binding.textEmpty.isVisible = state is UiState.Error

        when (state) {
            is UiState.Success -> adapter.submitList(state.data)
            is UiState.Error -> binding.textEmpty.setText(state.messageRes)
            UiState.Loading -> Unit
        }
    }

    private fun openDetail(brewery: Brewery) {
        startActivity(DetailActivity.newIntent(requireContext(), brewery.id))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerFavorites.adapter = null
        _binding = null
    }
}
