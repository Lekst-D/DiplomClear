package com.example.diplomclear;

import static android.content.ContentValues.TAG;

import static androidx.fragment.app.DialogFragment.STYLE_NO_FRAME;

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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.diplomclear.Classes.Post;
import com.example.diplomclear.Message.Messager;
import com.example.diplomclear.Message.MessegeList;
import com.example.diplomclear.SliderImage.CustomDialogFragment;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class AnotherUser extends AppCompatActivity {

    LinearLayout linearLayout;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListenner;
    private FirebaseUser user;
    private DatabaseReference myRef;
    private String IdUser;
    private DatabaseReference mDatabase;

    ArrayList<String> ImageFormPost;
    ArrayList<Post> AllUserPost = new ArrayList<>();

    String Name = null;
    String Surname = null;
    String UserPhoto = null;
    String Subscribes = null;
    String IDU = null;
    String IDListMessager = "";

    ImageView IDUserImage;

    ArrayList<String> subs = new ArrayList<>();
    ArrayList<String> images = new ArrayList<>();
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_another_user);

        IDUserImage=findViewById(R.id.IDUserImage);
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


        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("UserInfo").child(IdUser).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    Name = task.getResult().child("userName").getValue().toString();
                    Surname = task.getResult().child("userSurname").getValue().toString();
                    UserPhoto = task.getResult().child("userPhoto").getValue().toString();
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));

                    ((TextView) findViewById(R.id.UserName)).setText(Name + " " + Surname);

                    DownloadImageUser(UserPhoto,IDUserImage);
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
                        String ImagePost = document.get("Images").toString();
                        String UserID = document.get("UserID").toString();
                        String PostDate = document.get("DatePost").toString();
                        String PostTime = document.get("TimePost").toString();
                        String PostText = document.get("TextPost").toString();
                        String PostD = document.getId().toString();

                        Post post = new Post(ImagePost, UserID, PostText, PostDate, PostTime,PostD);

                        AllUserPost.add(post);
                    }

                    for (Post post : (AllUserPost)) {
                        ShowPost(post);
                    }

                    if (AllUserPost.size() != 0) {
                        HideLoad(true);
                    } else {
                        HideLoad(false);
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
                    if (task.getResult().getValue() != null) {
                        Subscribes = task.getResult().getValue().toString();

                        if (Subscribes.contains(IdUser)) {
                            Subscribe.setText("Отписаться");
                        } else {
                            Subscribe.setText("Подписаться");
                        }
                    } else {
                        Subscribe.setText("Подписаться");
                    }

                } else {
                    Subscribes = "";
                    Log.e("firebase", "Error getting data", task.getException());
                }
            }
        });


        Subscribe.setOnClickListener(new View.OnClickListener() {
                                         public void onClick(View v) {
                                             String textSub = Subscribe.getText().toString();

                                             if (textSub.contains("Подписаться")) {
                                                 Subscribes = Subscribes + "," + IdUser;
//                                                 Subscribes=Subscribes.trim();
//                                                 Subscribes = Subscribes.substring(0, Subscribes.length() - 1);

                                                 mDatabase.child("Subscribe").child(IDU).setValue(Subscribes);
                                                 Subscribe.setText("Отписаться");
                                             }
                                             if (textSub.contains("Отписаться")) {
                                                 subs = new ArrayList<String>(Arrays.asList((Subscribes.split(","))));

                                                 int i = subs.indexOf(IdUser);
                                                 Log.e("number i", "" + i);
                                                 Log.e("number i", "" + subs.get(i));

                                                 subs.remove(subs.get(i));
                                                 subs.remove("null");
                                                 subs.remove("");

                                                 Log.e("subs", subs.toString());
                                                 Log.e("Subscribes", Subscribes);
                                                 Subscribes = "null,";

                                                 for (int j = 0; j < subs.size(); j++) {

                                                     Subscribes += subs.get(j);

                                                     if (j != subs.size() - 1) {
                                                         Subscribes += ",";
                                                     }

                                                 }

//                                                 if(Subscribes.trim()=="null,"){Subscribes="null";}

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
                    if (task.getResult().hasChild("Name")) {
                        IDListMessager = "Children";
                        IDListMessager = task.getResult().child("Name").getValue().toString();
                    } else {
                        IDListMessager = "";
                    }
                } else {
                    Log.e("firebase", "Error getting data", task.getException());
                }
            }
        });

        Messager.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (IDListMessager == "") {
                    mDatabase.child("MessageList").child(IDU).child(IdUser).child("Name").setValue(IDU + "-" + IdUser);
                    mDatabase.child("MessageList").child(IdUser).child(IDU).child("Name").setValue(IDU + "-" + IdUser);

                    ShowMessager(IDU + "-" + IdUser);
                } else {
                    ShowMessager(IDListMessager);
                }
