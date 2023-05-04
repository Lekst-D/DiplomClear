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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.diplomclear.Classes.Post;
import com.example.diplomclear.Message.MessegeList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import com.squareup.picasso.Picasso;


public class Pages extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private DatabaseReference myRef;


    private FirebaseFirestore db;
    private String IdUser;
    private ArrayList<Post> AllUserPost = new ArrayList<>();

    private String Subscribes;
    private ArrayList<String> subs = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pages);

        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        IdUser = user.getUid();
        db = FirebaseFirestore.getInstance();

        Log.e("IdUser", IdUser);

        mDatabase.child("Subscribe").child(IdUser).get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().getValue() != null) {
                        Subscribes = task.getResult().getValue().toString();
                        subs = new ArrayList<String>(Arrays.asList((Subscribes.split(","))));
                        subs.remove("null");
                        subs.add(IdUser);
                        Log.e("subs", subs.toString());

                        AllPost();
                    }
                    else{
                        subs.add(IdUser);
                        AllPost();
                    }
                } else {
                    Log.e("firebase", "Error getting data", task.getException());
                }
            }
        });


        ImageButton ListSetting = (ImageButton) findViewById(R.id.IDList);
        ListSetting.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ListOpen();
            }
        });

        ImageButton Bmessage = findViewById(R.id.IDMessage);
        Bmessage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ShowMessageList();
            }
        });
    }


//    void TestImageShow() {
//        FirebaseStorage storage = FirebaseStorage.getInstance();
//        StorageReference storageRef = storage.getReference();
//
//        final long ONE_MEGABYTE = 1024 * 1024 * 1024;
//        storageRef.child("TMdmQbEc2vQxiSjLGUO0TNWMa3g2").child("TMdmQbEc2vQxiSjLGUO0TNWMa3g21681545477795.jpg")
//                .getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//            @Override
//            public void onSuccess(byte[] bytes) {
//                Bitmap bitmap=BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//
////__________________________________________________
//                File f = new File(Environment.getExternalStorageDirectory() + "/Download/", "35.jpg");
//                try {
//                    f.createNewFile();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
////Convert bitmap to byte array
//                ByteArrayOutputStream bos = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100 /*ignored for PNG*/, bos);
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
////__________________________________________________
//
//                imageView.setImageBitmap(bitmap);
//
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // Handle any errors
//            }
//        });
//
//
//    }

    public void AllPost() {
        Log.e("subs", subs.toString());

        db.collection("usersPosts").whereIn("UserID",subs).get()
//                .whereEqualTo("UserID", IdUser).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                String ImagePost = document.get("Images").toString();
                                String UserID = document.get("UserID").toString();
                                String PostDate = document.get("DatePost").toString();
                                String PostTime = document.get("TimePost").toString();
                                String PostText = document.get("TextPost").toString();

                                Post post = new Post(ImagePost, UserID, PostText, PostDate, PostTime);

                                AllUserPost.add(post);
                            }


                            for (Post post : (AllUserPost)) {
                                ShowPost(post);
                            }

                            File dir = new File(Environment.getExternalStorageDirectory() + "/Pictures/YouDeo");
                            if (!dir.exists()) {
                                new File(Environment.getExternalStorageDirectory() + "/Pictures/YouDeo").mkdirs();
                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    void ListOpen() {
        Intent intent = new Intent(this, ListAct.class);
        startActivity(intent);
    }

    public void ShowMessageList() {
        Intent intent = new Intent(this, MessegeList.class);
        startActivity(intent);
    }

    public String DownloadImage(String ImageName, ImageView Image) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        final long ONE_MEGABYTE = 1024 * 1024 * 1024;
        storageRef.child(IdUser).child(ImageName).getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
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

        return ImageName;
    }

    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
    void ShowPost(Post post) {

        LinearLayout listView = findViewById(R.id.IDListView);
        LayoutInflater inflater = getLayoutInflater();
        View myLayout = inflater.inflate(R.layout.user_post, null, false);

        ImageView Image = myLayout.findViewById(R.id.IDPostIMagePost);
        TextView FIO = myLayout.findViewById(R.id.IDUserFIO);
        TextView PostTime = myLayout.findViewById(R.id.IDPostTime);
        TextView PostText = myLayout.findViewById(R.id.IDPostText);


        File dir = new File(Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" + post.getImagePost());
        if (dir.exists()) {

            File file = new File(Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" + post.getImagePost());
            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            Image.setImageBitmap(myBitmap);

        } else {
            DownloadImage(post.getImagePost(), Image);
        }

        String Date=new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime());

        String DatePost=post.getPostDate();
        String TimePost=post.getPostTime();

        if(Date.contains(DatePost)){
            PostTime.setText(TimePost);
        }
        else{
            PostTime.setText(DatePost+" "+TimePost);
        }

        PostText.setText(post.getPostText());

        listView.addView(myLayout);
    }
}