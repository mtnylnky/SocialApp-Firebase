package com.example.fireinstagram;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    Button btn_main_giris, btn_main_kaydol;
    FirebaseUser baslangickullanici;


    @Override
    protected void onStart() {
        super.onStart();

        baslangickullanici= FirebaseAuth.getInstance().getCurrentUser();
        if (baslangickullanici!=null){
            startActivity(new Intent(MainActivity.this,AnaSayfaActivity.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_main_giris=findViewById(R.id.btn_main_giris);
        btn_main_kaydol=findViewById(R.id.btn_main_kaydol);

        btn_main_giris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,GirisActivity.class));
            }
        });

        btn_main_kaydol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,KaydolActivity.class));
            }
        });
    }
}
