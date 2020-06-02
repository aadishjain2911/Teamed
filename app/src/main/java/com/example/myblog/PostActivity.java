package com.example.myblog;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class PostActivity extends AppCompatActivity {

    Button post ;
    ImageButton image ;
    EditText name,description ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        post = (Button) findViewById(R.id.postButton) ;
        image = (ImageButton) findViewById(R.id.imageButton) ;
        name = (EditText) findViewById(R.id.name) ;
        description = (EditText) findViewById(R.id.description) ;

    }
}
