package com.example.fireinstagram.Cerceve;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fireinstagram.Adapter.GonderiAdapter;
import com.example.fireinstagram.Model.Gonderi;
import com.example.fireinstagram.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class GonderiDetayiFragment extends Fragment {
    private RecyclerView recyclerView;
    private GonderiAdapter gonderiAdapter;
    private List<Gonderi> gonderiListesi;
    String gonderiId;


    public GonderiDetayiFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_gonderi_detayi, container, false);

        SharedPreferences preferences=getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        gonderiId=preferences.getString("postid","none");

        recyclerView=view.findViewById(R.id.recycler_view_gonderiDetayi);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        gonderiListesi=new ArrayList<>();
        gonderiAdapter=new GonderiAdapter(getContext(),gonderiListesi);
        recyclerView.setAdapter(gonderiAdapter);

        gonderiOku();
        return view;
    }

    private void gonderiOku() {
        DatabaseReference gonderiYolu= FirebaseDatabase.getInstance().getReference("Gonderiler").child(gonderiId);
        gonderiYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                gonderiListesi.clear();
                Gonderi gonderi=dataSnapshot.getValue(Gonderi.class);
                gonderiListesi.add(gonderi);
                gonderiAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
