package com.example.fireinstagram.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.fireinstagram.AnaSayfaActivity;
import com.example.fireinstagram.Cerceve.ProfilFragment;
import com.example.fireinstagram.Model.Kullanici;
import com.example.fireinstagram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class KullaniciAdapter extends RecyclerView.Adapter<KullaniciAdapter.ViewHolder>{

    private Context mContext;
    private List<Kullanici> mKullanicilar;
    private boolean isfragment;
    private FirebaseUser firebaseKullanici;

    public KullaniciAdapter(Context mContext, List<Kullanici> mKullanicilar, boolean isfragment) {
        this.mContext = mContext;
        this.mKullanicilar = mKullanicilar;
        this.isfragment = isfragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view=LayoutInflater.from(mContext).inflate(R.layout.kullanici_ogesi,viewGroup,false);
        return new KullaniciAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        firebaseKullanici= FirebaseAuth.getInstance().getCurrentUser();
        final Kullanici kullanici=mKullanicilar.get(i);
        viewHolder.btn_takip_et.setVisibility(View.VISIBLE);
        viewHolder.kullaniciadi.setText(kullanici.getKullaniciadi());
        viewHolder.ad.setText(kullanici.getAd());
        Glide.with(mContext).load(kullanici.getResimurl()).into(viewHolder.profil_resmi);
        takipEdiliyor(kullanici.getId(),viewHolder.btn_takip_et);

        if (kullanici.getId().equals(firebaseKullanici.getUid())){
            viewHolder.btn_takip_et.setVisibility(View.GONE);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isfragment){

                    SharedPreferences.Editor editor=mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                    editor.putString("profileid",kullanici.getId());
                    editor.apply();

                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.cerceve_kapsayici,new ProfilFragment()).commit();
                } else {
                    Intent intent=new Intent(mContext, AnaSayfaActivity.class);
                    intent.putExtra("gonderenId",kullanici.getId());
                    mContext.startActivity(intent);
                }
            }
        });

        viewHolder.btn_takip_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.btn_takip_et.getText().toString().equals("Takip Et")){
                    FirebaseDatabase.getInstance().getReference().child("Takip").child(firebaseKullanici.getUid()).child("takipEdilenler").child(kullanici.getId()).setValue(true);

                    FirebaseDatabase.getInstance().getReference().child("Takip").child(kullanici.getId()).child("takipciler").child(firebaseKullanici.getUid()).setValue(true);

                    bildirimleriEkle(kullanici.getId());
                }
                else {
                    FirebaseDatabase.getInstance().getReference().child("Takip").child(firebaseKullanici.getUid()).child("takipEdilenler").child(kullanici.getId()).removeValue();

                    FirebaseDatabase.getInstance().getReference().child("Takip").child(kullanici.getId()).child("takipciler").child(firebaseKullanici.getUid()).removeValue();
                }
            }
        });
    }

    private void bildirimleriEkle(String kullaniciId){
        DatabaseReference bildirimEkleYolu=FirebaseDatabase.getInstance().getReference("Bildirimler").child(kullaniciId);

        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("kullaniciid",firebaseKullanici.getUid());
        hashMap.put("text","takibe başladı");
        hashMap.put("gonderiid","");
        hashMap.put("ispost",false);
        bildirimEkleYolu.push().setValue(hashMap);
    }

    @Override
    public int getItemCount() {

        return mKullanicilar.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView kullaniciadi;
        public TextView ad;
        public CircleImageView profil_resmi;
        public Button btn_takip_et;
        public ViewHolder(@NonNull View itemView){

            super(itemView);
            kullaniciadi=itemView.findViewById(R.id.txt_kullaniciadi_oge);
            ad=itemView.findViewById(R.id.txt_ad_oge);
            profil_resmi=itemView.findViewById(R.id.profil_resmi_oge);
            btn_takip_et=itemView.findViewById(R.id.btn_takip_et);
        }
    }

    private void takipEdiliyor(final String kullaniciId, final Button button){
        DatabaseReference takipYolu= FirebaseDatabase.getInstance().getReference().child("Takip").child(firebaseKullanici.getUid()).child("takipEdilenler");
        takipYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(kullaniciId).exists()){
                    button.setText("Takip Ediliyor");
                } else {
                    button.setText("Takip Et");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
