package com.jamitek.photosapp.ui.fragment

import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.jamitek.photosapp.R
import com.jamitek.photosapp.extension.getActivityViewModel
import com.jamitek.photosapp.ui.ServerSetupScreenEvent
import com.jamitek.photosapp.ui.adapter.ServerSetupAdapter
import com.jamitek.photosapp.ui.dialog.EditTextDialog
import com.jamitek.photosapp.ui.viewmodel.ServerSetupViewModel
import kotlinx.android.synthetic.main.fragment_server_setup.*

class ServerSetupFragment : Fragment(R.layout.fragment_server_setup) {

    private val viewModel by lazy { getActivityViewModel(ServerSetupViewModel::class.java) }
    private val adapter by lazy { ServerSetupAdapter(viewModel) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.adapter = adapter
        observe()

        cancel.setOnClickListener { findNavController().popBackStack() }

        save.setOnClickListener {
            viewModel.onSaveConfig()
            findNavController().popBackStack()
        }
    }

    private fun observe() {
        viewModel.newConfig.observe(viewLifecycleOwner) {
            adapter.notifyDataSetChanged()
        }

        viewModel.items.observe(viewLifecycleOwner) {
            adapter.notifyDataSetChanged()
        }

        viewModel.uiEvent.observe(viewLifecycleOwner) {
            when (it.get()) {
                ServerSetupScreenEvent.ShowServerAddressDialog -> EditTextDialog(
                    context = requireContext(),
                    title = getString(R.string.serverConfigAddress),
                    inputTypeFlags = InputType.TYPE_CLASS_TEXT.or(InputType.TYPE_TEXT_VARIATION_URI)
                ) { input -> viewModel.onServerUrlSet(input) }.show()

                ServerSetupScreenEvent.ShowUsernameDialog -> EditTextDialog(
                    context = requireContext(),
                    title = getString(R.string.serverConfigUsername)
                ) { input -> viewModel.onUsernameSet(input) }.show()

                ServerSetupScreenEvent.ShowPasswordDialog -> EditTextDialog(
                    context = requireContext(),
                    title = getString(R.string.serverConfigPassword),
                    inputTypeFlags = InputType.TYPE_CLASS_TEXT.or(InputType.TYPE_TEXT_VARIATION_PASSWORD)
                ) { input -> viewModel.onPasswordSet(input) }.show()
            }
        }
    }

}