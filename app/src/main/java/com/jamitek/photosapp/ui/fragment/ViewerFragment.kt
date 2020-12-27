package com.jamitek.photosapp.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.jamitek.photosapp.R
import com.jamitek.photosapp.ui.adapter.ViewerAdapter
import com.jamitek.photosapp.ui.viewmodel.RemoteLibraryViewModel
import kotlinx.android.synthetic.main.fragment_viewer.*

class ViewerFragment : Fragment(R.layout.fragment_viewer) {

    private val viewModel: RemoteLibraryViewModel by lazy {
        ViewModelProvider(requireActivity()).get(RemoteLibraryViewModel::class.java)
    }
    private val viewerAdapter: ViewerAdapter by lazy {
        ViewerAdapter(viewModel) { findNavController().popBackStack() }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager.adapter = viewerAdapter

        viewModel.selectedRemoteMedia.value?.let { photo ->
            viewPager.setCurrentItem(viewModel.allMedia.value?.indexOf(photo) ?: 0, false)
        }

        viewModel.onImageViewerOpened()
    }

}
