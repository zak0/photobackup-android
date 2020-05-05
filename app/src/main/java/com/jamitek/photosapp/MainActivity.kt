package com.jamitek.photosapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jamitek.photosapp.database.SharedPrefsPersistence
import com.jamitek.photosapp.storage.StorageAccessHelper
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener, BottomNavigationView.OnNavigationItemSelectedListener {

    private val navController: NavController by lazy { findNavController(R.id.navHostFragment) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navController.addOnDestinationChangedListener(this)
        bottomNav.setOnNavigationItemSelectedListener(this)
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        val bottomNavlessFragments = arrayListOf(R.id.viewerFragment)
        bottomNav.visibility = if (destination.id in bottomNavlessFragments) View.GONE else View.VISIBLE
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val actionId = when(item.itemId) {
            R.id.photos -> R.id.action_global_mainFragment
            R.id.settings -> R.id.action_global_settingsFragment
            else -> R.id.action_global_mainFragment
        }

        navController.navigate(actionId)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                StorageAccessHelper.REQUEST_CODE_SET_CAMERA_DIR -> {
                    data?.data?.let { uri ->
                        SharedPrefsPersistence.cameraDirUriString = uri.toString()
                        StorageAccessHelper.iterateCameraDir(this, uri.toString())
                    }
                }
                else -> super.onActivityResult(requestCode, resultCode, data)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
