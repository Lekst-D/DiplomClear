package com.example.diplomclear;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class MySubscribes extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private DatabaseReference myRef;

    private String IdUser;

    ArrayList<String> subs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_subscribes);

        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        IdUser=user.getUid();
        Log.d("IdUser",IdUser);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.getValue()!=null){
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                    Log.e("postSnapshot",postSnapshot.getKey().toString());
//                    Log.e("postSnapshot",postSnapshot.getValue().toString());
                    String ID=postSnapshot.getKey().toString();

                    if(ID!=IdUser){
                        subs.add(ID);
                    }
                }
                Log.e("subs",subs.toString());
                ShowSubscribes();}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("databaseError",databaseError.getMessage().toString());
            }
        };

        Query query = myRef.child("Subscribe").orderByValue()
                .startAt(IdUser.toUpperCase() )
                .endAt(IdUser.toLowerCase() + "\uf8ff");
        query.addValueEventListener(valueEventListener);

        ImageButton IDBack=findViewById(R.id.IDBack);
        IDBack.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        finish();
                    }
                });

    }

    public void ShowSubscribes()
    {
        GridView IDgridview = findViewById(R.id.IDgridview);
        IDgridview.setAdapter(new MySubscribes.ImageAdapterGridView(this, subs));
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

        public void UserInfo(TextView FIOTV, String idUser, LinearLayout IDSubAUser)
        {
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

                        FIOTV.setText(Surname+" "+Name);

                        IDSubAUser.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                Intent intent = new Intent(mContext, AnotherUser.class);
                                intent.putExtra("UserID",    idUser);
                                intent.putExtra("FIO",       Surname+" "+Name);
                                intent.putExtra("ImageUser", UserPhoto);
                                startActivity(intent);
                            }});
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

            TextView FIOTV=myLayout.findViewById(R.id.IDFIO);

            LinearLayout IDSubAUser=myLayout.findViewById(R.id.IDSubAUser);

            UserInfo(FIOTV,idUser,IDSubAUser);

            Log.d("idUser", idUser);

            return myLayout;
        }
    }
}