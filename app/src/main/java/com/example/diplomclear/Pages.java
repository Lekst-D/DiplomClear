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
import android.view.ViewGroup;
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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;


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
                            } else {
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

        db.collection("usersPosts").whereIn("UserID", subs).get()
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

                            File dir = new File(Environment.getExternalStorageDirectory() + "/Pictures/YouDeo");
                            if (!dir.exists()) {
                                new File(Environment.getExternalStorageDirectory() + "/Pictures/YouDeo").mkdirs();
                            }

                            for (Post post : (AllUserPost)) {
                                ShowPost(post);
                            }

                            if (AllUserPost.size() != 0) {
                                HideLoad(true);
                            } else {
                                HideLoad(false);
                            }

                        } else {

                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    void HideLoad(boolean check) {
//        ImageView IDID =findViewById(R.id.IDID);
//        IDID.setImageResource(R.drawable.two);

        LinearLayout IDLoad = findViewById(R.id.IDLoad);
        IDLoad.setVisibility(View.GONE);

        if (!check) {
            TextView IDTVTextNotPost = findViewById(R.id.IDTVTextNotPost);
            IDTVTextNotPost.setVisibility(View.VISIBLE);
        }
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
    ArrayList<String> FioUsers = new ArrayList<>();
    ArrayList<String> IDusers = new ArrayList<>();

    void ShowPost(Post post) {

        LinearLayout listView = findViewById(R.id.IDListView);
        LayoutInflater inflater = getLayoutInflater();
        View myLayout = inflater.inflate(R.layout.user_post, null, false);

        ImageView Image = myLayout.findViewById(R.id.IDPostIMagePost);
        TextView FIO = myLayout.findViewById(R.id.IDUserFIO);
        TextView PostTime = myLayout.findViewById(R.id.IDPostTime);
        TextView PostText = myLayout.findViewById(R.id.IDPostText);

        String idUserRequest = post.getUserID().toString();

        FIO.setText(idUserRequest);

        IDusers.add(idUserRequest);
        mDatabase.child("UserInfo").child(idUserRequest).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    String Name = task.getResult().child("userName").getValue().toString();
                    String Surname = task.getResult().child("userSurname").getValue().toString();
                    String fio = Surname + " " + Name;

                    FIO.setText(fio);
                }
            }
        });


        File dir = new File(Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" + post.getImagePost());
        if (dir.exists()) {

            File file = new File(Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" + post.getImagePost());
            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            Image.setImageBitmap(myBitmap);

        } else {
            DownloadImage(post.getImagePost(), Image);
        }

        LinearLayout IDImageView = myLayout.findViewById(R.id.IDImageView);

        ArrayList<String> images = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            images.add(Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" + post.getImagePost());
        }

        int sizeImage=images.size();
        Log.e("sizeImage",sizeImage+"");

        if(sizeImage<=2)
        {
            View myLayoutImages = inflater.inflate(R.layout.one_image, null, false);

            LinearLayout IDLineOne = myLayoutImages.findViewById(R.id.OneLine);
            LinearLayout IDLineTwo = myLayoutImages.findViewById(R.id.TwoLine);
            LinearLayout IDLineThree = myLayoutImages.findViewById(R.id.ThreeLine);

            if(sizeImage==1)
            {
                File file = new File( images.get(0));
                Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

                ImageView imageView = (ImageView)IDLineOne.getChildAt(0);
                imageView.setImageBitmap(myBitmap);

                imageView = (ImageView)IDLineOne.getChildAt(1);

                imageView.setVisibility(View.GONE);
            }
            else
            {
                File file = new File( images.get(0));
                Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                ImageView imageView =  (ImageView)IDLineOne.getChildAt(0);
                imageView.setImageBitmap(myBitmap);

                imageView =  (ImageView)IDLineOne.getChildAt(1);
                file = new File( images.get(1));
                myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                imageView.setImageBitmap(myBitmap);
            }

            IDImageView.addView(myLayoutImages);
        }
        else if(sizeImage<=8)
        {
            View myLayoutImages = inflater.inflate(R.layout.two_image, null, false);

            LinearLayout IDLineOne = myLayoutImages.findViewById(R.id.OneLine);
            LinearLayout IDLineTwo = myLayoutImages.findViewById(R.id.TwoLine);
            LinearLayout IDLineThree = myLayoutImages.findViewById(R.id.ThreeLine);

            File file = new File( images.get(0));
            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            ImageView imageView =  (ImageView)IDLineOne.getChildAt(0);
            imageView.setImageBitmap(myBitmap);

            imageView =  (ImageView)IDLineOne.getChildAt(1);
            file = new File( images.get(1));
            myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);

            if(sizeImage>=3)
            {
                file = new File( images.get(2));
                myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

                imageView = (ImageView)IDLineTwo.getChildAt(0);
                imageView.setImageBitmap(myBitmap);
                imageView.setVisibility(View.VISIBLE);
            }
            if(sizeImage>=4)
            {
                file = new File( images.get(3));
                myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

                imageView = (ImageView)IDLineTwo.getChildAt(1);
                imageView.setImageBitmap(myBitmap);
                imageView.setVisibility(View.VISIBLE);
            }
            if(sizeImage>=5){
                file = new File( images.get(4));
                myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

                imageView = (ImageView)IDLineTwo.getChildAt(2);
                imageView.setImageBitmap(myBitmap);
                imageView.setVisibility(View.VISIBLE);
            }
            if(sizeImage>=6){
                file = new File( images.get(5));
                myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

                imageView = (ImageView)IDLineTwo.getChildAt(3);
                imageView.setImageBitmap(myBitmap);
                imageView.setVisibility(View.VISIBLE);
            }
            if(sizeImage>=7){
                file = new File( images.get(6));
                myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

                imageView = (ImageView)IDLineTwo.getChildAt(4);
                imageView.setImageBitmap(myBitmap);
                imageView.setVisibility(View.VISIBLE);
            }
            if(sizeImage==8){
                file = new File( images.get(7));
                myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

                imageView = (ImageView)IDLineTwo.getChildAt(5);
                imageView.setImageBitmap(myBitmap);
                imageView.setVisibility(View.VISIBLE);
            }

            IDImageView.addView(myLayoutImages);

        }
        else if(sizeImage>=9)
        {
            View myLayoutImages = inflater.inflate(R.layout.three_image, null, false);

            LinearLayout IDLineOne = myLayoutImages.findViewById(R.id.OneLine);
            LinearLayout IDLineTwo = myLayoutImages.findViewById(R.id.TwoLine);
            LinearLayout IDLineThree = myLayoutImages.findViewById(R.id.ThreeLine);

            File file = new File( images.get(0));
            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            ImageView imageView =  (ImageView)IDLineOne.getChildAt(0);
            imageView.setImageBitmap(myBitmap);

            imageView =  (ImageView)IDLineOne.getChildAt(1);
            file = new File( images.get(1));
            myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);

            imageView =  (ImageView)IDLineOne.getChildAt(2);
            file = new File( images.get(2));
            myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);


            imageView =  (ImageView)IDLineTwo.getChildAt(0);
            file = new File( images.get(3));
            myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);

            imageView =  (ImageView)IDLineTwo.getChildAt(1);
            file = new File( images.get(4));
            myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);

            imageView =  (ImageView)IDLineTwo.getChildAt(2);
            file = new File( images.get(5));
            myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);


            imageView =  (ImageView)IDLineThree.getChildAt(0);
            file = new File( images.get(6));
            myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);

            imageView =  (ImageView)IDLineThree.getChildAt(1);
            file = new File( images.get(7));
            myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);

            imageView =  (ImageView)IDLineThree.getChildAt(2);
            file = new File( images.get(8));
            myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);

            IDImageView.addView(myLayoutImages);

        }
