package com.jamitek.photosapp.ui.adapter

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jamitek.photosapp.R
import com.jamitek.photosapp.model.RemoteMedia
import com.jamitek.photosapp.ui.viewmodel.RemoteLibraryViewModel
import kotlinx.android.synthetic.main.list_item_thumbnail.view.*

class ThumbnailsAdapter(
    private val viewModel: RemoteLibraryViewModel,
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
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_thumbnail, parent, false)
        )
    }

    override fun getItemCount(): Int = dataSet.size

    override fun onBindViewHolder(holder: ThumbnailsViewHolder, position: Int) {
        val photo = dataSet[position]

        // TODO Primarily try to use local thumbnails to prevent having to load anything over the
        //  network. Check local lib repo for this. FYI: Glide supports URIs as URLs.
        val thumbnailAddress = viewModel.authorizedThumbnailGlideUrl(photo.serverId)

        Glide
            .with(holder.itemView)
            .load(thumbnailAddress)
            .centerCrop()
            .placeholder(thumbnailPlaceholder)
            .into(holder.itemView.image)

        holder.itemView.setOnClickListener {
            viewModel.onThumbnailClicked(photo)
        }
    }

    class ThumbnailsViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
