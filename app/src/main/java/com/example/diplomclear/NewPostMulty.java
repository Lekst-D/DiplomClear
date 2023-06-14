package com.example.diplomclear;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.diplomclear.Classes.ImageUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewPostMulty extends AppCompatActivity {

    private Button IDNewImagePost;
    private EditText IdTextPost;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference myRef;
    private String IdUser;
    private String UserID;

    private String NameImageAll = "";
    private List<Uri> uris;

    private ActivityResultLauncher<PickVisualMediaRequest> pickMultipleMedia;

    int act_i=0;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post_multy);

        uris = new ArrayList<>();

        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        IdUser = user.getUid();

        IdTextPost = findViewById(R.id.IdTextPost);
        IDNewImagePost = findViewById(R.id.IDNewImagePost);

        pickMultipleMedia = registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia(9), uris -> {
            if (!uris.isEmpty()) {
                Log.d("PhotoPicker", "Number of items selected: " + uris.size());
                this.uris.clear();

                this.uris = uris;
                showImage();
//                for (Uri u : uris) {
//                    Log.e("uris", u.getPath());
//                    try {
//                        ImageAddUser(u);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//                ImageAddUser(uris);

            } else {
                Log.d("PhotoPicker", "No media selected");
            }
        });

        IDNewImagePost.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        try {
                            pickMultipleMedia.launch(new PickVisualMediaRequest.Builder()
                                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                                    .build());
                        } catch (Exception e) {
                        }
                    }
                }
        );

        ImageButton AddPost = findViewById(R.id.IDCheck);
        ImageButton CloseNewPost = findViewById(R.id.IDClose);



        AddPost.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View view) {
                if(uris!=null){
                    if(act_i==0){
                ImageAddUser(uris);
                    act_i+=1;}
                    else{
                        Toast toast = Toast.makeText(NewPostMulty.this, "Ваш пост уже выкладывается",Toast.LENGTH_LONG);
                        toast.show();
                    }
                }
                else{
                    Toast toast = Toast.makeText(NewPostMulty.this, "Выберите изображение",Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });

        CloseNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FinishAct();
            }
        });
    }
    private void FinishAct()
    {
        Intent intent = new Intent(NewPostMulty.this, User.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void showImage(){
        ImageView IDImageView=findViewById(R.id.IDImageView);
        IDImageView.setVisibility(View.GONE);

        LayoutInflater inflater = getLayoutInflater();

        LinearLayout IDImageMultyView=findViewById(R.id.IDImageMultyView);
        IDImageMultyView.removeAllViews();
        IDImageMultyView.setVisibility(View.VISIBLE);


        if (!uris.contains("null")) {

            ArrayList<String> ImageListSlider = new ArrayList<>();

            int sizeImage = uris.size();
            Log.e("sizeImage", sizeImage + "");

            if (sizeImage <= 2) {
                View myLayoutImages = inflater.inflate(R.layout.one_image, null, false);

                LinearLayout IDLineOne = myLayoutImages.findViewById(R.id.OneLine);
                LinearLayout IDLineTwo = myLayoutImages.findViewById(R.id.TwoLine);
                LinearLayout IDLineThree = myLayoutImages.findViewById(R.id.ThreeLine);

                if (sizeImage == 1) {
                    Log.e("Image Size","1");

                    ImageView imageView = (ImageView) IDLineOne.getChildAt(0);
                    imageView.setImageURI(uris.get(0));

                    imageView = (ImageView) IDLineOne.getChildAt(1);

                    imageView.setVisibility(View.GONE);
                } else {
                    Log.e("Image Size","2");

                    ImageView imageView = (ImageView) IDLineOne.getChildAt(0);
                    imageView.setImageURI(uris.get(0));

                    imageView = (ImageView) IDLineOne.getChildAt(1);
                    imageView.setImageURI(uris.get(1));

                }

                IDImageMultyView.addView(myLayoutImages);
            } else if (sizeImage <= 8) {
                View myLayoutImages = inflater.inflate(R.layout.two_image, null, false);

                LinearLayout IDLineOne = myLayoutImages.findViewById(R.id.OneLine);
                LinearLayout IDLineTwo = myLayoutImages.findViewById(R.id.TwoLine);
                LinearLayout IDLineThree = myLayoutImages.findViewById(R.id.ThreeLine);

                ImageView imageView = (ImageView) IDLineOne.getChildAt(0);
                imageView.setImageURI(uris.get(0));

                imageView = (ImageView) IDLineOne.getChildAt(1);
                imageView.setImageURI(uris.get(1));

                if (sizeImage >= 3) {
                    Log.e("Image Size","3");
                    imageView = (ImageView) IDLineTwo.getChildAt(0);
                    imageView.setImageURI(uris.get(2));
                    imageView.setVisibility(View.VISIBLE);
                }
                if (sizeImage >= 4) {
                    Log.e("Image Size","4");
                    imageView = (ImageView) IDLineTwo.getChildAt(1);
                    imageView.setImageURI(uris.get(3));
                    imageView.setVisibility(View.VISIBLE);
                }
                if (sizeImage >= 5) {
                    Log.e("Image Size","5");
                    imageView = (ImageView) IDLineTwo.getChildAt(2);
                    imageView.setImageURI(uris.get(4));
                    imageView.setVisibility(View.VISIBLE);
                }
                if (sizeImage >= 6) {
                    Log.e("Image Size","6");
                    imageView = (ImageView) IDLineTwo.getChildAt(3);
                    imageView.setImageURI(uris.get(5));
                    imageView.setVisibility(View.VISIBLE);
                }
                if (sizeImage >= 7) {
                    Log.e("Image Size","7");
                    imageView = (ImageView) IDLineTwo.getChildAt(4);
                    imageView.setImageURI(uris.get(6));
                    imageView.setVisibility(View.VISIBLE);
                }
                if (sizeImage == 8) {
                    Log.e("Image Size","8");
                    imageView = (ImageView) IDLineTwo.getChildAt(5);
                    imageView.setImageURI(uris.get(7));
                    imageView.setVisibility(View.VISIBLE);
                }

                IDImageMultyView.addView(myLayoutImages);

            } else if (sizeImage >= 9) {
                Log.e("Image Size","9");
                View myLayoutImages = inflater.inflate(R.layout.three_image, null, false);

                LinearLayout IDLineOne = myLayoutImages.findViewById(R.id.OneLine);
                LinearLayout IDLineTwo = myLayoutImages.findViewById(R.id.TwoLine);
                LinearLayout IDLineThree = myLayoutImages.findViewById(R.id.ThreeLine);

                ImageView imageView = (ImageView) IDLineOne.getChildAt(0);
                imageView.setImageURI(uris.get(0));
                imageView.setVisibility(View.VISIBLE);

                imageView = (ImageView) IDLineOne.getChildAt(1);
                imageView.setImageURI(uris.get(1));
                imageView.setVisibility(View.VISIBLE);

                imageView = (ImageView) IDLineOne.getChildAt(2);
                imageView.setImageURI(uris.get(2));
                imageView.setVisibility(View.VISIBLE);


                imageView = (ImageView) IDLineTwo.getChildAt(0);
                imageView.setImageURI(uris.get(3));
                imageView.setVisibility(View.VISIBLE);

                imageView = (ImageView) IDLineTwo.getChildAt(1);
                imageView.setImageURI(uris.get(4));
                imageView.setVisibility(View.VISIBLE);

                imageView = (ImageView) IDLineTwo.getChildAt(2);
                imageView.setImageURI(uris.get(5));
                imageView.setVisibility(View.VISIBLE);


                imageView = (ImageView) IDLineThree.getChildAt(0);
                imageView.setImageURI(uris.get(6));
                imageView.setVisibility(View.VISIBLE);

                imageView = (ImageView) IDLineThree.getChildAt(1);
                imageView.setImageURI(uris.get(7));
                imageView.setVisibility(View.VISIBLE);

                imageView = (ImageView) IDLineThree.getChildAt(2);
                imageView.setImageURI(uris.get(8));
                imageView.setVisibility(View.VISIBLE);

                IDImageMultyView.addView(myLayoutImages);

            }
        }

    }

    public String getPath(Uri uri) {
        // just some safety built in
        if (uri == null) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        // this is our fallback here
        return uri.getPath();
    }

    void ImageAddUser(List<Uri> uris) {
        for (Uri Photo : uris) {
            String NewName = "";

            ArrayList<String> PhotoForSend = new ArrayList<>();
            String namePhotos = "";
            String path = "";

            File dir = new File(Environment.getExternalStorageDirectory() + "/Pictures/YouDeo");
            if (!dir.exists()) {
                new File(Environment.getExternalStorageDirectory() + "/Pictures/YouDeo").mkdirs();
            }

            Bitmap photo = ImageUtils.getInstant().getCompressedBitmap(getPath(Photo));
            try {


                long time = System.currentTimeMillis();
                NewName = IdUser + time + ".jpg";

                path = Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" + NewName;
                File f = new File(path);
                f.createNewFile();
                FileOutputStream fos = new FileOutputStream(path);
                photo.compress(Bitmap.CompressFormat.JPEG, 100, fos);

                fos.flush();
                fos.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

            NameImageAll += NewName + ",";

            String Date = new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime());
            String Time = new SimpleDateFormat("HH.mm").format(Calendar.getInstance().getTime());


            FirebaseStorage storage = FirebaseStorage.getInstance();

            StorageReference storageRef = storage.getReference();
            StorageReference imagesRef = storageRef.child(IdUser + "/" + NewName);
            StorageReference spaceRef = storageRef.child("images/space.jpg");

            spaceRef.getName().equals(spaceRef.getName());    // true

//                    path = Environment.getExternalStorageDirectory() + "/Download/23.jpg";
            path = Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" + NewName;

            InputStream stream = null;
            try {
                stream = new FileInputStream(new File(path));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


            UploadTask uploadTask = imagesRef.putStream(stream);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri UriTest = uris.get(uris.size() - 1);
                    if (UriTest == Photo) {
                        // SendPost();
                    }
                }
            });
        }
        SendPost();
    }

    public void SendPost() {

        NameImageAll = NameImageAll.substring(0, NameImageAll.length() - 1);

        String textpost = IdTextPost.getText().toString().trim();


        String Date = new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime());
        String Time = new SimpleDateFormat("HH.mm").format(Calendar.getInstance().getTime());

        Map<String, Object> user = new HashMap<>();
        user.put("DatePost", Date);
        user.put("TimePost", Time);
        user.put("TextPost", textpost);
        user.put("Images", NameImageAll);
        user.put("UserID", IdUser);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usersPosts")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        FinishAct();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }
}