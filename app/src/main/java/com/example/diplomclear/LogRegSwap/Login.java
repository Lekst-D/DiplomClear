package com.example.diplomclear.LogRegSwap;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.diplomclear.Pages;
import com.example.diplomclear.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
    private static final int REQUEST_CODE_ASK_PERMISSIONS =1 ;
    private static final int PERMISSION_REQUEST_CODE =1 ;
    private EditText Email,Password;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Email=(EditText) findViewById(R.id.ELogin);
        Password=(EditText) findViewById(R.id.Epassword);

        mAuth= FirebaseAuth.getInstance();

        Button button=(Button) findViewById(R.id.BSelect);
        button.setOnClickListener(
                new View.OnClickListener() {

                    public void onClick(View v) {
                        String email=Email.getText().toString();
                        String pass=Password.getText().toString();

                        Login(email,pass);
                    }
                });
    }


    @SuppressLint("NotConstructor")
    public void Login(String email, String pass)
    {
        mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

//                    Toast.makeText(Login.this,"Complete login",Toast.LENGTH_SHORT).show();

                    CompleteLogin();

                }else
                {
//                    Toast.makeText(Login.this,"False login",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void RegistrationActivite(View view){
        Intent intent = new Intent(this, Registration.class);
        startActivity(intent);
    }

    public void ChangePassword(View view){
        Intent intent = new Intent(this, ChangePassword.class);
        startActivity(intent);
    }

    public void CompleteLogin(){

        Intent intent = new Intent(this, Pages.class);
        startActivity(intent);

//        Intent intent1 = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//        intent1.setData(Uri.parse("package:" + getPackageName()));
//        startActivity(intent1);

        Intent intent1 = new Intent();
        intent1.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent1.setData(uri);
        startActivity(intent1);

    }

    public void ShowPassword(View view){

    }

}