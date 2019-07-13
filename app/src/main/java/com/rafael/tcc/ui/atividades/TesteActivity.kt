package com.rafael.tcc.ui.atividades

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rafael.tcc.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_teste.*


class TesteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teste)

        val b = intent.extras
        val url = b.getString("URL")
        val nome = b.getString("Nome")

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

        tv_nome_lugar.text="$nome     "

    }
}
