package com.iTrust.tcc.ui.atividades.mensagens

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.iTrust.tcc.R
import com.iTrust.tcc.ui.modelos.ChatMessage
import com.iTrust.tcc.ui.modelos.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.latest_message_row.view.*

class LatestMessageRow(val chatMessage: ChatMessage): Item<ViewHolder>(){

    var chatPartnerUser: User? = null

    override fun getLayout(): Int {
        return R.layout.latest_message_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        val testetext: String = chatMessage.text.replace("\n"," ")
        viewHolder.itemView.message_textview_latest_message.text= testetext
        val chatPartnerId: String
        if (chatMessage.fromId == FirebaseAuth.getInstance().uid){
            chatPartnerId = chatMessage.toId
        }else{
            chatPartnerId = chatMessage.fromId
        }
        val ref = FirebaseDatabase.getInstance().getReference("/Users/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                chatPartnerUser = p0.getValue(User::class.java)
                viewHolder.itemView.textview_nome_latest_message.text = chatPartnerUser?.Nome_Completo
                if (chatPartnerUser?.UrlFoto=="" || chatPartnerUser?.UrlFoto==null){
                    return
                }else{
                    Picasso.get().load(chatPartnerUser?.UrlFoto).into(viewHolder.itemView.imageview_latest_message)
                }

            }

        })
    }
}