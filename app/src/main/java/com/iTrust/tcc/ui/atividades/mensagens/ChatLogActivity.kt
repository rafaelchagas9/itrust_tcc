package com.iTrust.tcc.ui.atividades.mensagens

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.crashlytics.android.Crashlytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.iTrust.tcc.R
import com.iTrust.tcc.ui.atividades.mensagens.ChatLogActivity.Companion.dataArmazenada
import com.iTrust.tcc.ui.modelos.ChatMessage
import com.iTrust.tcc.ui.modelos.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.activity_latest_messages.my_toolbar
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_from_row.view.textview_from_data
import kotlinx.android.synthetic.main.chat_to_row.view.*
import java.util.*


class ChatLogActivity : AppCompatActivity() {

    val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
        //Ferramenta de análise e relatório de crashes
        Fabric.with(this, Crashlytics())

        setSupportActionBar(my_toolbar)
        assert(supportActionBar != null)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val usuario = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        supportActionBar?.title=usuario.Nome_Completo


        recyclerview_chat_log.adapter = adapter
        procurarMensagens()
        botao_enviar_chat_log.setOnClickListener{
            enviarMensagem()
        }
        dataArmazenada=""
    }
    companion object{
        var dataArmazenada: String? = ""
    }

    private fun procurarMensagens() {
        val usuario = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val fromId = FirebaseAuth.getInstance().uid
        val toId = usuario.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        ref.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)
                if (chatMessage !=null){
                    Log.d("AASS", chatMessage.text)
                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid){
                        val usuarioAtual = LatestMessagesActivity.usarioAtual ?: return
                        adapter.add(ChatToItem(chatMessage.text, usuarioAtual, chatMessage.hora, chatMessage.dataCompleta))
                        recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
                    }else{
                        adapter.add(ChatFromItem(chatMessage.text, usuario, chatMessage.hora, chatMessage.dataCompleta))
                        recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
                    }


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

    private fun enviarMensagem() {
        val text = edittext_chat_log.text.toString()
        if(text==""){
            return
        }else {
            val fromId = FirebaseAuth.getInstance().uid
            val usuario = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
            val toId = usuario.uid
            val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
            val toRef = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()
            if (fromId == null) return
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
            val dataCompleta = "$dia/0$mesNumero/$ano"
            val hora = dataResultadoString.subSequence(11, 16).toString()
            val chatMessage = ChatMessage(ref.key!!, text, fromId, toId, dataCompleta, hora)
            ref.setValue(chatMessage)
                    .addOnSuccessListener {
                        Log.d("ChatLogActivity", "Mensagem salva ${ref.key}")
                        edittext_chat_log.text.clear()
                        recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
                    }
            toRef.setValue(chatMessage)

            val ultimaMensagemRef = FirebaseDatabase.getInstance().getReference("/ultimas-mensagens/$fromId/$toId")
            ultimaMensagemRef.setValue(chatMessage)

            val ultimaMensagemToRef = FirebaseDatabase.getInstance().getReference("/ultimas-mensagens/$toId/$fromId")
            ultimaMensagemToRef.setValue(chatMessage)
        }
    }

}

//Mensagem que foi recebida, então essa classe carrega a mensagem e as informações do usuário que
//enviou a mensagem
class ChatFromItem(val text: String, val user: User, val hora: String, val dataCompleta: String): Item<ViewHolder>() {
    override fun getLayout(): Int {
        return (R.layout.chat_from_row)
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        if(dataArmazenada.equals(dataCompleta)){
            viewHolder.itemView.textview_from_data.visibility= View.GONE
        }
        else{
            viewHolder.itemView.textview_from_data.text=dataCompleta
            viewHolder.itemView.textview_from_data.visibility=View.VISIBLE
            dataArmazenada=dataCompleta
        }
        viewHolder.itemView.textview_from_row.text=text
        viewHolder.itemView.textview_from_hora.text=hora
        //carregando a imagem do usuario
        val uri = user.UrlFoto
        if (uri==""){
            return
        }else {
            Picasso.get()
                    .load(uri)
                    .into(viewHolder.itemView.imageview_from_row)
        }
    }
}

//Mensagem que foi enviada, então essa classe carrega a mensagem e as informações do usuario logado
class ChatToItem(val text: String, val user: User, val hora: String, val dataCompleta: String): Item<ViewHolder>() {
    override fun getLayout(): Int {
        return (R.layout.chat_to_row)
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        if(dataArmazenada.equals(dataCompleta)){
            viewHolder.itemView.textview_to_data.visibility= View.GONE
        }
        else{
            viewHolder.itemView.textview_to_data.text=dataCompleta
            viewHolder.itemView.textview_to_data.visibility=View.VISIBLE
            dataArmazenada=dataCompleta
        }
        viewHolder.itemView.textview_to_row.text=text
        viewHolder.itemView.textview_to_hora.text=hora
        val uri = user.UrlFoto
        if (uri==""){
            return
        }else {
            Picasso.get()
                    .load(uri)
                    .into(viewHolder.itemView.imageview_to_row)
        }
    }
}