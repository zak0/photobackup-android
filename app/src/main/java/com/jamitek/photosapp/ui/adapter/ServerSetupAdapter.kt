package com.jamitek.photosapp.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jamitek.photosapp.R
import com.jamitek.photosapp.SettingsItem
import com.jamitek.photosapp.SettingsItemKey
import com.jamitek.photosapp.extension.context
import com.jamitek.photosapp.networking.ServerConfigSettingsItemKey
import com.jamitek.photosapp.ui.viewmodel.ServerSetupViewModel
import kotlinx.android.synthetic.main.list_item_settings.view.*

class ServerSetupAdapter(private val viewModel: ServerSetupViewModel) :
    RecyclerView.Adapter<ServerSetupAdapter.ServerSetupViewHolder>() {

    private companion object {
        const val TYPE_TITLE = 7
        const val TYPE_SETTING = 8
    }

    private val items = viewModel.items.value ?: emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServerSetupViewHolder {
        return ServerSetupViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(
                    if (viewType == TYPE_TITLE) {
                        R.layout.list_item_settings_section_title
                    } else {
                        R.layout.list_item_settings
                    },
                    parent,
                    false
                )
        )
    }

    override fun onBindViewHolder(holder: ServerSetupViewHolder, position: Int) {
        val item = items[position]
        if (getItemViewType(position) == TYPE_TITLE) {
            bindTitle(holder, item)
        } else {
            bindSettingItem(holder, item)
        }
    }

    private fun bindTitle(holder: ServerSetupViewHolder, item: SettingsItem) {
        holder.itemView.title.text = getTitle(item.key, holder.context)
    }

    private fun bindSettingItem(holder: ServerSetupViewHolder, item: SettingsItem) {
        holder.itemView.title.text = getTitle(item.key, holder.context)
        holder.itemView.value.text = item.value()
        holder.itemView.setOnClickListener {
        //    viewModel.onItemClicked(item.key as BackupSettingItemKey)
        }
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        return if (items[position].key.isTitle) TYPE_TITLE else TYPE_SETTING
    }

    private fun getTitle(itemKey: SettingsItemKey, context: Context): String =
        context.getString(
            when (itemKey as ServerConfigSettingsItemKey) {
                ServerConfigSettingsItemKey.SectionTitleAddress -> R.string.serverConfigSectionTitleAddress
                ServerConfigSettingsItemKey.ItemAddress -> R.string.serverConfigAddress
                ServerConfigSettingsItemKey.SectionTitleCredentials -> R.string.serverConfigSectionTitleCredentials
                ServerConfigSettingsItemKey.ItemUsername -> R.string.serverConfigUsername
                ServerConfigSettingsItemKey.ItemPassword -> R.string.serverConfigPassword
            }
        )

    class ServerSetupViewHolder(view: View) : RecyclerView.ViewHolder(view)
}