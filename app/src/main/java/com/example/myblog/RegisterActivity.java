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

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private EditText email,password,confirmPassword ;
    private Button alreadyAccount, createAccount ;
    private ProgressBar progressBar ;

    private FirebaseAuth mAuth ;

    private SignInButton signInButton ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance() ;

        email = (EditText) findViewById(R.id.email) ;
        password = (EditText) findViewById(R.id.password) ;
        confirmPassword = (EditText) findViewById(R.id.confirm_password) ;
        alreadyAccount = (Button) findViewById(R.id.account_already) ;
        createAccount = (Button) findViewById(R.id.create_account) ;
        progressBar = (ProgressBar) findViewById(R.id.registerProgress) ;
        signInButton = (SignInButton) findViewById(R.id.signInButton) ;

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String em,pass,copass ;
                em = email.getText().toString() ;
                pass = password.getText().toString() ;
                copass = confirmPassword.getText().toString() ;

                if (!TextUtils.isEmpty(em) && !TextUtils.isEmpty(pass) && !TextUtils.isEmpty(copass)) {

                    if (!pass.equals(copass)) {

                        Toast.makeText(RegisterActivity.this,"Password and confirm password should be same.",Toast.LENGTH_SHORT).show() ;

                    }

                    else {

                        progressBar.setVisibility(View.VISIBLE);

                        mAuth.createUserWithEmailAndPassword(em,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {

                                    Intent sendToSetup = new Intent(RegisterActivity.this,DetailsActivity.class) ;
                                    startActivity(sendToSetup);
                                    finish() ;

                                }

                                else {
                                    String errormessage = task.getException().getMessage() ;
                                    Toast.makeText(RegisterActivity.this,"Error : "+ errormessage,Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

                        progressBar.setVisibility(View.INVISIBLE);

                    }

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

                    if (TextUtils.isEmpty(copass)) {
                        confirmPassword.setError("Please confirm password.") ;
                        confirmPassword.requestFocus() ;
                    }

                }

            }
        });

        alreadyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendToLogin = new Intent(RegisterActivity.this,LoginActivity.class) ;
                startActivity(sendToLogin) ;
                finish() ;
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser() ;

        if (currentUser!=null) {
            sendToMain() ;

        }
    }

    private void sendToMain() {

        Intent mainintent = new Intent(RegisterActivity.this,MainActivity.class) ;
        startActivity(mainintent) ;
        finish();

    }
}
