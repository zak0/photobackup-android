package com.jamitek.photosapp.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.jamitek.photosapp.R
import com.jamitek.photosapp.extension.getActivityViewModel
import com.jamitek.photosapp.ui.adapter.LocalFoldersAdapter
import com.jamitek.photosapp.ui.viewmodel.LocalFoldersViewModel
import kotlinx.android.synthetic.main.fragment_local_folders.*

class LocalFoldersFragment : Fragment(R.layout.fragment_local_folders) {

    private val viewModel by lazy { getActivityViewModel(LocalFoldersViewModel::class.java) }
    private val adapter by lazy { LocalFoldersAdapter(viewModel) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.adapter = adapter

        // TODO Figure out a better place to trigger the scan. Manual and/or automatic...
        temporaryButton.setOnClickListener {
            viewModel.initReScan()
        }

        observe()
        errorText.visibility = if (viewModel.rootDirSet) View.GONE else View.VISIBLE
    }

    private fun observe() {
        viewModel.localFolders.observe(viewLifecycleOwner, Observer {
            adapter.notifyDataSetChanged()
        })

        viewModel.selectedFolder.observe(viewLifecycleOwner, Observer {
            it?.also {
                findNavController().navigate(R.id.action_localFoldersFragment_to_thumbnailsGridFragment)
            }
        })
    }
}