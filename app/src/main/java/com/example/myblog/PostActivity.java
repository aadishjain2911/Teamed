package com.example.myblog;

import androidx.annotation.NonNull;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class PostActivity extends AppCompatActivity {

        Button post ;
        ImageButton image ;
        EditText name,description ;
        Uri imageuri=null ;

        private ProgressBar postProgress ;

        private static final int GALLERY_REQUEST=1 ;

        private StorageReference storage ;
        private DatabaseReference mDatabase ;
        private FirebaseAuth mAuth ;
        private FirebaseFirestore firebaseFirestore ;

        private String user_id ;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_post);

            firebaseFirestore = FirebaseFirestore.getInstance() ;
            storage = FirebaseStorage.getInstance().getReference() ;
            mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog") ;
            mAuth = FirebaseAuth.getInstance() ;

            post = (Button) findViewById(R.id.postButton) ;
            image = (ImageButton) findViewById(R.id.imageButton) ;
            name = (EditText) findViewById(R.id.name) ;
            description = (EditText) findViewById(R.id.description) ;
            postProgress = (ProgressBar) findViewById(R.id.post_progress) ;

            user_id = mAuth.getCurrentUser().getUid() ;

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

            final String nm = name.getText().toString().trim() ;
            final String desc = description.getText().toString().trim() ;

            if (!TextUtils.isEmpty(nm) && !TextUtils.isEmpty(desc) && imageuri!=null) {

                postProgress.setVisibility(View.VISIBLE);

                StorageReference imagepath = storage.child("blog_images").child(user_id + ".jpg");
                imagepath.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> downloadUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl();

                        downloadUrl.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                String imagelink = uri.toString();
                                Map<String, String> postInfo = new HashMap<>();
                                postInfo.put("name", nm);
                                postInfo.put("description", desc);
                                postInfo.put("image", imagelink);

                                firebaseFirestore.collection("Posts").document(user_id).set(postInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {

                                            Toast.makeText(PostActivity.this, "Posted to blog.", Toast.LENGTH_SHORT).show();
                                            Intent mainintent = new Intent(PostActivity.this, MainActivity.class);
                                            startActivity(mainintent);
                                            finish();

                                        } else {

                                            String error = task.getException().getMessage();
                                            Toast.makeText(PostActivity.this, "Error : " + error, Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });

                            }
                        });
                    }
                });

                postProgress.setVisibility(View.INVISIBLE) ;

            }
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
