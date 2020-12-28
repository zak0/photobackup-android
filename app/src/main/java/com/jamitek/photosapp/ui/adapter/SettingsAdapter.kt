package com.jamitek.photosapp.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.jamitek.photosapp.SettingsItem
import com.jamitek.photosapp.SettingsItemKey
import com.jamitek.photosapp.databinding.ListItemSettingsBinding
import com.jamitek.photosapp.databinding.ListItemSettingsSectionTitleBinding

abstract class SettingsAdapter :
    RecyclerView.Adapter<SettingsAdapter.SettingsViewHolder<ViewBinding>>() {

    private companion object {
        const val TYPE_TITLE = 7
        const val TYPE_SETTING = 8
    }

    protected abstract val items: List<SettingsItem>
    protected abstract fun getItemTitle(itemKey: SettingsItemKey, context: Context): String
    protected abstract fun onItemClicked(key: SettingsItemKey)
    protected abstract fun onItemToggled(key: SettingsItemKey, isChecked: Boolean)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SettingsViewHolder<ViewBinding> {
        val inflater = LayoutInflater.from(parent.context)
        val binding = when (viewType) {
            TYPE_TITLE -> ListItemSettingsSectionTitleBinding.inflate(inflater, parent, false)
            TYPE_SETTING -> ListItemSettingsBinding.inflate(inflater, parent, false)

            else -> error("Unknown viewType: $viewType")
        }
        return SettingsViewHolder(binding)
    }

    override fun getItemViewType(position: Int): Int =
        if (items[position].key.isTitle) TYPE_TITLE else TYPE_SETTING

    override fun onBindViewHolder(holder: SettingsViewHolder<ViewBinding>, position: Int) {
        val item = items[position]
        if (getItemViewType(position) == TYPE_TITLE) {
            bindTitle(holder.binding as ListItemSettingsSectionTitleBinding, item)
        } else {
            bindSettingItem(holder.binding as ListItemSettingsBinding, item)
        }
    }

    private fun bindTitle(binding: ListItemSettingsSectionTitleBinding, item: SettingsItem) {
        binding.title.text = getItemTitle(item.key, binding.root.context)
    }

    private fun bindSettingItem(binding: ListItemSettingsBinding, item: SettingsItem) {
        binding.title.text = getItemTitle(item.key, binding.root.context)
        binding.value.text = item.value()

        val isToggleable = item.key.isToggleable
        binding.root.setOnClickListener {
            if (isToggleable) {
                // Clicking on an toggleable setting item will just toggle it
                binding.toggle.toggle()
            } else {
                // Clicking on a "normal" setting item will trigger its onClick action
                onItemClicked(item.key)
            }
        }

        // Handling of toggle, if this item is toggleable
        if (isToggleable) {
            binding.toggle.setOnCheckedChangeListener(null)
            binding.toggle.visibility = View.VISIBLE
            binding.toggle.isChecked = item.isToggled()
            binding.toggle.setOnCheckedChangeListener { _, isChecked ->
                onItemToggled(
                    item.key,
                    isChecked
                )
                notifyItemChanged(items.indexOf(item))
            }
        } else {
            binding.toggle.visibility = View.GONE
        }
    }


    override fun getItemCount(): Int = items.size

    class SettingsViewHolder<T : ViewBinding>(val binding: T) :
        RecyclerView.ViewHolder(binding.root)

}