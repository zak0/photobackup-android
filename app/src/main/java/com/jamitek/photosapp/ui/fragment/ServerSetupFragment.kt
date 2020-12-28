package com.jamitek.photosapp.ui.fragment

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.jamitek.photosapp.R
import com.jamitek.photosapp.databinding.FragmentServerSetupBinding
import com.jamitek.photosapp.extension.getActivityViewModel
import com.jamitek.photosapp.ui.ServerSetupScreenEvent
import com.jamitek.photosapp.ui.adapter.ServerSetupAdapter
import com.jamitek.photosapp.ui.dialog.EditTextDialog
import com.jamitek.photosapp.ui.viewmodel.ServerSetupViewModel

class ServerSetupFragment : Fragment(R.layout.fragment_server_setup) {

    private val viewModel by lazy { getActivityViewModel(ServerSetupViewModel::class.java) }
    private val adapter by lazy { ServerSetupAdapter(viewModel) }
    private var nullableBinding: FragmentServerSetupBinding? = null
    private val binding
        get() = nullableBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentServerSetupBinding.inflate(inflater, container, false).let {
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

        binding.cancel.setOnClickListener { findNavController().popBackStack() }

        binding.save.setOnClickListener {
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