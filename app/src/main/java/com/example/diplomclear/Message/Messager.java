package com.example.diplomclear.Message;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.diplomclear.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;

public class Messager extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private DatabaseReference myRef;
    private String IdUser;

    String IDListMessager="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messager);

        Bundle arguments = getIntent().getExtras();
        IDListMessager=arguments.get("IDListMessager").toString();

        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("Messager").child(IDListMessager).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object value = dataSnapshot.getValue();
                if (value == null){
                    ShowMessage("11111");
//                    dataSnapshot.getRef().setValue(new FBVocabulary(userVocabulary), completionListener);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        intent.putExtra("IDAnotherUser", IdUser);
//        intent.putExtra("IDUser", IDU);
//        intent.putExtra("IDListMessager", IDListMessager);

        ShowMessage("text");

        ImageButton IDPostMessage=findViewById(R.id.IDPostMessage);
        IDPostMessage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SendMessage();
            }
        });
    }

    public void ShowMessage(String text)
    {
        LinearLayout listView = findViewById(R.id.IDScrollLinear);
//        LayoutInflater inflater = getLayoutInflater();
//        View myLayout = inflater.inflate(R.layout.user_post, null, false);

        TextView textView=new TextView(this);
        textView.setText(text);

        listView.addView(textView);
    }

    public void SendMessage()
    {
        EditText IDMessageText=findViewById(R.id.IDMessageText);
        String TextMessage=IDMessageText.getText().toString();
        mDatabase.child("Messager").child(IDListMessager).push().setValue(TextMessage);
    }
}