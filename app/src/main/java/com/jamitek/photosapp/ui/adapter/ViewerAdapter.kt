package com.jamitek.photosapp.ui.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.jamitek.photosapp.R
import com.jamitek.photosapp.model.RemoteMedia
import com.jamitek.photosapp.networking.UrlHelper
import com.jamitek.photosapp.ui.viewmodel.RemoteLibraryViewModel
import kotlinx.android.synthetic.main.view_viewer_image.view.*

class ViewerAdapter(private val viewModel: RemoteLibraryViewModel) :
    RecyclerView.Adapter<ViewerAdapter.ViewerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewerViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.view_viewer_image, parent, false)
        return ViewerViewHolder(view)
    }

    override fun getItemCount(): Int = viewModel.photos.value?.size ?: 0

    override fun onBindViewHolder(holder: ViewerViewHolder, position: Int) {
        viewModel.photos.value?.get(position)?.also { photo ->
            // Primarily try to use local thumbnails to prevent having to load anything over the
            // network.
            val imageAddress =
                photo.localUriString?.let { Uri.parse(it) } ?: photo.serverId?.let {
                    val url = UrlHelper.photoUrl(it)
                    UrlHelper.authorizedGlideUrl(url)
                }

            Glide
                .with(holder.itemView.context)
                .load(imageAddress)
                //.format(DecodeFormat.PREFER_ARGB_8888) // TODO Test if this has an effect
                .override(Target.SIZE_ORIGINAL) // Without this, Glide downsamples the images
                .into(holder.itemView.image)

            holder.itemView.filenameLabel.text = photo.fileName

            holder.itemView.localFileIcon.visibility =
                if (photo.isLocal) View.VISIBLE else View.GONE
            holder.itemView.remoteFileIcon.visibility =
                if (photo.isRemote) View.VISIBLE else View.GONE
            holder.itemView.uploadButton.visibility =
                if (photo.isLocal && photo.status != RemoteMedia.Status.READY) View.VISIBLE else View.GONE

            setupUploadButton(holder, photo)
        }
    }

    /**
     * Just a dummy to test upload implementation...
     */
    private fun setupUploadButton(viewHolder: ViewerViewHolder, remoteMedia: RemoteMedia) {
        val context = viewHolder.itemView.context

        // TODO Upload a photo? (Probably not needed here in this class at all, at least for v1)
        viewHolder.itemView.uploadButton.setOnClickListener {
            val uploadLambda = { serverId: Int ->
                remoteMedia.serverId = serverId
//                ApiClient.uploadPhoto(context, photo) { success ->
//                    Toast.makeText(
//                        context,
//                        "Upload successful: $success",
//                        Toast.LENGTH_LONG
//                    ).show()
//                }
            }

            // POST metadata only if needed
            if (remoteMedia.isRemote) {
                // Metadata is already at the server
                remoteMedia.serverId?.also(uploadLambda)
            } else {
                // Server does not know of this photo yet
//                ApiClient.postPhotoMetaData(photo) { serverId ->
//                    serverId?.also {
//                        Toast.makeText(context, "Metadata POST successful, serverId: $serverId", Toast.LENGTH_LONG).show()
//                        uploadLambda(it)
//                    } ?: run {
//                        Toast.makeText(context, "Metadata POST failed...", Toast.LENGTH_LONG).show()
//                    }
//                }
            }
        }
    }

    class ViewerViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
