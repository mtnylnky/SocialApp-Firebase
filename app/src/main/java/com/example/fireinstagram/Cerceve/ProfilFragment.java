package com.example.fireinstagram.Cerceve;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.fireinstagram.Adapter.FotografAdapter;
import com.example.fireinstagram.Model.Gonderi;
import com.example.fireinstagram.Model.Kullanici;
import com.example.fireinstagram.ProfilDuzenleActivity;
import com.example.fireinstagram.R;
import com.example.fireinstagram.SeceneklerActivity;
import com.example.fireinstagram.TakipcilerActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfilFragment extends Fragment {

    ImageView resimSecenekler, profil_resmi;
    TextView txt_gonderiler, txt_takipciler, txt_takipEdilenler, txt_Ad,txt_bio,txt_kullaniciadi;
    Button btn_profili_duzenle;
    ImageButton imgbtn_fotograflarim,imgbtn_kaydedilenFotograflar;
    private List<String> kaydettiklerim;
    RecyclerView recyclerViewKaydettiklerim;
    FotografAdapter fotografAdapterKaydettiklerim;
    private List<Gonderi> gonderiList_kaydetiklerim;
    //Fotoğrafları profilde  görme recycler
    RecyclerView recyclerViewFotograflar;
    FotografAdapter fotografAdapter;
    List<Gonderi> gonderiList;
    FirebaseUser mevcutKullanici;
    String profileId;

    public ProfilFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_profil, container, false);

        mevcutKullanici= FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences prefs=getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileId=prefs.getString("profileid","none");
        resimSecenekler=view.findViewById(R.id.resimSecenekler_profilCerceve);
        profil_resmi=view.findViewById(R.id.profil_resmi_profilCercevesi);
        txt_gonderiler=view.findViewById(R.id.txt_gonderiler_profilCerceve);
        txt_takipciler=view.findViewById(R.id.txt_takipciler_profilCerceve);
        txt_takipEdilenler=view.findViewById(R.id.txt_takipEdilenler_profilCerceve);
        txt_Ad=view.findViewById(R.id.txt_ad_profilCercevesi);
        txt_bio=view.findViewById(R.id.txt_bio_profilCercevesi);
        txt_kullaniciadi=view.findViewById(R.id.txt_kullaniciadi_profilCerceve);
        btn_profili_duzenle=view.findViewById(R.id.btn_profilDuzenle_profilCerceve);
        imgbtn_fotograflarim=view.findViewById(R.id.imgbtn_fotograflarim_profilCercevesi);
        imgbtn_kaydedilenFotograflar=view.findViewById(R.id.imgbtn_kaydedilenFotograflar_profilCercevesi);
        recyclerViewFotograflar=view.findViewById(R.id.recyler_view_profilCercevesi);
        recyclerViewFotograflar.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new GridLayoutManager(getContext(),3);
        recyclerViewFotograflar.setLayoutManager(linearLayoutManager);
        gonderiList=new ArrayList<>();
        fotografAdapter=new FotografAdapter(getContext(),gonderiList);
        recyclerViewFotograflar.setAdapter(fotografAdapter);

        //Kaydettiğim gonderi recycler
        recyclerViewKaydettiklerim=view.findViewById(R.id.recyler_view_kaydet_profilCercevesi);
        recyclerViewKaydettiklerim.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager_Kaydettiklerim=new GridLayoutManager(getContext(),3);
        recyclerViewKaydettiklerim.setLayoutManager(linearLayoutManager_Kaydettiklerim);
        gonderiList_kaydetiklerim=new ArrayList<>();
        fotografAdapterKaydettiklerim=new FotografAdapter(getContext(),gonderiList_kaydetiklerim);
        recyclerViewKaydettiklerim.setAdapter(fotografAdapterKaydettiklerim);
        recyclerViewFotograflar.setVisibility(View.VISIBLE);
        recyclerViewKaydettiklerim.setVisibility(View.GONE);

        //metodlar cagir
        kullaniciBilgisi();
        takipcileriAl();
        gonderiSayisiAl();
        fotograflarim();
        kaydettiklerim();

        if (profileId.equals(mevcutKullanici.getUid())){
            btn_profili_duzenle.setText("Profil Düzenle");
        } else {
            takipKontrolu();
            imgbtn_kaydedilenFotograflar.setVisibility(View.GONE);
        }

        btn_profili_duzenle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btn=btn_profili_duzenle.getText().toString();
                if (btn.equals("Profil Düzenle")){
                    startActivity(new Intent(getContext(), ProfilDuzenleActivity.class));
                } else if (btn.equals("takip et")){
                    FirebaseDatabase.getInstance().getReference().child("Takip").child(mevcutKullanici.getUid())
                            .child("takipEdilenler").child(profileId).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Takip").child(profileId)
                            .child("takipciler").child(mevcutKullanici.getUid()).setValue(true);
                    bildirimleriEkle();
                } else if (btn.equals("takip ediliyor")){
                    FirebaseDatabase.getInstance().getReference().child("Takip").child(mevcutKullanici.getUid())
                            .child("takipEdilenler").child(profileId).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Takip").child(profileId)
                            .child("takipciler").child(mevcutKullanici.getUid()).removeValue();
                }
            }
        });

        resimSecenekler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), SeceneklerActivity.class);
                startActivity(intent);
            }
        });

        imgbtn_fotograflarim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewFotograflar.setVisibility(View.VISIBLE);
                recyclerViewKaydettiklerim.setVisibility(View.INVISIBLE);
            }
        });
        imgbtn_kaydedilenFotograflar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewFotograflar.setVisibility(View.GONE);
                recyclerViewKaydettiklerim.setVisibility(View.VISIBLE);
            }
        });

        txt_takipciler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), TakipcilerActivity.class);
                intent.putExtra("id",profileId);
                intent.putExtra("baslik","takipciler");
                startActivity(intent);
            }
        });

        txt_takipEdilenler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), TakipcilerActivity.class);
                intent.putExtra("id",profileId);
                intent.putExtra("baslik","takip edilenler");
                startActivity(intent);
            }
        });
        return view;
    }

    private void kullaniciBilgisi(){
        DatabaseReference kullaniciYolu=FirebaseDatabase.getInstance().getReference("Kullanıcılar").child(profileId);
        kullaniciYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (getContext()==null){
                    return;
                }
                Kullanici kullanici=dataSnapshot.getValue(Kullanici.class);
                Glide.with(getContext()).load(kullanici.getResimurl()).into(profil_resmi);
                txt_kullaniciadi.setText(kullanici.getKullaniciadi());
                txt_Ad.setText(kullanici.getAd());
                txt_bio.setText(kullanici.getBio());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void takipKontrolu(){
        DatabaseReference takipYolu=FirebaseDatabase.getInstance().getReference().child("Takip")
                .child(mevcutKullanici.getUid()).child("takipEdilenler");
        takipYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(profileId).exists()){
                    btn_profili_duzenle.setText("takip ediliyor");
                } else {
                    btn_profili_duzenle.setText("takip et");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void takipcileriAl(){
        //takipçi sayısını alır
        DatabaseReference takipciYolu=FirebaseDatabase.getInstance().getReference().child("Takip")
                .child(profileId).child("takipciler");
        takipciYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                txt_takipciler.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //Takip edilen syısını alır
        DatabaseReference takipEdilenYolu=FirebaseDatabase.getInstance().getReference().child("Takip")
                .child(profileId).child("takipEdilenler");
        takipEdilenYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                txt_takipEdilenler.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void gonderiSayisiAl(){
        DatabaseReference gonderiYolu=FirebaseDatabase.getInstance().getReference("Gonderiler");
        gonderiYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i=0;
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Gonderi gonderi=snapshot.getValue(Gonderi.class);
                    if (gonderi.getGonderen().equals(profileId)){i++;}
                }
                txt_gonderiler.setText(""+i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void fotograflarim(){
        final DatabaseReference fotografYolu=FirebaseDatabase.getInstance().getReference("Gonderiler");
        fotografYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                gonderiList.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Gonderi gonderi=snapshot.getValue(Gonderi.class);
                    if (gonderi.getGonderen().equals(profileId)){
                        gonderiList.add(gonderi);
                    }
                }
                Collections.reverse(gonderiList);
                fotografAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void kaydettiklerim(){
        kaydettiklerim=new ArrayList<>();
        DatabaseReference kaydettiklerimYolu=FirebaseDatabase.getInstance().getReference("Kaydedilenler")
                .child(mevcutKullanici.getUid());
        kaydettiklerimYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    kaydettiklerim.add(snapshot.getKey());
                }
                kaydettiklerimiOku();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void kaydettiklerimiOku() {
        DatabaseReference gonderidenOku=FirebaseDatabase.getInstance().getReference("Gonderiler");
        gonderidenOku.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                gonderiList_kaydetiklerim.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Gonderi gonderi=snapshot.getValue(Gonderi.class);
                    for (String id:kaydettiklerim){
                        if (gonderi.getGonderiId().equals(id)){
                            gonderiList_kaydetiklerim.add(gonderi);
                        }
                    }
                    fotografAdapterKaydettiklerim.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void bildirimleriEkle(){
        DatabaseReference bildirimEkleYolu=FirebaseDatabase.getInstance().getReference("Bildirimler").child(profileId);

        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("kullaniciid",mevcutKullanici.getUid());
        hashMap.put("text","seni takip etmeye başladı");
        hashMap.put("gonderiid","");
        hashMap.put("ispost",false);
        bildirimEkleYolu.push().setValue(hashMap);
    }

}
