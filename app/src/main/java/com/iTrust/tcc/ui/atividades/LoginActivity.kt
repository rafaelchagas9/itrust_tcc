//Projeto: TCC
//Version 0.1

package com.iTrust.tcc.ui.atividades

import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.crashlytics.android.Crashlytics
import com.google.firebase.auth.FirebaseAuth
import com.iTrust.tcc.R
import io.fabric.sdk.android.Fabric

class LoginActivity : AppCompatActivity() {

    private val TAG = "LoginActivity"

    //Declaração de variáveis globais
    private var email: String? = null
    private var senha: String? = null

    //Elementos da interface
    private var tv_forgot_password: TextView? = null
    private var edit_email: EditText? = null
    private var edit_senha: EditText? = null
    private var btn_login: Button? = null
    private var btn_criar_conta: Button? = null
    private var mProgressBar: ProgressDialog? = null


    //Referências ao Firabase
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        inicializar()
        //Ferramenta de análise e relatório de crashes
        Fabric.with(this, Crashlytics())

        window.setStatusBarColorTo(R.color.colorPrimary)

    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun Window.setStatusBarColorTo(color: Int){
        this.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        this.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        this.statusBarColor = ContextCompat.getColor(baseContext, color)
    }

    fun Intent.change(){
        startActivity(this)
        finish()

    }

    private fun inicializar(){
        tv_forgot_password = findViewById<TextView>(R.id.tv_forgot_password)
        edit_email = findViewById<EditText>(R.id.edit_email)
        edit_senha = findViewById<EditText>(R.id.edit_senha)
        btn_login = findViewById<Button>(R.id.btn_login)
        btn_criar_conta = findViewById<Button>(R.id.btn_criar_conta)
        mProgressBar = ProgressDialog(this)
        mAuth = FirebaseAuth.getInstance()

        tv_forgot_password!!.setOnClickListener{ startActivity(Intent(this@LoginActivity, EsqueceSenhaActivity::class.java))
        }

        btn_criar_conta!!.setOnClickListener{ startActivity(Intent(this@LoginActivity, CriarContaActivity::class.java))
        }

        btn_login!!.setOnClickListener{loginUser()}

    }

    private fun loginUser() {

        email = edit_email?.text.toString()
        senha = edit_senha?.text.toString()

        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(senha)){
            mProgressBar!!.setMessage("Verificando o usuário")
            mProgressBar!!.show()

            Log.d(TAG, "Login do usuário")

            //Verificando o usuário e senha
            mAuth!!.signInWithEmailAndPassword(email!!,senha!!).addOnCompleteListener(this){task ->
                mProgressBar!!.hide()

                //Verificando o sucesso ou falha do login e atualizando a interface do usuário
                if(task.isSuccessful){
                    Log.d(TAG, "Logado com sucesso")
                    updateUi()
                }else{
                    Log.e(TAG, "Usuário ou senha inválido", task.exception)
                    Toast.makeText(this@LoginActivity, "Erro de autenticação, verifique as informações", Toast.LENGTH_SHORT).show()
                }
            }
        }else{
            Toast.makeText(this@LoginActivity, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
        }

    }

    private fun updateUi() {
        val intent = Intent(this@LoginActivity, PermissaoActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        this.finish()
    }

}

