package com.jamitek.photosapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jamitek.photosapp.Event
import com.jamitek.photosapp.R
import com.jamitek.photosapp.remotelibrary.TimelineUseCase
import com.jamitek.photosapp.ui.TimelineScreenEvent

class TimelineSettingsViewModel(private val useCase: TimelineUseCase) : ViewModel() {

    /**
     * [Pair]<Title Resource ID, Icon Resource ID>
     */
    val items = listOf(
        R.string.timelineOptionBackUpNow to R.drawable.ic_backup_24dp,
        R.string.timelineOptionSettings to R.drawable.ic_settings_24
    )

    private val mutableEvent = MutableLiveData<Event<TimelineScreenEvent>>()
    val event: LiveData<Event<TimelineScreenEvent>> = mutableEvent

    fun onBackUpNowPressed() {
        emitEvent(
            if (useCase.cameraDirIsSet) {
                TimelineScreenEvent.StartBackupWorker
            } else {
                TimelineScreenEvent.NoBackupSourceToast
            }
        )
    }

    fun onSettingsPressed() {
        mutableEvent.value = Event(TimelineScreenEvent.ShowAppSettings)
    }

    private fun emitEvent(event: TimelineScreenEvent) {
        mutableEvent.value = Event(event)
    }

}