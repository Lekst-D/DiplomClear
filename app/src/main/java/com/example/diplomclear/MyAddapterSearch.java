package com.example.diplomclear;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.diplomclear.Classes.Post;

import java.io.File;
import java.util.ArrayList;

public class MyAddapterSearch extends ArrayAdapter<SearchList> {

    private LayoutInflater inflater;
    private int layout;
    private ArrayList<SearchList> searchs;


    public MyAddapterSearch(Context context, int resource,
                            ArrayList<SearchList> searchs) {
        super(context, resource, searchs);
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
        this.searchs = searchs;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View view = inflater.inflate(this.layout, parent, false);

        ImageView Image = view.findViewById(R.id.IDPostIMagePost);
        TextView FIO = view.findViewById(R.id.IDUserFIO);

        int CountPositions = searchs.size();

        //CountPositions - 1 - position
        //position

        SearchList search = searchs.get(position);

//        if (search.getImageUser() != "none") {
//            File file = new File(Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" + search.getImageUser());
//            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//            FIO.setText(search.getImageUser());
////        Image.setImageBitmap(myBitmap);
//            return view;
//        }

        FIO.setText(search.getFIO());

        return view;
    }
}