package com.jamitek.photosapp.networking

import com.jamitek.photosapp.SettingsItemKey

enum class ServerConfigSettingsItemKey(override val isTitle: Boolean = false) : SettingsItemKey {
    SectionTitleAddress(true),
    ItemAddress,
    SectionTitleCredentials(true),
    ItemUsername,
    ItemPassword
}
