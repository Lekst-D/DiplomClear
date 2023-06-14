package com.example.diplomclear;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
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

import java.util.ArrayList;
import java.util.Arrays;

public class Category extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference myRef;
    private String IdUser;
    private DatabaseReference mDatabase;

    private LinearLayout IDListView;


    ArrayList<String> Categories = new ArrayList<>();
    ArrayList<String> CategoriesHave = new ArrayList<>();
    Spinner IDspinner;

    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        IDListView = findViewById(R.id.IDListView);

        String Subscribes = "Укажите вашу деятельность," +
                "Мастер ногтевого сервиса," +
                "Визажист Лешмейкер," +
                "Парикмахер," +
                "Барбер," +
                "Мастер по перманентному макияжу," +
                "Мастер по депиляции," +
                "Броу-мастер,"+
                "Тату мастер";

        Categories = new ArrayList<String>(Arrays.asList((Subscribes.split(","))));

        IDspinner = findViewById(R.id.IDspinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, Categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        IDspinner.setAdapter(adapter);


        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        IdUser = user.getUid().toString();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("UserInfo").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object value = dataSnapshot.getValue();
                if (value == null) {
                    HideLoad(false);
                } else {

                    if (dataSnapshot.hasChild("Category")) {
                        CategoriesHave.clear();
                        Log.e("getChildren", dataSnapshot.getChildrenCount() + "");
                        Log.e("getChildren", dataSnapshot.child("Category").getValue() + "");

                        String categories = dataSnapshot.child("Category").getValue().toString();
                        CategoriesHave = new ArrayList<String>(Arrays.asList((categories.split(","))));
                        CategoriesHave.remove("null");
                        CategoriesHave.remove("");

                        Log.e("categories", categories);

                        IDListView.removeAllViews();

                        Log.e("CategoriesHave", CategoriesHave + "");

                        if (CategoriesHave.size() != 0) {
                            HideLoad(true);

                            for (String cat : CategoriesHave) {
                                ShowCategories(cat);
                            }

                        } else {
                            HideLoad(false);
                        }
                    } else {

                        if (CategoriesHave.size() != 0) {
                            HideLoad(true);

                            for (String cat : CategoriesHave) {
                                ShowCategories(cat);
                            }

                        } else {
                            HideLoad(false);
                        }

                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ImageButton IDBack = findViewById(R.id.IDBack);
        IDBack.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        finish();
                    }
                });

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ImageButton IDNewCategory = findViewById(R.id.IDNewCategory);
        IDNewCategory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Log.e("CategoriesHave", CategoriesHave + "");

                String newCategory = IDspinner.getSelectedItem().toString();

                if (!newCategory.contains("Укажите вашу деятельность")) {
                    if (CategoriesHave.contains(newCategory)) {
                        ShowNotAdd();
                    } else {
                        String text = "";

                        for (int i = 0; i < CategoriesHave.size(); i++) {

                            String st = CategoriesHave.get(i).toString();

                            Log.e("CategoriesHave", st + "");

                            text += st + ",";

//                            if (i != CategoriesHave.size() - 1) {
//
//                            } else {
//                                text += st;
//                            }
                        }

                        text += IDspinner.getSelectedItem().toString();
                        mDatabase.child("UserInfo").child(IdUser).child("Category").setValue(text);

                        IDspinner.setSelection(0);
                    }
                }
            }
        });

    }

    void ShowNotAdd() {
        Toast toast = Toast.makeText(this, "Вы уже отметели эту профессию ", Toast.LENGTH_LONG);
        toast.show();
    }

    void ShowCategories(String category) {
        LayoutInflater inflater = getLayoutInflater();
        View myLayoutImages = inflater.inflate(R.layout.category_line, null, false);

        TextView IDNameCategory = myLayoutImages.findViewById(R.id.IDNameCategory);
        IDNameCategory.setText(category);

        ImageView IDEditCategory = myLayoutImages.findViewById(R.id.IDEditCategory);
        IDEditCategory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CategoriesHave.remove(category);
                String text = "";

                for (String st : CategoriesHave) {
                    text = st + ",";
                }

                mDatabase.child("UserInfo").child(IdUser).child("Category").setValue(text);

            }
        });
        IDListView.addView(myLayoutImages);
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
}