package com.example.hub

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.hub.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        setupActionBarWithNavController(navController)
        setupDestinationListener() // icone no titulo

        //setupFab()
        //setupNavigationListener()

        /*
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

         */
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_toolbar_menu, menu)
        return true
    }

    // modo tema
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_toggle_theme -> {
                val currentNightMode = AppCompatDelegate.getDefaultNightMode()
                if (currentNightMode == AppCompatDelegate.MODE_NIGHT_YES) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupDestinationListener() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            // Lógica para mostrar o ícone ao lado do título
            val iconRes = when (destination.id) {
                R.id.calculatorFragment -> R.drawable.ic_hub_calculator
                R.id.scoreboardFragment -> R.drawable.ic_hub_scoreboard
                R.id.timerFragment -> R.drawable.ic_hub_timer
                else -> 0 // Sem ícone para o Hub ou outras telas
            }

            if (iconRes != 0) {
                supportActionBar?.setLogo(iconRes)
                supportActionBar?.setDisplayUseLogoEnabled(true)
            } else {
                supportActionBar?.setDisplayUseLogoEnabled(false)
                supportActionBar?.setLogo(null)
            }
        }
    }

    private fun setupFab() {
        binding.fabBackToHub.setOnClickListener {
            navController.popBackStack(R.id.hubFragment, false)
        }
    }

    private fun setupNavigationListener() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id) {
                R.id.hubFragment -> {
                    binding.fabBackToHub.visibility = View.GONE
                }
                 else -> {
                     binding.fabBackToHub.visibility = View.VISIBLE
                 }
            }
        }
    }
}