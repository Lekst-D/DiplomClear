package com.example.diplomclear;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class Pages extends AppCompatActivity {
    ImageButton ListSetting,Message;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pages);

        ListSetting=(ImageButton) findViewById(R.id.IDList);

        ListSetting.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                ListOpen();
            }
        });

        Message=(ImageButton) findViewById(R.id.IDMessage);
        Message.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                MessageOpen();
            }
        });
    }

    void ListOpen(){
        Intent intent = new Intent(this, ListAct.class);
        startActivity(intent);
    }

    void MessageOpen(){
//        Intent intent = new Intent(this, Message.class);
//        startActivity(intent);
    }
}