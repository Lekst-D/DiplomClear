package com.example.diplomclear;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.diplomclear.Classes.Post;
import com.example.diplomclear.Message.Messager;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AnotherUser extends AppCompatActivity {

    LinearLayout linearLayout;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListenner;
    private FirebaseUser user;
    private DatabaseReference myRef;
    private String IdUser;

    ArrayList<String> ImageFormPost;
    ArrayList<Post> AllUserPost = new ArrayList<>();

    String Name = null;
    String Surname = null;
    String UserPhoto = null;
    String Subscribes = null;
    String IDU = null;
    String IDListMessager="";

    ArrayList<String> subs = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_another_user);
//      --------------------------------------------------------
        Bundle arguments = getIntent().getExtras();
//
//        TextView FIO= findViewById(R.id.FIO);
//        TextView UID=findViewById(R.id.UserID);
//        TextView ImaUser=findViewById(R.id.ImageUser);
//
//        FIO.setText(arguments.get("FIO").toString());
//        UID.setText(arguments.get("UserID").toString());
//        ImaUser.setText(arguments.get("ImageUser").toString());
//      --------------------------------------------------------

        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        IdUser = arguments.get("UserID").toString();
        IDU = user.getUid();

        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("UserInfo").child(IdUser).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
//                    userName
//                            userSurname

                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    Name = task.getResult().child("userName").getValue().toString();
                    Surname = task.getResult().child("userSurname").getValue().toString();
                    UserPhoto = task.getResult().child("userPhoto").getValue().toString();
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));

                    ((TextView) findViewById(R.id.UserName)).setText(Name);
                    ((TextView) findViewById(R.id.UserSurname)).setText(Surname);
                }
            }
        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usersPosts").whereEqualTo("UserID", IdUser).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
//                                String ImagePost, String FIO_text,String PostText_text,String PostTime_text
                        Post post = new Post(document.get("Images").toString(), document.get("UserID").toString(), document.get("TextPost").toString(), document.get("dataTime").toString());

                        AllUserPost.add(post);
                    }

//                        for (int i = AllUserPost.size(); i >0; i--)
//                        {
//                            Post post=AllUserPost.get(i);
//                            ShowPost(post);
//                        }

                    for (Post post : (AllUserPost)) {
                        ShowPost(post);
                    }

//                        for (Post post : Lists.reverse(AllUserPost)) {
//                            ShowPost(post);
//                        }

//                    try {
//                        ShowThreeImage();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }

                    File dir = new File(Environment.getExternalStorageDirectory() + "/Pictures/YouDeo");
                    if (!dir.exists()) {
                        new File(Environment.getExternalStorageDirectory() + "/Pictures/YouDeo").mkdirs();
                    }

                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });


        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) LinearLayout IDAllImages = findViewById(R.id.IDAllImages);

        IDAllImages.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                ShowAllImage();
            }
        });

        Button Subscribe = findViewById(R.id.IDSubscribe);

        mDatabase.child("Subscribe").child(IDU).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    Subscribes = task.getResult().getValue().toString();

                    if (Subscribes.contains(IdUser)) {
                        Subscribe.setText("Отписаться");
                    } else {
                        Subscribe.setText("Подписаться");
                    }

                } else {
                    Log.e("firebase", "Error getting data", task.getException());
                }
            }
        });


        Subscribe.setOnClickListener(new View.OnClickListener() {
                                         public void onClick(View v) {
                                             String textSub = Subscribe.getText().toString();

                                             if (textSub.contains("Подписаться")) {
                                                 Subscribes = Subscribes + "," + IdUser;
                                                 mDatabase.child("Subscribe").child(IDU).setValue(Subscribes);
                                                 Subscribe.setText("Отписаться");
                                             }
                                             if (textSub.contains("Отписаться")) {
                                                 subs = new ArrayList<String>(Arrays.asList((Subscribes.split(","))));

                                                 int i = subs.indexOf(IdUser);
                                                 Log.e("number i", "" + i);
                                                 Log.e("number i", "" + subs.get(i));
                                                 subs.remove(subs.get(i));
                                                 Log.e("subs", subs.toString());
                                                 Log.e("Subscribes", Subscribes);
                                                 Subscribes = "";
//                                                 for (String st:subs) {
//                                                     Subscribes+=st+",";
//                                                 }
//
                                                 for (int j = 0; j < subs.size(); j++) {

                                                     Subscribes += subs.get(j);

                                                     if (j != subs.size() - 1) {
                                                         Subscribes += ",";
                                                     }

                                                 }

                                                 Log.e("Subscribes", Subscribes);
                                                 mDatabase.child("Subscribe").child(IDU).setValue(Subscribes);
                                                 Subscribe.setText("Подписаться");
                                             }
                                         }
                                     }
        );

        Button Messager = findViewById(R.id.IDMessager);

        mDatabase.child("MessageList").child(IDU).child(IdUser).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    if(task.getResult().hasChild("Name")){
                        IDListMessager="Children";
                        IDListMessager=task.getResult().child("Name").getValue().toString();
                    }
                    else{IDListMessager="";}
                } else {
                    Log.e("firebase", "Error getting data", task.getException());
                }
            }
        });

        Messager.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                Log.e("Message",Message);
