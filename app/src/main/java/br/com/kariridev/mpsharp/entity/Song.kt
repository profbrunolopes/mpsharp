package br.com.kariridev.mpsharp.entity

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Song (
    val id:Long,
    val name:String,
    val duration:Long,
    val uri: Uri
) : Parcelable