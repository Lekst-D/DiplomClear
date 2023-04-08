package com.example.diplomclear.LogRegSwap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.diplomclear.Classes.CustomDialogFragment;
import com.example.diplomclear.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ChangePassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
    }

    public void BackActivity(View view) {
        this.finish();
    }

    public void SwapPassword(View view) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        EditText email = (findViewById(R.id.EMail));
        String emailAddress = email.getText().toString();//"spektr509@gmail.com";

        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
//                            Log.d("Email", "Email sent.");

                            CustomDialogFragment dialog = new CustomDialogFragment();

                            Bundle args = new Bundle();
                            args.putString("text", "На вашу почту ("+email.getText().toString()+") была выслана ссылка для смены пароля");
                            dialog.setArguments(args);

                            dialog.show(getSupportFragmentManager(), "custom");
                        }
                        else{

                            CustomDialogFragment dialog = new CustomDialogFragment();

                            Bundle args = new Bundle();
                            args.putString("text", "Произошла ошибка, данная почта не найдена в базе данных");
                            dialog.setArguments(args);

                            dialog.show(getSupportFragmentManager(), "custom");

                        }
                    }
                });
    }

}