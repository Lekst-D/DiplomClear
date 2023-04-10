package com.example.diplomclear;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class User extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Button NewPost=findViewById(R.id.IDPost);
        NewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewPostView();
            }
        });
    }

    void NewPostView(){
        Intent intent = new Intent(this, NewPost.class);
        startActivity(intent);
    }
}