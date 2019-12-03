package com.iTrust.tcc.ui.fragmentos


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration

import com.iTrust.tcc.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.conselho_row.view.*
import kotlinx.android.synthetic.main.fragment_conselhos.*
import kotlinx.android.synthetic.main.fragment_conselhos.view.*
import kotlinx.android.synthetic.main.fragment_recomendado.*

class ConselhosFragment : Fragment() {

    private val adapter = GroupAdapter<ViewHolder>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_conselhos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerview_conselhos.adapter = adapter
        recyclerview_conselhos.addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
        adapter.add(conselho("'Quem olha para fora sonha, quem olha para dentro desperta.'", "Carl Jung", "https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcToX2kWAVcwJ4SaGwknyUZXcsjUQFTh8xBrjUN8BeQjJvqY4uA3"))
        adapter.add(conselho("'Nós poderiamos ser muito melhores se não quiséssemos ser tão bons.'", "Sigmund Freud", "https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcRVFZCs5PWj35kIWV_PQn7Jlw7dWazOEFZVbQpI3hqbBBAE0GqH"))
        adapter.add(conselho("'Os sonhos são as manifestações não falsificadas da atividade criativa inconsciente.'", "Carl Jung", "https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcToX2kWAVcwJ4SaGwknyUZXcsjUQFTh8xBrjUN8BeQjJvqY4uA3"))
        adapter.add(conselho("'Você pode saber o que disse, mas nunca o que outro escutou.'", "Jacques Lacan", "https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcRg4IGhe0yNySgQ0e7g2l4sKNDd2ssMZQ1iY0ljlPT9hgdHh7eW"))
        adapter.add(conselho("'É instintivo da mente humana que um homem mais deseje os prazeres que lhe são proibidos.'", "Torquato Tasso", "https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcQoiphjKl5M_1ej9YirZD22Cob66vatCH6lKTTDALf6UNwMcB3f"))
        adapter.add(conselho("'Quanto menos se conhece alguém, mais corremos o risco de construir a sua imagem de maneira distorcida, aumentando-a ou diminuindo-a.'", "Carl Jung", "https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcToX2kWAVcwJ4SaGwknyUZXcsjUQFTh8xBrjUN8BeQjJvqY4uA3"))
        adapter.add(conselho("'A maioria das pessoas não quer realmente a liberdade, pois liberdade envolve responsabilidade, e a maioria das pessoas tem medo de responsabilidade.'", "Sigmund Freud", "https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcRVFZCs5PWj35kIWV_PQn7Jlw7dWazOEFZVbQpI3hqbBBAE0GqH"))
    }


}


class conselho(private val conselho: String, val autor: String, private val foto: String):
        Item<ViewHolder>() {

    override fun getLayout(): Int {
        return (R.layout.conselho_row)
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.txtConselho.text  = conselho
        viewHolder.itemView.txtAutor.text  = autor
        //carregando a imagem do usuario
        val uri = foto
        if (uri==""){
            return
        }else {
            Picasso.get()
                    .load(uri)
                    .into(viewHolder.itemView.imageview_foto)
        }
    }
}


