package com.jamitek.photosapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jamitek.photosapp.R
import com.jamitek.photosapp.ui.viewmodel.LocalFoldersViewModel
import kotlinx.android.synthetic.main.list_item_local_folder.view.*

class LocalFoldersAdapter(
    private val viewModel: LocalFoldersViewModel
) : RecyclerView.Adapter<LocalFoldersAdapter.LocalFoldersViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalFoldersViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_local_folder, parent, false)
        return LocalFoldersViewHolder(view)
    }

    override fun getItemCount(): Int = viewModel.localFolders.value?.size ?: 0

    override fun onBindViewHolder(holder: LocalFoldersViewHolder, position: Int) {
        viewModel.localFolders.value?.get(position)?.also { localFolder ->
            holder.itemView.folderName.text = localFolder.name
            holder.itemView.mediaCount.text = "${localFolder.media.size}"
            holder.itemView.setOnClickListener {  }
        }
    }

    class LocalFoldersViewHolder(view: View) : RecyclerView.ViewHolder(view)
}