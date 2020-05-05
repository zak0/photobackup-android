package com.jamitek.photosapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jamitek.photosapp.model.Photo
import com.jamitek.photosapp.networking.UrlRepo
import kotlinx.android.synthetic.main.list_item_thumbnail.view.*

class ThumbnailsAdapter(
    private val viewModel: MainViewModel,
    private val dataSet: ArrayList<Photo>
) :
    RecyclerView.Adapter<ThumbnailsAdapter.ThumbnailsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThumbnailsViewHolder {
        return ThumbnailsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_thumbnail, parent, false)
        )
    }

    override fun getItemCount(): Int = dataSet.size

    override fun onBindViewHolder(holder: ThumbnailsViewHolder, position: Int) {
        val photo = dataSet[position]
        photo.remotePhoto?.also { remotePhoto ->
            val url = UrlRepo.thumbnailUrl(remotePhoto.id)
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
