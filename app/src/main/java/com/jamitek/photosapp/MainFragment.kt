package com.jamitek.photosapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.jamitek.photosapp.networking.ApiClient
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment(R.layout.fragment_main) {

    companion object {
        private const val TAG = "MainFragment"
    }

    private val adapter: ThumbnailsAdapter by lazy { ThumbnailsAdapter(viewModel) }
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        recycler.adapter = adapter
        recycler.layoutManager = GridLayoutManager(requireContext(), 3)

        get_all_button.setOnClickListener {
            ApiClient.getAllPhotos() { photoIds ->
                adapter.dataSet = ArrayList(photoIds)
                adapter.notifyDataSetChanged()
            }
        }

        observe()
    }

    private fun observe() {
        viewModel.selectedPhoto.observe(viewLifecycleOwner, Observer {
            it?.let { photoId ->
                findNavController().navigate(R.id.action_mainFragment_to_viewerFragment)
            }
        })
    }
}
