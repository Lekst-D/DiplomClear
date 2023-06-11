package com.example.diplomclear.SliderImage;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.ViewPager;

import com.example.diplomclear.Classes.Post;
import com.example.diplomclear.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CustomDialogFragment extends DialogFragment {

    ArrayList<String> mArrayUri;
    int height;
    int width;
    TextView IDAdress;
    LinearLayout IDInofPader;
    boolean statusclick = false;


    @SuppressLint("ResourceAsColor")
    @Override
    public void onResume() {

        getDialog().getWindow().setBackgroundDrawableResource(R.color.black);
        getDialog().getWindow().setDimAmount(0);

        super.onResume();
    }

    @SuppressLint("PrivateResource")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setStyle(DialogFragment.STYLE_NO_TITLE, com.google.android.material.R.style.Base_V14_Theme_Material3_Dark_Dialog);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference mDatabase;
    private String IdUser;
    private String PostID;
    private StorageReference storageRef;
    private ViewPager mViewPager;

    public void ChangeInfo(String UserImage) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("UserInfo").child(IdUser).child("userPhoto").setValue(UserImage);
    }

    public void RemoveStoreItem(String UserImage){
        StorageReference imagesRef = storageRef.child(IdUser + "/" + UserImage);
        imagesRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
            }
        });
    }

    public void RemoveImage(String UserImage) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String all = "";

//        mArrayUri;
        for (String st : mArrayUri) {
            if (st != UserImage) {
                all += st + ",";
            }
        }
        all = all.trim();
        all = all.substring(0, all.length() - 1);

        if (mArrayUri.size() != 0) {
            db.collection("usersPosts")
                    .document(PostID)
                    .update("Images", all)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully updated!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error updating document", e);
                        }
                    });
            RemoveStoreItem(UserImage);
            int item=mArrayUri.indexOf(UserImage);
            if(mArrayUri.size() == item+1){mViewPager.setCurrentItem(item-1);}
            else{mViewPager.setCurrentItem(item+1);}
            mArrayUri.remove(UserImage);

        }else{

            StorageReference imagesRef = storageRef.child(IdUser + "/" + UserImage);
            imagesRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // File deleted successfully
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Uh-oh, an error occurred!
                }
            });

            db.collection("usersPosts")
                    .document(PostID).delete();

            getDialog().dismiss();
        }


    }

    @SuppressLint("MissingInflatedId")
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();


        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        IdUser = user.getUid().toString();

//        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        mArrayUri = (getArguments().getStringArrayList("mArrayUri"));
        int postion = (getArguments().getInt("CurentPosition"));

        LayoutInflater inflater = getLayoutInflater();
        View myLayout = inflater.inflate(R.layout.activity_slider, null, false);

        mViewPager = myLayout.findViewById(R.id.viewPagerMain);
        IDAdress = myLayout.findViewById(R.id.IDAdress);
        IDInofPader = myLayout.findViewById(R.id.IDInofPader);

        String status = (getArguments().getString("Status"));

        PostID = (getArguments().getString("PostID"));

        ImageButton IDMenu = myLayout.findViewById(R.id.IDMenu);


        if (status != null) {

            IDMenu.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Initializing the popup menu and giving the reference as current context
                    PopupMenu popupMenu = new PopupMenu(getActivity(), IDMenu);

                    // Inflating popup menu from popup_menu.xml file
                    popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            // Toast message on menu item clicked
//                        Toast.makeText(getActivity(), "You Clicked " + menuItem.getTitle(), Toast.LENGTH_SHORT).show();
//                        return true;
                            int id = menuItem.getItemId();
                            int i = mViewPager.getCurrentItem();
                            String UserImage = mArrayUri.get(i);
                            switch (id) {
                                case R.id.IDImageSet:
                                    ChangeInfo(UserImage);
                                    return true;

//                                case R.id.IDRemove:
//                                    RemoveImage(UserImage);
//                                    return true;
                            }
                            return false;
                        }
                    });

                    // Showing the popup menu
                    popupMenu.show();

                }
            });
        } else {
            IDMenu.setVisibility(View.GONE);
        }

        ImageButton IDBack = myLayout.findViewById(R.id.IDBack);
        IDBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dismiss();
            }
        });

        LinearLayout IDAllobject = myLayout.findViewById(R.id.IDAllobject);

        // Initializing the ViewPagerAdapter
        ViewPagerAdapter mViewPagerAdapter = new ViewPagerAdapter(getActivity(), mArrayUri);
        // Adding the Adapter to the ViewPager
        mViewPager.setAdapter(mViewPagerAdapter);

        mViewPager.setCurrentItem(postion);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {

            }

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            public void onPageSelected(int position) {
                int myposition = position + 1;
                IDAdress.setText(myposition + "/" + mArrayUri.size());
            }

        });

        String st = "sdfsdf";
        st = mArrayUri.get(0);

        IDAdress.setText((postion + 1) + "/" + mArrayUri.size());


        return builder
                .setView(myLayout)
                .create();
    }
}