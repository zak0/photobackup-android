package com.jamitek.photosapp

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import kotlinx.android.synthetic.main.fragment_viewer.*

class ViewerFragment : Fragment(R.layout.fragment_viewer) {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val photoId = viewModel.selectedPhoto.value

        viewModel.onImageViewerOpened()

        val url = "http://192.168.1.105:3000/media/$photoId/file"
        val glideUrl = GlideUrl(url, LazyHeaders.Builder().addHeader("Authorization", "Basic amFha2tvYWRtaW46U2FsYWluZW5TYW5hMTMyNCFA").build())
        Glide
            .with(requireActivity())
            .load(glideUrl)
            .thumbnail(0.2f)
            .into(image)
    }

}
