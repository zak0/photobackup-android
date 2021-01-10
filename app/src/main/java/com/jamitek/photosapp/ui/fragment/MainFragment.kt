package com.jamitek.photosapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.jamitek.photosapp.R
import com.jamitek.photosapp.databinding.FragmentMainBinding
import com.jamitek.photosapp.extension.getActivityViewModel
import com.jamitek.photosapp.locallibrary.LocalLibraryScanner
import com.jamitek.photosapp.ui.adapter.TimelineAdapter
import com.jamitek.photosapp.ui.viewmodel.MediaTimelineViewModel
import com.jamitek.photosapp.util.DateUtil
import com.jamitek.photosapp.worker.BackupWorker

class MainFragment : Fragment(R.layout.fragment_main) {

    companion object {
        private const val TAG = "MainFragment"
    }

    private val adapter: TimelineAdapter by lazy { TimelineAdapter(viewModel) }
    private val viewModel: MediaTimelineViewModel by lazy {
        getActivityViewModel(
            MediaTimelineViewModel::class.java
        )
    }
    private var nullableBinding: FragmentMainBinding? = null
    private val binding
        get() = nullableBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentMainBinding.inflate(inflater, container, false).let {
        nullableBinding = it
        binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        nullableBinding = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.recycler.adapter = adapter
        //recycler.addOnScrollListener(ThumbnailsOnScrollListener(viewModel)) // TODO Uncomment when lazy loading is really built
        observe()
        viewModel.refreshRemotePhotos()

        binding.expandCollapseButton.setOnClickListener {
            toggleActionBarHeight()
        }

        binding.backupButton.setOnClickListener {

            // TODO Start the worker instead. This is here just for debugging purposes!!!
            LocalLibraryScanner(requireContext()).iterateCameraDir(true) {}
            //BackupWorker.startNow(requireContext())
        }

        binding.settingsButton.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_appSettingsFragment)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (viewModel.urlIsSet) {
            binding.errorText.visibility = View.GONE
        } else {
            binding.errorText.visibility = View.VISIBLE
            binding.errorText.setText(R.string.errorServerUrlNotSet)
        }
    }

    private fun observe() {
        viewModel.selectedRemoteMedia.observe(viewLifecycleOwner, Observer {
            it?.let { photo ->
                findNavController().navigate(R.id.action_mainFragment_to_viewerFragment)
            }
        })

        viewModel.groupedMedia.observe(viewLifecycleOwner, {
            adapter.notifyDataSetChanged()
        })

        viewModel.lastBackupTimestamp.observe(viewLifecycleOwner, {
            binding.subtitle.text = if (it > 0) {
                getString(R.string.timelineLastBackUpTime, DateUtil.timestampToDateString(it, "d MMM yyyy, HH:mm"))
            } else {
                getString(R.string.timelineLastBackUpNever)
            }
        })

        // TODO Uncomment when lazy loading is added
//        viewModel.nextGetOffset.observe(viewLifecycleOwner, Observer {
//            it?.let { offset ->
//                ApiClient.getAllPhotos(offset) { photos ->
//                    viewModel.onPhotosLoaded(photos)
//                }
//            }
//        })
    }

    private fun toggleActionBarHeight() {
        val newVisibility =
            if (binding.backupButton.visibility == View.GONE) View.VISIBLE else View.GONE

        val startRotation = binding.expandCollapseButton.rotation
        val endRotation = if (startRotation == -180f) 0f else -180f

        binding.expandCollapseButton.animate().apply {
            rotation(endRotation)
            duration = 150L
            start()
        }

        binding.settingsButton.visibility = newVisibility
        binding.backupButton.visibility = newVisibility
        binding.settingsButtonLabel.visibility = newVisibility
        binding.backButtonLabel.visibility = newVisibility
    }
}
