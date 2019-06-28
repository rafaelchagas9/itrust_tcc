//Projeto: TCC
//Version 0.1

package com.rafael.tcc.ui.atividades

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.rafael.tcc.R
import com.rafael.tcc.ui.MaskEditUtil
import kotlinx.android.synthetic.main.activity_criar_conta.*

class CriarContaActivity : AppCompatActivity() {


    //Elementos da interface gráfica
    //Os componentes Spinner foram declarados na linha 61
    //Porque iniciar aqui estava dando erro
    private var txt_nome: EditText? = null
    private var txt_sobrenome: EditText? = null
    private var txt_email: EditText? = null
    private var txt_senha: EditText? = null
    private var txt_cidade: EditText? = null
    private var txt_dataNasc: EditText? = null
    private var btn_criar_conta: Button? = null
    private var note_list_progress: ProgressBar? = null

    //Referências ao banco de dados
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null

    private var TAG = "CriarContaActivity"

    //Variáveis globais
    private var primeiro_nome: String? = null
    private var sobrenome: String? = null
    private var cidade: String? = null
    private var email: String? = null
    private var estado: String? = null
    private var condicao: String? = null
    private var senha: String? = null
    private var dataNasc: String? = null
    private var fotoSelecionadaUri: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_criar_conta)

        iniciar()
        txt_dataNasc?.addTextChangedListener(MaskEditUtil.mask(txt_dataNasc, MaskEditUtil.FORMAT_DATE))
    }

    private fun iniciar() {
        txt_nome = findViewById<EditText>(R.id.txt_nome)
        txt_senha = findViewById<EditText>(R.id.txt_senha)
        txt_sobrenome = findViewById(R.id.txt_sobrenome)
        txt_dataNasc = findViewById(R.id.txt_dataNasc)
        txt_cidade = findViewById(R.id.txt_cidade)
        txt_email = findViewById<EditText>(R.id.txt_email)
        btn_criar_conta = findViewById<Button>(R.id.btn_criar_conta)
        note_list_progress = findViewById<ProgressBar>(R.id.note_list_progress)

        //Definindo os valores dos estados brasileiros
        var estados = findViewById<Spinner>(R.id.spinner_estado)
        var adapter_estado = ArrayAdapter.createFromResource(this, R.array.estados, R.layout.spinner_item)
        adapter_estado.setDropDownViewResource(R.layout.spinner_dropdown_item)
        estados.adapter = adapter_estado

        //Definindo as possíveis condições do usuário
        var condicao = findViewById<Spinner>(R.id.spinner_condicao)
        var adapter_condicao = ArrayAdapter.createFromResource(this, R.array.condicao, R.layout.spinner_item)
        adapter_condicao.setDropDownViewResource(R.layout.spinner_dropdown_item)
        condicao.adapter = adapter_condicao



        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Users")
        mAuth = FirebaseAuth.getInstance()

        btn_criar_conta!!.setOnClickListener{
            criarNovaConta()
        }



    }

    private fun criarNovaConta() {
        primeiro_nome = txt_nome?.text.toString()
        sobrenome = txt_sobrenome?.text.toString()
        email = txt_email?.text.toString()
        senha = txt_senha?.text.toString()
        cidade = txt_cidade?.text.toString()
        dataNasc = txt_dataNasc?.text.toString()
        estado = spinner_estado?.selectedItem.toString()
        condicao = spinner_condicao?.selectedItem.toString()

        if(!TextUtils.isEmpty(primeiro_nome) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(senha) &&
                !TextUtils.isEmpty(estado)){
            note_list_progress?.visibility = ProgressBar.VISIBLE

            mAuth!!.createUserWithEmailAndPassword(email!!, senha!!).addOnCompleteListener(this){ task ->
                note_list_progress?.visibility = ProgressBar.GONE

                if (task.isSuccessful){
                    Log.d(TAG, "CreateUserWithEmail:Sucess")


                    //Verificar se o usúario confirmou o email
                    verificarEmail()

                    registrarDados()

                    //Atualiza as informações no banco de dados
                    updateUserInfoUi()

                } else{
                    Log.w(TAG, "CreateUserWithEmail:Failed", task.exception)
                    Toast.makeText(this@CriarContaActivity, "A autenticação falhou", Toast.LENGTH_LONG).show()
                }
        }
        }else{
            Toast.makeText(this,"Preencha todos os campos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun registrarDados() {
        val userId = mAuth!!.currentUser!!.uid
        val mUsuario = mAuth!!.currentUser
        val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(primeiro_nome)
                .build()
        mUsuario?.updateProfile(profileUpdates)

        val currentUserDb = mDatabaseReference!!.child(userId)
        currentUserDb.child("Primeiro Nome").setValue(primeiro_nome)
        currentUserDb.child("Sobrenome").setValue(sobrenome)
        currentUserDb.child("Nome Completo").setValue(primeiro_nome+sobrenome)
        currentUserDb.child("Data de nascimento").setValue(dataNasc)
        currentUserDb.child("Estado").setValue(estado)
        currentUserDb.child("Cidade").setValue(cidade)
        currentUserDb.child("Email").setValue(email)
        currentUserDb.child("Foto").setValue(fotoSelecionadaUri)

    }

    private fun verificarEmail() {
        val mUsuario = mAuth!!.currentUser
        mUsuario!!.sendEmailVerification().addOnCompleteListener(this){ task ->
            if(task.isSuccessful){
                Toast.makeText(this@CriarContaActivity, "Email de verificação enviado para"+mUsuario.getEmail(), Toast.LENGTH_LONG).show()
            } else{
                Log.e(TAG, "SendEmailVerification", task.exception)
                Toast.makeText(this@CriarContaActivity, "Falha ao enviar o email de verificação", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUserInfoUi() {
        Toast.makeText(this,"Cadastrado com sucesso, por favor, verifique seu e-mail", Toast.LENGTH_SHORT).show()
        val intent = Intent(this@CriarContaActivity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}
