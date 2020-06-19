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
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailsActivity extends AppCompatActivity {

    private CircleImageView image ;

    private static final int GALLERY_REQUEST=1 ;

    private boolean ischanged = false ;

    private Spinner branchSpinner,yearSpinner ;

    private Uri imageuri=null ;

    private File compressedImageFile ;

    private EditText name,presentSkills,pastExperiences,fieldsOfInterest ;

    private String branch ,year ;

    private Button submit ;

    private StorageReference storageReference ;
    private FirebaseAuth firebaseAuth ;
    private FirebaseFirestore firebaseFirestore ;

    private String user_id ;

    private ProgressBar progressBar ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();

        user_id = firebaseAuth.getCurrentUser().getUid();

        image = (CircleImageView) findViewById(R.id.image);
        name = (EditText) findViewById(R.id.name);
        branchSpinner = (Spinner) findViewById(R.id.branch);
        yearSpinner = (Spinner) findViewById(R.id.year);
        submit = (Button) findViewById(R.id.submit_details);
        presentSkills = (EditText) findViewById(R.id.present_skills);
        pastExperiences = (EditText) findViewById(R.id.past_experiences);
        fieldsOfInterest = (EditText) findViewById(R.id.fields_interest);
        progressBar = (ProgressBar) findViewById(R.id.detailsProgressBar);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.branches, R.layout.support_simple_spinner_dropdown_item);

        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        branchSpinner.setGravity(Gravity.CENTER) ;

        branchSpinner.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.years, R.layout.support_simple_spinner_dropdown_item);

        adapter2.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        yearSpinner.setAdapter(adapter2);

        submit.setEnabled(false);

        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {

                    if (task.getResult().exists()) {

                        progressBar.setVisibility(View.VISIBLE);

                        String prev_name = task.getResult().getString("name");
                        name.setText(prev_name);
                        year = task.getResult().getString("year") ;
                        branch = task.getResult().getString("branch");
                        String prev_pastexp = task.getResult().getString("past_experiences");
                        pastExperiences.setText(prev_pastexp);
                        String prev_preskil = task.getResult().getString("present_skills");
                        presentSkills.setText(prev_preskil);
                        String prev_fieldint = task.getResult().getString("fields_interest");
                        fieldsOfInterest.setText(prev_fieldint);
                        String prev_image = task.getResult().getString("image");

                        imageuri = Uri.parse(prev_image);

                        RequestOptions placeHolderrequest = new RequestOptions();
                        placeHolderrequest.placeholder(R.drawable.kindpng_4517876);

                        Glide.with(DetailsActivity.this).setDefaultRequestOptions(placeHolderrequest).load(prev_image).into(image);

                        progressBar.setVisibility(View.INVISIBLE);
                    }

                } else {

                    String error = task.getException().getMessage();
                    Toast.makeText(DetailsActivity.this, "Error : " + error, Toast.LENGTH_SHORT).show();

                }

                submit.setEnabled(true);

            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (ContextCompat.checkSelfPermission(DetailsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        Toast.makeText(DetailsActivity.this, "Please provide permission to access storage.", Toast.LENGTH_SHORT).show();
                        ActivityCompat.requestPermissions(DetailsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                    } else {

                        CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1).start(DetailsActivity.this);

                    }
                } else {

                    CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1).start(DetailsActivity.this);

                }

            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String nm = name.getText().toString();
                final String pe = pastExperiences.getText().toString();
                final String ps = presentSkills.getText().toString();
                final String fi = fieldsOfInterest.getText().toString();

                if (TextUtils.isEmpty(nm))
                    name.setError("Please enter name.");
                if (TextUtils.isEmpty(pe))
                    pastExperiences.setError("Please enter some past experiences.");
                if (TextUtils.isEmpty(ps))
                    presentSkills.setError("Please enter your present skills.");
                if (TextUtils.isEmpty(fi))
                    fieldsOfInterest.setError("Please enter your fields of interest.");

                branch = branchSpinner.getSelectedItem().toString() ;
                year = yearSpinner.getSelectedItem().toString() ;

                if (branch == "None" || branch == null) Toast.makeText(getApplicationContext(), "Please select your branch.", Toast.LENGTH_SHORT).show();
                else if (year == "None"|| year==null) Toast.makeText(getApplicationContext(), "Please select your year.", Toast.LENGTH_SHORT).show();

                else if (!TextUtils.isEmpty(nm) && !TextUtils.isEmpty(pe) && !TextUtils.isEmpty(ps) && !TextUtils.isEmpty(fi) ) {

                    progressBar.setVisibility(View.VISIBLE);

                    if (ischanged) {

                        StorageReference imagepath = storageReference.child("profile_images").child(user_id + ".jpg");
                        imagepath.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                storeData(taskSnapshot, nm, year, branch, pe, ps, fi);

                            }
                        });

                    } else {
                        String imagelink = imageuri.toString();

                        Map<String, String> userInfo = new HashMap<>();
                        userInfo.put("name", nm);
                        userInfo.put("year", year);
                        userInfo.put("branch", branch);
                        userInfo.put("past_experiences", pe);
                        userInfo.put("present_skills", ps);
                        userInfo.put("fields_interest", fi);
                        userInfo.put("image", imagelink);

                        firebaseFirestore.collection("Users").document(user_id).set(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {

                                    Toast.makeText(DetailsActivity.this, "Details updated successfully.", Toast.LENGTH_SHORT).show();
                                    Intent mainintent = new Intent(DetailsActivity.this, MainActivity.class);
                                    startActivity(mainintent);
                                    finish();

                                } else {

                                    String error = task.getException().getMessage();
                                    Toast.makeText(DetailsActivity.this, "Error : " + error, Toast.LENGTH_SHORT).show();

                                }

                            }
                        });
                    }

                    progressBar.setVisibility(View.INVISIBLE);
                }
            }

        });
    }

    private int getYear(Spinner yearSpinner, String prev_year) {
        int index = 0;

        for (int i=0;i<yearSpinner.getCount();i++){
            if (yearSpinner.getItemAtPosition(i).equals(prev_year)){
                index = i;
                break ;
            }
        }
        return index;
    }

    private void storeData( UploadTask.TaskSnapshot taskSnapshot,final String nm,final String yr,final String br,final String pe,final String ps,final String fi) {

        Task<Uri> downloadUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl() ;

        downloadUrl.addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                String imagelink = uri.toString() ;

                Map<String, String> userInfo = new HashMap<>() ;
                userInfo.put("name",nm) ;
                userInfo.put("year",yr) ;
                userInfo.put("branch",br) ;
                userInfo.put("past_experiences",pe) ;
                userInfo.put("present_skills",ps) ;
                userInfo.put("fields_interest",fi) ;
                userInfo.put("image",imagelink) ;

                firebaseFirestore.collection("Users").document(user_id).set(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            Toast.makeText(DetailsActivity.this,"Details updated successfully.",Toast.LENGTH_SHORT).show() ;
                            Intent mainintent = new Intent(DetailsActivity.this,MainActivity.class) ;
                            startActivity(mainintent) ;
                            finish() ;

                        }
                        else {

                            String error = task.getException().getMessage() ;
                            Toast.makeText(DetailsActivity.this,"Error : "+error,Toast.LENGTH_SHORT).show() ;

                        }

                    }
                }) ;

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

                ischanged = true ;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
            }
        }

    }
    private int getIndex(Spinner branchSpinner,String branch) {

        int index = 0;

        for (int i=0;i<branchSpinner.getCount();i++){
            if (branchSpinner.getItemAtPosition(i).equals(branch)){
                index = i;
                break ;
            }
        }
        return index;
    }
}

