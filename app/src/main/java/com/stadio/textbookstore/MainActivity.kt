package com.stadio.textbookstore

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.stadio.textbookstore.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //grab NavController from NavHostFragment in layout
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        //wire bottom nav items to nav graph destinations by ID
        binding.bottomNav.setupWithNavController(navController)

        //show the bottom nav ONLY on four main screens
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNav.visibility = when (destination.id) {
                R.id.homeFragment,
                R.id.sellFragment,
                R.id.messagesFragment,
                R.id.profileFragment -> View.VISIBLE
                else -> View.GONE
            }
        }
    }
}