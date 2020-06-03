package com.example.myblog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private Button login, register ;
    private EditText email,password ;

    private ProgressBar loginProgress ;

    private  FirebaseAuth mAuth ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance() ;

        login = (Button) findViewById(R.id.signin) ;
        register = (Button) findViewById(R.id.register) ;
        email = (EditText) findViewById(R.id.email) ;
        password = (EditText) findViewById(R.id.password) ;
        loginProgress = (ProgressBar) findViewById(R.id.login_progress) ;

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String em = email.getText().toString() ;
                String pass = password.getText().toString() ;

                if (!TextUtils.isEmpty(em) && !TextUtils.isEmpty(pass)) {

                    loginProgress.setVisibility(View.VISIBLE) ;

                    mAuth.signInWithEmailAndPassword(em,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {

                                sendToMain();
                            }

                            else {

                                String errormessage = task.getException().getMessage() ;
                                Toast.makeText(LoginActivity.this,"Error : "+ errormessage,Toast.LENGTH_SHORT).show();

                            }

                            loginProgress.setVisibility(View.INVISIBLE) ;

                        }
                    });

                }

                else {

                    if (TextUtils.isEmpty(em)) {
                        email.setError("Please provide e-mail.");
                        email.requestFocus() ;
                    }

                    if (TextUtils.isEmpty(pass)) {
                        password.setError("Please enter password.") ;
                        password.requestFocus() ;
                    }

                }

            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent regintent = new Intent(LoginActivity.this,RegisterActivity.class) ;
                startActivity(regintent) ;
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser() ;

        if (currentUser==null) {


        }

        else {

            sendToMain() ;

        }

    }

    private void sendToMain() {

        Intent mainintent = new Intent(LoginActivity.this,MainActivity.class) ;
        startActivity(mainintent) ;
        finish();

    }
}
