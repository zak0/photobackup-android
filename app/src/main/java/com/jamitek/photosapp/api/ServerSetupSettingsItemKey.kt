package com.jamitek.photosapp.api

import com.jamitek.photosapp.SettingsItemKey

enum class ServerSetupSettingsItemKey(
    override val isTitle: Boolean = false,
    override val isToggleable: Boolean = false
) : SettingsItemKey {
    SectionTitleAddress(isTitle = true),
    ItemAddress,
    SectionTitleCredentials(isTitle = true),
    ItemUsername,
    ItemPassword
}