//                mDatabase.child("MessageList").child(IDU+"-"+IdUser).setValue("true");


                if(IDListMessager=="") {
                    mDatabase.child("MessageList").child(IDU).child(IdUser).child("Name").setValue(IDU + "-" + IdUser);
                    mDatabase.child("MessageList").child(IdUser).child(IDU).child("Name").setValue(IDU + "-" + IdUser);

                    ShowMessager(IDU + "-" + IdUser);
                }
                else{
                    ShowMessager(IDListMessager);
                }
//                String IDListMessager=IDU+"-"+IdUser;

            }
        });


    }

    void ShowMessager(String IDListMessager)
    {
        Toast toast = Toast.makeText(this, IDListMessager,Toast.LENGTH_LONG);
        toast.show();

        Intent intent = new Intent(this, Messager.class);
        intent.putExtra("IDAnotherUser", IdUser);
        intent.putExtra("IDUser", IDU);
        intent.putExtra("IDListMessager", IDListMessager);
        startActivity(intent);
    }

    void ShowThreeImage() throws InterruptedException {
        int lenPost = AllUserPost.size();

        ImageView ImageView1 = findViewById(R.id.ImageView1);
        ImageView ImageView2 = findViewById(R.id.ImageView2);
        ImageView ImageView3 = findViewById(R.id.ImageView3);

        if (lenPost >= 1) {
            File file = new File(Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" + AllUserPost.get(0).getImagePost());
            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            ImageView1.setImageBitmap(myBitmap);
        }
        if (lenPost >= 2) {
            File file = new File(Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" + AllUserPost.get(1).getImagePost());
            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            ImageView2.setImageBitmap(myBitmap);
        }
        if (lenPost >= 3) {
            File file = new File(Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" + AllUserPost.get(2).getImagePost());
            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            ImageView3.setImageBitmap(myBitmap);
        }

    }

    void ShowAllImage() {
        Intent intent = new Intent(this, AllImageUser.class);
        intent.putExtra("IDUser", IdUser);
        startActivity(intent);
    }

    void NewPostView() {
        Intent intent = new Intent(this, NewPost.class);
        startActivity(intent);
    }

    public String DownloadImage(String ImageName, ImageView Image) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

//        "TMdmQbEc2vQxiSjLGUO0TNWMa3g21681545477795.jpg"
//        "TMdmQbEc2vQxiSjLGUO0TNWMa3g2"

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

                try {
                    ShowThreeImage();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

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

            try {
                ShowThreeImage();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } else {
            DownloadImage(post.getImagePost(), Image);
        }

//        FIO.setText(post.getFIO_text());
        PostTime.setText(post.getPostTime_text());
        PostText.setText(post.getPostText_text());

        listView.addView(myLayout);

//        MyAddapterPost stateAdapter = new MyAddapterPost(this, R.layout.user_post, AllUserPost);
//        usersList.setAdapter(stateAdapter);
    }
}