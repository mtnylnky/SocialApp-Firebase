package com.example.fireinstagram;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.fireinstagram.Model.Kullanici;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

public class ProfilDuzenleActivity extends AppCompatActivity {

    ImageView resim_kapatma, resim_profil;
    TextView txt_kaydet, txt_fotograf_degistir;
    MaterialEditText mEdt_ad, mEdt_Kullaniciadi, mEdt_biyografi;

    FirebaseUser mevcutKullanici;
    private StorageTask yuklemeGorevi;
    private Uri mResimUri;
    StorageReference depolamaYolu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil_duzenle);

        resim_kapatma=findViewById(R.id.kapat_resmi_profilDuzenleActivity);
        resim_profil=findViewById(R.id.profil_resmi_profilDuzenleActivity);
        txt_kaydet=findViewById(R.id.txt_kaydet_profilDuzenleActivity);
        txt_fotograf_degistir=findViewById(R.id.txt_fotograf_degistir_profilDuzenleActivity);
        mEdt_ad=findViewById(R.id.material_edt_text_Ad_profilDuzenleActivity);
        mEdt_Kullaniciadi=findViewById(R.id.material_edt_text_KullaniciAdi_profilDuzenleActivity);
        mEdt_biyografi=findViewById(R.id.material_edt_text_Biyografi_profilDuzenleActivity);

        mevcutKullanici= FirebaseAuth.getInstance().getCurrentUser();
        depolamaYolu= FirebaseStorage.getInstance().getReference("yuklemeler");
        DatabaseReference kullaniciYolu= FirebaseDatabase.getInstance().getReference("Kullanıcılar").child(mevcutKullanici.getUid());
        kullaniciYolu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Kullanici kullanici=dataSnapshot.getValue(Kullanici.class);
                mEdt_ad.setText(kullanici.getAd());
                mEdt_Kullaniciadi.setText(kullanici.getKullaniciadi());
                mEdt_biyografi.setText(kullanici.getBio());
                Glide.with(getApplicationContext()).load(kullanici.getResimurl()).into(resim_profil);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Kapat tıklaması
        resim_kapatma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Fotograf değiştir yazsına tıklandıgında
        txt_fotograf_degistir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setAspectRatio(1,1)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(ProfilDuzenleActivity.this);
            }
        });

        //Profil resmine tıkladığında
        resim_profil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setAspectRatio(1,1)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(ProfilDuzenleActivity.this);
            }
        });

        //Kaydet tıklandığında
        txt_kaydet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profiliGuncelle(mEdt_ad.getText().toString(),mEdt_Kullaniciadi.getText().toString(),mEdt_biyografi.getText().toString());
            }
        });
    }

    private void profiliGuncelle(String ad, String kullaniciadi, String biyografi) {
        DatabaseReference guncellemeYolu=FirebaseDatabase.getInstance().getReference("Kullanıcılar").child(mevcutKullanici.getUid());
        HashMap<String,Object> kullaniciGuncelleHashmap=new HashMap<>();
        kullaniciGuncelleHashmap.put("ad",ad);
        kullaniciGuncelleHashmap.put("kullaniciadi",kullaniciadi);
        kullaniciGuncelleHashmap.put("bio",biyografi);
        guncellemeYolu.updateChildren(kullaniciGuncelleHashmap);
    }

    private String dosyaUzantisiAl(Uri uri){
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void resimYukle(){
        final ProgressDialog pd =new ProgressDialog(this);
        pd.setMessage("Yükleniyor...");
        pd.show();
        if (mResimUri!=null){
            final StorageReference dosyaYolu=depolamaYolu.child(System.currentTimeMillis()+"."+dosyaUzantisiAl(mResimUri));
            yuklemeGorevi=dosyaYolu.putFile(mResimUri);
            yuklemeGorevi.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return dosyaYolu.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task <Uri> task) {
                    if (task.isSuccessful()){
                        Uri indirmeUrisi=task.getResult();
                        String benimUrim=indirmeUrisi.toString();
                        DatabaseReference kullaniciYolu=FirebaseDatabase.getInstance().getReference("Kullanıcılar").child(mevcutKullanici.getUid());
                        HashMap<String,Object> resimHashMap=new HashMap<>();
                        resimHashMap.put("resimurl",""+benimUrim);
                        kullaniciYolu.updateChildren(resimHashMap);
                        pd.dismiss();
                    }
                    else {
                        Toast.makeText(ProfilDuzenleActivity.this,"Yükleme Başarısız!!!",Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProfilDuzenleActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{
            Toast.makeText(this,"Resim Seçilmedi",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE&&resultCode==RESULT_OK){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            mResimUri=result.getUri();
            resimYukle();
        }
        else {
            Toast.makeText(this,"Birşeyler ters gitti!",Toast.LENGTH_SHORT).show();
        }
    }
}
