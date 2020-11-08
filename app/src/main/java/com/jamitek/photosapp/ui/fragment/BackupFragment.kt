package com.jamitek.photosapp.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.jamitek.photosapp.R
import com.jamitek.photosapp.extension.getActivityViewModel
import com.jamitek.photosapp.ui.viewmodel.BackupViewModel
import kotlinx.android.synthetic.main.fragment_backup.*

class BackupFragment : Fragment(R.layout.fragment_backup) {

    private val viewModel by lazy { getActivityViewModel(BackupViewModel::class.java) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe()
    }

    private fun observe() {

    }
}