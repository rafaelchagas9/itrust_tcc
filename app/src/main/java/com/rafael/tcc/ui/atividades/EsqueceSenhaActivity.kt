package com.rafael.tcc.ui.atividades

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.rafael.tcc.R

class EsqueceSenhaActivity : AppCompatActivity() {

    private val TAG = "EsqueceSenhaActivity"

    //Elementos da interface
    private var txt_email: EditText? = null
    private var btn_recuperar_senha: Button? = null

    //Referências ao database
    private var mAuth: FirebaseAuth? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_esquece_senha)
        inicializar()
    }

    private fun inicializar() {
        txt_email = findViewById(R.id.txt_email) as EditText
        btn_recuperar_senha = findViewById(R.id.btn_recuperar_senha) as Button

        mAuth = FirebaseAuth.getInstance()

        btn_recuperar_senha!!.setOnClickListener{enviarEmailRecuperacao()}
    }

    private fun enviarEmailRecuperacao() {
        val email = txt_email?.text.toString()

        if(!TextUtils.isEmpty(email)){
            mAuth!!.sendPasswordResetEmail(email).addOnCompleteListener{task ->
                if(task.isSuccessful) {
                    val mensagem = "Email enviado"
                    Log.d(TAG, mensagem)
                    Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show()
                    updateUi()
                }else{
                    Log.w(TAG, task.exception!!.message)
                    Toast.makeText(this, "Não foi possível enviar o e-mail, por favor, verifique a informação acima",
                            Toast.LENGTH_SHORT).show()
                }
            }
        }else{
            Toast.makeText(this, "Preencha o campo de e-mail", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUi() {
        val intent = Intent(this@EsqueceSenhaActivity, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}
