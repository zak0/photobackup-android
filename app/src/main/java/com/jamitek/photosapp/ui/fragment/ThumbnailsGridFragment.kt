package com.jamitek.photosapp.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.jamitek.photosapp.R
import com.jamitek.photosapp.extension.getActivityViewModel
import com.jamitek.photosapp.ui.viewmodel.LocalFoldersViewModel

open class ThumbnailsGridFragment : Fragment(R.layout.fragment_thumbnails_grid) {

    private val viewModel by lazy { getActivityViewModel(LocalFoldersViewModel::class.java) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.onFolderOpened()
    }

}