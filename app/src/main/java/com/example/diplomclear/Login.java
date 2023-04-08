package com.example.diplomclear;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
    public void RegistrationActivite(View view){
        Intent intent = new Intent(this, Registration.class);
        startActivity(intent);
    }

    public void ChangePassword(View view){
        Intent intent = new Intent(this, ChangePassword.class);
        startActivity(intent);
    }
}