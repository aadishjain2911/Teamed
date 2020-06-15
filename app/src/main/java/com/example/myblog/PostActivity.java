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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import io.grpc.Compressor;

import static io.opencensus.tags.TagKey.MAX_LENGTH;

public class PostActivity extends AppCompatActivity {

    private static final int MAXLENGTH = 100;
    Button post ;
        ImageButton image ;
        EditText name,description ;

        private ProgressBar postProgress ;

        private static final int GALLERY_REQUEST=1 ;

        private StorageReference storage ;
        private FirebaseAuth mAuth ;
        private FirebaseFirestore firebaseFirestore ;

        private String user_id ;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_post);

            firebaseFirestore = FirebaseFirestore.getInstance() ;
            storage = FirebaseStorage.getInstance().getReference() ;
            mAuth = FirebaseAuth.getInstance() ;

            post = (Button) findViewById(R.id.postButton) ;
            name = (EditText) findViewById(R.id.name) ;
            description = (EditText) findViewById(R.id.description) ;
            postProgress = (ProgressBar) findViewById(R.id.post_progress) ;

            user_id = mAuth.getCurrentUser().getUid() ;

            post.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    postProgress.setVisibility(View.VISIBLE);
                    startPost();
                    postProgress.setVisibility(View.INVISIBLE) ;
                }
            });

        }

        private void startPost() {

            final String nm = name.getText().toString().trim() ;
            final String desc = description.getText().toString().trim() ;

            if (!TextUtils.isEmpty(nm) && !TextUtils.isEmpty(desc)) {

  //              String randomName = random() ;

//                        File newimagefile = new File(imageuri.getPath()) ;
//
//                        compressedImageFile = new Compressor(PostActivity.this,newimagefile) ;


                Map<String, Object> postInfo = new HashMap<>();
                postInfo.put("name", nm);
                postInfo.put("description", desc);
                postInfo.put("user_id",user_id) ;
                postInfo.put("timestamp",FieldValue.serverTimestamp()) ;

                firebaseFirestore.collection("Posts").add(postInfo).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
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

            else {
                if (TextUtils.isEmpty(nm)) name.setError("Please add name.");
                if (TextUtils.isEmpty(desc)) description.setError("Please add description.");
            }

        }

 //       @Override
//        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//            super.onActivityResult(requestCode, resultCode, data);
//
//            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//
//                CropImage.ActivityResult result = CropImage.getActivityResult(data);
//                if (resultCode == RESULT_OK) {
//
//                    imageuri = result.getUri();
//                    image.setImageURI(imageuri) ;
//
//                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//
//                    Exception error = result.getError();
//                }
//            }
//
//        }

//        public static String random() {
//            Random generator = new Random();
//            StringBuilder randomStringBuilder = new StringBuilder();
//            int randomLength = generator.nextInt(MAXLENGTH);
//            char tempChar;
//            for (int i = 0; i < randomLength; i++){
//                tempChar = (char) (generator.nextInt(96) + 32);
//                randomStringBuilder.append(tempChar);
//            }
//            return randomStringBuilder.toString();
//        }
}
