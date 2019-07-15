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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

public class GonderiActivity extends AppCompatActivity {

    Uri resimUri;
    String benimUrim="";
    StorageTask yuklemeGorevi;
    StorageReference resimYukleYolu;

    ImageView image_Kapat,image_Eklendi;
    TextView txt_Gonder;
    EditText edt_Gonderi_hakkinda,Hakkinda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gonderi);

        image_Kapat=findViewById(R.id.close_Gonderi);
        image_Eklendi=findViewById(R.id.eklenen_resim_gonderisi);
        txt_Gonder=findViewById(R.id.txt_Gonder);
        edt_Gonderi_hakkinda=findViewById(R.id.edt_gonderi_hakkinda);

        resimYukleYolu= FirebaseStorage.getInstance().getReference("gonderiler");
        image_Kapat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GonderiActivity.this,AnaSayfaActivity.class));
                finish();
            }
        });

        txt_Gonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resimYukle();
            }
        });

        CropImage.activity().setAspectRatio(1,1).start(GonderiActivity.this);
    }

    private String dosyasUzantisiAl(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void resimYukle(){
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Gönderiliyor...");
        progressDialog.show();

        if (resimUri != null){
            final StorageReference dosyaYolu=resimYukleYolu.child(System.currentTimeMillis()+"."+dosyasUzantisiAl(resimUri));
            yuklemeGorevi=dosyaYolu.putFile(resimUri);
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
                public void onComplete(@NonNull Task<Uri> task) {

                    if (task.isSuccessful()){
                        Uri indirmeUrisi=task.getResult();
                        benimUrim=indirmeUrisi.toString();

                        DatabaseReference veriYolu=FirebaseDatabase.getInstance().getReference("Gonderiler");
                        String gonderiId=veriYolu.push().getKey();

                        HashMap<String,Object> hashMap=new HashMap<>();
                        hashMap.put("gonderiId",gonderiId);
                        hashMap.put("gonderiResmi",benimUrim);
                        hashMap.put("gonderiHakkinda",edt_Gonderi_hakkinda.getText().toString());
                        hashMap.put("gonderen", FirebaseAuth.getInstance().getCurrentUser().getUid());

                        veriYolu.child(gonderiId).setValue(hashMap);
                        progressDialog.dismiss();
                        startActivity(new Intent(GonderiActivity.this,AnaSayfaActivity.class));
                        finish();
                    } else{
                        Toast.makeText(GonderiActivity.this,"Gönderme Başarısız",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(GonderiActivity.this,AnaSayfaActivity.class));
                        finish();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(GonderiActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(GonderiActivity.this,"Seçilen resim yok",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE || resultCode ==RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            resimUri=result.getUri();
            image_Eklendi.setImageURI(resimUri);
        } else {
            Toast.makeText(this,"Resim Seçilmedi",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(GonderiActivity.this,AnaSayfaActivity.class));
            finish();
        }
    }
}
