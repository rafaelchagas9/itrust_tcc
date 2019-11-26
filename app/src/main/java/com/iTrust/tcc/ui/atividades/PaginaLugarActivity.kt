package com.iTrust.tcc.ui.atividades

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.iTrust.tcc.R
import com.iTrust.tcc.ui.atividades.mensagens.NewMessageActivity
import com.iTrust.tcc.ui.modelos.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_comentarios.*
import kotlinx.android.synthetic.main.activity_teste.*
import kotlinx.android.synthetic.main.fragment_profile.view.*


class PaginaLugarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teste)

        val b = intent.extras
        val nome = b.getString("Nome")
        val url = b!!.getString("URL")
        val endereco = b!!.getString("Endereco")
        val fone = b!!.getString("Fone")
        val hora = b!!.getString("Hora")
        val site = b!!.getString("Site")

        floatingActionButton.setOnClickListener{
            val i = Intent(this, ComentariosActivity::class.java)
            i.putExtra("Nome", nome)
            startActivity(i)
        }

        if (url!=null) {
            Picasso
                    .get()
                    .load("$url") // load the image
                    .into(img_place)
        }else{
            Picasso
                    .get()
                    .load("https://www.lifewire.com/thmb/tJCIpF-chKxWvl0xjy-0ZuEI85E=/768x0/filters:no_upscale():max_bytes(150000):strip_icc()/random-numbers-over-blackboard-166043947-57bb63065f9b58cdfd31d1fe.jpg") // load the image
                    .into(img_place)
        }

        if (endereco !=null ){
            txtEndereco.text = endereco
        }

        if (fone !=null && fone != "404"){
            txtFone.text = fone
        }else{
            txtFone.text = "Este local não disponibilizou um telefone"
        }

        if (site !=null && site != "404" ){
            txtSite.text = site
        }else{
            txtSite.text = "Este local não conta com website"
        }

        if (hora != null ){
            val diasSemanaHorario = hora.split(",")
            txtHoraSegunda.text = diasSemanaHorario[0].replace("Monday", "Segunda-feira")
            txtHoraTerca.text   = diasSemanaHorario[1].replace("Tuesday", "Terça-feira")
            txtHoraQuarta.text  = diasSemanaHorario[2].replace("Wednesday", "Quarta-feira")
            txtHoraQuinta.text  = diasSemanaHorario[3].replace("Thursday", "Quinta-feira")
            txtHoraSexta.text   = diasSemanaHorario[4].replace("Friday", "Sexta-feira")
            txtHoraSabado.text  = diasSemanaHorario[5].replace("Saturday", "Sábado")
            txtHoraDomingo.text = diasSemanaHorario[6].replace("Sunday", "Domingo").replace("]}","")
        }else{
            txtHoraSegunda.text = "Não foi possível obter o horário de funcionamento"
            esconderTextView()
        }

        tv_nome_lugar.text= nome

    }



    private fun esconderTextView() {
        txtHoraTerca.visibility = View.INVISIBLE
        txtHoraQuarta.visibility = View.INVISIBLE
        txtHoraQuinta.visibility = View.INVISIBLE
        txtHoraSexta.visibility = View.INVISIBLE
        txtHoraSabado.visibility = View.INVISIBLE
        txtHoraDomingo.visibility = View.INVISIBLE
    }
}
