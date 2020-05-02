package com.jamitek.photosapp

import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ThumbnailsOnScrollListener(private val viewModel: MainViewModel) : RecyclerView.OnScrollListener() {
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