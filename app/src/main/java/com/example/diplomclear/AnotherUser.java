package com.example.diplomclear;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

public class AnotherUser extends AppCompatActivity {
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_another_user);

//      --------------------------------------------------------

        Bundle arguments = getIntent().getExtras();

        TextView FIO= findViewById(R.id.FIO);
        TextView UID=findViewById(R.id.UserID);
        TextView ImaUser=findViewById(R.id.ImageUser);

        FIO.setText(arguments.get("FIO").toString());
        UID.setText(arguments.get("UserID").toString());
        ImaUser.setText(arguments.get("ImageUser").toString());

//      --------------------------------------------------------

    }
}