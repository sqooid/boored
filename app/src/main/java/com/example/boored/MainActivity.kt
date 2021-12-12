package com.example.boored

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MotionEventCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.boored.databinding.ActivityMainBinding
import com.example.boored.util.Constants
import com.google.android.material.appbar.AppBarLayout

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // onCreate method for main activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setupToolbar()

        setupDestinationChangeListener()

        createNotificationChannel()
    }

    // Back button navigation
    override fun onSupportNavigateUp() =
        navigateUp(findNavController(R.id.nav_host_fragment), binding.drawerLayout)

    // Toolbar nav drawer setup
    private fun setupToolbar() {
        // Nav controller
        val navController = findNavController(R.id.nav_host_fragment)


        setSupportActionBar(binding.toolbar)

        // Setting up action bar
        setupActionBarWithNavController(navController, binding.drawerLayout)

        // Setting up left navigation drawer
        binding.navigationView.setupWithNavController(navController)


    }


    /**
     * Create notification channel for android 8.0 and higher
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(Constants.CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * NavController destination change listener
     */
    private fun setupDestinationChangeListener() {
        val navController = findNavController(R.id.nav_host_fragment)
        navController.addOnDestinationChangedListener { _, dest, _ ->
            val toolBar = findViewById<Toolbar>(R.id.toolbar)
            val appBar = findViewById<AppBarLayout>(R.id.appbar_layout)
//            val toolBarParams = toolBar.layoutParams as AppBarLayout.LayoutParams
            when (dest.id) {
                R.id.detailedViewFragment -> {
                    appBar.setExpanded(true, false)
                    supportActionBar?.hide()
                }
                R.id.tag_search -> {
                    appBar.setExpanded(true, true)
                    supportActionBar?.show()

                }
                R.id.favourites -> {
                    appBar.setExpanded(true, true)
                    supportActionBar?.show()
                }
            }
        }
    }
}

