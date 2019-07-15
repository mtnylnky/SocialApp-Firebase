package com.example.fireinstagram.Adapter;

import com.example.fireinstagram.R;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.fireinstagram.AnaSayfaActivity;
import com.example.fireinstagram.Model.Kullanici;
import com.example.fireinstagram.Model.Yorum;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class YorumAdapter extends RecyclerView.Adapter<YorumAdapter.ViewHolder>{

    private Context mContext;
    private List<Yorum> mYorumListesi;

    private FirebaseUser mevcutKullanici;

    public YorumAdapter(Context mContext, List<Yorum> mYorumListesi) {
        this.mContext = mContext;
        this.mYorumListesi = mYorumListesi;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.yorum_ogesi,viewGroup,false);
        return new YorumAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        mevcutKullanici= FirebaseAuth.getInstance().getCurrentUser();
        final Yorum yorum=mYorumListesi.get(i);
        viewHolder.txt_yorum.setText(yorum.getYorum());
        kullaniciBilgisiAl(viewHolder.profil_resmi,viewHolder.txt_kullanici_adi,yorum.getGonderen());

        viewHolder.txt_yorum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, AnaSayfaActivity.class);
                intent.putExtra("gonderenId",yorum.getGonderen());
                mContext.startActivity(intent);
            }
        });

        viewHolder.profil_resmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, AnaSayfaActivity.class);
                intent.putExtra("gonderenId",yorum.getGonderen());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mYorumListesi.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView profil_resmi;
        public TextView txt_kullanici_adi, txt_yorum;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profil_resmi=itemView.findViewById(R.id.profil_resmi_yorumOgesi);
            txt_kullanici_adi=itemView.findViewById(R.id.txt_kullaniciadi_yorumOgesi);
            txt_yorum=itemView.findViewById(R.id.txt_yorum_yorumOgesi);
        }
    }

    private void kullaniciBilgisiAl(final ImageView imageView, final TextView kullaniciadi, String gonderenId){
        DatabaseReference gonderenIdyolu= FirebaseDatabase.getInstance().getReference().child("Kullanıcılar").child(gonderenId);
        gonderenIdyolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Kullanici kullanici=dataSnapshot.getValue(Kullanici.class);
                Glide.with(mContext).load(kullanici.getResimurl()).into(imageView);
                kullaniciadi.setText(kullanici.getAd());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
