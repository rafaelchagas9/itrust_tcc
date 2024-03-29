package com.iTrust.tcc.ui.fragmentos

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.iTrust.tcc.R
import com.iTrust.tcc.ui.atividades.SearchActivity
import com.iTrust.tcc.ui.atividades.mensagens.LatestMessagesActivity
import kotlinx.android.synthetic.main.fragment_home.view.*








class HomeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)
        //Configurando o adaptador e setando o adaptador
        val adapter = MyViewPageAdapter(childFragmentManager)
        view.viewpager.adapter = adapter

        //Configurando as tabs e dando nome para as mesmas
        view.tabs.setupWithViewPager(view.viewpager)
        view.tabs.getTabAt(0)?.text = "Recomendados"
        view.tabs.getTabAt(1)?.text = "Conselhos"

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

class MyViewPageAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager){

    // Array com as tabs
    private val tabTitles = arrayOf("Recomendados", "Conselhos")

    override fun getPageTitle(position: Int): CharSequence? {
        return tabTitles[position]
    }

    override fun getItem(position: Int): Fragment {
        // Retornando o fragmento de acordo com o item selecionado
        return when (position) {
            0 -> RecomendadoFragment()
            1 -> ConselhosFragment()
            else -> RecomendadoFragment()
        }
    }

    override fun getCount(): Int {
        return tabTitles.size
    }


}
