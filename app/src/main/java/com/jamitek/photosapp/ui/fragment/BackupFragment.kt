package com.jamitek.photosapp.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.jamitek.photosapp.R
import com.jamitek.photosapp.extension.getActivityViewModel
import com.jamitek.photosapp.storage.StorageAccessHelper
import com.jamitek.photosapp.ui.BackupScreenEvent
import com.jamitek.photosapp.ui.adapter.BackupAdapter
import com.jamitek.photosapp.ui.viewmodel.BackupViewModel
import com.jamitek.photosapp.worker.BackupWorker
import kotlinx.android.synthetic.main.fragment_backup.*

class BackupFragment : Fragment(R.layout.fragment_backup) {

    private val viewModel by lazy { getActivityViewModel(BackupViewModel::class.java) }
    private val adapter by lazy { BackupAdapter(viewModel) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.adapter = adapter
        observe()
    }

    private fun observe() {
        viewModel.items.observe(viewLifecycleOwner) {
            adapter.notifyDataSetChanged()
        }

        viewModel.uiEvent.observe(viewLifecycleOwner) {
            val event = it.get()
            when (it.get()) {
                BackupScreenEvent.ShowCameraDirSelection -> StorageAccessHelper
                    .promptCameraDirSelection(
                        requireActivity()
                    )
                BackupScreenEvent.ShowServerSetup -> findNavController().navigate(R.id.action_backupFragment_to_serverSetupFragment)
                BackupScreenEvent.StartBackupWorker -> BackupWorker.startNow(requireContext())
                else -> error("Unhandled BackupScreenEvent: $event")
            }
        }
    }
}