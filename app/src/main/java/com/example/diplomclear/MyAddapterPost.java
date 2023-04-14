package com.example.diplomclear;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.diplomclear.Classes.Post;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class MyAddapterPost extends ArrayAdapter<Post> {

    private LayoutInflater inflater;
    private int layout;
    private ArrayList<Post> posts;



    public MyAddapterPost(Context context, int resource,
                          ArrayList<Post> posts) {
        super(context, resource, posts);
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
        this.posts=posts;
    }
    public View getView(int position, View convertView, ViewGroup parent) {

        View view=inflater.inflate(this.layout, parent, false);

        ImageView Image = view.findViewById(R.id.IDPostIMagePost);
        TextView FIO = view.findViewById(R.id.IDUserFIO);
        TextView PostTime = view.findViewById(R.id.IDPostTime);
        TextView PostText = view.findViewById(R.id.IDPostText);

        int CountPositions=posts.size();

        //CountPositions - 1 - position
        //position
        
        Post post=posts.get(CountPositions - 1 - position);

        File file=new File(Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/"+post.getImagePost());
        Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        Image.setImageBitmap(myBitmap);

        FIO.setText(post.getFIO_text());
        PostText.setText(post.getPostText_text());
        PostTime.setText(post.getPostTime_text());

        return view;
    }
}