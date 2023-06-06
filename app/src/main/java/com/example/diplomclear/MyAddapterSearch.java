package com.example.diplomclear;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.diplomclear.Classes.Post;

import java.io.File;
import java.util.ArrayList;

public class MyAddapterSearch extends ArrayAdapter<SearchList> {

    private LayoutInflater inflater;
    private int layout;
    private ArrayList<SearchList> searchs;
    private Context context;
    private String UID;




    public MyAddapterSearch(Context context, int resource,
                            ArrayList<SearchList> searchs,String UID) {
        super(context, resource, searchs);
        this.context=context;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
        this.searchs = searchs;
        this.UID = UID;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View view = inflater.inflate(this.layout, parent, false);

        ImageView Image = view.findViewById(R.id.IDPostIMagePost);
        TextView FIO = view.findViewById(R.id.IDUserFIO);
        LinearLayout LinearLayout=view.findViewById(R.id.IDLinearLayout);

        SearchList search = searchs.get(position);

        String GteID=search.getIDUser().trim();

        if(!UID.trim().contains(GteID)){
        LinearLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

//                Toast toast = Toast.makeText(getContext(), "Hello Android!",Toast.LENGTH_LONG);
//                toast.show();

                Intent intent = new Intent(getContext(), AnotherUser.class);
                intent.putExtra("UserID",    search.getIDUser());
                intent.putExtra("FIO",       search.getFIO());
                intent.putExtra("ImageUser", search.getImageUser());

                context.startActivity(intent);
            }});}
        else{
            LinearLayout.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {



                    Intent intent = new Intent(getContext(), User.class);
                    intent.putExtra("UserID",    search.getIDUser());
                    intent.putExtra("FIO",       search.getFIO());
                    intent.putExtra("ImageUser", search.getImageUser());

                    context.startActivity(intent);
                }});}

        int CountPositions = searchs.size();

        //CountPositions - 1 - position
        //position


//        Log.e("Myadappter","non if");
        if (!search.getImageUser().contains("none")) {
            File file = new File(Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" + search.getImageUser());
            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            FIO.setText(search.getImageUser());
//            Log.e("Myadappter","if");
//        Image.setImageBitmap(myBitmap);
            return view;
        }

        FIO.setText(search.getFIO());

        return view;
    }
}