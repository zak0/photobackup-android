package com.jamitek.photosapp.ui.adapter

import android.content.Context
import com.jamitek.photosapp.R
import com.jamitek.photosapp.SettingsItemKey
import com.jamitek.photosapp.api.ServerSetupSettingsItemKey
import com.jamitek.photosapp.ui.viewmodel.ServerSetupViewModel

class ServerSetupAdapter(private val viewModel: ServerSetupViewModel) : SettingsAdapter() {

    override fun getItemTitle(itemKey: SettingsItemKey, context: Context): String =
        context.getString(
            when (itemKey as ServerSetupSettingsItemKey) {
                ServerSetupSettingsItemKey.SectionTitleAddress -> R.string.serverConfigSectionTitleAddress
                ServerSetupSettingsItemKey.ItemAddress -> R.string.serverConfigAddress
                ServerSetupSettingsItemKey.SectionTitleCredentials -> R.string.serverConfigSectionTitleCredentials
                ServerSetupSettingsItemKey.ItemUsername -> R.string.serverConfigUsername
                ServerSetupSettingsItemKey.ItemPassword -> R.string.serverConfigPassword
            }
        )

    override fun onItemClicked(key: SettingsItemKey) {
        viewModel.onItemClicked(key as ServerSetupSettingsItemKey)
    }

    override fun onItemToggled(key: SettingsItemKey, isChecked: Boolean) = Unit

    override val items = viewModel.items.value ?: emptyList()
}
