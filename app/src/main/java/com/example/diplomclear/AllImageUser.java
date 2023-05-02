package com.example.diplomclear;

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
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

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

import java.io.File;
import java.util.ArrayList;

public class AllImageUser extends AppCompatActivity {

    ArrayList<String> ImageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_image_user);

        ImageList = new ArrayList<>();

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

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

        ImageButton IDMessage = findViewById(R.id.IDMessage);
        IDMessage.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View v) {
                        ShowMessage();
                    }
                }
        );

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