//
//        LinearLayout ImageLineOne=new LinearLayout(this);
//        ImageLineOne.setLayoutParams(
//                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                        ViewGroup.LayoutParams.WRAP_CONTENT));
//        ImageLineOne.setGravity(View.FOCUS_LEFT);
//        ImageLineOne.setOrientation(LinearLayout.HORIZONTAL);
//
//        LinearLayout ImageLineTwo=new LinearLayout(this);
//        ImageLineTwo.setLayoutParams(
//                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                        ViewGroup.LayoutParams.WRAP_CONTENT));
//        ImageLineTwo.setGravity(View.FOCUS_LEFT);
//        ImageLineTwo.setOrientation(LinearLayout.HORIZONTAL);
//
//        LinearLayout ImageLineThree=new LinearLayout(this);
//        ImageLineThree.setLayoutParams(
//                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                        ViewGroup.LayoutParams.WRAP_CONTENT));
//        ImageLineThree.setGravity(View.FOCUS_LEFT);
//        ImageLineThree.setOrientation(LinearLayout.HORIZONTAL);
//
//        int sizeImage=images.size();
//        Log.e("sizeImage",sizeImage+"");
//
//        if(sizeImage <= 2)
//        {
//            Log.e("sizeImage",sizeImage+"");
//            for (String st:images) {
//                File file = new File( st);
//                Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//
//                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                        ViewGroup.LayoutParams.MATCH_PARENT, 1);
//
//                ImageView IDLineOneOne = new ImageView(this);
//
//                IDLineOneOne.setLayoutParams(layoutParams);
//                IDLineOneOne.setVisibility(View.VISIBLE);
//                IDLineOneOne.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                IDLineOneOne.setImageBitmap(myBitmap);
//
//                IDLineOneOne.setPadding(2,2,0,0);
//
//                ImageLineOne.addView(IDLineOneOne);
//            }
//
//        }
//        else if(sizeImage<=8)
//        {
//            int height= (int) (ViewGroup.LayoutParams.MATCH_PARENT*0.8f);
//
//
//
//            for (int i=0;i<2;i++) {
//                File file = new File(images.get(i));
//                Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//
//                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
//                        ViewGroup.LayoutParams.MATCH_PARENT,
//                        height, 1);
//
//                ImageView IDLineOneOne = new ImageView(this);
//
//                IDLineOneOne.setLayoutParams(layoutParams);
//                IDLineOneOne.setVisibility(View.VISIBLE);
//                IDLineOneOne.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                IDLineOneOne.setImageBitmap(myBitmap);
//                IDLineOneOne.setPadding(2,2,0,0);
//
//                ImageLineOne.addView(IDLineOneOne);
//            }
//
//            height= (int) (ViewGroup.LayoutParams.MATCH_PARENT*0.2);
//            int width=(int) (ViewGroup.LayoutParams.MATCH_PARENT/6) -12;
//
//            File file = new File(images.get(1));
//            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//
//            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
//                    width,
//                    height, 1);
//
//            ImageView IDLineOneOne = new ImageView(this);
//
//            IDLineOneOne.setLayoutParams(layoutParams);
//            IDLineOneOne.setVisibility(View.VISIBLE);
//            IDLineOneOne.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            IDLineOneOne.setImageBitmap(myBitmap);
//            IDLineOneOne.setPadding(2,2,0,0);
//
//            ImageLineTwo.addView(IDLineOneOne);
//
////            for (int i=2;i<8;i++) {
////                File file = new File(images.get(i));
////                Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
////
////                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
////                        ViewGroup.LayoutParams.MATCH_PARENT,
////                        ViewGroup.LayoutParams.MATCH_PARENT, 1);
////
////                ImageView IDLineOneOne = new ImageView(this);
////
////                IDLineOneOne.setLayoutParams(layoutParams);
////                IDLineOneOne.setVisibility(View.VISIBLE);
////                IDLineOneOne.setScaleType(ImageView.ScaleType.CENTER_CROP);
////                IDLineOneOne.setImageBitmap(myBitmap);
////                IDLineOneOne.setPadding(2,2,0,0);
////
////                ImageLineTwo.addView(IDLineOneOne);
////            }
//        }
//        else if(sizeImage>=9)
//        {
//
//            for (int i=0;i<3;i++) {
//                File file = new File(images.get(i));
//                Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//
//                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                        ViewGroup.LayoutParams.MATCH_PARENT, 1);
//
//                ImageView IDLineOneOne = new ImageView(this);
//
//                IDLineOneOne.setLayoutParams(layoutParams);
//                IDLineOneOne.setVisibility(View.VISIBLE);
//                IDLineOneOne.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                IDLineOneOne.setImageBitmap(myBitmap);
//                IDLineOneOne.setPadding(2,2,0,0);
//
//                ImageLineOne.addView(IDLineOneOne);
//            }
//
//            for (int i=3;i<6;i++) {
//                File file = new File(images.get(i));
//                Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//
//                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                        ViewGroup.LayoutParams.MATCH_PARENT, 1);
//
//                ImageView IDLineOneOne = new ImageView(this);
//
//                IDLineOneOne.setLayoutParams(layoutParams);
//                IDLineOneOne.setVisibility(View.VISIBLE);
//                IDLineOneOne.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                IDLineOneOne.setImageBitmap(myBitmap);
//                IDLineOneOne.setPadding(2,2,0,0);
//
//                ImageLineTwo.addView(IDLineOneOne);
//            }
//
//            for (int i=6;i<9;i++) {
//                File file = new File(images.get(i));
//                Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//
//                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                        ViewGroup.LayoutParams.MATCH_PARENT, 1);
//
//                ImageView IDLineOneOne = new ImageView(this);
//
//                IDLineOneOne.setLayoutParams(layoutParams);
//                IDLineOneOne.setVisibility(View.VISIBLE);
//                IDLineOneOne.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                IDLineOneOne.setImageBitmap(myBitmap);
//                IDLineOneOne.setPadding(2,2,0,0);
//
//                ImageLineThree.addView(IDLineOneOne);
//            }
//        }

