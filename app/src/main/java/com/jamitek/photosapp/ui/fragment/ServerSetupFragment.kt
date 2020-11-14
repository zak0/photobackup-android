package com.jamitek.photosapp.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import com.jamitek.photosapp.R
import com.jamitek.photosapp.extension.getActivityViewModel
import com.jamitek.photosapp.ui.adapter.ServerSetupAdapter
import com.jamitek.photosapp.ui.viewmodel.ServerSetupViewModel
import kotlinx.android.synthetic.main.fragment_server_setup.*

class ServerSetupFragment : Fragment(R.layout.fragment_server_setup) {

    private val viewModel by lazy { getActivityViewModel(ServerSetupViewModel::class.java) }
    private val adapter by lazy { ServerSetupAdapter(viewModel) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.adapter = adapter
        observe()
    }

    private fun observe() {
        viewModel.items.observe(viewLifecycleOwner) {
            adapter.notifyDataSetChanged()
        }
    }

}