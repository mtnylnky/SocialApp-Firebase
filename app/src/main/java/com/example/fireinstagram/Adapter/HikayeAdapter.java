package com.example.fireinstagram.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.fireinstagram.HikayeActivity;
import com.example.fireinstagram.HikayeEkleActivity;
import com.example.fireinstagram.Model.Hikaye;
import com.example.fireinstagram.Model.Kullanici;
import com.example.fireinstagram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class HikayeAdapter extends RecyclerView.Adapter<HikayeAdapter.ViewHolder>{

    private Context mContext;
    private List<Hikaye> mHikaye;

    public HikayeAdapter(Context mContext, List<Hikaye> mHikaye) {
        this.mContext = mContext;
        this.mHikaye = mHikaye;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i==0){
            View view= LayoutInflater.from(mContext).inflate(R.layout.hikaye_ekleme_ogesi,viewGroup,false);
            return new HikayeAdapter.ViewHolder(view);
        } else {
            View view=LayoutInflater.from(mContext).inflate(R.layout.hikaye_ogesi,viewGroup,false);
            return new HikayeAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final Hikaye hikaye=mHikaye.get(i);

        //metodları çağırma
        kullaniciBilgisi(viewHolder,hikaye.getKullaniciid(),i);
        if (viewHolder.getAdapterPosition()!=0){
            gorulenHikayeler(viewHolder,hikaye.getKullaniciid());
        }
        if (viewHolder.getAdapterPosition()==0){
            hikayem(viewHolder.txt_hikaye_ekle,viewHolder.img_hikaye_ekle_fotografi,false);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.getAdapterPosition()==0){
                    hikayem(viewHolder.txt_hikaye_ekle,viewHolder.img_hikaye_ekle_fotografi,true);
                }else {
                    //Hikayeye gidecek
                    Intent intent=new Intent(mContext, HikayeActivity.class);
                    intent.putExtra("kullaniciid",hikaye.getKullaniciid());
                    mContext.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mHikaye.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView img_hikaye_fotografi, img_hikaye_ekle_fotografi, img_hikaye_gorulen_fotografi;
        public TextView txt_hikaye_kullaniciadi, txt_hikaye_ekle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            img_hikaye_fotografi=itemView.findViewById(R.id.hikaye_fotografi_hikayeOgesi);
            img_hikaye_ekle_fotografi=itemView.findViewById(R.id.hikaye_ekle_resmi_hikayeEklemeOgesi);
            img_hikaye_gorulen_fotografi=itemView.findViewById(R.id.hikaye_fotografi_gorulen_hikayeOgesi);
            txt_hikaye_kullaniciadi=itemView.findViewById(R.id.txt_hikaye_kullanici_adi_hikayeOgesi);
            txt_hikaye_ekle=itemView.findViewById(R.id.txt_hikayeEkle_hikayeEkleOgesi);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position==0){
            return 0;
        }
        return 1;
    }

    private void kullaniciBilgisi(final ViewHolder viewHolder, final String kullaniciid, final int pos){
        DatabaseReference kullaniciYolu= FirebaseDatabase.getInstance().getReference("Kullanıcılar").child(kullaniciid);
        kullaniciYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Kullanici kullanici=dataSnapshot.getValue(Kullanici.class);
                Glide.with(mContext).load(kullanici.getResimurl()).into(viewHolder.img_hikaye_fotografi);
                if (pos!=0){
                    Glide.with(mContext).load(kullanici.getResimurl()).into(viewHolder.img_hikaye_gorulen_fotografi);
                    viewHolder.txt_hikaye_kullaniciadi.setText(kullanici.getKullaniciadi());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void hikayem(final TextView textView, final ImageView imageView, final boolean click){
        DatabaseReference hikayeYolu=FirebaseDatabase.getInstance().getReference("Hikaye")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        hikayeYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int sayac=0;
                long simdikizaman=System.currentTimeMillis();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Hikaye hikaye=snapshot.getValue(Hikaye.class);
                    if (simdikizaman>hikaye.getBaslamazamani()&&simdikizaman<hikaye.getBitiszamani()){
                        sayac++;
                    }
                }
                if (click){
                    if (sayac<0){
                        AlertDialog alertDialog=new AlertDialog.Builder(mContext).create();
                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Hikaye Görüntüleme",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Hikaye sayfasına gidecek

                                        Intent intent=new Intent(mContext, HikayeActivity.class);
                                        intent.putExtra("kullaniciid",FirebaseAuth.getInstance().getCurrentUser());
                                        mContext.startActivity(intent);

                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Hikaye Ekle",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent hikayeEkleIntent=new Intent(mContext, HikayeEkleActivity.class);
                                        mContext.startActivity(hikayeEkleIntent);
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    } else {
                        Intent hikayeEklemeIntent=new Intent(mContext,HikayeEkleActivity.class);
                        mContext.startActivity(hikayeEklemeIntent);
                    }
                } else {
                    if (sayac>0){
                        textView.setText("Hikayem");
                        imageView.setVisibility(View.GONE);
                    } else {
                        textView.setText("Hikaye Ekle");
                        imageView.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void gorulenHikayeler(final ViewHolder viewHolder, String kullaniciid){
        DatabaseReference hikayeYolu=FirebaseDatabase.getInstance().getReference("Hikaye").child(kullaniciid);
        hikayeYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i=0;
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    if (!snapshot.child("görüntülemeler").child(FirebaseAuth.getInstance().getUid()).exists()&&System.currentTimeMillis()<snapshot.getValue(Hikaye.class).getBitiszamani()){
                        i++;
                    }
                }
                if (i>0){
                    viewHolder.img_hikaye_fotografi.setVisibility(View.VISIBLE);
                    viewHolder.img_hikaye_gorulen_fotografi.setVisibility(View.GONE);
                } else {
                    viewHolder.img_hikaye_fotografi.setVisibility(View.GONE);
                    viewHolder.img_hikaye_gorulen_fotografi.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
