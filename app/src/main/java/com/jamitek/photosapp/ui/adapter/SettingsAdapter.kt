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
import kotlinx.android.synthetic.main.list_item_settings.view.*

abstract class SettingsAdapter : RecyclerView.Adapter<SettingsAdapter.SettingsViewHolder>() {

    private companion object {
        const val TYPE_TITLE = 7
        const val TYPE_SETTING = 8
    }

    protected abstract val items: List<SettingsItem>
    protected abstract fun getItemTitle(itemKey: SettingsItemKey, context: Context): String
    protected abstract fun onItemClicked(key: SettingsItemKey)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
        return SettingsViewHolder(
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

    override fun getItemViewType(position: Int): Int =
        if (items[position].key.isTitle) TYPE_TITLE else TYPE_SETTING

    override fun onBindViewHolder(holder: SettingsViewHolder, position: Int) {
        val item = items[position]
        if (getItemViewType(position) == TYPE_TITLE) {
            bindTitle(holder, item)
        } else {
            bindSettingItem(holder, item)
        }
    }

    private fun bindTitle(holder: SettingsViewHolder, item: SettingsItem) {
        holder.itemView.title.text = getItemTitle(item.key, holder.context)
    }

    private fun bindSettingItem(holder: SettingsViewHolder, item: SettingsItem) {
        holder.itemView.title.text = getItemTitle(item.key, holder.context)
        holder.itemView.value.text = item.value()
        holder.itemView.setOnClickListener { onItemClicked(item.key) }

        // Handling of toggle, if this item is toggleable
        if (item.key.isToggleable) {
            holder.itemView.toggle.visibility = View.VISIBLE
            holder.itemView.toggle.isChecked = item.isToggled()
        } else {
            holder.itemView.toggle.visibility = View.GONE
        }
    }


    override fun getItemCount(): Int = items.size

    class SettingsViewHolder(view: View) : RecyclerView.ViewHolder(view)

}