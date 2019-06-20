//Projeto: TCC
//Version 0.1

package com.rafael.tcc

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.w3c.dom.Text

class CriarContaActivity : AppCompatActivity() {


    //Elementos da interface gráfica

    private var txt_usuario: EditText? = null
    private var txt_email: EditText? = null
    private var txt_estado: EditText? = null
    private var txt_senha: EditText? = null
    private var btn_criar_conta: Button? = null
    private var rg_classe: RadioGroup? = null
    private var mProgressBar: ProgressDialog? = null

    //Referências ao banco de dados


    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null

    private var TAG = "CriarContaActivity"

    //Variáveis globais

    private var usuario: String? = null
    private var email: String? = null
    private var estado: String? = null
    private var senha: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_criar_conta)

        iniciar()
    }

    private fun iniciar() {
        txt_usuario = findViewById(R.id.txt_usuario) as EditText
        txt_senha = findViewById(R.id.txt_senha) as EditText
        txt_email = findViewById(R.id.txt_email) as EditText
        txt_estado = findViewById(R.id.txt_estado) as EditText
        btn_criar_conta = findViewById(R.id.btn_criar_conta) as Button
        mProgressBar = ProgressDialog(this)

        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference!!.child("Users")
        mAuth = FirebaseAuth.getInstance()

        btn_criar_conta!!.setOnClickListener{
            criarNovaConta()
        }



    }

    private fun criarNovaConta() {
        usuario = txt_usuario?.text.toString()
        email = txt_email?.text.toString()
        senha = txt_senha?.text.toString()
        estado = txt_estado?.text.toString()

        if(!TextUtils.isEmpty(usuario) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(senha) &&
                TextUtils.isEmpty(estado)){
            Toast.makeText(this,"Cadastrado com sucesso", Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText(this,"Preencha todos os campos", Toast.LENGTH_SHORT).show()
        }

        mProgressBar!!.setMessage("Registrando usuário")
        mProgressBar!!.show()

        mAuth!!.createUserWithEmailAndPassword(email!!, senha!!).addOnCompleteListener(this){ task ->
            mProgressBar!!.hide()

            if (task.isSuccessful){
                Log.d(TAG, "CreateUserWithEmail:Sucess")

                val userId = mAuth!!.currentUser!!.uid

                //Verificar se o usúario confirmou o email
                verificarEmail()

                val currentUserDb = mDatabaseReference!!.child(userId)
                currentUserDb.child("Usuário").setValue(usuario)

                //Atualiza as informações no banco de dados
                updateUserInfoUi()

            } else{
                Log.w(TAG, "CreateUserWithEmail:Failed", task.exception)
                Toast.makeText(this@CriarContaActivity, "A autenticação falhou", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun verificarEmail() {
        val mUsuario = mAuth!!.currentUser
        mUsuario!!.sendEmailVerification().addOnCompleteListener(this){ task ->
            if(task.isSuccessful){
                Toast.makeText(this@CriarContaActivity, "Email de verificação enviado para"+mUsuario.getEmail(), Toast.LENGTH_LONG).show()
            } else{
                Log.e(TAG, "SendEmailVerfication", task.exception)
                Toast.makeText(this@CriarContaActivity, "Falha ao enviar o email de verificação", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUserInfoUi() {
        val intent = Intent(this@CriarContaActivity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}
