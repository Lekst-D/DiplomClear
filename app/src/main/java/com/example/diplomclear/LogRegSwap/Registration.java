package com.example.diplomclear.LogRegSwap;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.diplomclear.Classes.CustomDialogFragment;
import com.example.diplomclear.Classes.UserInfo;
import com.example.diplomclear.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Registration extends AppCompatActivity {

    private FirebaseAuth mAuth;


    EditText name,password,mail,phone,surname,password_pav,birth_day;
    Button reg;

    String dateBirth;

    Calendar dateAndTime=Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        name=(EditText) findViewById(R.id.EName);
        password=(EditText) findViewById(R.id.EPassword);
        password_pav=(EditText) findViewById(R.id.EPasswordPovtor);
        mail=(EditText) findViewById(R.id.EMail);
        phone=(EditText) findViewById(R.id.EPhone);
        surname=(EditText)findViewById(R.id.ESurname);
        birth_day=(EditText)findViewById(R.id.EDataBirth);
        reg=(Button) findViewById(R.id.BADD);


        mAuth=FirebaseAuth.getInstance();
    }

    public void setDate(View v) {
        new DatePickerDialog(Registration.this, d,
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

        birth_day.setText(DateUtils.formatDateTime(this,
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

    public void RegistrationClick(View v) {

        RadioButton AgreePersonalData=(RadioButton)findViewById(R.id.AgreePersonalData);

        String Name=name.getText().toString();
        String Email=mail.getText().toString();
        String Phone=phone.getText().toString();
        String Password=password.getText().toString();
        String PasswordPav=password_pav.getText().toString();
        String Surname=surname.getText().toString();
        String BirthDay=birth_day.getText().toString();

        if(Name.matches(""))
        {
            Log.e("RegMistake","name is empty ");

            CallCustDialFrag("Вы не заполнили поле","Заполните поле вода имя");

            return;
        }
        if(Name.contains(" "))
        {
            Log.e("RegMistake","name contain backsplash ");

            CallCustDialFrag("Ошибка заполнения поля","Поле имя не должно содержать пробел");

            return;
        }

        if(Surname.matches(""))
        {
            Log.e("RegMistake","surname is empty ");

            CallCustDialFrag("Вы не заполнили поле","Заполните поле вода фамилия");

            return;
        }
        if(Surname.contains(" "))
        {
            Log.e("RegMistake","surname contain backsplash ");

            CallCustDialFrag("Ошибка заполнения поля","Поле фамилия не должно содержать пробел");

            return;
        }

        if(BirthDay.matches(""))
        {
            Log.e("RegMistake","birth day is empty ");

            CallCustDialFrag("Вы не заполнили поле","Заполните поле вода даты рождения");

            return;
        }


        if(Email.matches(""))
        {
            Log.e("RegMistake","Email is empty ");

            CallCustDialFrag("Вы не заполнили поле","Заполните поле вода почта");

            return;
        }
        if(Email.contains(" "))
        {
            Log.e("RegMistake","Email contain backsplash ");

            CallCustDialFrag("Ошибка заполнения поля","Поле почта не должно содержать пробел");

            return;
        }

        if(Phone.matches(""))
        {
            Log.e("RegMistake","phone is empty ");

            CallCustDialFrag("Вы не заполнили поле","Заполните поле вода телефон");

            return;
        }
        if(Phone.contains(" "))
        {
            Log.e("RegMistake","phone contain backsplash ");

            CallCustDialFrag("Ошибка заполнения поля","Поле телефон не должно содержать пробел");

            return;
        }

        if(Password.matches(""))
        {
            Log.e("RegMistake","password is empty ");

            CallCustDialFrag("Вы не заполнили поле","Заполните поле вода пароль");

            return;
        }
        if(Password.contains(" "))
        {
            Log.e("RegMistake","password contain backsplash ");

            CallCustDialFrag("Ошибка заполнения поля","Поле пароль не должно содержать пробел");

            return;
        }

        if(PasswordPav.matches(""))
        {
            Log.e("RegMistake","repeat password is empty ");

            CallCustDialFrag("Вы не заполнили поле","Заполните поле вода повтор пароля");

            return;
        }
        if(PasswordPav.contains(" "))
        {
            Log.e("RegMistake","repeat password contain backsplash ");

            CallCustDialFrag("Ошибка заполнения поля","Поле повтор пароля не должно содержать пробел");

            return;
        }

        if(!Password.equals(PasswordPav))
        {
            Log.e("RegMistake","password is not equally retreat password ");

            CallCustDialFrag("Ошибка заполнения поля","Поле пароля и поле повтора пароля не одинаковые");

            return;
        }

        if(!AgreePersonalData.isChecked())
        {
            Log.e("RegMistake","The user don't agree give personal data");

            CallCustDialFrag("Ошибка при использованнии данных пользователя","Пользователь не разрешил использование данных");

            return;
        }




        mAuth.createUserWithEmailAndPassword(Email,Password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(Registration.this,"Complete registration",Toast.LENGTH_SHORT).show();
                    FirebaseUser user =mAuth.getInstance().getCurrentUser();
                    Log.e("firebase", user.getUid(), task.getException());

//                    FirebaseDatabase.getInstance().getReference().child("UserInfo")
//                            .child(user.getUid()).push().setValue(
//                            new UserInfo(Name,Surname,Phone,BirthDay)
//                    );


                    FirebaseDatabase.getInstance().getReference().child("UserInfo")
                            .child(user.getUid()).setValue
                                    (new UserInfo(Name,Surname,Phone,BirthDay,user.getUid().toString(),"none")
                                    );

                    Map<String, Object> userdb = new HashMap<>();
                    userdb.put("Name", Name);
                    userdb.put("Surname", Surname);
                    userdb.put("Categories", "");
                    userdb.put("UserPhoto", "");
                    userdb.put("UserID", user.getUid().toString());

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("userInfo")
                            .add(userdb)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error adding document", e);
                                }
                            });

                }
                else{
                    Toast.makeText(Registration.this,"False registration",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void BackActivity(View view) {
        this.finish();
    }
}