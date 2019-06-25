package com.rafael.tcc.ui.atividades

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.rafael.tcc.R
import com.rafael.tcc.ui.fragmentos.*
import android.view.View


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

    }

    private val navListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        var fragmentoSelecionado: Fragment? = null

        //Definindo o fragmento a ser exibido através do id do item selecionado no menu
        when (item.itemId) {
            R.id.navigation_home -> fragmentoSelecionado = HomeFragment()
            R.id.navigation_search -> fragmentoSelecionado = SearchFragment()
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
    }
    }



