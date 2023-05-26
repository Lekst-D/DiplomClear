package com.example.diplomclear.EditInfo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.diplomclear.Category;
import com.example.diplomclear.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChangeInfoUser extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser user;

    private String IdUser;
    EditText EName, ESurname,EDateBirth,EUserPhone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_info_user);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        IdUser=user.getUid();

        EName=findViewById(R.id.NameUser);
        ESurname=findViewById(R.id.SurnameUser);
        EDateBirth=findViewById(R.id.BirthDayUser);
        EUserPhone=findViewById(R.id.PhoneUser);


        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("UserInfo").child(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
//                    userName
//                            userSurname

                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    String Name = task.getResult().child("userName").getValue().toString();
                    String Surname = task.getResult().child("userSurname").getValue().toString();
                    String UserPhone = task.getResult().child("userPhone").getValue().toString();
                    String DateBirth= task.getResult().child("birthDay").getValue().toString();
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));

                    EName.setText(Name);
                    ESurname.setText(Surname);
                    EDateBirth.setText(DateBirth);
                    EUserPhone.setText(UserPhone);
                }
            }
        });

        Button ChangeInfoID=findViewById(R.id.ChangeInfoID);

        ChangeInfoID.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String Name = EName.getText().toString();
                String Surname = ESurname.getText().toString();
                String UserPhone = EUserPhone.getText().toString();
                String DateBirth= EDateBirth.getText().toString();

                mDatabase.child("UserInfo").child(IdUser).child("userName").setValue(Name);
                mDatabase.child("UserInfo").child(IdUser).child("userSurname").setValue(Surname);
                mDatabase.child("UserInfo").child(IdUser).child("userPhone").setValue(UserPhone);
                mDatabase.child("UserInfo").child(IdUser).child("birthDay").setValue(DateBirth);

            }
        });

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) LinearLayout IDChangeSecurity=findViewById(R.id.IDChangeSecurity);
        IDChangeSecurity.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        ChangeSecurity();
                    }
                });

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) LinearLayout IDCategory=findViewById(R.id.IDCategory);
        IDCategory.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        ShowAllCategory();
                    }
                });

        ImageButton IDBack=findViewById(R.id.IDBack);
        IDBack.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        finish();
                    }
                });

    }
    void ShowAllCategory(){
        Intent intent = new Intent(this, Category.class);
        startActivity(intent);
    }

    void ChangeSecurity(){
        Intent intent = new Intent(this, SecurityChange.class);
        startActivity(intent);
    }
}