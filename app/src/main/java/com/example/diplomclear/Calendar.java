package com.example.diplomclear;

import static androidx.fragment.app.DialogFragment.STYLE_NO_FRAME;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.diplomclear.LogRegSwap.Registration;
import com.example.diplomclear.SliderImage.CustomDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Calendar extends AppCompatActivity
        implements DialogCalender.OnInputListener {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference myRef;
    private String IdUser;
    private DatabaseReference mDatabase;


    LinearLayout listView;

    static Calendar getInstance() {
        return null;
    }

    ;

    @Override
    public void sendInput(CalendarInfo input,String UID) {
        if (UID.trim() == "New!") {
        mDatabase.child("Calendar").child(IdUser).push().setValue(input);
        }else{
            if(input.getTime().trim()=="Delete"){
                mDatabase.child("Calendar").child(IdUser).child(UID).removeValue();}
            else{
            mDatabase.child("Calendar").child(IdUser).child(UID).setValue(input);}
        }
//        Toast toast = Toast.makeText(this,
//                "time "+input.getTime()+
//                        "date"+input.getDate()+
//                        "info"+input.getRecord()
//                ,Toast.LENGTH_LONG);
//        toast.show();
    }

    ArrayList<CalendarInfo> CalendarInfos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        IdUser = user.getUid().toString();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        ImageButton IDBack = findViewById(R.id.IDBack);
        IDBack.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        finish();
                    }
                });

        LinearLayout IDNewSave = findViewById(R.id.IDNewSave);
        IDNewSave.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Bundle args = new Bundle();
                        args.putString("time", "");
                        args.putString("date", "");
                        args.putString("record", "");
                        args.putString("UID", "New!");

                        DialogCalender dialog = new DialogCalender();
                        dialog.setArguments(args);
//                        dialog.setStyle(STYLE_NO_FRAME,
//                                android.R.style.Theme_Black_NoTitleBar_Fullscreen);

                        dialog.show(getSupportFragmentManager(), "custom");
                    }
                });
        listView = findViewById(R.id.IDListView);

        mDatabase.child("Calendar").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object value = dataSnapshot.getValue();
                if (value == null) {
                    listView.removeAllViews();
                    HideLoad(false);
                } else {

                    listView.removeAllViews();

                    for (DataSnapshot document : dataSnapshot.getChildren()) {

                        String time = document.child("time").getValue().toString();

                        String date = document.child("date").getValue().toString();

                        String record = document.child("record").getValue().toString();

                        CalendarInfo info = new CalendarInfo(time, date, record);
                        CalendarInfos.add(info);

                        ShowRecords(info,document.getKey().toString());

                    }

                    if (CalendarInfos.size() != 0) {
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


    }

    void HideLoad(boolean check) {
        TextView IDTVTextNotPost = findViewById(R.id.IDTVTextNotPost);

        LinearLayout IDLoad = findViewById(R.id.IDLoad);
        IDLoad.setVisibility(View.GONE);
        IDTVTextNotPost.setVisibility(View.GONE);
        if (!check) {

            IDTVTextNotPost.setVisibility(View.VISIBLE);
        }
    }

    void ShowRecords(CalendarInfo info,String id) {
        LayoutInflater inflater = getLayoutInflater();
        View myLayout = inflater.inflate(R.layout.calender_line, null, false);

        TextView IDTimeSave = myLayout.findViewById(R.id.IDTimeSave);
        TextView IDTimeDate = myLayout.findViewById(R.id.IDTimeDate);
        TextView IDNameCategory = myLayout.findViewById(R.id.IDNameCategory);

        IDTimeSave.setText(info.getTime());
        IDTimeDate.setText(info.getDate());
        IDNameCategory.setText(info.getRecord());

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) LinearLayout IDCalendarLine = myLayout.findViewById(R.id.IDCalendarLine);
        IDCalendarLine.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Bundle args = new Bundle();
                        args.putString("time", info.getTime());
                        args.putString("date", info.getDate());
                        args.putString("record", info.getRecord());
                        args.putString("UID", id);

                        DialogCalender dialog = new DialogCalender();
                        dialog.setArguments(args);
//                        dialog.setStyle(STYLE_NO_FRAME,
//                                android.R.style.Theme_Black_NoTitleBar_Fullscreen);

                        dialog.show(getSupportFragmentManager(), "custom");
                    }
                });

        listView.addView(myLayout,0);
    }

}