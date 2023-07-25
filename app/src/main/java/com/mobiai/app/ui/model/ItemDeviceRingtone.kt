package com.mobiai.app.ui.model

import android.net.Uri

class ItemDeviceRingtone(
    val id: Int,
    val name: String,
    val artistName: String = "",
    val uri: Uri,
    val durationText : String = "--",
    val type: String,
    var isChoose: Boolean = false,
    var isPlaying: Boolean = false,
    val isShowedProgress: Boolean = false,
    )