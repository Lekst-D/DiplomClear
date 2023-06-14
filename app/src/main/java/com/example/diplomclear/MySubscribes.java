package com.example.diplomclear;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
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
import android.widget.TextView;

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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class MySubscribes extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private DatabaseReference myRef;

    private String IdUser;
    private Context mContext;

    ArrayList<String> subs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_subscribes);

        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        IdUser = user.getUid();
        mContext = this;
        Log.d("IdUser", IdUser);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.e("Query", "Answer");

                if (dataSnapshot.getValue() != null) {
                    subs.clear();

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                    Log.e("postSnapshot",postSnapshot.getKey().toString());
//                    Log.e("postSnapshot",postSnapshot.getValue().toString());
                        String ID = postSnapshot.getKey().toString();

                        if (ID.trim() != IdUser.trim()) {
                            subs.add(ID);
                        }
                    }
                    Log.e("subs", subs.toString());

                    LinearLayout IDSubList = findViewById(R.id.IDSubList);
                    IDSubList.removeAllViews();

                    if (subs.size() != 0) {
                        HideLoad(true);
                    } else {
                        HideLoad(false);
                    }

                    ShowSubscribes();
                }
                else{
                    HideLoad(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("databaseError", databaseError.getMessage().toString());
                HideLoad(false);
            }
        };

        Log.e("Query", IdUser);
        Query query = myRef.child("Subscribe").orderByValue()
                .startAt(IdUser.toUpperCase()+ "\uf8ff")
                .endAt(IdUser.toLowerCase() + "\uf8ff");
        query.addValueEventListener(valueEventListener);
        Log.e("Query", IdUser);

        ImageButton IDBack = findViewById(R.id.IDBack);
        IDBack.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        finish();
                    }
                });

    }

    void HideLoad(boolean check) {
//        ImageView IDID =findViewById(R.id.IDID);
//        IDID.setImageResource(R.drawable.two);
        TextView IDTVTextNotPost = findViewById(R.id.IDTVTextNotPost);
        LinearLayout IDLoad = findViewById(R.id.IDLoad);

        IDLoad.setVisibility(View.GONE);

        if (!check) {
            IDTVTextNotPost.setVisibility(View.VISIBLE);
        }
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

    public void UserInfo(TextView FIOTV, String idUser, LinearLayout IDSubAUser,ImageView imageUser) {
        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("UserInfo").child(idUser).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    String Name = task.getResult().child("userName").getValue().toString();
                    String Surname = task.getResult().child("userSurname").getValue().toString();
                    String UserPhoto = task.getResult().child("userPhoto").getValue().toString();
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));

                    DownloadImageUser(UserPhoto,imageUser);

                    FIOTV.setText(Surname + " " + Name);

                    IDSubAUser.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            Intent intent = new Intent(mContext, AnotherUser.class);
                            intent.putExtra("UserID", idUser);
                            intent.putExtra("FIO", Surname + " " + Name);
                            intent.putExtra("ImageUser", UserPhoto);
                            startActivity(intent);
                        }
                    });
                }
            }
        });

    }

    public void ShowSubscribes() {
//        GridView IDgridview = findViewById(R.id.IDgridview);
//        IDgridview.setAdapter(new Subscribes.ImageAdapterGridView(this, subs));

        LinearLayout IDSubList = findViewById(R.id.IDSubList);

        for (String idUser : subs) {
            if(idUser.contains(IdUser)){continue;}

            Log.e("idUser",idUser);

            LayoutInflater inflater = getLayoutInflater();
            View myLayout = inflater.inflate(R.layout.search_users, null, false);

            LinearLayout IDLinearLayout = myLayout.findViewById(R.id.IDLinearLayout);
            TextView textView = myLayout.findViewById(R.id.IDUserFIO);

            ImageView imageView=myLayout.findViewById(R.id.IDUserImage);

            UserInfo(textView,idUser,IDLinearLayout,imageView);


            IDSubList.addView(myLayout);

        }

        LinearLayout IDLinearLayout = new LinearLayout(this);
        ViewGroup.LayoutParams layoutParams = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.WRAP_CONTENT, 20);

        IDLinearLayout.setLayoutParams(layoutParams);


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

        public void UserInfo(TextView FIOTV, String idUser, LinearLayout IDSubAUser) {
            DatabaseReference mDatabase;
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("UserInfo").child(idUser).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    } else {
                        String Name = task.getResult().child("userName").getValue().toString();
                        String Surname = task.getResult().child("userSurname").getValue().toString();
                        String UserPhoto = task.getResult().child("userPhoto").getValue().toString();
                        Log.d("firebase", String.valueOf(task.getResult().getValue()));

                        FIOTV.setText(Surname + " " + Name);

                        IDSubAUser.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                Intent intent = new Intent(mContext, AnotherUser.class);
                                intent.putExtra("UserID", idUser);
                                intent.putExtra("FIO", Surname + " " + Name);
                                intent.putExtra("ImageUser", UserPhoto);
                                startActivity(intent);
                            }
                        });
                    }
                }
            });

        }

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        public View getView(int position, View convertView, ViewGroup parent) {

            String idUser = Subs.get(position);

            LayoutInflater inflater = getLayoutInflater();
            View myLayout = inflater.inflate(R.layout.subscribe, null, false);

            ImageView IDImageView = myLayout.findViewById(R.id.IDPostIMagePost);
//            File file = new File(Environment.getExternalStorageDirectory() + "/Pictures/YouDeo/" + nameImage);
//            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//            IDImageView.setImageBitmap(myBitmap);

            TextView FIOTV = myLayout.findViewById(R.id.IDFIO);

            LinearLayout IDSubAUser = myLayout.findViewById(R.id.IDSubAUser);

            UserInfo(FIOTV, idUser, IDSubAUser);

            Log.d("idUser", idUser);

            return myLayout;
        }
    }
}