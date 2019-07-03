package com.rafael.tcc.ui.atividades

import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.rafael.tcc.R
import com.rafael.tcc.ui.fragmentos.FavoriteFragment
import com.rafael.tcc.ui.fragmentos.HomeFragment
import com.rafael.tcc.ui.fragmentos.ProfileFragment
import com.rafael.tcc.ui.fragmentos.MapFragment


class MainActivity : AppCompatActivity() {

    //Referências ao banco de dados
    private val mDatabase: FirebaseDatabase? = FirebaseDatabase.getInstance()
    private val mDatabaseReference: DatabaseReference? = mDatabase!!.reference.child("Users")
    private val mAuth: FirebaseAuth? = FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Declaração do navigation view e declaração do Listener
        val bottomNav = findViewById<BottomNavigationView>(com.rafael.tcc.R.id.bottom_nav_view)
        bottomNav.setOnNavigationItemSelectedListener(navListener)

        //Definindo que o programa deve iniciar com o Fragmento Home e não em branco
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment()).commit()

        verificarUsuarioLogado()
    }

    private fun verificarUsuarioLogado() {
        val mUsuario = mAuth!!.currentUser
        if(mUsuario!=null){
            Log.e("TESTE", ""+mUsuario)
        }
        else{
            Log.e("Sessão expirada", "REDIRECIONANDO")
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            this.finish()
        }

    }

    private val navListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        var fragmentoSelecionado: Fragment? = null

        //Definindo o fragmento a ser exibido através do id do item selecionado no menu
        when (item.itemId) {
            R.id.navigation_home -> fragmentoSelecionado = HomeFragment()
            R.id.navigation_search -> {fragmentoSelecionado = MapFragment()
                createLocationRequest()}
            R.id.navigation_favorites -> fragmentoSelecionado = FavoriteFragment()
            R.id.navigation_perfil -> fragmentoSelecionado = ProfileFragment()
        }

        supportFragmentManager.beginTransaction().setCustomAnimations(android.R.anim.fade_in,
                android.R.anim.fade_out).replace(R.id.fragment_container, fragmentoSelecionado!!).commit()



        true
    }

    fun onBtnSairClick(v: View) {
        mAuth?.signOut()
        val intent = Intent(this@MainActivity, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        this.finish()
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
                    exception.startResolutionForResult(this@MainActivity, 61124)
                } catch (sendEx: IntentSender.SendIntentException) {
                }
            }
        }
    }

}



