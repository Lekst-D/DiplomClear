package com.example.diplomclear;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.diplomclear.LogRegSwap.Login;
import com.example.diplomclear.Message.MessegeList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import io.grpc.internal.ServiceConfigUtil;

public class ListAct extends AppCompatActivity {

    String Name;
    String Surname;
    String UserPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference();

        Name=null;
        Surname=null;
        UserPhoto=null;

        mDatabase.child("UserInfo").child(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
//                    userName
//                            userSurname

                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Name=task.getResult().child("userName").getValue().toString();
                    Surname=task.getResult().child("userSurname").getValue().toString();
                    UserPhoto=task.getResult().child("userPhoto").getValue().toString();
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                }
            }
        });

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

        ImageButton IDMessageList =findViewById(R.id.IDMessage);
        IDMessageList.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View v) {
                        ShowMessage();
                    }
                }
        );

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) LinearLayout IDSubscribe=findViewById(R.id.IDSubscribe);
        IDSubscribe.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        ShowSibscribes();
                    }
                });

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) LinearLayout IDMySubscribes=findViewById(R.id.IDMySubscribes);
        IDMySubscribes.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        ShowMySibscribers();
                    }
                });

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) LinearLayout IDChangeSecurity=findViewById(R.id.IDChangeSecurity);
        IDChangeSecurity.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        ChangeSecurity();
                    }
                });
    }

    void ChangeSecurity(){
        Intent intent = new Intent(this, SecurityChange.class);
        startActivity(intent);
    }

    void ShowMySibscribers(){
        Intent intent = new Intent(this, MySubscribes.class);
        startActivity(intent);
    }

    void ShowSibscribes(){
        Intent intent = new Intent(this, Subscribes.class);
        startActivity(intent);
    }

    void ShowMessage(){
        Intent intent = new Intent(this, MessegeList.class);
        startActivity(intent);
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
        intent.putExtra("Name",      Name);
        intent.putExtra("Surname",   Surname);
        intent.putExtra("ImageUser", UserPhoto);
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