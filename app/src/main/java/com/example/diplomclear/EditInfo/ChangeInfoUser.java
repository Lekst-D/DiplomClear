package com.example.diplomclear.EditInfo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.diplomclear.Category;
import com.example.diplomclear.Classes.CustomDialogFragment;
import com.example.diplomclear.LogRegSwap.Registration;
import com.example.diplomclear.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class ChangeInfoUser extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser user;

    private String IdUser;
    EditText EName, ESurname,EDateBirth,EUserPhone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_info_user);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        IdUser=user.getUid();

        EName=findViewById(R.id.NameUser);
        ESurname=findViewById(R.id.SurnameUser);
        EDateBirth=findViewById(R.id.BirthDayUser);
        EUserPhone=findViewById(R.id.PhoneUser);

        EDateBirth.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                closeKeyboard();
            }}
        );

        EUserPhone.addTextChangedListener(new TextWatcher() {
            int len = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String str = EUserPhone.getText().toString();
                len = str.length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    String str = s.toString();

                    String val = EUserPhone.getText().toString();
//                    if ((val.length() == 5 && len < val.length()) || (val.length() == 13 && len < val.length())) {
//                        str += "-";
//                        phone.setText(str);
//                        phone.setSelection(str.length());
//                    }

                    if((val.length() == 1)&& (len < val.length()))
                    {
                        str = "+"+str;
                        EUserPhone.setText(str);
                        EUserPhone.setSelection(str.length());
                    }

                    if((val.length() == 5)&& (len < val.length()))
                    {
                        str += "-";
                        EUserPhone.setText(str);
                        EUserPhone.setSelection(str.length());
                    }

                    if((val.length() == 9)&& (len < val.length()))
                    {
                        str += "-";
                        EUserPhone.setText(str);
                        EUserPhone.setSelection(str.length());
                    }

                    if((val.length() == 12)&& (len < val.length()))
                    {
                        str += "-";
                        EUserPhone.setText(str);
                        EUserPhone.setSelection(str.length());
                    }
                } catch (Exception ignored) {
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }

        });


        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("UserInfo").child(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
//                    userName
//                            userSurname

                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    String Name = task.getResult().child("userName").getValue().toString();
                    String Surname = task.getResult().child("userSurname").getValue().toString();
                    String UserPhone = task.getResult().child("userPhone").getValue().toString();
                    String DateBirth= task.getResult().child("birthDay").getValue().toString();
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));

                    EName.setText(Name);
                    ESurname.setText(Surname);
                    EDateBirth.setText(DateBirth);
                    EUserPhone.setText(UserPhone);
                }
            }
        });

        Button ChangeInfoID=findViewById(R.id.ChangeInfoID);

        ChangeInfoID.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String Name = EName.getText().toString();
                String Surname = ESurname.getText().toString();
                String UserPhone = EUserPhone.getText().toString();
                String DateBirth= EDateBirth.getText().toString();

                mDatabase.child("UserInfo").child(IdUser).child("userName").setValue(Name);
                mDatabase.child("UserInfo").child(IdUser).child("userSurname").setValue(Surname);
                mDatabase.child("UserInfo").child(IdUser).child("userPhone").setValue(UserPhone);
                mDatabase.child("UserInfo").child(IdUser).child("birthDay").setValue(DateBirth);

            }
        });

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) LinearLayout IDChangeSecurity=findViewById(R.id.IDChangeSecurity);
        IDChangeSecurity.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        ChangeSecurity();
                    }
                });

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) LinearLayout IDCategory=findViewById(R.id.IDCategory);
        IDCategory.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        ShowAllCategory();
                    }
                });

        ImageButton IDBack=findViewById(R.id.IDBack);
        IDBack.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        finish();
                    }
                });

    }

    String dateBirth;

    Calendar dateAndTime=Calendar.getInstance();

    public void setDate() {
        new DatePickerDialog(this, d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    DatePickerDialog.OnDateSetListener d=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialDateTime();
        }
    };

    private void setInitialDateTime() {

        EDateBirth.setText(DateUtils.formatDateTime(this,
                dateAndTime.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR
        ));
        dateBirth=DateUtils.formatDateTime(this, dateAndTime.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_YEAR);
    }

    public void CallCustDialFrag(String title,String text) {

        CustomDialogFragment dialog = new CustomDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("text", text);
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), "custom");
    }

    private void closeKeyboard()
    {
        // this will give us the view
        // which is currently focus
        // in this layout
        View view = this.getCurrentFocus();

        // if nothing is currently
        // focus then this will protect
        // the app from crash
        if (view != null) {

            // now assign the system
            // service to InputMethodManager
            InputMethodManager manager
                    = (InputMethodManager)
                    getSystemService(
                            Context.INPUT_METHOD_SERVICE);
            manager
                    .hideSoftInputFromWindow(
                            view.getWindowToken(), 0);
        }

        setDate();
    }

    void ShowAllCategory(){
        Intent intent = new Intent(this, Category.class);
        startActivity(intent);
    }

    void ChangeSecurity(){
        Intent intent = new Intent(this, SecurityChange.class);
        startActivity(intent);
    }
}