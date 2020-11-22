package com.jamitek.photosapp.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jamitek.photosapp.R
import com.jamitek.photosapp.extension.dependencyRoot
import com.jamitek.photosapp.storage.StorageAccessHelper
import com.jamitek.photosapp.ui.viewmodel.LocalCameraViewModel
import com.jamitek.photosapp.ui.viewmodel.BackupViewModel
import com.jamitek.photosapp.ui.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener,
    BottomNavigationView.OnNavigationItemSelectedListener {

    private val navController by lazy { findNavController(R.id.navHostFragment) }
    private val viewModelFactory by lazy { ViewModelFactory(dependencyRoot) }
    private val localCameraViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(
            LocalCameraViewModel::class.java
        )
    }

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
        // TODO Copy nice sliding hiding animation for bottom nav bar from uptimeapp
        bottomNav.visibility =
            if (destination.id in bottomNavlessFragments) View.GONE else View.VISIBLE
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val actionId = when (item.itemId) {
            R.id.photos -> R.id.action_global_mainFragment
            R.id.backup -> R.id.action_global_localFoldersFragment
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
                        persistUriPermission(uri)
                        localCameraViewModel.onCameraDirChanged(uri)
                    }
                }

                else -> super.onActivityResult(requestCode, resultCode, data)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun persistUriPermission(uri: Uri) {
        contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION.or(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        )
    }
}
