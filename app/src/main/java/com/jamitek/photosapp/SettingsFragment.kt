package com.jamitek.photosapp

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.jamitek.photosapp.database.SharedPrefsPersistence
import com.jamitek.photosapp.storage.StorageAccessHelper
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setCameraDirButton.setOnClickListener {
            StorageAccessHelper.promptRootDirSelection(requireActivity())
        }

        scanCameraDirButton.setOnClickListener {
            GlobalScope.launch {
                SharedPrefsPersistence.cameraDirUriString?.also {
                    StorageAccessHelper.iterateCameraDir(requireActivity(), it)
                }
            }
        }

        cameraDirLabel.text = "Camera dir: ${SharedPrefsPersistence.cameraDirUriString}"
    }
}