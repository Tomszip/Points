package com.example.points.ui.main

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.points.R
import com.example.points.databinding.ActivityMainBinding
import com.example.points.ui.favorites.FavoritesFragment
import com.example.points.ui.list.BreweryListFragment

/**
 * Base structure of the app: a container for the two tabs and the bottom navigation.
 * Both Fragments are added once and then shown / hidden, so switching tabs
 * keeps the search results instead of calling the API again.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applySystemBarsPadding()

        // On rotation the FragmentManager restores both fragments (and which one is hidden)
        if (savedInstanceState == null) addTabs()
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            showTab(if (item.itemId == R.id.navFavorites) TAG_FAVORITES else TAG_BREWERIES)
            true
        }
    }

    private fun addTabs() {
        val favorites = FavoritesFragment()
        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainer, BreweryListFragment(), TAG_BREWERIES)
            .add(R.id.fragmentContainer, favorites, TAG_FAVORITES)
            .hide(favorites)
            .commit()
    }

    private fun showTab(tagToShow: String) {
        val tagToHide = if (tagToShow == TAG_FAVORITES) TAG_BREWERIES else TAG_FAVORITES
        val toShow = supportFragmentManager.findFragmentByTag(tagToShow) ?: return
        val toHide = supportFragmentManager.findFragmentByTag(tagToHide) ?: return
        supportFragmentManager.beginTransaction().show(toShow).hide(toHide).commit()
    }

    /**
     * Keeps the content away from the status bar.
     * The bottom inset is handled by BottomNavigationView itself.
     */
    private fun applySystemBarsPadding() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, 0)
            insets
        }
    }

    private companion object {
        const val TAG_BREWERIES = "breweries"
        const val TAG_FAVORITES = "favorites"
    }
}
