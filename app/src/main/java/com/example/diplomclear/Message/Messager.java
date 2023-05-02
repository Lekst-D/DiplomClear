package com.example.diplomclear.Message;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Messager extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private DatabaseReference myRef;

    private String IdUser;
    private String FIO;

    String IDListMessager="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messager);

        Bundle arguments = getIntent().getExtras();
        IDListMessager=arguments.get("IDListMessager").toString();

        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        IdUser=user.getUid();


        mDatabase.child("UserInfo").child(IdUser).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    FIO=task.getResult().child("userSurname").getValue().toString()+" "+task.getResult().child("userName").getValue().toString();
                } else {
                    Log.e("firebase", "Error getting data", task.getException());
                }
            }
        });

        Toast toast = Toast.makeText(this, IDListMessager,Toast.LENGTH_LONG);
        toast.show();


        mDatabase.child("Messager").child(IDListMessager).addValueEventListener(new ValueEventListener() {
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
                        String IDU=document.child("idu").getValue().toString();
                        String FIO=document.child("fio").getValue().toString();
                        String DateMess=document.child("dateMess").getValue().toString();
                        String TimeMess=document.child("timeMess").getValue().toString();
                        String TextMess=document.child("textMess").getValue().toString();
//                        String TextMess="повтор";
                        String ImageMess=document.child("imageMess").getValue().toString();
                        Message message=new Message(IDU,FIO,DateMess,TimeMess,TextMess,ImageMess);

                        ShowMessage(message);
                    }

//                    Iterable<DataSnapshot> keys = dataSnapshot.getChildren();
//                    for(DataSnapshot key :keys){
//                        Log.d("key",key.child("idu").getValue().toString());
//                    }
//                    Log.e("dataSnapshot",dataSnapshot.getChildrenCount()+"");

//                    String IDU=dataSnapshot.child("idu").getValue().toString();
//                    String FIO=dataSnapshot.child("fio").getValue().toString();
//                    String DateMess=dataSnapshot.child("dateMess").getValue().toString();
//                    String TimeMess=dataSnapshot.child("timeMess").getValue().toString();
//                    String TextMess=dataSnapshot.child("textMess").getValue().toString();
//                    String ImageMess=dataSnapshot.child("imageMess").getValue().toString();
//                    Message message=new Message(IDU,FIO,DateMess,TimeMess,TextMess,ImageMess);
//
//                    ShowMessage(value);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        intent.putExtra("IDAnotherUser", IdUser);
//        intent.putExtra("IDUser", IDU);
//        intent.putExtra("IDListMessager", IDListMessager);

//        mDatabase.child("Messager").child(IDListMessager).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                for (DataSnapshot document : task.getResult().getChildren())
//                {
//                    String IDU=document.child("idu").getValue().toString();
//                    String FIO=document.child("fio").getValue().toString();
//                    String DateMess=document.child("dateMess").getValue().toString();
//                    String TimeMess=document.child("timeMess").getValue().toString();
//                    String TextMess=document.child("textMess").getValue().toString();
//                    String ImageMess=document.child("imageMess").getValue().toString();
//                    Message message=new Message(IDU,FIO,DateMess,TimeMess,TextMess,ImageMess);
//
//                    ShowMessage(message);
//                }
//            }
//        });

        ImageButton IDPostMessage=findViewById(R.id.IDPostMessage);
        IDPostMessage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SendMessage();
            }
        });
    }


    @SuppressLint("ResourceAsColor")
    public void ShowMessage(Message message)
    {

        String IDU=message.getIDU();
        String FIO=message.getFIO();
        String DateMess=message.getDateMess();
        String TimeMess=message.getTimeMess();
        String TextMess=message.getTextMess();
        String ImageMess=message.getImageMess();

        LinearLayout listView = findViewById(R.id.IDScrollLinear);

        LayoutInflater inflater = getLayoutInflater();
        View myLayout = inflater.inflate(R.layout.mess, null, false);

        TextView textMess=myLayout.findViewById(R.id.IDMessText);
        textMess.setText(TextMess);

        LinearLayout linearLayout=myLayout.findViewById(R.id.IDLinearLayoutMess);
//        linearLayout.setBackgroundColor(R.color.ColorAnotherUserMess);

        if(IDU==IdUser)
        {
            linearLayout.setVerticalGravity(Gravity.RIGHT);
            linearLayout.setHorizontalGravity(Gravity.RIGHT);
//            linearLayout.setBackgroundColor(R.color.ColorUserMess);
//            linearLayout.setVerticalGravity(Gravity.RIGHT);
        }

//        TextView textView=new TextView(this);
//        textView.setText(text);
//
//        listView.addView(textView);

        listView.addView(myLayout);
    }

    public void SendMessage()
    {
        String Date=new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime());
        String Time = new SimpleDateFormat("HH.mm").format(Calendar.getInstance().getTime());

        String ImageMess="";

        EditText IDMessageText=findViewById(R.id.IDMessageText);
        String TextMessage=IDMessageText.getText().toString();

//     Message(String IDU, String FIO, String DateMess,String TimeMess, String TextMess,String ImageMess) {

        Message message=new Message(IdUser,FIO,Date,Time,TextMessage,ImageMess);

        mDatabase.child("Messager").child(IDListMessager).push().setValue(message);
    }
}