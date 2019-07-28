package com.iTrust.tcc.ui.fragmentos

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.iTrust.tcc.R
import com.iTrust.tcc.ui.atividades.SearchActivity
import com.iTrust.tcc.ui.atividades.mensagens.LatestMessagesActivity
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)
        view.fab_pesquisar.setOnClickListener {

            Intent(activity, SearchActivity::class.java).apply {
                val transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(activity, view, getString(R.string.roundedColorView_transitionName))
                startActivity(this, transitionActivityOptions.toBundle())
            }
        }
        view.fab_chat.setOnClickListener{
            Intent(activity, LatestMessagesActivity::class.java).apply {
                val transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(activity, view, getString(R.string.roundedColorView_transitionName))
                startActivity(this, transitionActivityOptions.toBundle())
            }
        }

        return view
    }

}
