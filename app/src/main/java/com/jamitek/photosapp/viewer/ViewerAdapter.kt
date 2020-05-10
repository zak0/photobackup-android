package com.jamitek.photosapp.viewer

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.jamitek.photosapp.MainViewModel
import com.jamitek.photosapp.R
import com.jamitek.photosapp.networking.UrlHelper
import kotlinx.android.synthetic.main.view_viewer_image.view.*

class ViewerAdapter(private val viewModel: MainViewModel) :
    RecyclerView.Adapter<ViewerAdapter.ViewerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewerViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.view_viewer_image, parent, false)
        return ViewerViewHolder(view)
    }

    override fun getItemCount(): Int = viewModel.photos.value?.size ?: 0

    override fun onBindViewHolder(holder: ViewerViewHolder, position: Int) {
        viewModel.photos.value?.get(position)?.also { photo ->
            // Primarily try to use local thumbnails to prevent having to load anything over the
            // network.
            val imageAddress =
                photo.localUriString?.let { Uri.parse(it) } ?: photo.serverId?.let {
                    val url = UrlHelper.photoUrl(it)
                    UrlHelper.authorizedGlideUrl(url)
                }

            Glide
                .with(holder.itemView.context)
                .load(imageAddress)
                //.format(DecodeFormat.PREFER_ARGB_8888) // TODO Test if this has an effect
                .override(Target.SIZE_ORIGINAL) // Without this, Glide downsamples the images
                .placeholder(R.drawable.ic_broken_image_24dp)
                .into(holder.itemView.image)
        }
    }

    class ViewerViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
