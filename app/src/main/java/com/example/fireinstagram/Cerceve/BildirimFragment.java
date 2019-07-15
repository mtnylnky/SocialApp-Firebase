package com.example.fireinstagram.Cerceve;


import android.os.Bundle;
import android.service.autofill.Dataset;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.fireinstagram.Adapter.BildirimAdapter;
import com.example.fireinstagram.Model.Bildirim;
import com.example.fireinstagram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class BildirimFragment extends Fragment {

    private RecyclerView recyclerView;
    private BildirimAdapter bildirimAdapter;
    private List<Bildirim> bildirimListesi;

    public BildirimFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_bildirim, container, false);
        recyclerView=view.findViewById(R.id.recycler_view_bildirimCercevesi);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        bildirimListesi=new ArrayList<>();
        bildirimAdapter=new BildirimAdapter(getContext(),bildirimListesi);
        recyclerView.setAdapter(bildirimAdapter);

        bildirimleriOku();

        return view;
    }

    private void bildirimleriOku() {
        FirebaseUser mevcutKullanici= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference bildirimYolu= FirebaseDatabase.getInstance().getReference("Bildirimler").child(mevcutKullanici.getUid());
        bildirimYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bildirimListesi.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Bildirim bildirim=snapshot.getValue(Bildirim.class);
                    bildirimListesi.add(bildirim);
                }
                Collections.reverse(bildirimListesi);
                bildirimAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
