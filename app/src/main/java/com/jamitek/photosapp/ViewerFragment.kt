package com.jamitek.photosapp

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.jamitek.photosapp.networking.UrlRepo
import kotlinx.android.synthetic.main.fragment_viewer.*

class ViewerFragment : Fragment(R.layout.fragment_viewer) {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.selectedPhoto.value?.let { photo ->
            val url = UrlRepo.photoUrl(photo.id)
            val glideUrl = UrlRepo.authorizedGlideUrl(url)
            Glide
                .with(requireActivity())
                .load(glideUrl)
                //.format(DecodeFormat.PREFER_ARGB_8888) // TODO Test if this has an effect
                .override(Target.SIZE_ORIGINAL) // Without this, Glide downsamples the images
                .into(image)
        }

        viewModel.onImageViewerOpened()
    }

}
