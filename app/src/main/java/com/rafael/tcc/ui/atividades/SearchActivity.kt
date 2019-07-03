package com.rafael.tcc.ui.atividades

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.*
import com.google.android.libraries.places.api.net.*
import com.mancj.materialsearchbar.MaterialSearchBar
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter
import com.rafael.tcc.R
import com.rafael.tcc.ui.Local
import kotlinx.android.synthetic.main.activity_search.*


class SearchActivity : AppCompatActivity(), OnMapReadyCallback {

    var mMap: GoogleMap? = null
    var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    var placesClient: PlacesClient? = null

    var mLastKnowLocation: Location? = null
    var locationCallback: LocationCallback? = null
    var predictionsList: MutableList<AutocompletePrediction> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_pesquisar) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        mFusedLocationProviderClient = (LocationServices.getFusedLocationProviderClient(this))
        Places.initialize(this, "AIzaSyDcKodtmFJ9lclROlcSifa8coLbwDyGCBs")
        placesClient = Places.createClient(this)
        val token: AutocompleteSessionToken = AutocompleteSessionToken.newInstance()
        searchBar.setOnSearchActionListener(object : MaterialSearchBar.OnSearchActionListener {
            override fun onButtonClicked(buttonCode: Int) {
                if (buttonCode == MaterialSearchBar.BUTTON_BACK){
                    searchBar.disableSearch()
                }
            }

            override fun onSearchStateChanged(enabled: Boolean) {

            }

            override fun onSearchConfirmed(text: CharSequence?) {
                startSearch(text.toString(), true, null, true)
            }
        })

        searchBar.setSuggestionsClickListener(object: SuggestionsAdapter.OnItemViewClickListener{
            override fun OnItemDeleteListener(position: Int, v: View?) {

            }

            override fun OnItemClickListener(position: Int, v: View?) {
                if(position >= predictionsList.size){
                    return
                }
                val selectedPrediction: AutocompletePrediction = predictionsList[position]
                val sugestao = searchBar.lastSuggestions[position].toString()
                searchBar.text=sugestao
                Handler().postDelayed({
                    searchBar.clearSuggestions()

                }, 500)
                val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(searchBar.windowToken, InputMethodManager.HIDE_IMPLICIT_ONLY)
                val placeId = selectedPrediction.placeId
                //A linha abaixo pode ser usada pra obter qualquer informação sobre o local
                val placeFields: List<Place.Field> = listOf(Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.TYPES,
                        Place.Field.ADDRESS, Place.Field.PHOTO_METADATAS, Place.Field.PHONE_NUMBER)
                val fetchPlaceRequest: FetchPlaceRequest = FetchPlaceRequest.builder(placeId, placeFields).build()
                placesClient!!.fetchPlace(fetchPlaceRequest)
                        .addOnSuccessListener {fetchPlaceResponse: FetchPlaceResponse? ->
                            val place: Place? = fetchPlaceResponse?.place
                            var fotoTeste: String? = (place?.photoMetadatas?.get(0)).toString()
                            var conver: String? = fotoTeste?.substring(fotoTeste.lastIndexOf(" ") + 16)
                            Log.i("SearchActivity", "Lugar encontrado"+ (place?.photoMetadatas?.get(0)))
                            Log.i("SearchActivity", "Lugar encontrado"+ fotoTeste)
                            Log.i("SearchActivity", "Lugar encontrado"+ conver)
                            val latLngPlace: LatLng? = place?.latLng
                            if (latLngPlace!=null){
                                mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngPlace, 18F))
                                Log.e("FDAS", ""+latLngPlace)
                                val lugar = Local(place.latLng!!, place.name!!, place.photoMetadatas!!,
                                        place.types!!, place.address!!, place.phoneNumber!!)
                            }
                        }
                        .addOnFailureListener{exception ->
                            if (exception is ApiException){
                                Log.e("SearchActivity","Local não encontrado")
                            }
                        }
            }

        }
        )

        searchBar.addTextChangeListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val predictionsRequest: FindAutocompletePredictionsRequest = FindAutocompletePredictionsRequest.builder()
                        .setTypeFilter(TypeFilter.ESTABLISHMENT)
                        .setSessionToken(token)
                        .setQuery(s.toString())
                        .build()
                placesClient!!.findAutocompletePredictions(predictionsRequest)
                        .addOnCompleteListener{task: Task<FindAutocompletePredictionsResponse> ->
                            if (task.isSuccessful){
                                val predictionsResponse: FindAutocompletePredictionsResponse? = task.result
                                if (predictionsResponse != null){
                                    predictionsList = predictionsResponse.autocompletePredictions
                                    val sugestoesList = ArrayList<String>()
                                    for (i in predictionsList.indices){
                                        val prediction: AutocompletePrediction = predictionsList[i]
                                        sugestoesList.add(prediction.getFullText(null).toString())
                                    }
                                    searchBar.updateLastSuggestions(sugestoesList)
                                    if (!searchBar.isSuggestionsVisible){
                                        searchBar.showSuggestionsList()
                                    }
                                }
                            }else{
                                Log.e("SearchActivity", "Erro ao exibir sugestões de pesquisa")
                            }
                        }
            }
        }
        )

    }
    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap?) {
        MapsInitializer.initialize(this)
        mMap = googleMap
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_pesquisar) as SupportMapFragment?
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
        getDeviceLocation()
        mMap?.setOnMyLocationButtonClickListener {
            if (searchBar.isSuggestionsVisible){
                searchBar.clearSuggestions()
            }
            if (searchBar.isSearchEnabled){
                searchBar.disableSearch()
            }

            return@setOnMyLocationButtonClickListener false
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
                    Toast.makeText(this, "Não foi possível obter a localização", Toast.LENGTH_LONG).show()
                }
    }
}


