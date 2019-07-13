package com.rafael.tcc.ui

import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place

data class Local(var latLNG: LatLng, var Nome: String, var urlImagem: String, var tipo: MutableList<Place.Type>, var endereco:String)
