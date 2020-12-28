package com.jamitek.photosapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.jamitek.photosapp.R
import com.jamitek.photosapp.databinding.FragmentBackupBinding
import com.jamitek.photosapp.extension.getActivityViewModel
import com.jamitek.photosapp.storage.StorageAccessHelper
import com.jamitek.photosapp.ui.BackupScreenEvent
import com.jamitek.photosapp.ui.adapter.BackupAdapter
import com.jamitek.photosapp.ui.viewmodel.BackupViewModel
import com.jamitek.photosapp.worker.BackupWorker

class BackupFragment : Fragment(R.layout.fragment_backup) {

    private val viewModel by lazy { getActivityViewModel(BackupViewModel::class.java) }
    private val adapter by lazy { BackupAdapter(viewModel) }
    private var nullableBinding: FragmentBackupBinding? = null
    private val binding
        get() = nullableBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentBackupBinding.inflate(inflater, container, false).let {
        nullableBinding = it
        binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        nullableBinding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.adapter = adapter
        observe()
    }

    private fun observe() {
        viewModel.items.observe(viewLifecycleOwner) {
            adapter.notifyDataSetChanged()
        }

        viewModel.uiEvent.observe(viewLifecycleOwner) {
            val event = it.get()
            when (event) {
                BackupScreenEvent.ShowCameraDirSelection -> StorageAccessHelper
                    .promptCameraDirSelection(
                        requireActivity()
                    )
                BackupScreenEvent.ShowServerSetup -> findNavController().navigate(R.id.action_backupFragment_to_serverSetupFragment)
                BackupScreenEvent.StartBackupWorker -> BackupWorker.startNow(requireContext())
                else -> event?.also { error("Unhandled BackupScreenEvent: $event") }
            }
        }
    }
}