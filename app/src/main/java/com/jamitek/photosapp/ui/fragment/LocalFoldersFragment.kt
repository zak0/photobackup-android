package com.jamitek.photosapp.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.jamitek.photosapp.R
import com.jamitek.photosapp.storage.StorageAccessHelper
import kotlinx.android.synthetic.main.fragment_local_folders.*

class LocalFoldersFragment : Fragment(R.layout.fragment_local_folders) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        grantStorageAccessButton.setOnClickListener {
            StorageAccessHelper.promptLocalFoldersRootDirSelection(requireActivity())
        }
    }
}