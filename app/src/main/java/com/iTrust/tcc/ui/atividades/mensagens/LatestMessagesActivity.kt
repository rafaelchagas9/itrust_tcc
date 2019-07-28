package com.iTrust.tcc.ui.atividades.mensagens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.iTrust.tcc.R
import com.iTrust.tcc.ui.atividades.LoginActivity
import com.iTrust.tcc.ui.atividades.mensagens.NewMessageActivity.Companion.USER_KEY
import com.iTrust.tcc.ui.modelos.ChatMessage
import com.iTrust.tcc.ui.modelos.User
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_latest_messages.*

class LatestMessagesActivity : AppCompatActivity() {

    val adapter = GroupAdapter<ViewHolder>()

    companion object{
        var usarioAtual: User? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)
        setSupportActionBar(my_toolbar)

        recyclerview_latest_message.adapter=adapter
        recyclerview_latest_message.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        adapter.setOnItemClickListener{item, view ->
            val intent = Intent(this, ChatLogActivity::class.java)
            val row = item as LatestMessageRow
            intent.putExtra(USER_KEY, row.chatPartnerUser)
            startActivity(intent)
        }

        buscarUsuarioAtual()
        verificarUltimasMensagens()

        fab_nova_mensagem.setOnClickListener{
            val intent = Intent(this, NewMessageActivity::class.java)
            startActivity(intent)
        }


    }

    val mapaUltimasMensagens = HashMap<String, ChatMessage>()

    private fun atualizarMensagensRecyclerView(){
        adapter.clear()
        mapaUltimasMensagens.values.forEach {
            adapter.add(LatestMessageRow(it))
        }
    }

    private fun verificarUltimasMensagens() {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("ultimas-mensagens/$fromId")
        ref.addChildEventListener(object: ChildEventListener{

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return
                mapaUltimasMensagens[p0.key!!] = chatMessage
                atualizarMensagensRecyclerView()
            }
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return
                mapaUltimasMensagens[p0.key!!] = chatMessage
                atualizarMensagensRecyclerView()
            }
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }
            override fun onChildRemoved(p0: DataSnapshot) {
                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return
                mapaUltimasMensagens[p0.key!!] = chatMessage
                atualizarMensagensRecyclerView()
            }


        })
    }

    private fun buscarUsuarioAtual() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/Users/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                usarioAtual = p0.getValue(User::class.java)
                Log.d("LATT", "${usarioAtual?.Nome_Completo}")
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu_latest_message, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.menu_action_sair -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
