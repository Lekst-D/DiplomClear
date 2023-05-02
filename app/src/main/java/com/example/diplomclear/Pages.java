package com.example.diplomclear;

import static android.content.ContentValues.TAG;
import static android.os.FileUtils.copy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.diplomclear.Message.MessegeList;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import com.squareup.picasso.Picasso;


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


//        TestImageShow();

        ImageButton Bmessage=findViewById(R.id.IDMessage);
        Bmessage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ShowMessage();
            }
        });
    }


    void TestImageShow() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        ImageView imageView=findViewById(R.id.ImageTest);

        final long ONE_MEGABYTE = 1024 * 1024 * 1024;
        storageRef.child("TMdmQbEc2vQxiSjLGUO0TNWMa3g2").child("TMdmQbEc2vQxiSjLGUO0TNWMa3g21681545477795.jpg")
                .getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap=BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

//__________________________________________________
                File f = new File(Environment.getExternalStorageDirectory() + "/Download/", "35.jpg");
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

                imageView.setImageBitmap(bitmap);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });


    }

    void ShowMessage(){
        Intent intent = new Intent(this, MessegeList.class);
        startActivity(intent);
    }

    void ListOpen(){
        Intent intent = new Intent(this, ListAct.class);
        startActivity(intent);
    }

    public void CloseView(View view) {
        finish();
    }

    public void ShowMessageList(View view) {
        Intent intent = new Intent(this, MessegeList.class);
        startActivity(intent);
    }
}