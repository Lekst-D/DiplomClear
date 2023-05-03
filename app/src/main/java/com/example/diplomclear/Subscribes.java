package com.example.diplomclear;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class Subscribes extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private DatabaseReference myRef;

    private String IdUser;
    private String Subscribes;

    ArrayList<String> subs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribes);

        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        IdUser=user.getUid();


        mDatabase.child("Subscribe").child(IdUser).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
//                    Log.e("Subscribe",task.getResult().getValue().toString());
                    Subscribes=task.getResult().getValue().toString();
                    subs = new ArrayList<String>(Arrays.asList((Subscribes.split(","))));
                } else {
                    Log.e("firebase", "Error getting data", task.getException());
                }
            }
        });
    }

    public void ShowSubscribes()
    {

    }

    public class ImageAdapterGridView extends BaseAdapter {
        private Context mContext;
        ArrayList<String> Subs;

        public ImageAdapterGridView(Context c, ArrayList<String> Subs) {
            mContext = c;
            this.Subs = Subs;
        }

        public int getCount() {
            return Subs.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        public View getView(int position, View convertView, ViewGroup parent) {

            String nameImage = Subs.get(position);

            LayoutInflater inflater = getLayoutInflater();
            View myLayout = inflater.inflate(R.layout.subscribe, null, false);

            ImageView IDImageView = myLayout.findViewById(R.id.IDPostIMagePost);
//            File file = new File(Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" + nameImage);
//            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//            IDImageView.setImageBitmap(myBitmap);

            TextView FIOTV=myLayout.findViewById(R.id.IDFIO);

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