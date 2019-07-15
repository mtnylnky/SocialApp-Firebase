package com.example.fireinstagram;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class KaydolActivity extends AppCompatActivity {

    EditText edt_kullaniciadi, edt_adi, edt_email, edt_sifre;
    Button btn_kaydol;
    TextView txt_girissayfasinagit;
    FirebaseAuth yetki;
    DatabaseReference yol;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kaydol);

        edt_kullaniciadi=findViewById(R.id.edt_kullaniciadi);
        edt_adi=findViewById(R.id.edt_adi);
        edt_email=findViewById(R.id.edt_email);
        edt_sifre=findViewById(R.id.edt_sifre);
        btn_kaydol=findViewById(R.id.btn_kaydol);
        txt_girissayfasinagit=findViewById(R.id.txt_girissayfasi_git);
        yetki=FirebaseAuth.getInstance();

        txt_girissayfasinagit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(KaydolActivity.this,GirisActivity.class));
            }
        });

        btn_kaydol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd=new ProgressDialog(KaydolActivity.this);
                pd.setMessage("Lütfen Bekleyin");
                pd.show();

                String str_kullaniciadi=edt_kullaniciadi.getText().toString();
                String str_adi=edt_adi.getText().toString();
                String str_email=edt_email.getText().toString();
                String str_sifre=edt_sifre.getText().toString();

                if (TextUtils.isEmpty(str_kullaniciadi)||TextUtils.isEmpty(str_email)||TextUtils.isEmpty(str_email)||TextUtils.isEmpty(str_sifre)){
                    Toast.makeText(KaydolActivity.this,"Lütfen tüm alanları doldurunuz!!!",Toast.LENGTH_SHORT).show();
                }else if (str_sifre.length()<6){
                    Toast.makeText(KaydolActivity.this,"Şifreniz minimum 6 karakterden oluşmalı!!!",Toast.LENGTH_SHORT).show();
                }else{
                    kaydet(str_kullaniciadi, str_adi, str_email, str_sifre);
                }
            }
        });
    }

    private void kaydet(final String kullaniciadi,final String ad,String email,String sifre){
        yetki.createUserWithEmailAndPassword(email, sifre)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){
                            FirebaseUser firebasekullanici=yetki.getCurrentUser();
                            String kullaniciId=firebasekullanici.getUid();
                            yol= FirebaseDatabase.getInstance().getReference().child("Kullanıcılar").child(kullaniciId);
                            HashMap<String, Object> hashMap=new HashMap<>();
                            hashMap.put("id",kullaniciId);
                            hashMap.put("kullaniciadi",kullaniciadi.toLowerCase());
                            hashMap.put("ad",ad);
                            hashMap.put("bio","");
                            hashMap.put("resimurl","https://firebasestorage.googleapis.com/v0/b/fireinstagram-8e7cb.appspot.com/o/placeholder.png?alt=media&token=829407ff-c54a-4fbb-a460-dccedc65c035");

                            yol.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        pd.dismiss();
                                        Intent intent=new Intent(KaydolActivity.this,AnaSayfaActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                }
                            });
                        }else {
                            Toast.makeText(KaydolActivity.this,"Bu email adresi veya şifre ile kayıt başarısız.",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
