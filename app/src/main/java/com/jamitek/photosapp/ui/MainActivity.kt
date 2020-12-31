package com.jamitek.photosapp.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.jamitek.photosapp.R
import com.jamitek.photosapp.databinding.ActivityMainBinding
import com.jamitek.photosapp.extension.dependencyRoot
import com.jamitek.photosapp.storage.StorageAccessHelper
import com.jamitek.photosapp.ui.viewmodel.RootViewModel
import com.jamitek.photosapp.ui.viewmodel.ViewModelFactory

class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {

    private val navController by lazy { findNavController(R.id.navHostFragment) }
    private val viewModelFactory by lazy { ViewModelFactory(dependencyRoot) }
    private val localCameraViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(
            RootViewModel::class.java
        )
    }
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController.addOnDestinationChangedListener(this)
        NavigationUI.setupWithNavController(binding.toolbar, navController)
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        val toolbarlessFragments = listOf(R.id.mainFragment, R.id.viewerFragment)

        binding.toolbar.visibility =
            if (destination.id in toolbarlessFragments) View.GONE else View.VISIBLE
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
