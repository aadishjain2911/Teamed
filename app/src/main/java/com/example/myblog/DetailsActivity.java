package com.example.myblog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class DetailsActivity extends AppCompatActivity {

    private ImageView image ;

    private static final int GALLERY_REQUEST=1 ;

    private Uri imageuri=null ;

    EditText name,branch,year ;

    Button submit ;

    private StorageReference storageReference ;
    private FirebaseAuth firebaseAuth ;

    private ProgressBar progressBar ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        firebaseAuth = FirebaseAuth.getInstance() ;
        storageReference = FirebaseStorage.getInstance().getReference() ;

        image = (ImageView) findViewById(R.id.image) ;
        name = (EditText) findViewById(R.id.name) ;
        branch = (EditText) findViewById(R.id.branch) ;
        year = (EditText) findViewById(R.id.year) ;
        submit = (Button) findViewById(R.id.submit_details) ;
        progressBar = (ProgressBar) findViewById(R.id.progressBar) ;

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {

                    if (ContextCompat.checkSelfPermission(DetailsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        Toast.makeText(DetailsActivity.this,"Please provide permission to access storage.",Toast.LENGTH_SHORT).show();
                        ActivityCompat.requestPermissions(DetailsActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1) ;

                    }

                    else {

                        CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1,1).start(DetailsActivity.this);

                    }
                }

            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nm,yr,br ;
                nm = name.getText().toString() ;
                yr = year.getText().toString() ;
                br = branch.getText().toString() ;

                if (!TextUtils.isEmpty(nm) && !TextUtils.isEmpty(yr) && !TextUtils.isEmpty(br)) {

                    progressBar.setVisibility(View.VISIBLE) ;
                    String user_id = firebaseAuth.getCurrentUser().getUid() ;

                    StorageReference imagepath = storageReference.child("profile_images").child(user_id+".jpg") ;
                    imagepath.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
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

                    progressBar.setVisibility(View.INVISIBLE) ;
                }

                else {

                    if (TextUtils.isEmpty(nm)) name.setError("Please enter name.");
                    if (TextUtils.isEmpty(yr)) year.setError("Please enter year.");
                    if (TextUtils.isEmpty(br)) year.setError("Please enter branch.");

                }

                Intent mainintent = new Intent(DetailsActivity.this,MainActivity.class) ;
                startActivity(mainintent) ;
                finish() ;

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                imageuri = result.getUri();
                image.setImageURI(imageuri) ;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
            }
        }

    }
}
