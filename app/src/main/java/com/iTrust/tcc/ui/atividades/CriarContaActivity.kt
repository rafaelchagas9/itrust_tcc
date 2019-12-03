//Projeto: TCC
//Version 0.1

package com.iTrust.tcc.ui.atividades

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.iTrust.tcc.R
import com.iTrust.tcc.ui.MaskEditUtil
import kotlinx.android.synthetic.main.activity_criar_conta.*

class CriarContaActivity : AppCompatActivity() {


    //Elementos da interface gráfica
    //Os componentes Spinner foram declarados na linha 61
    //Porque iniciar aqui estava dando erro
    private var note_list_progress: ProgressBar? = null

    //Referências ao banco de dados
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null

    private var TAG = "CriarContaActivity"

    //Variáveis globais
    private var primeiroNome: String? = null
    private var sobrenome: String? = null
    private var cidade: String? = null
    private var email: String? = null
    private var estado: String? = null
    private var condicao: String? = null
    private var senha: String? = null
    private var dataNasc: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_criar_conta)

        iniciar()
        txtTermosUso.movementMethod = LinkMovementMethod.getInstance()
        val text = "<a href='https://pastebin.com/67FG11TA'> Declaro ter lido e aceitado os termos de uso   </a>"
        txtTermosUso.text = (Html.fromHtml(text))
        txt_dataNasc?.addTextChangedListener(MaskEditUtil.mask(txt_dataNasc, MaskEditUtil.FORMAT_DATE))
    }

    private fun iniciar() {
        note_list_progress = findViewById<ProgressBar>(R.id.note_list_progress)

        //Definindo os valores dos estados brasileiros
        val adapterEstado = ArrayAdapter.createFromResource(this, R.array.estados, R.layout.spinner_item)
        adapterEstado.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinner_estado.adapter = adapterEstado

        //Definindo as possíveis condições do usuário
        val adapterCondicao = ArrayAdapter.createFromResource(this, R.array.condicao, R.layout.spinner_item)
        adapterCondicao.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinner_condicao.adapter = adapterCondicao



        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Users")
        mAuth = FirebaseAuth.getInstance()

        btn_criar_conta!!.setOnClickListener{
            criarNovaConta()
        }



    }

    private fun criarNovaConta() {
        primeiroNome = txt_nome?.text.toString()
        sobrenome = txt_sobrenome?.text.toString()
        email = txt_email?.text.toString()
        senha = txt_senha?.text.toString()
        cidade = txt_cidade?.text.toString()
        dataNasc = txt_dataNasc?.text.toString()
        estado = spinner_estado?.selectedItem.toString()
        condicao = spinner_condicao?.selectedItem.toString()

        if(!TextUtils.isEmpty(primeiroNome) && !TextUtils.isEmpty(sobrenome) && !TextUtils.isEmpty(email)
                && !TextUtils.isEmpty(cidade) && !TextUtils.isEmpty(dataNasc) && !TextUtils.isEmpty(senha) &&
                !TextUtils.isEmpty(estado) && !TextUtils.isEmpty(condicao) && checkBoxTermosUso.isSelected ){
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
                .setDisplayName(primeiroNome)
                .build()
        mUsuario?.updateProfile(profileUpdates)

        val currentUserDb = mDatabaseReference!!.child(userId)
        currentUserDb.child("uid").setValue(userId)
        currentUserDb.child("Primeiro_Nome").setValue(primeiroNome)
        currentUserDb.child("Sobrenome").setValue(sobrenome)
        currentUserDb.child("Nome_Completo").setValue("$primeiroNome $sobrenome")
        currentUserDb.child("Data_de_nascimento").setValue(dataNasc)
        currentUserDb.child("Estado").setValue(estado)
        currentUserDb.child("Cidade").setValue(cidade)
        currentUserDb.child("Email").setValue(email)
        currentUserDb.child("Condicao").setValue(condicao)

    }

    private fun verificarEmail() {
        val mUsuario = mAuth!!.currentUser
        mUsuario!!.sendEmailVerification().addOnCompleteListener(this){ task ->
            if(task.isSuccessful){
                Toast.makeText(this@CriarContaActivity, "Email de verificação enviado para"+mUsuario.email, Toast.LENGTH_LONG).show()
            } else{
                Log.e(TAG, "SendEmailVerification", task.exception)
                Toast.makeText(this@CriarContaActivity, "Falha ao enviar o email de verificação", Toast.LENGTH_SHORT).show()
              }
        }
    }

    private fun updateUserInfoUi() {
        Toast.makeText(this,"Cadastrado com sucesso, por favor, verifique seu e-mail", Toast.LENGTH_SHORT).show()
        val intent = Intent(this@CriarContaActivity, PermissaoActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}
