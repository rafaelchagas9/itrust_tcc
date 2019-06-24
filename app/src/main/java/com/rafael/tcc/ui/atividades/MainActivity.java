package com.rafael.tcc.ui.atividades;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.rafael.tcc.R;
import com.rafael.tcc.ui.fragmentos.FavoriteFragment;
import com.rafael.tcc.ui.fragmentos.HomeFragment;
import com.rafael.tcc.ui.fragmentos.ProfileFragment;
import com.rafael.tcc.ui.fragmentos.SearchFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Declaração do navigation view e declaração do Listener
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav_view);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        //Definindo que o programa deve iniciar com o Fragmento Home e não em branco
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        
    }
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment fragmentoSelecionado = null;

                    //Definindo o fragmento a ser exibido através do id do item selecionado no menu
                    switch (item.getItemId()){
                        case R.id.navigation_home:
                            fragmentoSelecionado = new HomeFragment();
                            break;
                        case R.id.navigation_search:
                            fragmentoSelecionado = new SearchFragment();
                            break;
                        case R.id.navigation_favorites:
                            fragmentoSelecionado = new FavoriteFragment();
                            break;
                        case R.id.navigation_perfil:
                            fragmentoSelecionado = new ProfileFragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in,
                            android.R.anim.fade_out).replace(R.id.fragment_container, fragmentoSelecionado).commit();

                    return true;
                }
            };
}


