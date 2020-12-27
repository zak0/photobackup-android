package com.jamitek.photosapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jamitek.photosapp.R
import com.jamitek.photosapp.ui.viewmodel.RemoteLibraryViewModel
import kotlinx.android.synthetic.main.list_item_timeline.view.*

class TimelineAdapter(private val viewModel: RemoteLibraryViewModel) : RecyclerView.Adapter<TimelineAdapter.TimelineViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.list_item_timeline, parent, false)

        return TimelineViewHolder(view)
    }

    override fun getItemCount(): Int = viewModel.groupedMedia.value?.size ?: 0

    override fun onBindViewHolder(holder: TimelineViewHolder, position: Int) {
        viewModel.groupedMedia.value?.get(position)?.also { timelineItem ->
            holder.itemView.dateLabel.text = timelineItem.first

            holder.itemView.recycler.adapter = ThumbnailsAdapter(viewModel, timelineItem.second)
            holder.itemView.recycler.layoutManager = GridLayoutManager(holder.itemView.context, 4)
        }
    }

    class TimelineViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
