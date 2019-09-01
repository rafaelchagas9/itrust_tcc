package com.iTrust.tcc.ui.atividades.mensagens

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.iTrust.tcc.R
import com.iTrust.tcc.ui.modelos.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_latest_messages.my_toolbar
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*


class NewMessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)
        setSupportActionBar(my_toolbar)
        assert(supportActionBar != null)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title="Selecione o usuário"


        buscarDadosUsuario()
    }

    companion object{
        val USER_KEY = "USER_KEY"
    }

    private fun buscarDadosUsuario() {
        val ref = FirebaseDatabase.getInstance().getReference("/Users")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()
                p0.children.forEach{
                    Log.d("FASDFGF", it.toString())
                    val user = it.getValue(User::class.java)
                    if (user!=null) adapter.add(UserItem(user))
                }

                adapter.setOnItemClickListener{item, view ->
                    val intent = Intent(view.context, ChatLogActivity::class.java)
                    val userItem = item as UserItem
                    intent.putExtra(USER_KEY, userItem.user)
                    startActivity(intent)
                    finish()
                }

                recyclerview_nova_mensagem.adapter = adapter
            }
        })
    }
}

class UserItem(val user: User): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.usuario_textview_nova_mensagem.text = user.Nome_Completo

        if (user.UrlFoto != "") {
            Picasso.get()
                    .load(user.UrlFoto) // load the image
                    .into(viewHolder.itemView.usuario_imageview_nova_mensagem)
        }
    }
    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }

}


