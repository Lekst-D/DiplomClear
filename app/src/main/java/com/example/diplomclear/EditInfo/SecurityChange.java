package com.example.diplomclear.EditInfo;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.diplomclear.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ms.square.android.expandabletextview.ExpandableTextView;

public class SecurityChange extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference myRef;
    private String IdUser;



    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_change);

        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        IdUser = user.getUid().toString();

        Button BADDPass = findViewById(R.id.BADDForPassword);
        BADDPass.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                newPassword();
            }
        });

        Button BADDEmail = findViewById(R.id.BADDForEmail);
        BADDEmail.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                newEmail();
            }
        });

        LinearLayout IDShowPassword=findViewById(R.id.IDShowPassword);
        LinearLayout IDEmailShow=findViewById(R.id.IDEmailShow);

        LinearLayout IDForPassword=findViewById(R.id.IDForPassword);
        LinearLayout IDForEmail=findViewById(R.id.IDForEmail);

        IDShowPassword.setVisibility(View.VISIBLE);
        IDEmailShow.setVisibility(View.GONE);

        IDForPassword.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                IDShowPassword.setVisibility(View.VISIBLE);
                IDEmailShow.setVisibility(View.GONE);
            }
        });

        IDForEmail.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                IDShowPassword.setVisibility(View.GONE);
                IDEmailShow.setVisibility(View.VISIBLE);
            }
        });

        // sample code snippet to set the text content on the ExpandableTextView
//        ExpandableTextView expTv1 =findViewById(R.id.expand_text_view);

// IMPORTANT - call setText on the ExpandableTextView to set the text content to display
//        expTv1.setText("Copyright 2014 Manabu Shimobe\n" +
//                "\n" +
//                "Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
//                "you may not use this file except in compliance with the License.\n" +
//                "You may obtain a copy of the License at\n" +
//                "\n" +
//                "http://www.apache.org/licenses/LICENSE-2.0\n" +
//                "\n" +
//                "Unless required by applicable law or agreed to in writing, software\n" +
//                "distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
//                "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
//                "See the License for the specific language governing permissions and\n" +
//                "limitations under the License.");

    }
    EditText OldPaswword, OldEmail, NewPassword,NewEmail;
    String newPass,oldPass,email;

    void newPassword() {


        OldPaswword = findViewById(R.id.IDOldPasswordForPassword);
        OldEmail = findViewById(R.id.IDEmailForPassword);
        NewPassword = findViewById(R.id.IDNewPasswordForPassword);

        newPass = NewPassword.getText().toString();
        oldPass = OldPaswword.getText().toString();
        email = OldEmail.getText().toString();


        Log.e("Change", "asdfasdf");

        AuthCredential credential = EmailAuthProvider
                .getCredential(email, oldPass);

        Log.e("Change", "asdfasdf");

        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "Password updated");
                                    } else {
                                        Log.d(TAG, "Error password not updated");
                                    }
                                }
                            });
                        } else {
                            Log.d(TAG, "Error auth failed");
                        }
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

    void newEmail() {


        OldPaswword = findViewById(R.id.IDOldPasswordForEmail);
        OldEmail = findViewById(R.id.IDEmailForEmail);
        NewEmail = findViewById(R.id.IDNewEmailForEmail);

        String newEmail = NewEmail.getText().toString();
        String oldPass = OldPaswword.getText().toString();
        String email = OldEmail.getText().toString();

        Log.e("Change", "asdfasdf");

        AuthCredential credential = EmailAuthProvider
                .getCredential(email, oldPass);

        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    user.updateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Password updated");
                            } else {
                                Log.d(TAG, "Error password not updated");
                            }
                        }
                    });

                } else {
                    Log.d(TAG, "Error auth failed");
                }
            }
        });
    }
}
