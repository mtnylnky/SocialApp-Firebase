package com.example.fireinstagram.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.fireinstagram.Cerceve.GonderiDetayiFragment;
import com.example.fireinstagram.Model.Gonderi;
import com.example.fireinstagram.R;

import java.util.List;

public class FotografAdapter extends RecyclerView.Adapter<FotografAdapter.ViewHolder> {

    private Context context;
    private List<Gonderi> mGonderiler;

    public FotografAdapter(Context context, List<Gonderi> mGonderiler) {
        this.context = context;
        this.mGonderiler = mGonderiler;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(context).inflate(R.layout.fotograflar_ogesi,viewGroup,false);
        return new FotografAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final Gonderi gonderi=mGonderiler.get(i);
        Glide.with(context).load(gonderi.getGonderiResmi()).into(viewHolder.gonderi_resmi);

        //Gonderi resmine tıklama Olayı
        viewHolder.gonderi_resmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor =context.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("postid",gonderi.getGonderiId());
                editor.apply();
                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.cerceve_kapsayici,
                        new GonderiDetayiFragment()).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mGonderiler.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView gonderi_resmi;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            gonderi_resmi=itemView.findViewById(R.id.gonderi_resmi_fotograflarOgesi);
        }
    }
}
