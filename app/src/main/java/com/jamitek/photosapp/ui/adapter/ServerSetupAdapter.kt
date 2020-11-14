package com.jamitek.photosapp.ui.adapter

import android.content.Context
import com.jamitek.photosapp.R
import com.jamitek.photosapp.SettingsItemKey
import com.jamitek.photosapp.networking.ServerConfigSettingsItemKey
import com.jamitek.photosapp.ui.viewmodel.ServerSetupViewModel

class ServerSetupAdapter(private val viewModel: ServerSetupViewModel) : SettingsAdapter() {

    override fun getItemTitle(itemKey: SettingsItemKey, context: Context): String =
        context.getString(
            when (itemKey as ServerConfigSettingsItemKey) {
                ServerConfigSettingsItemKey.SectionTitleAddress -> R.string.serverConfigSectionTitleAddress
                ServerConfigSettingsItemKey.ItemAddress -> R.string.serverConfigAddress
                ServerConfigSettingsItemKey.SectionTitleCredentials -> R.string.serverConfigSectionTitleCredentials
                ServerConfigSettingsItemKey.ItemUsername -> R.string.serverConfigUsername
                ServerConfigSettingsItemKey.ItemPassword -> R.string.serverConfigPassword
            }
        )

    override fun onItemClicked(key: SettingsItemKey) {

    }

    override val items = viewModel.items.value ?: emptyList()
}
