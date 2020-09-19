package com.jamitek.photosapp.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.jamitek.photosapp.R
import com.jamitek.photosapp.extension.getActivityViewModel
import com.jamitek.photosapp.storage.StorageAccessHelper
import com.jamitek.photosapp.ui.viewmodel.LocalFoldersViewModel
import kotlinx.android.synthetic.main.fragment_local_folders.*

class LocalFoldersFragment : Fragment(R.layout.fragment_local_folders) {

    private val viewModel by lazy { getActivityViewModel(LocalFoldersViewModel::class.java) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        grantStorageAccessButton.setOnClickListener {
            StorageAccessHelper.promptLocalFoldersRootDirSelection(requireActivity())
        }

        grantStorageAccessButton.setOnLongClickListener {
            viewModel.initScan()
            false
        }

        observe()
    }

    private fun observe() {
        viewModel.localFolders.observe(viewLifecycleOwner, Observer {
            val sb = StringBuilder()
            it!!.forEach {
                sb.append(it.name)
                sb.append(" - ")
                sb.append(it.media.size)
                sb.append("\n")
            }

            errorText.text = sb
        })
    }
}