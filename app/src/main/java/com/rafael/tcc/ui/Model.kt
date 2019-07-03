package com.rafael.tcc.ui

import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.android.libraries.places.api.model.Place

data class Local(var latLNG: LatLng, var Nome: String, var urlImagem: MutableList<PhotoMetadata>, var tipo: MutableList<Place.Type>, var endereco:String,
                 var fone: String)
