package com.example.diplomclear.Message;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.diplomclear.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
//                    ShowMessage("11111");
//                    dataSnapshot.getRef().setValue(new FBVocabulary(userVocabulary), completionListener);
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
                                document.child("Name").toString());
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    String Name = null;
    String Surname = null;
    String UserPhoto = null;

    void ShowMessListName(String idAU,TextView textView){
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
                }
            }});
    }

    void ShowMessager(String IDListMessager,String IDU)
    {
//        Toast toast = Toast.makeText(this, IDListMessager,Toast.LENGTH_LONG);
//        toast.show();

        Intent intent = new Intent(this, Messager.class);
        intent.putExtra("IDAnotherUser", IdUser);
        intent.putExtra("IDUser", IDU);
        intent.putExtra("IDListMessager", IDListMessager);
        startActivity(intent);
    }

    void ShowMessageList(String idAU,String idMess)
    {

        LinearLayout listView = findViewById(R.id.IDScrollLinear);

        LayoutInflater inflater = getLayoutInflater();
        View myLayout = inflater.inflate(R.layout.search_users, null, false);

        LinearLayout IDLinearLayout=myLayout.findViewById(R.id.IDLinearLayout);
        TextView textView=myLayout.findViewById(R.id.IDUserFIO);

        textView.setText(idAU);

        ShowMessListName(idAU,textView);

        IDLinearLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ShowMessager(idMess,idAU);
            }});


        listView.addView(myLayout);
    }
}