package com.iTrust.tcc.ui.fragmentos


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.iTrust.tcc.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.comentario_row.view.txtNome
import kotlinx.android.synthetic.main.fragment_recomendado.*
import kotlinx.android.synthetic.main.lugar_row.view.*

class RecomendadoFragment : Fragment() {

    private val adapter = GroupAdapter<ViewHolder>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recomendado, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerview_testando.adapter = adapter
        adapter.add(lugar("Teste", "https://lh3.googleusercontent.com/proxy/aOYN-6NCMIMlW38i5QY9UH3TRkyN_SEOzODpvZ1bpRyVNcMj9mKV7qObHDbUC8Kh6HxiS4AZS1XlgnWUso8NU-KD2a7W9oDug_RE7HGfppuURWKSs8djuyzPJlX9uWfk22o1f9knx24nxe7dsZ8EBiJx8G1rLH3DTWQB55WXRWllBeRvFMii5wIi5g=w360-h360-k-no",
                "Rua das Flores", "4.5"))
        adapter.add(lugar("VLW", "https://lh3.googleusercontent.com/proxy/aOYN-6NCMIMlW38i5QY9UH3TRkyN_SEOzODpvZ1bpRyVNcMj9mKV7qObHDbUC8Kh6HxiS4AZS1XlgnWUso8NU-KD2a7W9oDug_RE7HGfppuURWKSs8djuyzPJlX9uWfk22o1f9knx24nxe7dsZ8EBiJx8G1rLH3DTWQB55WXRWllBeRvFMii5wIi5g=w360-h360-k-no",
                "Rua SORO ACABA", "10"))
    }


}

class lugar(val nome: String, private val foto: String, private val endereco: String, private val avaliacao: String):
        Item<ViewHolder>() {

    override fun getLayout(): Int {
        return (R.layout.lugar_row)
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.txtNome.text  = nome
        viewHolder.itemView.txtAval.text  = avaliacao
        viewHolder.itemView.txtEnder.text = endereco
        //carregando a imagem do usuario
        val uri = foto
        if (uri==""){
            return
        }else {
            Picasso.get()
                    .load(uri)
                    .into(viewHolder.itemView.imgFoto)
        }
    }
}
