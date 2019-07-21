package com.rafael.tcc.ui.fragmentos

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.dynamic.SupportFragmentWrapper
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.net.PlacesClient

import com.rafael.tcc.R
import kotlinx.android.synthetic.main.activity_search.*
import android.widget.RelativeLayout
import com.google.android.gms.location.*
import com.rafael.tcc.ui.atividades.MainActivity
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.mancj.materialsearchbar.MaterialSearchBar
import com.rafael.tcc.ui.atividades.PermissaoActivity
import com.rafael.tcc.ui.atividades.SearchActivity
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.android.synthetic.main.fragment_map.view.*
import kotlinx.android.synthetic.main.activity_search.view.*


class MapFragment : Fragment(), OnMapReadyCallback {

    var mMap: GoogleMap? = null
    var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    var placesClient: PlacesClient? = null

    var mLastKnowLocation: Location? = null
    var locationCallback: LocationCallback? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_map, container, false)

        var mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        mFusedLocationProviderClient = (LocationServices.getFusedLocationProviderClient(activity as Activity))
        Places.initialize(activity as Activity, "AIzaSyDcKodtmFJ9lclROlcSifa8coLbwDyGCBs")
        placesClient = Places.createClient(activity as Activity)
        getDeviceLocation()
        val token: AutocompleteSessionToken = AutocompleteSessionToken.newInstance()
        view.btn_iniciar_pesquisa.setOnClickListener{
            val intent = Intent(activity, SearchActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        return view
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap?) {
        MapsInitializer.initialize(context)
        mMap = googleMap
        var mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        val mapView = mapFragment?.view
        mMap?.isMyLocationEnabled = true
        mMap?.uiSettings?.isMyLocationButtonEnabled = true
        if (mapView != null) {
            val locationButton = (mapView.findViewById<View>(Integer.parseInt("1")).parent as View).findViewById<View>(Integer.parseInt("2"))
            val rlp = locationButton.layoutParams as RelativeLayout.LayoutParams
            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
            rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
            rlp.setMargins(0, 0, 200, 200)
        }
    }

    @SuppressLint("MissingPermission")
    fun getDeviceLocation() {
        mFusedLocationProviderClient?.lastLocation
                ?.addOnSuccessListener { location: Location? ->
                    if (location!=null) {
                        mLastKnowLocation = location
                        if (mLastKnowLocation != null) {
                            mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(mLastKnowLocation!!.latitude,
                                    mLastKnowLocation!!.longitude), 18F))
                        }
                    }else{
                            val locationRequest = LocationRequest.create()?.apply {
                                interval = 10000
                                fastestInterval = 5000
                                priority = LocationRequest.PRIORITY_HIGH_ACCURACY

                                locationCallback = object : LocationCallback() {
                                    override fun onLocationResult(locationResult: LocationResult?) {
                                        super.onLocationResult(locationResult)
                                        if (locationResult == null) return
                                        mLastKnowLocation = locationResult.lastLocation
                                        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(mLastKnowLocation!!.latitude,
                                                mLastKnowLocation!!.longitude), 18F))
                                        mFusedLocationProviderClient!!.removeLocationUpdates(locationCallback)
                                    }
                                }
                            }
                            mFusedLocationProviderClient!!.requestLocationUpdates(locationRequest, locationCallback, null)
                        }
                }
                ?.addOnFailureListener{
                 Toast.makeText(activity, "Não foi possível obter a localização", Toast.LENGTH_LONG).show()
                }
                }
    }

