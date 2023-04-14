package com.example.diplomclear;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.diplomclear.Classes.Post;
import com.example.diplomclear.Classes.UserInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Locale;

public class Search extends AppCompatActivity {
    private ListView usersList;
    private EditText SearchText;
    private Button SearchButton;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListenner;
    private FirebaseUser user;
    private DatabaseReference myRef;
    private String IdUser;
    private FirebaseFirestore db;


    ArrayList<String> Users = new ArrayList<>();
    ArrayList<SearchList> AllUserSearchs = new ArrayList<>();


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        IdUser = user.getUid().toString();
        db = FirebaseFirestore.getInstance();

        usersList = findViewById(R.id.IDListView);
        SearchText = findViewById(R.id.IDTextSearch);
        SearchButton = findViewById(R.id.IDButtonSearch);

//        myRef.child("UserInfo").child(IdUser).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//        myRef.child("UserInfo").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {

        SearchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String searchText = (SearchText.getText().toString()).trim();
                String[] searchTextPart = searchText.split(" ");

                ValueEventListener valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

//                            if( searchTextPart[0] in postSnapshot.child("userSurname").getValue().toString())

                            //TODO get the data here
                            String UserName = postSnapshot.child("userName").getValue().toString();

                            Log.d("Name", UserName);

                            String name = postSnapshot.child("userName").getValue().toString();
                            String surname = postSnapshot.child("userSurname").getValue().toString();

                            if(searchTextPart.length>1) {
                                boolean hasString = (surname.toLowerCase())
                                        .contains(searchTextPart[1].toLowerCase());
//                                Log.d("1", searchTextPart[1]);
//                                Log.d("2", surname);
                                if(!hasString)
                                    continue;
                            }
                            Users.add(name + " " + surname);
                            String FIO=name + " " + surname;
                            String Image=postSnapshot.child("userPhoto").getValue().toString();
                            AllUserSearchs.add(new SearchList(Image,FIO));

                        }

                        ShowSearchList();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };

                Query query = myRef.child("UserInfo").orderByChild("userName")
                        .startAt(searchTextPart[0].toUpperCase() )
                        .endAt(searchTextPart[0].toLowerCase() + "\uf8ff");
                query.addValueEventListener(valueEventListener);
            }
            }
        );


//        db.collection("userInfo")
//                .whereGreaterThanOrEqualTo("Name", "12")
//                .whereLessThanOrEqualTo("Name", "12")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.d(TAG, document.getId() + " => " + document.getData());
//                                Users.add(document.getData().toString());
//                            }
//                            ShowSearchList();
//
//                        } else {
//                            Log.d(TAG, "Error getting documents: ", task.getException());
//                        }
//                    }
//                });


//        myRef.child("UserInfo").orderByChild("userName").startAt("1")
//                .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                if (!task.isSuccessful()) {
//                    Log.e("firebase", "Error getting data", task.getException());
//                } else {
//                    int i =0;
//                    for (DataSnapshot st : task.getResult().getChildren() ) {
//                        Log.e("number "+i,st.getValue().toString());
//                        Log.e("number "+i,st.child("userName").getValue().toString());
//                        String name = st.child("userName").getValue().toString();
//                        String surname = st.child("userSurname").getValue().toString();
////                        Log.d("firebase user name", name);
//                        Users.add(name + " " + surname);
//
//                        i++;
////                        adapter.add(st.getValue().toString());
//                    }
////                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
////                    String name = task.getResult().child("userName").getValue().toString();
////                    String surname = task.getResult().child("userSurname").getValue().toString();
////                    Log.d("firebase user name", name);
////                    Users.add(name + " " + surname);
//
//                    ShowSearchList();
//
////                    UserInfo userInfo=new UserInfo(task.getResult().getValue(UserInfo).toString());
////                    Users.add(task.getResult().getValue(UserInfo).toString());
//                }
//            }
//        });

//        Users.add("asdfadsf");
//        Users.add("asdfadsf");
//        Users.add("asdfadsf");


    }

    void ShowSearchList() {

        MyAddapterSearch adapter = new MyAddapterSearch(this, R.layout.search_users, AllUserSearchs);
        adapter.notifyDataSetChanged();
        usersList.setAdapter(adapter);

//
//        ArrayAdapter<String> adapter = new ArrayAdapter(this,
//                android.R.layout.simple_list_item_1, Users);
//
//        usersList.setAdapter(adapter);

        MyAddapterSearch stateAdapter = new MyAddapterSearch(this, R.layout.search_users, AllUserSearchs);
        usersList.setAdapter(stateAdapter);
    }
}