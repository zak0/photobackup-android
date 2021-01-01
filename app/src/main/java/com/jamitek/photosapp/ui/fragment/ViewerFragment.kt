package com.jamitek.photosapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.jamitek.photosapp.R
import com.jamitek.photosapp.databinding.FragmentViewerBinding
import com.jamitek.photosapp.ui.adapter.ViewerAdapter
import com.jamitek.photosapp.ui.viewmodel.MediaTimelineViewModel

class ViewerFragment : Fragment(R.layout.fragment_viewer) {

    private val viewModel: MediaTimelineViewModel by lazy {
        ViewModelProvider(requireActivity()).get(MediaTimelineViewModel::class.java)
    }
    private val viewerAdapter: ViewerAdapter by lazy {
        ViewerAdapter(viewModel) { findNavController().popBackStack() }
    }
    private var nullableBinding: FragmentViewerBinding? = null
    private val binding
        get() = nullableBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentViewerBinding.inflate(inflater, container, false).let {
        nullableBinding = it
        binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        nullableBinding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewPager.adapter = viewerAdapter

        viewModel.selectedRemoteMedia.value?.let { photo ->
            binding.viewPager.setCurrentItem(viewModel.allMedia.value?.indexOf(photo) ?: 0, false)
        }

        viewModel.onImageViewerOpened()
    }

}
