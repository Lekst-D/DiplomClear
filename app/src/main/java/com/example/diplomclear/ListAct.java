package com.example.diplomclear;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.diplomclear.LogRegSwap.Login;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ListAct extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);


        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) LinearLayout IDUser=findViewById(R.id.IDUser);
        IDUser.setOnClickListener(
                new View.OnClickListener() {

                    public void onClick(View v) {
                        ShowUser();
                    }
                });



        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) LinearLayout IDSearch=findViewById(R.id.IDSearch);
        IDSearch.setOnClickListener(
                new View.OnClickListener() {

                    public void onClick(View v) {
                        ShowSearch();
                    }
                });


        findViewById(R.id.IDBack).setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        BackActivity();
                    }
                });
    }

    public void LogOut(View view){
        FirebaseAuth mAuth=FirebaseAuth.getInstance();
        FirebaseUser user=mAuth.getCurrentUser();


        user=mAuth.getCurrentUser();
        if(user!=null){
            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(this, Login.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }
    }

    public void PagesActive(View view){
        Intent intent = new Intent(this, Pages.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    void ShowUser(){
        Intent intent = new Intent(this, User.class);
        startActivity(intent);
    }

    void ShowSearch(){
        Intent intent = new Intent(this, Search.class);
        startActivity(intent);
    }


    void BackActivity(){
        finish();
    }
}