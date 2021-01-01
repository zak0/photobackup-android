package com.jamitek.photosapp.ui.adapter

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jamitek.photosapp.R
import com.jamitek.photosapp.databinding.ListItemThumbnailBinding
import com.jamitek.photosapp.model.RemoteMedia
import com.jamitek.photosapp.ui.viewmodel.MediaTimelineViewModel

class ThumbnailsAdapter(
    private val viewModel: MediaTimelineViewModel,
    private val dataSet: ArrayList<RemoteMedia>
) :
    RecyclerView.Adapter<ThumbnailsAdapter.ThumbnailsViewHolder>() {

    companion object {
        var thumbnailPlaceholder: Drawable? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThumbnailsViewHolder {
        thumbnailPlaceholder ?: run {
            thumbnailPlaceholder =
                ColorDrawable(parent.context.resources.getColor(R.color.colorSecondaryText, null))
        }

        return ThumbnailsViewHolder(
            ListItemThumbnailBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = dataSet.size

    override fun onBindViewHolder(holder: ThumbnailsViewHolder, position: Int) {
        val media = dataSet[position]

        // TODO Primarily try to use local thumbnails to prevent having to load anything over the
        //  network. Check local lib repo for this. FYI: Glide supports URIs as URLs.
        val thumbnailAddress = viewModel.authorizedThumbnailGlideUrl(media.serverId)

        holder.binding.playButtonOverlay.visibility =
            if (media.type == "Video") View.VISIBLE else View.GONE

        Glide
            .with(holder.itemView)
            .load(thumbnailAddress)
            .centerCrop()
            .placeholder(thumbnailPlaceholder)
            .into(holder.binding.image)

        holder.itemView.setOnClickListener {
            viewModel.onThumbnailClicked(media)
        }
    }

    class ThumbnailsViewHolder(val binding: ListItemThumbnailBinding) :
        RecyclerView.ViewHolder(binding.root)
}