//        {
//            ImageView IDLineOneOne = new ImageView(this);
//
//            File file = new File(Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" + post.getImagePost());
//            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//
//            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                    ViewGroup.LayoutParams.MATCH_PARENT, 1);
//
//            IDLineOneOne.setLayoutParams(layoutParams);
//            IDLineOneOne.setVisibility(View.VISIBLE);
//            IDLineOneOne.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            IDLineOneOne.setImageBitmap(myBitmap);
//
//            ImageLine.addView(IDLineOneOne);
//        }
//        {
//            ImageView IDLineOneOne = new ImageView(this);
//
//            File file = new File(Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" + post.getImagePost());
//            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//
//            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                    ViewGroup.LayoutParams.MATCH_PARENT, 1);
//
//            IDLineOneOne.setLayoutParams(layoutParams);
//            IDLineOneOne.setVisibility(View.VISIBLE);
//            IDLineOneOne.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            IDLineOneOne.setImageBitmap(myBitmap);
//
//            ImageLine.addView(IDLineOneOne);
//        }
//        {
//            ImageView IDLineOneOne = new ImageView(this);
//
//            File file = new File(Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" + post.getImagePost());
//            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//
//            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                    ViewGroup.LayoutParams.MATCH_PARENT, 1);
//
//            IDLineOneOne.setLayoutParams(layoutParams);
//            IDLineOneOne.setVisibility(View.VISIBLE);
//            IDLineOneOne.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            IDLineOneOne.setImageBitmap(myBitmap);
//
//            ImageLine.addView(IDLineOneOne);
//        }


//        listView.addView(ImageLineOne);
//        listView.addView(ImageLineTwo);
//        listView.addView(ImageLineThree);

        String Date = new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime());

        String DatePost = post.getPostDate();
        String TimePost = post.getPostTime();

        if (Date.contains(DatePost)) {
            PostTime.setText(TimePost);
        } else {
            PostTime.setText(DatePost + " " + TimePost);
        }

        PostText.setText(post.getPostText());

        listView.addView(myLayout);
    }
}