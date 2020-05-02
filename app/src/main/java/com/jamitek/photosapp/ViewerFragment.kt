package com.jamitek.photosapp

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
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
                .thumbnail(0.2f)
                .into(image)

            Toast.makeText(requireContext(), photo.dateTimeOriginal, Toast.LENGTH_LONG).show()
        }

        viewModel.onImageViewerOpened()
    }

}
