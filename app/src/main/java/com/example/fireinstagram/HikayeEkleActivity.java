package com.example.fireinstagram;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.example.fireinstagram.Model.Hikaye;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

public class HikayeEkleActivity extends AppCompatActivity {
    private Uri mResimUrl;
    String myUrl;
    private StorageTask depolamaGorevi;
    StorageReference depolamaYoluReferance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hikaye_ekle);

        depolamaYoluReferance= FirebaseStorage.getInstance().getReference("hikaye");
        CropImage.activity().setAspectRatio(8,15).start(HikayeEkleActivity.this);
    }

    private String dosyaUzantisiAl(Uri uri){
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void hikayeYayinla(){
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Gönderiliyor...");
        progressDialog.show();
        if (mResimUrl!=null){
            final StorageReference resimStorageReference=depolamaYoluReferance.child(System.currentTimeMillis()
                    +"."+dosyaUzantisiAl(mResimUrl));
            depolamaGorevi=resimStorageReference.putFile(mResimUrl);
            depolamaGorevi.continueWithTask(new Continuation() {
                @Override
                public Task<Uri> then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return resimStorageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri indirmeUrl=task.getResult();
                        myUrl=indirmeUrl.toString();
                        String myid=FirebaseAuth.getInstance().getCurrentUser().getUid();
                        DatabaseReference hikayeYoluReference= FirebaseDatabase.getInstance().getReference("Hikaye").child(myid);
                       String hikayeid=hikayeYoluReference.push().getKey();
                       long bitiszamani=System.currentTimeMillis()+86400000;
                        HashMap<String, Object> hashMap=new HashMap<>();
                        hashMap.put("resimurl",myUrl);
                        hashMap.put("baslamazamani", ServerValue.TIMESTAMP);
                        hashMap.put("bitiszamani",bitiszamani);
                        hashMap.put("hikayeid",hikayeid);
                        hashMap.put("kullaniciid",myid);
                        hikayeYoluReference.child(hikayeid).setValue(hashMap);
                        progressDialog.dismiss();
                        finish();
                    } else {
                        Toast.makeText(HikayeEkleActivity.this,"Hikaye ekleme başarısız oldu!!!",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(HikayeEkleActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this,"Resim seçili değil!!!",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE&&resultCode==RESULT_OK){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            mResimUrl=result.getUri();

            //Metodu Cagir
            hikayeYayinla();
        } else {
            Toast.makeText(this,"Hikaye ekleme başarısız oldu!",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(HikayeEkleActivity.this,AnaSayfaActivity.class));
            finish();
        }
    }
}
