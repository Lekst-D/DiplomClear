package com.example.diplomclear;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.diplomclear.Classes.Post;

import java.util.ArrayList;

public class AnotherUser extends AppCompatActivity {

    LinearLayout linearLayout;

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

//        ListView listView= findViewById(R.id.IDListView);

        ArrayList<Post> AllUserPost = new ArrayList<>();

        for (int i = 1; i < 3; i++){
            Post post=new Post("TMdmQbEc2vQxiSjLGUO0TNWMa3g21682336917808.jpg",
                "TMdmQbEc2vQxiSjLGUO0TNWMa3g2",
                "text post",
                "20:48 24.04.2023");
        AllUserPost.add(post);}

//        MyAddapterPost stateAdapter = new MyAddapterPost(this, R.layout.user_post, AllUserPost);
//        listView.setAdapter(stateAdapter);


        PostAdd();
        PostAdd();
        PostAdd();

    }

    private void PostAdd()
    {
        LinearLayout listView=findViewById(R.id.IDListView);
        LayoutInflater inflater = getLayoutInflater();
        View myLayout = inflater.inflate(R.layout.user_post, null, false);
        listView.addView(myLayout);
    }

}