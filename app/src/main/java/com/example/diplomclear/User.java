package com.example.diplomclear;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.diplomclear.Classes.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class User extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListenner;
    private FirebaseUser user;
    private DatabaseReference myRef;
    private String IdUser;

    ArrayList<String> ImageFormPost;
    ArrayList<Post> AllUserPost = new ArrayList<>();

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

        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        IdUser=user.getUid().toString();

        Bundle arguments = getIntent().getExtras();
//        String Name=arguments.get("Name").toString();
//        String SurName=arguments.get("Surname").toString();
//        String UserPhoto=arguments.get("ImageUser").toString();

//        ((TextView)findViewById(R.id.UserName)).setText(Name);
//        ((TextView)findViewById(R.id.UserSurname)).setText(SurName);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

//        DocumentReference docRef = db.collection("users")
//                .document("ZJeLaqh93tn8lRZTGCu9");
//        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    if (document.exists()) {
//                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
//                    } else {
//                        Log.d(TAG, "No such document");
//                    }
//                } else {
//                    Log.d(TAG, "get failed with ", task.getException());
//                }
//            }
//        });

        db.collection("usersPosts")
            .whereEqualTo("UserID", IdUser)
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
//                                String ImagePost, String FIO_text,String PostText_text,String PostTime_text
                            Post post=new Post(document.get("Images").toString(),
                                    document.get("UserID").toString(),
                                    document.get("TextPost").toString(),
                                    document.get("dataTime").toString());

                            AllUserPost.add(post);
                        }

                        for (Post post : AllUserPost) {
                            ShowPost(post);
                        }
                        ImageView ImageView1=findViewById(R.id.ImageView1);
                        ImageView ImageView2=findViewById(R.id.ImageView2);
                        ImageView ImageView3=findViewById(R.id.ImageView3);

//                        File file=new File(Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/"+AllUserPost.get(0).getImagePost());
//                        Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//                        ImageView1.setImageBitmap(myBitmap);
//
//                        file=new File(Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/"+AllUserPost.get(1).getImagePost());
//                        myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//                        ImageView2.setImageBitmap(myBitmap);
//
//                        file=new File(Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/"+AllUserPost.get(2).getImagePost());
//                        myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//                        ImageView3.setImageBitmap(myBitmap);

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



    void NewPostView(){
        Intent intent = new Intent(this, NewPost.class);
        startActivity(intent);
    }

    public String DownloadImage(String ImageName,ImageView Image)
    {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

//        "TMdmQbEc2vQxiSjLGUO0TNWMa3g21681545477795.jpg"
//        "TMdmQbEc2vQxiSjLGUO0TNWMa3g2"

        final long ONE_MEGABYTE = 1024 * 1024 * 1024;
        storageRef.child(IdUser).child(ImageName)
                .getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap=BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

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
    void ShowPost(Post post){

        LinearLayout listView=findViewById(R.id.IDListView);
        LayoutInflater inflater = getLayoutInflater();
        View myLayout = inflater.inflate(R.layout.user_post, null, false);

        ImageView Image = myLayout.findViewById(R.id.IDPostIMagePost);
        TextView FIO = myLayout.findViewById(R.id.IDUserFIO);
        TextView PostTime = myLayout.findViewById(R.id.IDPostTime);
        TextView PostText = myLayout.findViewById(R.id.IDPostText);


        File dir = new File(Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/"+post.getImagePost());
        if (dir.exists()) {

            File file=new File(Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/"+post.getImagePost());
            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            Image.setImageBitmap(myBitmap);

        }else
        {
            DownloadImage(post.getImagePost(),Image);
        }

//        FIO.setText(post.getFIO_text());
        PostTime.setText(post.getPostTime_text());
        PostText.setText(post.getPostText_text());

        listView.addView(myLayout);

//        MyAddapterPost stateAdapter = new MyAddapterPost(this, R.layout.user_post, AllUserPost);
//        usersList.setAdapter(stateAdapter);
    }

}