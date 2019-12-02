package com.iTrust.tcc.ui.atividades

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.crashlytics.android.Crashlytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.iTrust.tcc.R
import com.iTrust.tcc.ui.modelos.Comentario
import com.iTrust.tcc.ui.modelos.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.activity_comentarios.*
import kotlinx.android.synthetic.main.activity_latest_messages.my_toolbar
import kotlinx.android.synthetic.main.comentario_row.view.*
import java.util.*

class ComentariosActivity : AppCompatActivity() {

    //Declaração de variáveis
    private val mAuth: FirebaseAuth? = FirebaseAuth.getInstance()
    private val fromId = FirebaseAuth.getInstance().uid
    private val mUsuario = mAuth!!.currentUser
    private val url = mUsuario?.photoUrl
    private val adapter = GroupAdapter<ViewHolder>()
    private var fotinha: String ?= null
    private var nomeLugar: String ?= null
    private var nominho: String ?= null
    private lateinit var user: User


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comentarios)
        configuracoesIniciais()


        button.setOnClickListener{
            val text = txtTexto.text.toString()
            if(text==""){
                return@setOnClickListener
            }else{
                val ref = FirebaseDatabase.getInstance().getReference("/comentarios/$nomeLugar").push()
                recuperandoNomeFotoUsuario()
                val dataCompleta = stringData()
                val aleatorio = UUID.randomUUID().toString()
                val comment = Comentario(aleatorio, text, nomeLugar.toString(), fromId.toString(), fotinha.toString(), nominho.toString(),dataCompleta)
                ref.setValue(comment)
                        .addOnSuccessListener {
                            Log.d("ChatLogActivity", "Mensagem salva ${ref.key}")
                            txtTexto.text.clear()
                        }
            }
        }
    }

    private fun stringData(): String {
        val tempoMs = System.currentTimeMillis()
        val dataResultadoString = Date(tempoMs).toString()
        val dia = dataResultadoString.subSequence(8, 10)
        val mes = dataResultadoString.subSequence(4, 7).toString()
        val mesNumero = transformarMes(mes)
        val ano = dataResultadoString.subSequence(30, 34)
        return "$dia/$mesNumero/$ano"
    }

    private fun transformarMes(mes:String): String {
        //Transforma o mês(Exemplo, se o mês for 'JAN' ele vai retornar mês 01)
        var mesNumero: String?= null
        when (mes) {
            "Jan" -> {
                mesNumero = "01"
            }
            "Fev" -> {
                mesNumero = "02"
            }
            "Mar" -> {
                mesNumero = "03"
            }
            "Apr" -> {
                mesNumero = "04"
            }
            "May" -> {
                mesNumero = "05"
            }
            "Jun" -> {
                mesNumero = "06"
            }
            "Jul" -> {
                mesNumero = "07"
            }
            "Aug" -> {
                mesNumero = "08"
            }
            "Sep" -> {
                mesNumero = "09"
            }
            "Oct" -> {
                mesNumero = "10"
            }
            "Nov" -> {
                mesNumero = "11"
            }
            "Dec" -> {
                mesNumero = "12"
            }
        }
        return mesNumero.toString()
    }

    private fun recuperandoNomeFotoUsuario() {
        val postListener = object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                Log.e("rerwrwerwer", p0.toString())
                user = p0.getValue(User::class.java)!!
                nominho = user.Nome_Completo
                fotinha = user.UrlFoto
            }

        }


        val refUsuarioComentario = FirebaseDatabase.getInstance().getReference("Users/$fromId")
        refUsuarioComentario.addValueEventListener(postListener)
    }

    private fun configuracoesIniciais() {
        configurarFabric()
        configurarActionBar()
        obterNomeDoLocal()
        carregarInformacoesUsuario()
        recyclerview_comentarios.adapter = adapter
        carregarComentarios()
    }

    private fun obterNomeDoLocal() {
        val b = intent.extras
        if (b!=null){
            nomeLugar = b.getString("Nome")
        }else{
            Toast.makeText(this, "Falha ao obter o local, por favor, tente novamente", Toast.LENGTH_LONG).show()
        }
    }

    private fun configurarFabric() {
        //Ferramenta de análise e relatório de crashes
        Fabric.with(this, Crashlytics())
    }

    private fun configurarActionBar() {
        setSupportActionBar(my_toolbar)
        assert(supportActionBar != null)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title="Comentários"
    }

    private fun carregarInformacoesUsuario() {
        //Buscando as informações do usuário no banco de dados
        recuperandoNomeFotoUsuario()
        //Buscando
        if (url!=null) {
            Picasso
                    .get()
                    .load("$url") // load the image
                    .into(circleImageView)
        }

    }

    private fun carregarComentarios() {
        val ref = FirebaseDatabase.getInstance().getReference("/comentarios/$nomeLugar")

        ref.addChildEventListener(object: ChildEventListener {

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val comentario = p0.getValue(Comentario::class.java)
                if (comentario !=null){
                    Log.d(" dasdasdas", comentario.text + comentario.nome + comentario.fromIdNome + comentario.fromIdFoto)
                    adapter.add(Comentarios(comentario.text, comentario.fromIdNome, comentario.fromIdFoto,
                            comentario.dataCompleta))

                    recyclerview_comentarios.adapter = adapter
                    recyclerview_comentarios.scrollToPosition(adapter.itemCount - 1)
                    }
                }


            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }


        })


    }

}

class Comentarios(val text: String, private val fromIdNome: String, private val FromIdFoto: String, private val dataCompleta: String):
        Item<ViewHolder>() {

    override fun getLayout(): Int {
        return (R.layout.comentario_row)
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_from_row.text=text
        viewHolder.itemView.textview_from_hora.text=dataCompleta
        viewHolder.itemView.txtNome.text = fromIdNome
        //carregando a imagem do usuario
        val uri = FromIdFoto
        if (uri==""){
            return
        }else {
            Picasso.get()
                    .load(uri)
                    .into(viewHolder.itemView.imageview_from_row)
        }
    }
}
