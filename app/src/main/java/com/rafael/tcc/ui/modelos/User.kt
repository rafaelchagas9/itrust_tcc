package com.rafael.tcc.ui.modelos

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(val Email:String, val UrlFoto: String, val uid:String):Parcelable{
    constructor() : this("", "","")
}