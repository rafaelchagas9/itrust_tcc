package com.iTrust.tcc.ui.modelos

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(val Nome_Completo:String, val UrlFoto: String, val uid:String):Parcelable{
    constructor() : this("", "","")
}