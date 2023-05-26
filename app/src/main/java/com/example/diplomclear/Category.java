package com.example.diplomclear;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.diplomclear.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Category extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference myRef;
    private String IdUser;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        IdUser = user.getUid().toString();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("UserInfo").child(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {

                    Log.d("firebase", String.valueOf(task.getResult().getValue()));

//                    if (task.getResult().hasChild("categories")) {
//
//                        String shortText = task.getResult().child("").getValue().toString();
//                        ((TextView) findViewById(R.id.expandable_text)).setText(shortText);
//
////                        if (AllUserPost.size() != 0) {
////                            HideLoad(true);
////                        } else {
////                            HideLoad(false);
////                        }
//
//                    }
//                    else
//                    {
//                        ((TextView) findViewById(R.id.expandable_text)).setText("Вы не оставили текс, о себе");
//                    }

                }
            }
        });

        ImageButton IDBack=findViewById(R.id.IDBack);
        IDBack.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        finish();
                    }
                });

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ImageButton IDNewCategory=findViewById(R.id.IDNewCategory);
        IDNewCategory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });

    }

    void HideLoad(boolean check) {
//        ImageView IDID =findViewById(R.id.IDID);
//        IDID.setImageResource(R.drawable.two);

        LinearLayout IDLoad = findViewById(R.id.IDLoad);
        IDLoad.setVisibility(View.GONE);

        if (!check) {
            TextView IDTVTextNotPost = findViewById(R.id.IDTVTextNotPost);
            IDTVTextNotPost.setVisibility(View.VISIBLE);
        }
    }
}