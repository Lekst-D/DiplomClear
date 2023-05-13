package com.example.diplomclear;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.diplomclear.LogRegSwap.Login;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListenner;
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        if(user!=null){
            //Toast.makeText(MainActivity.this,"User is active",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, Pages.class);
//            Intent intent = new Intent(this, AddImages.class);
            startActivity(intent);
        }else{
            // Toast.makeText(MainActivity.this,"User isn't active",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
        }

    }



    public void Login(View view) {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }
}