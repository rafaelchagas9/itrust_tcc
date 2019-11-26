package com.iTrust.tcc.ui.atividades

import android.annotation.SuppressLint
import android.app.Activity
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
import com.iTrust.tcc.R
import kotlinx.android.synthetic.main.activity_search.*
import android.content.Intent
import android.content.IntentSender
import android.speech.RecognizerIntent
import com.google.android.gms.common.api.ResolvableApiException
import java.util.*
import kotlin.collections.ArrayList


class SearchActivity : AppCompatActivity(), OnMapReadyCallback {

    var mMap: GoogleMap? = null
    var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    var placesClient: PlacesClient? = null

    var mLastKnowLocation: Location? = null
    var locationCallback: LocationCallback? = null
    var predictionsList: MutableList<AutocompletePrediction> = ArrayList()

    var urlFoto: String?= null
    var nomeLugar: String? = null
    var endLugar: String? = null
    var foneLugar: String? = null
    var horaLugar: String? = null
    var siteLugar: String? = null
    var horarioFormatado: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        createLocationRequest()

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
                if (buttonCode == MaterialSearchBar.BUTTON_SPEECH){
                    openVoiceRecognizer()
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
                        Place.Field.ADDRESS, Place.Field.PHOTO_METADATAS, Place.Field.PHONE_NUMBER, Place.Field.OPENING_HOURS,
                        Place.Field.WEBSITE_URI)
                val fetchPlaceRequest: FetchPlaceRequest = FetchPlaceRequest.builder(placeId, placeFields).build()
                placesClient!!.fetchPlace(fetchPlaceRequest)
                        .addOnSuccessListener {fetchPlaceResponse: FetchPlaceResponse? ->
                            val place: Place? = fetchPlaceResponse?.place
                            val primeiraFotoMetadata: String? = (place?.photoMetadatas?.get(0).toString())
                            val conver: String? = primeiraFotoMetadata?.substring(primeiraFotoMetadata.lastIndexOf(" ") + 16)?.replace("}", "")
                            urlFoto = "https://maps.googleapis.com/maps/api/place/photo?photoreference=$conver&sensor=false&maxheight=406&maxwidth=576&key=AIzaSyDcKodtmFJ9lclROlcSifa8coLbwDyGCBs"
                            if(place?.name!=null) nomeLugar = place.name
                            if(place?.address!=null) endLugar = place.address
                            if(place?.phoneNumber!=null) foneLugar = place.phoneNumber
                            if(place?.openingHours!=null) horaLugar = place.openingHours.toString()
                            if(place?.websiteUri!=null) siteLugar = place.websiteUri.toString()
                            Log.i("SearchActivity", "Lugar encontrado $urlFoto")
                            val latLngPlace: LatLng? = place?.latLng
                            if (latLngPlace!=null){
                                mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngPlace, 18F))
                                Log.e("FDAS", ""+latLngPlace)
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
                //Verificando se o campo de pesquisa está vazio, se estiver, as sugestões são cleared
                if (count == 0){
                    searchBar.clearSuggestions()
                }
                val predictionsRequest: FindAutocompletePredictionsRequest = FindAutocompletePredictionsRequest.builder()
                        .setCountry("br")
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

        btn_selecionar_local.setOnClickListener{
            val i = Intent(this, PaginaLugarActivity::class.java)
            if (nomeLugar == null) {
                Toast.makeText(this, "Por favor, selecione um local válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            //Verificando se algum campo está nulo, se sim, o programa atribui o valor "404"
            //para que seja fácil identificar e fazer o tratamento necessário
            if (urlFoto == null) {
                urlFoto="404"
            }
            if (endLugar == null) {
                endLugar="404"
            }
            if (foneLugar == null) {
                foneLugar="404"
            }
            if (horaLugar == null) {
                horaLugar="404"
            }else{
                limparStringHorario()
            }
            if (siteLugar == null) {
                siteLugar="404"
            }
            i.putExtra("Nome", nomeLugar)
            i.putExtra("Endereco", endLugar)
            i.putExtra("Fone", foneLugar)
            i.putExtra("Hora", horarioFormatado)
            i.putExtra("Site", siteLugar)
            i.putExtra("URL", urlFoto)
            startActivity(i)
        }

    }

    private fun limparStringHorario() {
        horarioFormatado = horaLugar?.replaceBefore("weekday", "")?.replaceBefore("Monday","")
    }

    private fun openVoiceRecognizer() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        startActivityForResult(intent, 10)
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
    fun createLocationRequest() {
        val locationRequest = LocationRequest.create()?.apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest!!)

// ...

        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener { locationSettingsResponse ->
            // All location settings are satisfied. The client can initialize
            // location requests here.
            // ...
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException){
                // A localização não está ativa ou não está no modo alta precisão,
                // mas isso pode ser resolvido através de uma caixa de diálogo.
                try {
                    // Exibe a caixa de diálogo para o usuário ativar o GPS,
                    exception.startResolutionForResult(this@SearchActivity, 61124)
                } catch (sendEx: IntentSender.SendIntentException) {
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //Verificando se o usuário selecionou alguma imagem e se a seleção deu certo
        if (requestCode == 10 && resultCode == Activity.RESULT_OK && data != null){
            val resultadoFala = ArrayList<String>(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS))
            searchBar.enableSearch()
            searchBar.text = resultadoFala[0]
        }
    }

    }



