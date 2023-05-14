package com.example.diplomclear;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.diplomclear.Classes.ImageUtils;
import com.example.diplomclear.Message.MessegeList;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
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

public class AllImageUser extends AppCompatActivity {

    ArrayList<String> ImageList,NewImageList;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference myRef;
    private String IdUser;

    ActivityResultLauncher<PickVisualMediaRequest> pickMultipleMedia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_image_user);

        ImageList = new ArrayList<>();
        NewImageList= new ArrayList<>();

        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        IdUser=user.getUid();

        DatabaseReference mDatabas = FirebaseDatabase.getInstance().getReference();

        Bundle arguments = getIntent().getExtras();
        String UserID = arguments.get("IDUser").toString();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference allImage = storageRef.child(UserID);

        allImage.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {

                        for (StorageReference prefix : listResult.getPrefixes()) {
                            // All the prefixes under listRef.
                            // You may call listAll() recursively on them.
                        }

                        for (StorageReference item : listResult.getItems()) {

                            String Name = item.getName();
                            Log.e("Name File", Name);

                            ImageList.add(Name);
                        }

                        if (ImageList.size() != 0) {
                            HideLoad(true);
                        } else {
                            HideLoad(false);
                        }

                        ShowImages();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Uh-oh, an error occurred!
                    }
                });

        ImageButton arrowback_white = findViewById(R.id.IDList);
        arrowback_white.setOnClickListener(
                new View.OnClickListener()
                {
                   public void onClick(View v) {
                       finish();
                   }
                }
        );

        pickMultipleMedia =  registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia(9), uris -> {
            if (!uris.isEmpty()) {
                Log.d("PhotoPicker", "Number of items selected: " + uris.size());

                for (Uri u:uris) {
                    Log.e("uris", u.getPath());
                    try {
                        ImageAddUser(u);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            } else {
                Log.d("PhotoPicker", "No media selected");
            }
        });

        ImageButton IDAddImage = findViewById(R.id.IDAddImage);
        IDAddImage.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View v) {
                        try {
                            pickMultipleMedia.launch(new PickVisualMediaRequest.Builder()
                                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                                    .build());
                        }
                        catch (Exception e){}
                    }
                }
        );

    }

    void HideLoad(boolean check) {
//        ImageView IDID =findViewById(R.id.IDID);
//        IDID.setImageResource(R.drawable.two);

        TextView IDTVTextNotPost = findViewById(R.id.IDTVTextNotPost);

        LinearLayout IDLoad = findViewById(R.id.IDLoad);
        IDLoad.setVisibility(View.GONE);
        IDTVTextNotPost.setVisibility(View.GONE);

        if (!check) {

            IDTVTextNotPost.setVisibility(View.VISIBLE);
        }
    }

    void ImageAddUser(Uri Photo) throws IOException {
        ArrayList<String> PhotoForSend = new ArrayList<>();
        String namePhotos="";
        String NewName="";
        String path="";

        File dir = new File(Environment.getExternalStorageDirectory() + "/Pictures/YouDeo");
        if (!dir.exists()) {
            new File(Environment.getExternalStorageDirectory() + "/Pictures/YouDeo").mkdirs();
        }

        Bitmap photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Photo);
        try {


            long time = System.currentTimeMillis();
            NewName=IdUser+ time + ".jpg";

            path = Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" +NewName;
            File f = new File(path);
            f.createNewFile();
            FileOutputStream fos = new FileOutputStream(path);
            photo.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            fos.flush();
            fos.close();


            namePhotos=String.join(", ", PhotoForSend);
//                    ArrayList<String> Names = new ArrayList<>(Arrays.asList(name.split(",")));

//                    Log.e("name new photo",PhotoForSend.toString());
//                    Log.e("name new photo",name.toString());
//                    Log.e("name new photo",Names.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }

        String Date=new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime());
        String Time = new SimpleDateFormat("HH.mm").format(Calendar.getInstance().getTime());


        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageRef = storage.getReference();
        StorageReference imagesRef = storageRef.child(IdUser+"/"+NewName);
        StorageReference spaceRef = storageRef.child("images/space.jpg");

        spaceRef.getName().equals(spaceRef.getName());    // true

//                    path = Environment.getExternalStorageDirectory() + "/Download/23.jpg";
        path = Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/"+NewName;

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

    void ShowMessage(){
        Intent intent = new Intent(this, MessegeList.class);
        startActivity(intent);
    }

    public void ShowImages() {
        GridView IDgridview = findViewById(R.id.IDgridview);
        IDgridview.setAdapter(new ImageAdapterGridView(this, ImageList));
    }

    public class ImageAdapterGridView extends BaseAdapter {
        private Context mContext;
        ArrayList<String> ImageList;

        public ImageAdapterGridView(Context c, ArrayList<String> ImageList) {
            mContext = c;
            this.ImageList = ImageList;
        }

        public int getCount() {
            return ImageList.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        public View getView(int position, View convertView, ViewGroup parent) {

            String nameImage = ImageList.get(position);

            LayoutInflater inflater = getLayoutInflater();
            View myLayout = inflater.inflate(R.layout.image_gallery, null, false);

            ImageView IDImageView = myLayout.findViewById(R.id.IDImageView);
            File file = new File(Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" + nameImage);
            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            IDImageView.setImageBitmap(myBitmap);

            Log.d("Image Name All Gallery", nameImage);

            return myLayout;


//            ImageView mImageView;
//
//            if (convertView == null) {
//                mImageView = new ImageView(mContext);
//                mImageView.setLayoutParams(new GridView.LayoutParams(230, 230));
//                mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                mImageView.setPadding(16, 16, 16, 16);
//            } else {
//                mImageView = (ImageView) convertView;
//            }
//
//            File file=new File(Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/"+ImageList.get(position));
//            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//            mImageView.setImageBitmap(myBitmap);
////            mImageView.setImageResource(ImageList.get(position));
//            return mImageView;
        }
    }

}