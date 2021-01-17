package com.jamitek.photosapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.jamitek.photosapp.R
import com.jamitek.photosapp.databinding.ViewViewerImageBinding
import com.jamitek.photosapp.extension.context
import com.jamitek.photosapp.ui.viewmodel.TimelineViewModel

class ViewerAdapter(
    private val viewModel: TimelineViewModel,
    private val popBackStack: () -> Unit
) :
    RecyclerView.Adapter<ViewerAdapter.ViewerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewerViewHolder {
        return ViewerViewHolder(
            ViewViewerImageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = viewModel.allMedia.value?.size ?: 0

    override fun onBindViewHolder(holder: ViewerViewHolder, position: Int) {
        viewModel.allMedia.value?.get(position)?.also { media ->
            // TODO Primarily try to use local thumbnails to prevent having to load anything over the
            //  network. User local lib repo for this. Glide supports local file URIs directly.

            val mediaAddress = if (media.type == "Picture") {
                viewModel.authorizedImageGlideUrl(media.serverId)
            } else if (media.type == "Video") {
                Toast.makeText(
                    holder.context,
                    R.string.viewerVideoPlaybackNotSupported,
                    Toast.LENGTH_LONG
                ).show()

                // TODO Actually play videos. Now just displays video thumbnail
                viewModel.authorizedThumbnailGlideUrl(media.serverId)
            } else {
                error("Unknown media type '${media.type}'.")
            }

            Glide
                .with(holder.context)
                .load(mediaAddress)
                //.format(DecodeFormat.PREFER_ARGB_8888) // TODO Test if this has an effect
                .override(Target.SIZE_ORIGINAL) // Without this, Glide downsamples the images
                .into(holder.binding.image)

            holder.binding.filenameLabel.text = media.fileName
            holder.binding.backButton.setOnClickListener { popBackStack() }

        }
    }

    class ViewerViewHolder(val binding: ViewViewerImageBinding) :
        RecyclerView.ViewHolder(binding.root)
}
