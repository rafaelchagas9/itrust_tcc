package com.iTrust.tcc.ui.atividades

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.crashlytics.android.Crashlytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.iTrust.tcc.R
import com.iTrust.tcc.ui.modelos.ChatMessage
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

    private val mAuth: FirebaseAuth? = FirebaseAuth.getInstance()
    private val mUsuario = mAuth!!.currentUser
    private val url = mUsuario?.photoUrl
    private var nome: String ?= null
    val adapter = GroupAdapter<ViewHolder>()
    var fotinha: String ?= null
    var nominho: String ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comentarios)
        //Ferramenta de análise e relatório de crashes
        Fabric.with(this, Crashlytics())
        val b = intent.extras
        nome = b.getString("Nome")
        configurarActionBar()
        carregarFotoUsuario()
        recyclerview_comentarios.adapter = adapter
        carregarComentarios()

        button.setOnClickListener{
            val text = txtTexto.text.toString()
            if(text==""){
                return@setOnClickListener
            }else{
                val lugarNome = nome
                val fromId = FirebaseAuth.getInstance().uid
                val ref = FirebaseDatabase.getInstance().getReference("/comentarios/$nome").push()

                val postListener = object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        Log.e("rerwrwerwer", p0.toString())
                        val user = p0.getValue(User::class.java)
                        Log.e("rerwrwerwer", user.toString())
                        fotinha = user?.UrlFoto
                        nominho = user?.Nome_Completo
                    }

                }

                val refUsuarioComentario = FirebaseDatabase.getInstance().getReference("Users/$fromId")
                refUsuarioComentario.addValueEventListener(postListener)



                val tempoMs = System.currentTimeMillis()
                val dataResultadoString = Date(tempoMs).toString()
                val dia = dataResultadoString.subSequence(8, 10)
                val mes = dataResultadoString.subSequence(4, 7)
                var mesNumero: Int? = null

                when (mes) {
                    "Jan" -> {
                        mesNumero = 1
                    }
                    "Fev" -> {
                        mesNumero = 2
                    }
                    "Mar" -> {
                        mesNumero = 3
                    }
                    "Apr" -> {
                        mesNumero = 4
                    }
                    "May" -> {
                        mesNumero = 5
                    }
                    "Jun" -> {
                        mesNumero = 6
                    }
                    "Jul" -> {
                        mesNumero = 7
                    }
                    "Aug" -> {
                        mesNumero = 8
                    }
                    "Sep" -> {
                        mesNumero = 9
                    }
                    "Oct" -> {
                        mesNumero = 10
                    }
                    "Nov" -> {
                        mesNumero = 11
                    }
                    "Dec" -> {
                        mesNumero = 12
                    }
                }
                val ano = dataResultadoString.subSequence(30, 34)
                val dataCompleta = "$dia/$mesNumero/$ano"
                val hora = dataResultadoString.subSequence(11, 16).toString()
                val aleatorio = UUID.randomUUID().toString()
                val comment = Comentario(aleatorio, text, lugarNome.toString(), fromId.toString(), fotinha.toString(), nominho.toString(),dataCompleta, hora)
                ref.setValue(comment)
                        .addOnSuccessListener {
                            Log.d("ChatLogActivity", "Mensagem salva ${ref.key}")
                            txtTexto.text.clear()
                        }
            }
        }
    }

    private fun configurarActionBar() {
        setSupportActionBar(my_toolbar)
        assert(supportActionBar != null)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title="Comentários"
    }

    private fun carregarFotoUsuario() {
        if (url!=null) {
            Picasso
                    .get()
                    .load("$url") // load the image
                    .into(circleImageView)
        }

    }

    private fun carregarComentarios() {
        val ref = FirebaseDatabase.getInstance().getReference("/comentarios/$nome")

        ref.addChildEventListener(object: ChildEventListener {

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val comentario = p0.getValue(Comentario::class.java)
                if (comentario !=null){
                    Log.d(" dasdasdas", comentario.text + comentario.nome + comentario.fromIdNome + comentario.fromIdFoto)
                    adapter.add(Comentarios(comentario.text, comentario.fromIdNome, comentario.fromIdFoto,
                            comentario.hora, comentario.dataCompleta))
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

class Comentarios(val text: String, val fromIdNome: String, val FromIdFoto: String, val hora: String, val dataCompleta: String):
        Item<ViewHolder>() {

    override fun getLayout(): Int {
        return (R.layout.comentario_row)
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        Log.e("MEU CU", fromIdNome + FromIdFoto)
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
