package com.example.diplomclear;

import static androidx.fragment.app.DialogFragment.STYLE_NO_FRAME;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.diplomclear.SliderImage.CustomDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Calendar extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference myRef;
    private String IdUser;
    private DatabaseReference mDatabase;

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

        LinearLayout IDNewSave=findViewById(R.id.IDNewSave);
        IDNewSave.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {

//                        Bundle args = new Bundle();
//                        args.putStringArrayList("mArrayUri", ImageListSlider);
//                        args.putInt("CurentPosition",position);

                        DialogCalender dialog = new DialogCalender();
//                        dialog.setArguments(args);
//                        dialog.setStyle(STYLE_NO_FRAME,
//                                android.R.style.Theme_Black_NoTitleBar_Fullscreen);

                        dialog.show(getSupportFragmentManager(), "custom");

                    }
                });

    }
}