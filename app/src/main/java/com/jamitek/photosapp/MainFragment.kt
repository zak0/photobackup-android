package com.jamitek.photosapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.jamitek.photosapp.networking.ApiClient
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment(R.layout.fragment_main) {

    companion object {
        private const val TAG = "MainFragment"
    }

    private val adapter: TimelineAdapter by lazy { TimelineAdapter(viewModel) }
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        recycler.adapter = adapter
        //recycler.addOnScrollListener(ThumbnailsOnScrollListener(viewModel)) // TODO Uncomment when lazy loading is really built

        observe()

        ApiClient.getAllPhotos() { photos ->
            viewModel.onPhotosLoaded(photos)
        }
    }

    private fun observe() {
        viewModel.selectedPhoto.observe(viewLifecycleOwner, Observer {
            it?.let { photo ->
                findNavController().navigate(R.id.action_mainFragment_to_viewerFragment)
            }
        })

        viewModel.photosPerDate.observe(viewLifecycleOwner, Observer {
            adapter.notifyDataSetChanged()
        })

        // TODO Uncomment when lazy loading is added
//        viewModel.nextGetOffset.observe(viewLifecycleOwner, Observer {
//            it?.let { offset ->
//                ApiClient.getAllPhotos(offset) { photos ->
//                    viewModel.onPhotosLoaded(photos)
//                }
//            }
//        })
    }
}
