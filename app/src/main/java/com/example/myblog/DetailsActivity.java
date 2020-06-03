package com.example.myblog;

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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class DetailsActivity extends AppCompatActivity {

    private ImageView image ;

    private static final int GALLERY_REQUEST=1 ;

    Uri imageuri=null ;

    EditText name,branch,year ;

    Button submit ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Toolbar setupToolbar = (Toolbar) findViewById(R.id.setup_toolbar) ;
        setSupportActionBar(setupToolbar) ;

        getActionBar().setTitle("Account Details");

        image = (ImageView) findViewById(R.id.image) ;
        name = (EditText) findViewById(R.id.name) ;
        branch = (EditText) findViewById(R.id.branch) ;
        year = (EditText) findViewById(R.id.year) ;
        submit = (Button) findViewById(R.id.submit_details) ;

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {

                    if (ContextCompat.checkSelfPermission(DetailsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        Toast.makeText(DetailsActivity.this,"Please provide permission to access storage.",Toast.LENGTH_SHORT).show();
                        ActivityCompat.requestPermissions(DetailsActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1) ;

                        Intent galleryintent = new Intent(Intent.ACTION_GET_CONTENT) ;
                        galleryintent.setType("image/*") ;
                        startActivityForResult(galleryintent,GALLERY_REQUEST) ;

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

                Intent mainintent = new Intent(DetailsActivity.this,MainActivity.class) ;
                startActivity(mainintent) ;
                finish() ;

            }
        });

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
