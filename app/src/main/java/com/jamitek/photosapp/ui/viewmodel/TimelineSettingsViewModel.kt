package com.jamitek.photosapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jamitek.photosapp.Event
import com.jamitek.photosapp.R
import com.jamitek.photosapp.ui.TimelineScreenEvent

class TimelineSettingsViewModel : ViewModel() {

    /**
     * [Pair]<Title Resource ID, Icon Resource ID>
     */
    val items = listOf(
        R.string.timelineOptionBackUpNow to R.drawable.ic_backup_24dp,
        R.string.timelineOptionSettings to R.drawable.ic_settings_24
    )

    val mutableEvent = MutableLiveData<Event<TimelineScreenEvent>>()
    val event: LiveData<Event<TimelineScreenEvent>> = mutableEvent

    fun onBackUpNowPressed() {
        mutableEvent.value = Event(TimelineScreenEvent.StartBackupWorker)
    }

    fun onSettingsPressed() {
        mutableEvent.value = Event(TimelineScreenEvent.ShowAppSettings)
    }

}