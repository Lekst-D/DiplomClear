package com.example.diplomclear.Message;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.diplomclear.ListAct;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MessegeList extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private DatabaseReference myRef;

    private String IdUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messege_list);

        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        IdUser=user.getUid();

        mDatabase.child("MessageList").child(IdUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object value = dataSnapshot.getValue();
                if (value == null){

                    if (dataSnapshot.getChildrenCount() != 0) {
                        HideLoad(true);
                    } else {
                        HideLoad(false);
                    }

                }
                else
                {
                    LinearLayout listView = findViewById(R.id.IDScrollLinear);
                    listView.removeAllViews();

                    for (DataSnapshot document : dataSnapshot.getChildren())
                    {
                        Log.e("document",document.getValue().toString());
                        Log.e("document",document.getKey().toString());



                        ShowMessageList(document.getKey().toString(),
                                document.child("Name").getValue().toString()
                        );
                    }

                    if (dataSnapshot.getChildrenCount() != 0) {
                        HideLoad(true);
                    } else {
                        HideLoad(false);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ImageButton IDList=findViewById(R.id.IDList);
        IDList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ListOpen();
            }
        });
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

    void ListOpen(){
        Intent intent = new Intent(this, ListAct.class);
        startActivity(intent);
    }

    String Name = null;
    String Surname = null;
    String UserPhoto = null;

    void ShowMessListName(String idAU,TextView textView,ImageView IDUserImage){
        mDatabase.child("UserInfo").child(idAU).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else
                {
                    Name = task.getResult().child("userName").getValue().toString();
                    Surname = task.getResult().child("userSurname").getValue().toString();
                    UserPhoto = task.getResult().child("userPhoto").getValue().toString();

                    textView.setText(Surname+" "+Name);

                    if(UserPhoto.trim()!="null")
                    {
                        DownloadImageUser(UserPhoto.trim(),IDUserImage);
                    }
                }
            }});
    }

    void ShowMessager(String IDListMessager,String IDU)
    {
//        Toast toast = Toast.makeText(this, IDListMessager,Toast.LENGTH_LONG);
//        toast.show();

        Intent intent = new Intent(this, Messager.class);
        intent.putExtra("IDAnotherUser", IDU);
        intent.putExtra("IDUser", IdUser);
        intent.putExtra("IDListMessager", IDListMessager);
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

    void ShowMessageList(String idAU,String idMess)
    {

        LinearLayout listView = findViewById(R.id.IDScrollLinear);

        LayoutInflater inflater = getLayoutInflater();
        View myLayout = inflater.inflate(R.layout.search_users, null, false);

        LinearLayout IDLinearLayout=myLayout.findViewById(R.id.IDLinearLayout);
        TextView textView=myLayout.findViewById(R.id.IDUserFIO);

        ImageView IDUserImage=myLayout.findViewById(R.id.IDUserImage);


        textView.setText(idAU);

        ShowMessListName(idAU,textView,IDUserImage);

        IDLinearLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ShowMessager(idMess,idAU);
            }});


        listView.addView(myLayout);
    }
}