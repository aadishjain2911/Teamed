package com.example.myblog;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class PostActivity extends AppCompatActivity {

    Button post ;
    ImageButton image ;
    EditText name,description ;
    Uri imageuri=null ;

    private static final int GALLERY_REQUEST=1 ;

    private StorageReference storage ;

    private ProgressDialog progress ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        storage = FirebaseStorage.getInstance().getReference() ;

        progress = new ProgressDialog(this) ;

        post = (Button) findViewById(R.id.postButton) ;
        image = (ImageButton) findViewById(R.id.imageButton) ;
        name = (EditText) findViewById(R.id.name) ;
        description = (EditText) findViewById(R.id.description) ;

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryintent = new Intent(Intent.ACTION_GET_CONTENT) ;
                galleryintent.setType("image/*") ;
                startActivityForResult(galleryintent,GALLERY_REQUEST) ;

            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               startPost();

            }
        });

    }

    private void startPost() {

        progress.setMessage("Posting to Blog...");
        progress.show();

        String nm = name.getText().toString().trim() ;
        String desc = description.getText().toString().trim() ;

        if (!TextUtils.isEmpty(nm) && !TextUtils.isEmpty(desc) && imageuri!=null) {

            StorageReference filepath = storage.child("Images").child(imageuri.getLastPathSegment()) ;

            filepath.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Task<Uri> downloadUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl() ;

                    downloadUrl.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            String imagelink = uri.toString() ;

                        }
                    });
                }
            });

        }

        progress.dismiss();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode==RESULT_OK) {

            imageuri = data.getData() ;
            image.setImageURI(imageuri) ;

        }
    }
}
