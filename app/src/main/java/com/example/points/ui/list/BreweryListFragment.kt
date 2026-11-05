package com.example.points.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.points.R
import com.example.points.data.model.Brewery
import com.example.points.data.model.SearchFilter
import com.example.points.databinding.FragmentBreweryListBinding
import com.example.points.ui.detail.DetailActivity
import com.example.points.util.UiState
import com.example.points.util.ViewModelFactory

/**
 * Main screen (View in MVVM): only draws the state and forwards user actions.
 * All the logic lives in BreweryListViewModel.
 */
class BreweryListFragment : Fragment() {

    // The binding only exists between onCreateView and onDestroyView
    private var _binding: FragmentBreweryListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BreweryListViewModel by viewModels { ViewModelFactory(requireContext()) }
    private val adapter = BreweryAdapter { brewery -> openDetail(brewery) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBreweryListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerBreweries.adapter = adapter
        setupFilterChips()
        setupSearchView()
        binding.buttonRetry.setOnClickListener { viewModel.retry() }
        viewModel.uiState.observe(viewLifecycleOwner) { state -> render(state) }
    }

    /** Restores the saved filter first, then listens for changes. */
    private fun setupFilterChips() {
        val savedChip = if (viewModel.currentFilter == SearchFilter.CITY) R.id.chipCity else R.id.chipName
        binding.chipGroupFilter.check(savedChip)
        updateSearchHint(viewModel.currentFilter)

        binding.chipGroupFilter.setOnCheckedStateChangeListener { _, checkedIds ->
            val filter = if (checkedIds.firstOrNull() == R.id.chipCity) SearchFilter.CITY else SearchFilter.NAME
            updateSearchHint(filter)
            viewModel.onFilterChanged(filter)
        }
    }

    /** Restores the saved text first, then searches on every change (real-time search). */
    private fun setupSearchView() {
        binding.searchView.setQuery(viewModel.currentQuery, false)
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.onQueryChanged(newText.orEmpty())
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.searchView.clearFocus() // hides the keyboard
                return true
            }
        })
    }

    private fun updateSearchHint(filter: SearchFilter) {
        val hint = if (filter == SearchFilter.CITY) R.string.search_hint_city else R.string.search_hint_name
        binding.searchView.queryHint = getString(hint)
    }

    private fun render(state: UiState<List<Brewery>>) {
        binding.progressBar.isVisible = state is UiState.Loading
        binding.recyclerBreweries.isVisible = state is UiState.Success
        binding.layoutError.isVisible = state is UiState.Error

        when (state) {
            is UiState.Success -> adapter.submitList(state.data)
            is UiState.Error -> binding.textError.setText(state.messageRes)
            UiState.Loading -> Unit
        }
    }

    private fun openDetail(brewery: Brewery) {
        startActivity(DetailActivity.newIntent(requireContext(), brewery.id))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerBreweries.adapter = null // the adapter must not keep the old view alive
        _binding = null
    }
}
