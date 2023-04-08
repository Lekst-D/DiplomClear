package com.example.diplomclear.LogRegSwap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.diplomclear.Pages;
import com.example.diplomclear.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
    private EditText Email,Password;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Email=(EditText) findViewById(R.id.ELogin);
        Password=(EditText) findViewById(R.id.Epassword);

        mAuth= FirebaseAuth.getInstance();

        Button button=(Button) findViewById(R.id.BSelect);
        button.setOnClickListener(
                new View.OnClickListener() {

                    public void onClick(View v) {
                        String email=Email.getText().toString();
                        String pass=Password.getText().toString();

                        Login(email,pass);
                    }
                });
    }


    @SuppressLint("NotConstructor")
    public void Login(String email, String pass)
    {
        mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

//                    Toast.makeText(Login.this,"Complete login",Toast.LENGTH_SHORT).show();

                    CompleteLogin();

                }else
                {
//                    Toast.makeText(Login.this,"False login",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void RegistrationActivite(View view){
        Intent intent = new Intent(this, Registration.class);
        startActivity(intent);
    }

    public void ChangePassword(View view){
        Intent intent = new Intent(this, ChangePassword.class);
        startActivity(intent);
    }

    public void CompleteLogin(){
        Intent intent = new Intent(this, Pages.class);
        startActivity(intent);
    }
}