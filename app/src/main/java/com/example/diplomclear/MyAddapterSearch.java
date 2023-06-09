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

import androidx.annotation.NonNull;

import com.example.diplomclear.Classes.Post;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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

    public String DownloadImageUser(String ImageName, ImageView Image) {

        File dir = new File(Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" + ImageName);
        if (dir.exists()) {

            File file = new File(Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" + ImageName);
            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            Image.setImageBitmap(myBitmap);

        } else {


            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();

            final long ONE_MEGABYTE = 1024 * 1024 * 1024;
            storageRef.child(UID).child(ImageName).getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

//__________________________________________________
                    File f = new File(Environment.getExternalStorageDirectory() + "/Pictures/YouDeo", ImageName);
                    try {
                        f.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

//Convert bitmap to byte array
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100 /*ignored for PNG*/, bos);
                    byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(f);
                        fos.write(bitmapdata);
                        fos.flush();
                        fos.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//__________________________________________________

                    Image.setImageBitmap(bitmap);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        }
        return ImageName;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View view = inflater.inflate(this.layout, parent, false);

        ImageView Image = view.findViewById(R.id.IDUserImage);
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
//            File file = new File(Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" + search.getImageUser());
//            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//            FIO.setText(search.getImageUser());
////            Log.e("Myadappter","if");
//            Image.setImageBitmap(myBitmap);

            DownloadImageUser(search.getImageUser().trim(),Image);

        }

        FIO.setText(search.getFIO());

        return view;
    }
}