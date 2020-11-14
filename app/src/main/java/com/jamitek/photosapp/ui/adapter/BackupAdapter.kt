package com.jamitek.photosapp.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jamitek.photosapp.R
import com.jamitek.photosapp.SettingsItem
import com.jamitek.photosapp.SettingsItemKey
import com.jamitek.photosapp.backup.BackupSettingItemKey
import com.jamitek.photosapp.extension.context
import com.jamitek.photosapp.ui.viewmodel.BackupViewModel
import kotlinx.android.synthetic.main.list_item_settings.view.*
import kotlinx.android.synthetic.main.list_item_settings_section_title.view.title

class BackupAdapter(private val viewModel: BackupViewModel) :
    RecyclerView.Adapter<BackupAdapter.BackupViewHolder>() {

    private companion object {
        const val TYPE_TITLE = 7
        const val TYPE_SETTING = 8
    }

    private val items = viewModel.items.value ?: emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BackupViewHolder {
        return BackupViewHolder(
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

    override fun onBindViewHolder(holder: BackupViewHolder, position: Int) {
        val item = items[position]
        if (getItemViewType(position) == TYPE_TITLE) {
            bindTitle(holder, item)
        } else {
            bindSettingItem(holder, item)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position].key.isTitle) TYPE_TITLE else TYPE_SETTING
    }

    override fun getItemCount(): Int = items.size

    private fun bindTitle(holder: BackupViewHolder, item: SettingsItem) {
        holder.itemView.title.text = item.key.asTitle(holder.context)
    }

    private fun bindSettingItem(holder: BackupViewHolder, item: SettingsItem) {
        holder.itemView.title.text = item.key.asTitle(holder.context)
        holder.itemView.value.text = item.value()
        holder.itemView.setOnClickListener { viewModel.onItemClicked(item.key as BackupSettingItemKey) }
    }

    private fun getTitle(itemKey: SettingsItemKey, context: Context): String = itemKey.asTitle(context)

    class BackupViewHolder(view: View) : RecyclerView.ViewHolder(view)
}

private fun SettingsItemKey.asTitle(context: Context): String = context.getString(
    when (this as BackupSettingItemKey) {
        BackupSettingItemKey.SECTION_TITLE_BACKUP_STATUS -> R.string.backupSectionTitleBackupStatus
        BackupSettingItemKey.ITEM_PHOTOS_STATUS -> R.string.backupPhotosStatus
        BackupSettingItemKey.ITEM_BACKUP_STATUS -> R.string.backupBackupStatus
        BackupSettingItemKey.ITEM_CAMERA_DIR -> R.string.backupCameraDir
        BackupSettingItemKey.SECTION_TITLE_CONNECTION_STATUS -> R.string.backupSectionTitleConnectionStatus
        BackupSettingItemKey.ITEM_CONNECTION_STATUS -> R.string.backupConnectionStatus
        BackupSettingItemKey.ITEM_SERVER_DETAILS -> R.string.backupServerDetails
        BackupSettingItemKey.SECTION_TITLE_SERVER_ADMIN -> R.string.backupSectionTitleServerAdmin
        BackupSettingItemKey.ITEM_RESCAN_LIBRARY -> R.string.backupRescanLibrary
    }
)