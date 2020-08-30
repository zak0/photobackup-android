package com.jamitek.photosapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.jamitek.photosapp.R
import com.jamitek.photosapp.networking.UrlHelper
import com.jamitek.photosapp.ui.viewmodel.RemoteLibraryViewModel
import kotlinx.android.synthetic.main.view_viewer_image.view.*

class ViewerAdapter(private val viewModel: RemoteLibraryViewModel) :
    RecyclerView.Adapter<ViewerAdapter.ViewerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewerViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.view_viewer_image, parent, false)
        return ViewerViewHolder(view)
    }

    override fun getItemCount(): Int = viewModel.photos.value?.size ?: 0

    override fun onBindViewHolder(holder: ViewerViewHolder, position: Int) {
        viewModel.photos.value?.get(position)?.also { photo ->
            // TODO Primarily try to use local thumbnails to prevent having to load anything over the
            //  network. User local lib repo for this. Glide supports local file URIs directly.
            val imageAddress = UrlHelper.authorizedGlideUrl(UrlHelper.photoUrl(photo.serverId))

            Glide
                .with(holder.itemView.context)
                .load(imageAddress)
                //.format(DecodeFormat.PREFER_ARGB_8888) // TODO Test if this has an effect
                .override(Target.SIZE_ORIGINAL) // Without this, Glide downsamples the images
                .into(holder.itemView.image)

            holder.itemView.filenameLabel.text = photo.fileName

        }
    }

    class ViewerViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
