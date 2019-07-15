package com.example.fireinstagram;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.fireinstagram.Model.Hikaye;
import com.example.fireinstagram.Model.Kullanici;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import jp.shts.android.storiesprogressview.StoriesProgressView;

public class HikayeActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener {

    int sayac=0;
    long basmaZamani=0L;
    long sinir=500L;
    StoriesProgressView storiesProgressView;
    ImageView resim, hikaye_fotografi;
    TextView hikaye_kullaniciadi;
    String kullaniciid;
    List<String> resimler;
    List<String> hikayeid;
    LinearLayout r_gorulen;
    TextView goruntuleme_sayisi;
    ImageView hikaye_silme;

    private View.OnTouchListener onTouchListener=new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    basmaZamani = System.currentTimeMillis();
                    storiesProgressView.resume();
                    return false;
                case MotionEvent.ACTION_UP:
                    long simdi=System.currentTimeMillis();
                    storiesProgressView.resume();
                    return sinir<simdi-basmaZamani;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hikaye);
        storiesProgressView=findViewById(R.id.hikayeler_hikayeActivity);
        resim=findViewById(R.id.resim_hikayeActivity);
        hikaye_fotografi=findViewById(R.id.hikaye_fotografi_hikayeActivity);
        hikaye_kullaniciadi=findViewById(R.id.hikaye_kullaniciadi_hikayeActivity);
        kullaniciid=getIntent().getStringExtra("kullaniciid");
        r_gorulen=findViewById(R.id.r_gorulen);
        goruntuleme_sayisi=findViewById(R.id.goruntuleme_sayisi);
        hikaye_silme=findViewById(R.id.hikaye_silme);
        View geri=findViewById(R.id.geri_hikayeActivity);
        View atla=findViewById(R.id.atla_hikayeActivity);
        r_gorulen.setVisibility(View.GONE);
        hikaye_silme.setVisibility(View.GONE);

        if (kullaniciid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            r_gorulen.setVisibility(View.VISIBLE);
            hikaye_silme.setVisibility(View.VISIBLE);
        }

        //Metodları cagir
        hikayeleriAl(kullaniciid);
        kullaniciBilgisi(kullaniciid);


        geri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.reverse();
            }
        });
        geri.setOnTouchListener(onTouchListener);

        atla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.skip();
            }
        });
        atla.setOnTouchListener(onTouchListener);

        r_gorulen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HikayeActivity.this,TakipcilerActivity.class);
                intent.putExtra("id",kullaniciid);
                intent.putExtra("hikayeid",hikayeid.get(sayac));
                intent.putExtra("baslik","Görüntüleme");
                startActivity(intent);
            }
        });

        hikaye_silme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference hikayeYolu=FirebaseDatabase.getInstance().getReference("Hikaye")
                        .child(kullaniciid).child(hikayeid.get(sayac));
                hikayeYolu.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(HikayeActivity.this,"Hikaye Silindi!!!",Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onNext() {
        Glide.with(getApplicationContext()).load(resimler.get(++sayac)).into(resim);
        //Metod cagir
        goruntulemeEkle(hikayeid.get(sayac));
        goruntulemeSayisi(hikayeid.get(sayac));
    }

    @Override
    public void onPrev() {
        if (sayac-1<10) return;
        Glide.with(getApplicationContext()).load(resimler.get(--sayac)).into(resim);
        //Metodları cagir
        goruntulemeSayisi(hikayeid.get(sayac));
    }

    @Override
    public void onComplete() {
        finish();
    }

    @Override
    protected void onDestroy() {
        storiesProgressView.destroy();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        storiesProgressView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        storiesProgressView.resume();
        super.onResume();
    }

    private void hikayeleriAl(String kullaniciid){
        resimler=new ArrayList<>();
        hikayeid=new ArrayList<>();
        DatabaseReference hikayeYolu= FirebaseDatabase.getInstance().getReference("Hikaye").child(kullaniciid);
        hikayeYolu.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                resimler.clear();
                hikayeid.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Hikaye hikaye=snapshot.getValue(Hikaye.class);
                    long mevcutzaman=System.currentTimeMillis();
                    if (mevcutzaman>hikaye.getBaslamazamani()&&mevcutzaman<hikaye.getBitiszamani()){
                        resimler.add(hikaye.getResimurl());
                        hikayeid.add(hikaye.getHikayeid());
                    }
                }

                storiesProgressView.setStoriesCount(resimler.size());
                storiesProgressView.setStoryDuration(5000L);
                storiesProgressView.setStoriesListener(HikayeActivity.this);
                storiesProgressView.startStories(sayac);
                Glide.with(getApplicationContext()).load(resimler.get(sayac)).into(resim);
                //Metodlari cagir
                goruntulemeEkle(hikayeid.get(sayac));
                goruntulemeSayisi(hikayeid.get(sayac));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void kullaniciBilgisi(final String kullaniciid){
        DatabaseReference kullaniciYolu=FirebaseDatabase.getInstance().getReference("Kullanıcılar").child(kullaniciid);
        kullaniciYolu.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Kullanici kullanici=dataSnapshot.getValue(Kullanici.class);
                Glide.with(getApplicationContext()).load(kullanici.getResimurl()).into(hikaye_fotografi);
                hikaye_kullaniciadi.setText(kullanici.getKullaniciadi());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void goruntulemeEkle(String hikayeid){
        FirebaseDatabase.getInstance().getReference("Hikaye").child(kullaniciid).child(hikayeid)
                .child("goruntulemeler").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
    }

    private void goruntulemeSayisi(String hikayeid){
        DatabaseReference goruntulemeSayisiYolu=FirebaseDatabase.getInstance().getReference("Hikaye")
                .child(kullaniciid).child(hikayeid).child("goruntulemeler");
        goruntulemeSayisiYolu.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                goruntuleme_sayisi.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
