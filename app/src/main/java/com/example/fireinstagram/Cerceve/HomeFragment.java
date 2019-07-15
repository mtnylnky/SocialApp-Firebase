package com.example.fireinstagram.Cerceve;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.fireinstagram.Adapter.GonderiAdapter;
import com.example.fireinstagram.Adapter.HikayeAdapter;
import com.example.fireinstagram.Model.Gonderi;
import com.example.fireinstagram.Model.Hikaye;
import com.example.fireinstagram.R;
import com.google.firebase.auth.FirebaseAuth;
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
public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private GonderiAdapter gonderiAdapter;
    private List<Gonderi> gonderiListeleri;
    private List<String> takipListesi;
    ProgressBar progressBar;

    //Hikaye bolumu
    private RecyclerView recyclerViewHikaye;
    private HikayeAdapter hikayeAdapter;
    private List<Hikaye> hikayeListesi;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView=view.findViewById(R.id.recycler_view_HomeFragment);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        gonderiListeleri=new ArrayList<>();
        gonderiAdapter=new GonderiAdapter(getContext(),gonderiListeleri);
        recyclerView.setAdapter(gonderiAdapter);

        //Hikaye bolumu
        recyclerViewHikaye=view.findViewById(R.id.recycler_view_hikaye_HomeFragment);
        recyclerViewHikaye.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager1=new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        recyclerViewHikaye.setLayoutManager(linearLayoutManager1);
        hikayeListesi=new ArrayList<>();
        hikayeAdapter=new HikayeAdapter(getContext(),hikayeListesi);
        recyclerViewHikaye.setAdapter(hikayeAdapter);


        progressBar=view.findViewById(R.id.progress_homeFragment);
        takipKontrolu();
        return view;
    }

    private void takipKontrolu(){
        takipListesi=new ArrayList<>();
        DatabaseReference takipyolu= FirebaseDatabase.getInstance().getReference("Takip")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("takipEdilenler");

        takipyolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                takipListesi.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    takipListesi.add(snapshot.getKey());
                }
                gonderileriOku();
                hikayeOku();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void gonderileriOku(){
        DatabaseReference gonderiYolu= FirebaseDatabase.getInstance().getReference("Gonderiler");
        gonderiYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                gonderiListeleri.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Gonderi gonderi=snapshot.getValue(Gonderi.class);
                    for (String id : takipListesi){
                        if (gonderi.getGonderen().equals(id)){
                            gonderiListeleri.add(gonderi);
                        }
                    }
                }
                gonderiAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void hikayeOku(){
        final DatabaseReference hikayeYolu=FirebaseDatabase.getInstance().getReference("Hikaye");
        hikayeYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long simdikizaman=System.currentTimeMillis();
                hikayeListesi.clear();
                hikayeListesi.add(new Hikaye("","",FirebaseAuth.getInstance().getCurrentUser().getUid(),0,0));
                for (String id:takipListesi){
                    int hikayeSayaci=0;
                    Hikaye hikaye=null;
                    for (DataSnapshot snapshot:dataSnapshot.child(id).getChildren()){
                        hikaye=snapshot.getValue(Hikaye.class);
                        if (simdikizaman>hikaye.getBaslamazamani()&& simdikizaman<hikaye.getBitiszamani()){
                            hikayeSayaci++;
                        }
                    }
                    if (hikayeSayaci>0){
                        hikayeListesi.add(hikaye);
                    }
                }
                hikayeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
