//Projeto: TCC
//Version 0.1

package com.rafael.tcc.ui.atividades

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.os.Handler
import com.rafael.tcc.R

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        changeToLogin()
    }

    fun changeToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        Handler().postDelayed({
            intent.change()

        }, 2000)
    }
    fun Intent.change(){
        startActivity(this)
        finish()

    }
}
