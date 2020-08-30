package com.jamitek.photosapp.extension

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jamitek.photosapp.ui.viewmodel.ViewModelFactory

fun <T : ViewModel> Fragment.getActivityViewModel(viewModelClass: Class<T>): T {
    return ViewModelProvider(
        requireActivity(),
        ViewModelFactory(requireActivity().dependencyRoot)
    ).get(viewModelClass)
}