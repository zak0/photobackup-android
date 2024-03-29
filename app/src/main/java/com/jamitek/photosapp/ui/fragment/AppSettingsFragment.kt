package com.jamitek.photosapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.jamitek.photosapp.R
import com.jamitek.photosapp.databinding.FragmentAppSettingsBinding
import com.jamitek.photosapp.extension.getActivityViewModel
import com.jamitek.photosapp.storage.StorageAccessHelper
import com.jamitek.photosapp.ui.AppSettingsScreenEvent
import com.jamitek.photosapp.ui.adapter.BackupAdapter
import com.jamitek.photosapp.ui.viewmodel.AppSettingsViewModel
import com.jamitek.photosapp.worker.BackupWorker

class AppSettingsFragment : Fragment(R.layout.fragment_app_settings) {

    private val viewModel by lazy { getActivityViewModel(AppSettingsViewModel::class.java) }
    private val adapter by lazy { BackupAdapter(viewModel) }
    private var nullableBinding: FragmentAppSettingsBinding? = null
    private val binding
        get() = nullableBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentAppSettingsBinding.inflate(inflater, container, false).let {
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
                AppSettingsScreenEvent.ShowCameraDirSelection -> StorageAccessHelper
                    .promptCameraDirSelection(
                        requireActivity()
                    )
                AppSettingsScreenEvent.ShowServerSetup -> findNavController().navigate(R.id.action_backupFragment_to_serverSetupFragment)
                AppSettingsScreenEvent.StartBackupWorker -> BackupWorker.startNow(requireContext())
                else -> event?.also { error("Unhandled BackupScreenEvent: $event") }
            }
        }
    }
}