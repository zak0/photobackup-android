package com.jamitek.photosapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jamitek.photosapp.databinding.ListItemTimelineSettingsBinding
import com.jamitek.photosapp.extension.context
import com.jamitek.photosapp.ui.viewmodel.TimelineSettingsViewModel

class TimelineSettingsAdapter(private val viewModel: TimelineSettingsViewModel) :
    RecyclerView.Adapter<TimelineSettingsAdapter.TimelineSettingsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineSettingsViewHolder =
        TimelineSettingsViewHolder(
            ListItemTimelineSettingsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: TimelineSettingsViewHolder, position: Int) {
        val (labelRes, iconRes) = viewModel.items[position]
        Glide.with(holder.context).load(iconRes).into(holder.binding.icon)
        holder.binding.label.setText(labelRes)
        holder.binding.root.setOnClickListener {
            when(position) {
                0 -> viewModel.onBackUpNowPressed()
                1 -> viewModel.onSettingsPressed()
                else -> error("Unhandled timeline settings click on index $position!")
            }
        }
    }

    override fun getItemCount(): Int = viewModel.items.size

    class TimelineSettingsViewHolder(val binding: ListItemTimelineSettingsBinding) :
        RecyclerView.ViewHolder(binding.root)

}
