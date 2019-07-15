package com.example.fireinstagram;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.fireinstagram.Cerceve.AramaFragment;
import com.example.fireinstagram.Cerceve.BildirimFragment;
import com.example.fireinstagram.Cerceve.HomeFragment;
import com.example.fireinstagram.Cerceve.ProfilFragment;
import com.google.firebase.auth.FirebaseAuth;

public class AnaSayfaActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Fragment seciliCerceve=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ana_sayfa);

        bottomNavigationView=findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        Bundle intent =getIntent().getExtras();
        if (intent!=null){
            String gonderen=intent.getString("gonderenId");
            SharedPreferences.Editor editor=getSharedPreferences("PREFS",MODE_PRIVATE).edit();
            editor.putString("profileid",gonderen);
            editor.apply();
            getSupportFragmentManager().beginTransaction().replace(R.id.cerceve_kapsayici,new ProfilFragment()).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.cerceve_kapsayici,new HomeFragment()).commit();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener=
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()){
                        case R.id.nav_home:
                            //Ana çerçeveyi çağır
                            seciliCerceve= new HomeFragment();
                            break;
                        case R.id.nav_arama:
                            //Arama çerçeveyi çağır
                            seciliCerceve= new AramaFragment();
                            break;
                        case R.id.nav_ekle:
                            //Ekle çerçeveyi çağır
                            seciliCerceve=null;
                            startActivity(new Intent(AnaSayfaActivity.this,GonderiActivity.class));
                            break;
                        case R.id.nav_kalp:
                            //Favori çerçeveyi çağır
                            seciliCerceve= new BildirimFragment();
                            break;
                        case R.id.nav_profil:
                            //Profil çerçeveyi çağır
                            SharedPreferences.Editor editor=getSharedPreferences("PREFS",MODE_PRIVATE).edit();
                            editor.putString("profileid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                            editor.apply();
                            seciliCerceve= new ProfilFragment();
                            break;

                    }
                    if (seciliCerceve!=null){
                        getSupportFragmentManager().beginTransaction().replace(R.id.cerceve_kapsayici,seciliCerceve).commit();
                    }
                    return true;
                }
            };
}
