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
import android.widget.ImageButton;
import android.widget.ImageView;

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

        TestImageShow();
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    void TestImageShow() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        ImageView imageView=findViewById(R.id.ImageTest);

//        storageRef.child("TMdmQbEc2vQxiSjLGUO0TNWMa3g2").child("TMdmQbEc2vQxiSjLGUO0TNWMa3g21681450955291.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//            @Override
//            public void onSuccess(Uri uri) {
//                Log.e("Successfull","UriImage");
//                Log.e("Successfull",uri.getPath());
//
////                new DownloadImageTask((ImageView) findViewById(R.id.ImageTest))
////                        .execute(uri.getPath());
//
//
////                imageView.setImageBitmap(mIcon_val);
//                // Got the download URL for 'users/me/profile.png'
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                Log.e("Failure","UriImage");
//                // Handle any errors
//            }
//        });

        final long ONE_MEGABYTE = 1024 * 1024*1000;
        storageRef.child("TMdmQbEc2vQxiSjLGUO0TNWMa3g2").child("TMdmQbEc2vQxiSjLGUO0TNWMa3g21681450955291.jpg")
                .getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap=BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

//__________________________________________________
//                File f = new File(Environment.getExternalStorageDirectory() + "/Download/", "TMdmQbEc2vQxiSjLGUO0TNWMa3g21681450955291.jpg");
//                try {
//                    f.createNewFile();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
////Convert bitmap to byte array
//                ByteArrayOutputStream bos = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 0 /*ignored for PNG*/, bos);
//                byte[] bitmapdata = bos.toByteArray();
//
////write the bytes in file
//                FileOutputStream fos = null;
//                try {
//                    fos = new FileOutputStream(f);
//                    fos.write(bitmapdata);
//                    fos.flush();
//                    fos.close();
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//__________________________________________________

//                BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
                imageView.setImageBitmap(bitmap);
                // Data for "images/island.jpg" is returns, use this as needed
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
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