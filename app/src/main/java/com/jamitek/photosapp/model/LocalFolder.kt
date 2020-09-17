package com.jamitek.photosapp.model

data class LocalFolder (
    /** Nice displayable name of this filder */
    val name: String,

    /** URI for this folder */
    val uriString: String,

    /** Media inside of this folder, EXCLUDING media in subfolders */
    val media: ArrayList<LocalMedia>
)