package com.jamitek.photosapp.viewer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.jamitek.photosapp.MainViewModel
import com.jamitek.photosapp.R
import com.jamitek.photosapp.networking.UrlRepo
import kotlinx.android.synthetic.main.view_viewer_image.view.*

class ViewerAdapter(private val viewModel: MainViewModel) : RecyclerView.Adapter<ViewerAdapter.ViewerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_viewer_image, parent, false)
        return ViewerViewHolder(view)
    }

    override fun getItemCount(): Int = viewModel.photos.value?.size ?: 0

    override fun onBindViewHolder(holder: ViewerViewHolder, position: Int) {
        viewModel.photos.value?.get(position)?.remotePhoto?.also { remotePhoto ->
            val url = UrlRepo.photoUrl(remotePhoto.id)
            val glideUrl = UrlRepo.authorizedGlideUrl(url)
            Glide
                .with(holder.itemView.context)
                .load(glideUrl)
                //.format(DecodeFormat.PREFER_ARGB_8888) // TODO Test if this has an effect
                .override(Target.SIZE_ORIGINAL) // Without this, Glide downsamples the images
                .into(holder.itemView.image)
        }
    }

    class ViewerViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
