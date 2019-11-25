package com.iTrust.tcc.ui.fragmentos


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.iTrust.tcc.R
import kotlinx.android.synthetic.main.fragment_conselhos.view.*
import kotlinx.android.synthetic.main.fragment_recomendado.*

class ConselhosFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_conselhos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.f1.text = "Não beber coca cola"
        view.f2.text = "Evitar baforar lança depois de usar crack"
    }


}


