package com.jamitek.photosapp.ui

import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jamitek.photosapp.ui.viewmodel.MediaTimelineViewModel

class ThumbnailsOnScrollListener(private val viewModel: MediaTimelineViewModel) : RecyclerView.OnScrollListener() {
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        (recyclerView.layoutManager as? GridLayoutManager)?.let { gridLayoutManager ->
            val lastVisible = gridLayoutManager.findLastVisibleItemPosition()
            val itemCount = recyclerView.adapter?.itemCount ?: Integer.MAX_VALUE

            Log.d("XXX", "itemCount: $itemCount, lastVisible: $lastVisible")

            if (itemCount - lastVisible < 6) {
                //viewModel.onShouldLoadMorePhotos()
                Log.d("XXX", "should load more")
            }
        }
    }
}