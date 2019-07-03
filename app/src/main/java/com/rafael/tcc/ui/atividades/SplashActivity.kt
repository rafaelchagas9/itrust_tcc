//Projeto: TCC
//Version 0.1

package com.rafael.tcc.ui.atividades

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.rafael.tcc.R


class SplashActivity : AppCompatActivity() {
    private val mDatabase: FirebaseDatabase? = FirebaseDatabase.getInstance()
    private val mDatabaseReference: DatabaseReference? = mDatabase!!.reference.child("Users")
    private val mAuth: FirebaseAuth? = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        changeToLoginActivity()
    }

    fun changeToLoginActivity() {
        val mUsuario = mAuth!!.currentUser
        //Verificando se já existe algum usuário logado no dispositivo
        if (mUsuario!=null){
            val intent = Intent(this, PermissaoActivity::class.java)
            Handler().postDelayed({
                intent.change()

            }, 2000)
        }else{
            val intent = Intent(this, LoginActivity::class.java)
            Log.e("TESTE", ""+mAuth)
            Handler().postDelayed({
                intent.change()

            }, 2000)
        }
    }
    fun Intent.change(){
        startActivity(this, ActivityOptions.makeSceneTransitionAnimation(this@SplashActivity).toBundle())
        finish()
    }
}
