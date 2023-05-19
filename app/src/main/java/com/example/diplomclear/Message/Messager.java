package com.example.diplomclear.Message;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.diplomclear.Classes.ImageUtils;
import com.example.diplomclear.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
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

public class Messager extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private DatabaseReference myRef;

    private String IdUser;
    private String IdAnotherUser;
    private String FIO;

    String IDListMessager = "";

    TextView IdUserAnother;
    ScrollView IDScrollVIew;
    ImageView IDImagesMessage;

    private static final int CImageMax = 9;

    ArrayList<Uri> ImageList;

    ActivityResultLauncher<PickVisualMediaRequest> pickMultipleMedia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messager);

        IDScrollVIew = findViewById(R.id.IDScrollVIew);

        IDImagesMessage = findViewById(R.id.IDImagesMessage);

        Bundle arguments = getIntent().getExtras();
        IDListMessager = arguments.get("IDListMessager").toString();
        IdAnotherUser = arguments.get("IDAnotherUser").toString();

        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        IdUser = user.getUid();

        ImageList = new ArrayList<>();

        mDatabase.child("UserInfo").child(IdUser).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    FIO = task.getResult().child("userSurname").getValue().toString() + " " + task.getResult().child("userName").getValue().toString();
                } else {
                    Log.e("firebase", "Error getting data", task.getException());
                }
            }
        });

        pickMultipleMedia = registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia(9), uris -> {
            if (!uris.isEmpty()) {
                Log.d("PhotoPicker", "Number of items selected: " + uris.size());

                for (Uri u : uris) {
                    Log.e("uris", u.getPath());

                    ImageList.add(u);

                }
                ShowImage();
            } else {
                Log.d("PhotoPicker", "No media selected");
            }
        });

        IDImagesMessage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                pickMultipleMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            }
        });

        mDatabase.child("Messager").child(IDListMessager).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object value = dataSnapshot.getValue();
                if (value == null) {
                    HideLoad(false);
                } else {
                    LinearLayout listView = findViewById(R.id.IDScrollLinear);
                    listView.removeAllViews();

                    for (DataSnapshot document : dataSnapshot.getChildren()) {
                        String IDU = document.child("idu").getValue().toString();
                        String FIO = document.child("fio").getValue().toString();
                        String DateMess = document.child("dateMess").getValue().toString();
                        String TimeMess = document.child("timeMess").getValue().toString();
                        String TextMess = document.child("textMess").getValue().toString();
//                        String TextMess="повтор";
                        String ImageMess = document.child("imageMess").getValue().toString();
                        Message message = new Message(IDU, FIO, DateMess, TimeMess, TextMess, ImageMess);

                        ShowMessage(message);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ImageView IDPostMessage = findViewById(R.id.IDPostMessage);
        IDPostMessage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    SendMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        IdUserAnother = findViewById(R.id.IdUserAnother);

        mDatabase.child("UserInfo").child(IdAnotherUser).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {


                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    String Name = task.getResult().child("userName").getValue().toString();
                    String Surname = task.getResult().child("userSurname").getValue().toString();
                    IdUserAnother.setText(Surname + " " + Name);
                }
            }
        });

        ImageButton IDBack = findViewById(R.id.IDBack);
        IDBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

    }

    void ShowImage() {
        LayoutInflater inflater = getLayoutInflater();
        LinearLayout IDImageAddMess = findViewById(R.id.IDImageAddMess);


        for (Uri uri : ImageList) {
            View myLayoutImages = inflater.inflate(R.layout.image_message_edit, null, false);

            ImageView IDImage = myLayoutImages.findViewById(R.id.IDImage);
            IDImage.setImageURI(uri);

            ImageView IDCloseRound = myLayoutImages.findViewById(R.id.IDCloseRound);

            IDCloseRound.setOnClickListener(new View.OnClickListener() {
                                                public void onClick(View v) {
                                                    ImageList.remove(uri);
                                                    IDImageAddMess.removeView(myLayoutImages);
                                                }
                                            }
            );

            IDImageAddMess.addView(myLayoutImages);
        }

    }

    void HideLoad(boolean check) {

        LinearLayout IDLoad = findViewById(R.id.IDLoad);
        if (IDLoad.getVisibility() != View.INVISIBLE) {
            IDLoad.setVisibility(View.INVISIBLE);
        }

        if (!check) {
            TextView IDTVTextNotPost = findViewById(R.id.IDTVTextNotPost);
            IDTVTextNotPost.setVisibility(View.VISIBLE);
        }
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

    private LayoutInflater inflater;
    private View myLayout;

    @SuppressLint("ResourceAsColor")
    public void ShowMessage(Message message) {

        inflater = null;
        myLayout = null;

        String IDU = message.getIDU();
        String FIO = message.getFIO();
        String DateMess = message.getDateMess();
        String TimeMess = message.getTimeMess();
        String TextMess = message.getTextMess();
        String ImageMess = message.getImageMess();


        LinearLayout listView = findViewById(R.id.IDScrollLinear);

        if (IDU.contains(IdUser)) {
            inflater = getLayoutInflater();
            myLayout = inflater.inflate(R.layout.mess_user, null, false);
        } else {
            inflater = getLayoutInflater();
            myLayout = inflater.inflate(R.layout.mess, null, false);
        }

        myLayout.setPadding(0,3,0,3);

        TextView textMess = myLayout.findViewById(R.id.IDMessText);
        textMess.setText(TextMess);

        LinearLayout linearLayout = myLayout.findViewById(R.id.IDLinearLayoutMess);

        TextView IDMessDataTime = myLayout.findViewById(R.id.IDMessDataTime);

        String Date = new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime());

        if (Date.contains(DateMess)) {
            IDMessDataTime.setText(TimeMess);
        } else {
            IDMessDataTime.setText(DateMess + " " + TimeMess);
        }

        LinearLayout IDImageView = myLayout.findViewById(R.id.IDImageView);

        if (!ImageMess.contains("null")) {

            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));

            ArrayList<String> images = new ArrayList<>();
            images = new ArrayList<String>(Arrays.asList((ImageMess.split(","))));
            images.remove("null");

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

                    DownloadImage(images.get(0).trim(),imageView);

                    imageView = (ImageView)IDLineOne.getChildAt(1);

                    imageView.setVisibility(View.GONE);
                }
                else
                {
                    File file = new File( Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" +images.get(0).trim());
                    Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    ImageView imageView =  (ImageView)IDLineOne.getChildAt(0);
                    imageView.setImageBitmap(myBitmap);
                    DownloadImage(images.get(0).trim(),imageView);


                    imageView = (ImageView)IDLineOne.getChildAt(1);
                    file = new File( Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" +images.get(1).trim());
                    myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    imageView.setImageBitmap(myBitmap);
                    DownloadImage(images.get(1).trim(),imageView);

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
                DownloadImage(images.get(0).trim(),imageView);

                imageView =  (ImageView)IDLineOne.getChildAt(1);
                file = new File( Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" +images.get(1).trim());
                myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                imageView.setImageBitmap(myBitmap);
                DownloadImage(images.get(1).trim(),imageView);

                if(sizeImage>=3)
                {
                    file = new File( Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" +images.get(2).trim());

                    myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

                    imageView = (ImageView)IDLineTwo.getChildAt(0);
                    imageView.setImageBitmap(myBitmap);
                    imageView.setVisibility(View.VISIBLE);
                    DownloadImage(images.get(2).trim(),imageView);
                }
                if(sizeImage>=4)
                {
                    file = new File( Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" +images.get(3).trim());

                    myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

                    imageView = (ImageView)IDLineTwo.getChildAt(1);
                    imageView.setImageBitmap(myBitmap);
                    imageView.setVisibility(View.VISIBLE);
                    DownloadImage(images.get(3).trim(),imageView);
                }
                if(sizeImage>=5){
                    file = new File( Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" +images.get(4).trim());

                    myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

                    imageView = (ImageView)IDLineTwo.getChildAt(2);
                    imageView.setImageBitmap(myBitmap);
                    imageView.setVisibility(View.VISIBLE);
                    DownloadImage(images.get(4).trim(),imageView);
                }
                if(sizeImage>=6){
                    file = new File( Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" +images.get(5).trim());

                    myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

                    imageView = (ImageView)IDLineTwo.getChildAt(3);
                    imageView.setImageBitmap(myBitmap);
                    imageView.setVisibility(View.VISIBLE);
                    DownloadImage(images.get(5).trim(),imageView);
                }
                if(sizeImage>=7){
                    file = new File( Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" +images.get(6).trim());

                    myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

                    imageView = (ImageView)IDLineTwo.getChildAt(4);
                    imageView.setImageBitmap(myBitmap);
                    imageView.setVisibility(View.VISIBLE);
                    DownloadImage(images.get(6).trim(),imageView);
                }
                if(sizeImage==8){
                    file = new File( Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" +images.get(7).trim());

                    myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

                    imageView = (ImageView)IDLineTwo.getChildAt(5);
                    imageView.setImageBitmap(myBitmap);
                    imageView.setVisibility(View.VISIBLE);
                    DownloadImage(images.get(7).trim(),imageView);
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
                DownloadImage(images.get(0).trim(),imageView);

                imageView =  (ImageView)IDLineOne.getChildAt(1);
                file = new File( Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" +images.get(1).trim());
                myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                imageView.setImageBitmap(myBitmap);
                DownloadImage(images.get(1).trim(),imageView);

                imageView =  (ImageView)IDLineOne.getChildAt(2);
                file = new File( Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" +images.get(2).trim());
                myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                imageView.setImageBitmap(myBitmap);
                DownloadImage(images.get(2).trim(),imageView);


                imageView =  (ImageView)IDLineTwo.getChildAt(0);
                file = new File( Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" +images.get(3).trim());
                myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                imageView.setImageBitmap(myBitmap);
                DownloadImage(images.get(3).trim(),imageView);

                imageView =  (ImageView)IDLineTwo.getChildAt(1);
                file = new File( Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" +images.get(4).trim());
                myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                imageView.setImageBitmap(myBitmap);
                DownloadImage(images.get(4).trim(),imageView);

                imageView =  (ImageView)IDLineTwo.getChildAt(2);
                file = new File( Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" +images.get(5).trim());
                myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                imageView.setImageBitmap(myBitmap);
                DownloadImage(images.get(5).trim(),imageView);


                imageView =  (ImageView)IDLineThree.getChildAt(0);
                file = new File( Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" +images.get(6).trim());
                myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                imageView.setImageBitmap(myBitmap);
                DownloadImage(images.get(6).trim(),imageView);

                imageView =  (ImageView)IDLineThree.getChildAt(1);
                file = new File( Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" +images.get(7).trim());
                myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                imageView.setImageBitmap(myBitmap);
                DownloadImage(images.get(7).trim(),imageView);

                imageView =  (ImageView)IDLineThree.getChildAt(2);
                file = new File( Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" +images.get(8).trim());
                myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                imageView.setImageBitmap(myBitmap);
                DownloadImage(images.get(8).trim(),imageView);

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
        else {
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            IDImageView.setLayoutParams(new LinearLayout.LayoutParams(
                    0, 0));
        }

        listView.addView(myLayout);

        IDScrollVIew.fullScroll(IDScrollVIew.FOCUS_DOWN);
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


    public void SendMessage() throws IOException {
        ArrayList<String> PhotoForSend = new ArrayList<>();
        String namePhotos = "null";

        for (Uri uri : ImageList) {

            String NewName = "";

            File dir = new File(Environment.getExternalStorageDirectory() + "/Pictures/YouDeo");
            if (!dir.exists()) {
                new File(Environment.getExternalStorageDirectory() + "/Pictures/YouDeo").mkdirs();
            }

            Bitmap photo = ImageUtils.getInstant().getCompressedBitmap(getPath(uri));
            try {


                long time = System.currentTimeMillis();
                NewName = IdUser + time + ".jpg";

                String path = Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" + NewName;
                File f = new File(path);
                f.createNewFile();
                FileOutputStream fos = new FileOutputStream(path);
                photo.compress(Bitmap.CompressFormat.JPEG, 100, fos);

                fos.flush();
                fos.close();

                PhotoForSend.add(NewName);
                namePhotos = String.join(", ", PhotoForSend);


            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        for (String NewName : PhotoForSend) {

            FirebaseStorage storage = FirebaseStorage.getInstance();

            StorageReference storageRef = storage.getReference();
            StorageReference imagesRef = storageRef.child(IDListMessager + "/" + NewName);
            StorageReference spaceRef = storageRef.child("images/space.jpg");

            spaceRef.getName().equals(spaceRef.getName());    // true

//                    path = Environment.getExternalStorageDirectory() + "/Download/23.jpg";
            String path = Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" + NewName;

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


        String Date = new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime());
        String Time = new SimpleDateFormat("HH.mm").format(Calendar.getInstance().getTime());


        EditText IDMessageText = findViewById(R.id.IDMessageText);
        String TextMessage = IDMessageText.getText().toString();

//     Message(String IDU, String FIO, String DateMess,String TimeMess, String TextMess,String ImageMess) {

        Message message = new Message(IdUser, FIO, Date, Time, TextMessage, namePhotos);
        if (TextMessage != "") {
            mDatabase.child("Messager").child(IDListMessager).push().setValue(message);

            IDMessageText.setText("");

            ImageList.removeAll(ImageList);
            LinearLayout IDImageAddMess = findViewById(R.id.IDImageAddMess);
            IDImageAddMess.removeAllViews();
        }

        IDScrollVIew.fullScroll(IDScrollVIew.FOCUS_DOWN);
    }
}