//                String IDListMessager=IDU+"-"+IdUser;

            }
        });

        ImageButton IDList = findViewById(R.id.IDList);
        IDList.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        ListOpen(v);
                    }
                }
        );

        ImageButton IDMessageList = findViewById(R.id.IDMessageList);
        IDMessageList.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        ShowMessage();
                    }
                }
        );


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

    void ShowMessage() {
        Intent intent = new Intent(this, MessegeList.class);
        startActivity(intent);
    }

    void ListOpen(View view) {
        Intent intent = new Intent(this, ListAct.class);
        startActivity(intent);
    }

    void ShowMessager(String IDListMessager) {
//        Toast toast = Toast.makeText(this, IDListMessager,Toast.LENGTH_LONG);
//        toast.show();

        Intent intent = new Intent(this, Messager.class);
        intent.putExtra("IDAnotherUser", IdUser);
        intent.putExtra("IDUser", IDU);
        intent.putExtra("IDListMessager", IDListMessager);
        startActivity(intent);
    }

    void ShowAllImage() {
        Intent intent = new Intent(this, AllImageUser.class);
        intent.putExtra("IDUser", IdUser);
        intent.putExtra("User", "Another");
        startActivity(intent);
    }

    void NewPostView() {
        Intent intent = new Intent(this, NewPost.class);
        startActivity(intent);
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
        }
        return ImageName;
    }

    void ShowThreeImage() throws InterruptedException {
        int lenPost = images.size();

        ImageView ImageView1 = findViewById(R.id.ImageView1);
        ImageView ImageView2 = findViewById(R.id.ImageView2);
        ImageView ImageView3 = findViewById(R.id.ImageView3);

        if (lenPost >= 1) {
            File file = new File(Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" + images.get(0));
            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            ImageView1.setImageBitmap(myBitmap);

            int position=0;

            ImageView1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Bundle args = new Bundle();
                    args.putStringArrayList("mArrayUri", images);
                    args.putInt("CurentPosition",position);

                    CustomDialogFragment dialog = new CustomDialogFragment();
                    dialog.setArguments(args);
                    dialog.setStyle(STYLE_NO_FRAME,
                            android.R.style.Theme_Black_NoTitleBar_Fullscreen);

                    dialog.show(getSupportFragmentManager(), "custom");
                }}
            );
        }
        if (lenPost >= 2) {
            File file = new File(Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" + images.get(1));
            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            ImageView2.setImageBitmap(myBitmap);

            int position=1;

            ImageView2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Bundle args = new Bundle();
                    args.putStringArrayList("mArrayUri", images);
                    args.putInt("CurentPosition",position);

                    CustomDialogFragment dialog = new CustomDialogFragment();
                    dialog.setArguments(args);
                    dialog.setStyle(STYLE_NO_FRAME,
                            android.R.style.Theme_Black_NoTitleBar_Fullscreen);

                    dialog.show(getSupportFragmentManager(), "custom");
                }}
            );
        }
        if (lenPost >= 3) {
            File file = new File(Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" + images.get(2));
            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            ImageView3.setImageBitmap(myBitmap);

            int position=2;

            ImageView3.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Bundle args = new Bundle();
                    args.putStringArrayList("mArrayUri", images);
                    args.putInt("CurentPosition",position);

                    CustomDialogFragment dialog = new CustomDialogFragment();
                    dialog.setArguments(args);
                    dialog.setStyle(STYLE_NO_FRAME,
                            android.R.style.Theme_Black_NoTitleBar_Fullscreen);

                    dialog.show(getSupportFragmentManager(), "custom");
                }}
            );
        }

    }

    public String DownloadImage(String ImageName, ImageView Image,ArrayList<String> ImageListSlider) {

        File dir = new File(Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" + ImageName);
        if (dir.exists()) {

            File file = new File(Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" + ImageName);
            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            Image.setImageBitmap(myBitmap);

        }
        else {


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
        }

        int position=ImageListSlider.indexOf(ImageName);

        Image.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putStringArrayList("mArrayUri", ImageListSlider);
                args.putInt("CurentPosition",position);

                CustomDialogFragment dialog = new CustomDialogFragment();
                dialog.setArguments(args);
                dialog.setStyle(STYLE_NO_FRAME,
                        android.R.style.Theme_Black_NoTitleBar_Fullscreen);

                dialog.show(getSupportFragmentManager(), "custom");
            }}
        );

        try {
            Log.e("ThreePost", "111111111111111");
            ShowThreeImage();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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

        ImageView IDUserPostImage= myLayout.findViewById(R.id.IDUserPostImage);
        if(UserPhoto.trim()!="null")
        {
            DownloadImageUser(UserPhoto.trim(),IDUserPostImage);
        }

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

        LinearLayout IDImageView = myLayout.findViewById(R.id.IDImageView);

        String ImageMess=post.getImagePost();


        if (!ImageMess.contains("null")) {

            ArrayList<String> ImageListSlider= new ArrayList<>();

            ArrayList<String> images = new ArrayList<>();
            images = new ArrayList<String>(Arrays.asList((ImageMess.split(","))));
            images.remove("null");

            for (String st: images) {
                ImageListSlider.add(st.trim());
            }

            this.images.addAll(ImageListSlider);

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
                    File file = new File( Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" +images.get(0).trim());
                    Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

                    ImageView imageView = (ImageView)IDLineOne.getChildAt(0);
                    imageView.setImageBitmap(myBitmap);

                    DownloadImage(images.get(0).trim(),imageView,ImageListSlider);

                    imageView = (ImageView)IDLineOne.getChildAt(1);

                    imageView.setVisibility(View.GONE);
                }
                else
                {
                    File file = new File( Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" +images.get(0).trim());
                    Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    ImageView imageView =  (ImageView)IDLineOne.getChildAt(0);
                    imageView.setImageBitmap(myBitmap);
                    DownloadImage(images.get(0).trim(),imageView,ImageListSlider);


                    imageView = (ImageView)IDLineOne.getChildAt(1);
                    file = new File( Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" +images.get(1).trim());
                    myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    imageView.setImageBitmap(myBitmap);
                    DownloadImage(images.get(1).trim(),imageView,ImageListSlider);

                }

                IDImageView.addView(myLayoutImages);
            }
            else if(sizeImage<=8)
            {
                View myLayoutImages = inflater.inflate(R.layout.two_image, null, false);

                LinearLayout IDLineOne = myLayoutImages.findViewById(R.id.OneLine);
                LinearLayout IDLineTwo = myLayoutImages.findViewById(R.id.TwoLine);
                LinearLayout IDLineThree = myLayoutImages.findViewById(R.id.ThreeLine);

                File file = new File( Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" +images.get(0).trim());
                Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                ImageView imageView =  (ImageView)IDLineOne.getChildAt(0);
                imageView.setImageBitmap(myBitmap);
                DownloadImage(images.get(0).trim(),imageView,ImageListSlider);

                imageView =  (ImageView)IDLineOne.getChildAt(1);
                file = new File( Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" +images.get(1).trim());
                myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                imageView.setImageBitmap(myBitmap);
                DownloadImage(images.get(1).trim(),imageView,ImageListSlider);

                if(sizeImage>=3)
                {
                    file = new File( Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" +images.get(2).trim());

                    myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

                    imageView = (ImageView)IDLineTwo.getChildAt(0);
                    imageView.setImageBitmap(myBitmap);
                    imageView.setVisibility(View.VISIBLE);
                    DownloadImage(images.get(2).trim(),imageView,ImageListSlider);
                }
                if(sizeImage>=4)
                {
                    file = new File( Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" +images.get(3).trim());

                    myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

                    imageView = (ImageView)IDLineTwo.getChildAt(1);
                    imageView.setImageBitmap(myBitmap);
                    imageView.setVisibility(View.VISIBLE);
                    DownloadImage(images.get(3).trim(),imageView,ImageListSlider);
                }
                if(sizeImage>=5){
                    file = new File( Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" +images.get(4).trim());

                    myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

                    imageView = (ImageView)IDLineTwo.getChildAt(2);
                    imageView.setImageBitmap(myBitmap);
                    imageView.setVisibility(View.VISIBLE);
                    DownloadImage(images.get(4).trim(),imageView,ImageListSlider);
                }
                if(sizeImage>=6){
                    file = new File( Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" +images.get(5).trim());

                    myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

                    imageView = (ImageView)IDLineTwo.getChildAt(3);
                    imageView.setImageBitmap(myBitmap);
                    imageView.setVisibility(View.VISIBLE);
                    DownloadImage(images.get(5).trim(),imageView,ImageListSlider);
                }
                if(sizeImage>=7){
                    file = new File( Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" +images.get(6).trim());

                    myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

                    imageView = (ImageView)IDLineTwo.getChildAt(4);
                    imageView.setImageBitmap(myBitmap);
                    imageView.setVisibility(View.VISIBLE);
                    DownloadImage(images.get(6).trim(),imageView,ImageListSlider);
                }
                if(sizeImage==8){
                    file = new File( Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" +images.get(7).trim());

                    myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

                    imageView = (ImageView)IDLineTwo.getChildAt(5);
                    imageView.setImageBitmap(myBitmap);
                    imageView.setVisibility(View.VISIBLE);
                    DownloadImage(images.get(7).trim(),imageView,ImageListSlider);
                }

                IDImageView.addView(myLayoutImages);

            }
            else if(sizeImage>=9)
            {
                View myLayoutImages = inflater.inflate(R.layout.three_image, null, false);

                LinearLayout IDLineOne = myLayoutImages.findViewById(R.id.OneLine);
                LinearLayout IDLineTwo = myLayoutImages.findViewById(R.id.TwoLine);
                LinearLayout IDLineThree = myLayoutImages.findViewById(R.id.ThreeLine);

                File file = new File( Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" +images.get(0).trim());
                Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                ImageView imageView =  (ImageView)IDLineOne.getChildAt(0);
                imageView.setImageBitmap(myBitmap);
                DownloadImage(images.get(0).trim(),imageView,ImageListSlider);

                imageView =  (ImageView)IDLineOne.getChildAt(1);
                file = new File( Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" +images.get(1).trim());
                myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                imageView.setImageBitmap(myBitmap);
                DownloadImage(images.get(1).trim(),imageView,ImageListSlider);

                imageView =  (ImageView)IDLineOne.getChildAt(2);
                file = new File( Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" +images.get(2).trim());
                myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                imageView.setImageBitmap(myBitmap);
                DownloadImage(images.get(2).trim(),imageView,ImageListSlider);


                imageView =  (ImageView)IDLineTwo.getChildAt(0);
                file = new File( Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" +images.get(3).trim());
                myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                imageView.setImageBitmap(myBitmap);
                DownloadImage(images.get(3).trim(),imageView,ImageListSlider);

                imageView =  (ImageView)IDLineTwo.getChildAt(1);
                file = new File( Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" +images.get(4).trim());
                myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                imageView.setImageBitmap(myBitmap);
                DownloadImage(images.get(4).trim(),imageView,ImageListSlider);

                imageView =  (ImageView)IDLineTwo.getChildAt(2);
                file = new File( Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" +images.get(5).trim());
                myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                imageView.setImageBitmap(myBitmap);
                DownloadImage(images.get(5).trim(),imageView,ImageListSlider);


                imageView =  (ImageView)IDLineThree.getChildAt(0);
                file = new File( Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" +images.get(6).trim());
                myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                imageView.setImageBitmap(myBitmap);
                DownloadImage(images.get(6).trim(),imageView,ImageListSlider);

                imageView =  (ImageView)IDLineThree.getChildAt(1);
                file = new File( Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" +images.get(7).trim());
                myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                imageView.setImageBitmap(myBitmap);
                DownloadImage(images.get(7).trim(),imageView,ImageListSlider);

                imageView =  (ImageView)IDLineThree.getChildAt(2);
                file = new File( Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" +images.get(8).trim());
                myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                imageView.setImageBitmap(myBitmap);
                DownloadImage(images.get(8).trim(),imageView,ImageListSlider);

                IDImageView.addView(myLayoutImages);

            }
//

//            Log.e("sizeImage",sizeImage+"");
//            Log.e("images",images+"");
//
//
//
//            for (String st:images) {
//                ImageView imageView=new ImageView(this);
//                imageView.setLayoutParams(new LinearLayout.LayoutParams(
//                        LinearLayout.LayoutParams.MATCH_PARENT,
//                        LinearLayout.LayoutParams.MATCH_PARENT));
//
//                File file = new File(Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" + st.trim());
//                Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//
//
//                if (file.exists()) {
//                    Log.e("images exists",st+"");
//                    imageView.setImageBitmap(myBitmap);
//
//                } else {
//                    Log.e("images not exists",st+"");
//                    DownloadImage(st, imageView);
//                }
//
//
//
//                listView.addView(imageView);
//            }

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