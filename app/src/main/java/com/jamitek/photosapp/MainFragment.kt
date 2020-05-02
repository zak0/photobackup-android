package com.jamitek.photosapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.jamitek.photosapp.networking.ApiClient
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment(R.layout.fragment_main) {

    companion object {
        private const val TAG = "MainFragment"
        fun newInstance() = MainFragment()
    }

    private val adapter: ThumbnailsAdapter by lazy { ThumbnailsAdapter() }
    private lateinit var viewModel: MainViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        recycler.adapter = adapter
        recycler.layoutManager = GridLayoutManager(requireContext(), 3)

        get_all_button.setOnClickListener {
            ApiClient.getAllPhotos() { photoIds ->
                adapter.dataSet = ArrayList(photoIds)
                adapter.notifyDataSetChanged()
            }
        }

    }
}
