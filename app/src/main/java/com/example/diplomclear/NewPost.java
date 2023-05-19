package com.example.diplomclear;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NewPost extends AppCompatActivity {

    ImageView ImagePost;
    Button ShowImage, NewPost;
    ImageButton AddPost, CloseNewPost;
    EditText TextPost;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference myRef;

    private static final int SELECT_PICTURE = 1;

    private String selectedImagePath;
    private Uri UriAdressPhoto;
    private String IdUser;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        //elements form form
        ImagePost = findViewById(R.id.IDImageView);
        ShowImage = findViewById(R.id.IDNewImagePost);
        NewPost = findViewById(R.id.IDaddPost);
        TextPost = findViewById(R.id.IdTextPost);

        AddPost=findViewById(R.id.IDCheck);
        CloseNewPost=findViewById(R.id.IDClose);

        ShowImage.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, SELECT_PICTURE);
            }
        });

        //firebase
        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        IdUser=user.getUid().toString();

        AddPost.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View view) {

                ArrayList<String> PhotoForSend = new ArrayList<>();
                String namePhotos="";

                File dir = new File(Environment.getExternalStorageDirectory() + "/Pictures/YouDeo");
                if (!dir.exists()) {
                    new File(Environment.getExternalStorageDirectory() + "/Pictures/YouDeo").mkdirs();
                }

                String path = Environment.getExternalStorageDirectory() + "/Download/1.jpeg";

                Uri u = UriAdressPhoto;

                Bitmap photo = ImageUtils.getInstant().getCompressedBitmap(selectedImagePath);
                try {
                    String NewName="";
                    long time = System.currentTimeMillis();
                    NewName=IdUser+ time + ".jpg";

                    path = Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" +NewName;
                    File f = new File(path);
                    f.createNewFile();
                    FileOutputStream fos = new FileOutputStream(path);
                    photo.compress(Bitmap.CompressFormat.JPEG, 100, fos);

                    fos.flush();
                    fos.close();

                    PhotoForSend.add(NewName);


                    namePhotos=String.join(", ", PhotoForSend);

                } catch (IOException e) {
                    e.printStackTrace();
                }


                String textpost=TextPost.getText().toString();

                Date currentTime = Calendar.getInstance().getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd.MM.yyyy");
                String datePost=sdf.format( currentTime );

                Log.e("date time not mistake day",sdf.format( currentTime )+"");
                Log.e("date time not mistake mouth",currentTime.getMonth()+"");
                Log.e("date time not mistake year",currentTime.getYear()+"");

                Log.e("date time not mistake hours",currentTime.getMonth()+"");
                Log.e("date time not mistake minutes",currentTime.getYear()+"");
                Log.e("date time not mistake all date",currentTime.toString());


                String Date=new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime());
                String Time = new SimpleDateFormat("HH.mm").format(Calendar.getInstance().getTime());

                Map<String, Object> user = new HashMap<>();
                user.put("DatePost", Date);
                user.put("TimePost", Time);
                user.put("TextPost", textpost);
                user.put("Images", namePhotos);
                user.put("UserID", IdUser);

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("usersPosts")
                        .add(user)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());

                                CloseThisSuccess();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                            }
                        });

                for (String Photo:PhotoForSend) {


                    FirebaseStorage storage = FirebaseStorage.getInstance();

                    StorageReference storageRef = storage.getReference();
                    StorageReference imagesRef = storageRef.child(IdUser+"/"+Photo);
                    StorageReference spaceRef = storageRef.child("images/space.jpg");

                    spaceRef.getName().equals(spaceRef.getName());    // true

//                    path = Environment.getExternalStorageDirectory() + "/Download/23.jpg";
                    path = Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/"+Photo;

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
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                            // ...
                        }
                    });
                }

            }
        });

        CloseNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("Result_ok", resultCode + "");
        if (resultCode == RESULT_OK) {
            Log.e("Result_ok", resultCode + "");
            if (requestCode == SELECT_PICTURE) {
                Log.e("requestCode", requestCode + "");

                Uri selectedImageUri = data.getData();
                UriAdressPhoto = selectedImageUri;
                selectedImagePath = getPath(selectedImageUri);

//                ImagePost.setImageURI(selectedImageUri);

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

    public void CloseThisSuccess()
    {
        Intent intent = new Intent(this, User.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}