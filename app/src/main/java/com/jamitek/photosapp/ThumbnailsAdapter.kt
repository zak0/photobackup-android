package com.jamitek.photosapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jamitek.photosapp.networking.UrlRepo
import kotlinx.android.synthetic.main.list_item_thumbnail.view.*

class ThumbnailsAdapter(private val viewModel: MainViewModel) :
    RecyclerView.Adapter<ThumbnailsAdapter.ThumbnailsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThumbnailsViewHolder {
        return ThumbnailsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_thumbnail, parent, false)
        )
    }

    override fun getItemCount(): Int = viewModel.photos.value?.size ?: 0

    override fun onBindViewHolder(holder: ThumbnailsViewHolder, position: Int) {
        viewModel.photos.value?.get(position)?.let { photo ->
            val url = UrlRepo.thumbnailUrl(photo.id)
            val glideUrl = UrlRepo.authorizedGlideUrl(url)
            Glide
                .with(holder.itemView)
                .load(glideUrl)
                .centerCrop()
                .into(holder.itemView.image)

            holder.itemView.setOnClickListener {
                viewModel.onThumbnailClicked(photo)
            }
        }
    }

    class ThumbnailsViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
