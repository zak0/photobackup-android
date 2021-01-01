package com.jamitek.photosapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jamitek.photosapp.databinding.ListItemTimelineBinding
import com.jamitek.photosapp.ui.viewmodel.MediaTimelineViewModel

class TimelineAdapter(private val viewModel: MediaTimelineViewModel) :
    RecyclerView.Adapter<TimelineAdapter.TimelineViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineViewHolder {
        return TimelineViewHolder(
            ListItemTimelineBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = viewModel.groupedMedia.value?.size ?: 0

    override fun onBindViewHolder(holder: TimelineViewHolder, position: Int) {
        viewModel.groupedMedia.value?.get(position)?.also { timelineItem ->
            holder.binding.dateLabel.text = timelineItem.first

            holder.binding.recycler.adapter = ThumbnailsAdapter(viewModel, timelineItem.second)
            holder.binding.recycler.layoutManager = GridLayoutManager(holder.itemView.context, 4)
        }
    }

    class TimelineViewHolder(val binding: ListItemTimelineBinding) :
        RecyclerView.ViewHolder(binding.root)
}
