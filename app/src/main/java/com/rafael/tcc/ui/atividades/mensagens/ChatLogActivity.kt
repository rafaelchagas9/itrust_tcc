package com.rafael.tcc.ui.atividades.mensagens

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.rafael.tcc.R
import com.rafael.tcc.ui.modelos.ChatMessage
import com.rafael.tcc.ui.modelos.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.activity_latest_messages.my_toolbar
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class ChatLogActivity : AppCompatActivity() {

    val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        setSupportActionBar(my_toolbar)
        assert(supportActionBar != null)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val usuario = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        supportActionBar?.title=usuario.Email

        recyclerview_chat_log.adapter = adapter
        procurarMensagens()
        botao_enviar_chat_log.setOnClickListener{
            enviarMensagem()
        }
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
                        adapter.add(ChatToItem(chatMessage.text, usuarioAtual))
                    }else{
                        adapter.add(ChatFromItem(chatMessage.text, usuario))
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
        val fromId = FirebaseAuth.getInstance().uid
        val usuario = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId = usuario.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        val toRef = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()
        if (fromId==null) return
        val chatMessage = ChatMessage(ref.key!!, text, fromId, toId, System.currentTimeMillis()/1000)
        ref.setValue(chatMessage)
                .addOnSuccessListener {
                    Log.d("ChatLogActivity", "Mensagem salva ${ref.key}")
                    edittext_chat_log.text.clear()
                    recyclerview_chat_log.scrollToPosition(adapter.itemCount-1)
                }
        toRef.setValue(chatMessage)

        val ultimaMensagemRef = FirebaseDatabase.getInstance().getReference("/ultimas-mensagens/$fromId/$toId")
        ultimaMensagemRef.setValue(chatMessage)

        val ultimaMensagemToRef = FirebaseDatabase.getInstance().getReference("/ultimas-mensagens/$toId/$fromId")
        ultimaMensagemToRef.setValue(chatMessage)
    }
}
class ChatFromItem(val text: String, val user: User): Item<ViewHolder>() {
    override fun getLayout(): Int {
        return (R.layout.chat_from_row)
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_from_row.text=text
        //carregando a imagem do usuario
        val uri = user.UrlFoto
        if (uri==""){
            Picasso.get()
                    .load("https://firebasestorage.googleapis.com/v0/b/projetotcc-5f8c5.appspot.com/o/user.png?alt=media&token=d463a005-f347-48e4-8eb6-0dff6bba301b")
                    .into(viewHolder.itemView.imgageview_from_row)
        }else {
            Picasso.get()
                    .load(uri)
                    .into(viewHolder.itemView.imgageview_from_row)
        }
    }
}
class ChatToItem(val text: String, val user: User): Item<ViewHolder>() {
    override fun getLayout(): Int {
        return (R.layout.chat_to_row)
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_to_row.text=text
        val uri = user.UrlFoto
        Picasso.get()
                .load(uri)
                .into(viewHolder.itemView.imageview_to_row)
    }
}