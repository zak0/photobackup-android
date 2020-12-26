package com.jamitek.photosapp

class SettingsItem (
    val key: SettingsItemKey,
    val value: () -> String = { "Not implemented" },
    val isToggled: () -> Boolean = { false }
)