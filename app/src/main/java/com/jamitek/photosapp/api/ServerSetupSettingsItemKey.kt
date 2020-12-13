package com.jamitek.photosapp.api

import com.jamitek.photosapp.SettingsItemKey

enum class ServerSetupSettingsItemKey(override val isTitle: Boolean = false) : SettingsItemKey {
    SectionTitleAddress(true),
    ItemAddress,
    SectionTitleCredentials(true),
    ItemUsername,
    ItemPassword
}
