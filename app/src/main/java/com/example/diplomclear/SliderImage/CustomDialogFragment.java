package com.example.diplomclear.SliderImage;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.ViewPager;

import com.example.diplomclear.R;

import java.io.File;
import java.util.ArrayList;

public class CustomDialogFragment extends DialogFragment {

    ArrayList<String> mArrayUri;
    int height;
    int width;
    TextView IDAdress;
    LinearLayout IDInofPader;
    boolean statusclick=false;

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
    public void onStart()
    {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null)
        {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @SuppressLint("MissingInflatedId")
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());

//        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        mArrayUri=(getArguments().getStringArrayList("mArrayUri"));
        int postion=(getArguments().getInt("CurentPosition"));

        LayoutInflater inflater = getLayoutInflater();
        View myLayout = inflater.inflate(R.layout.activity_slider, null, false);

        ViewPager mViewPager = myLayout.findViewById(R.id.viewPagerMain);
        IDAdress = myLayout.findViewById(R.id.IDAdress);
        IDInofPader=myLayout.findViewById(R.id.IDInofPader);

        ImageButton IDBack= myLayout.findViewById(R.id.IDBack);
        IDBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dismiss();
            }});

        LinearLayout IDAllobject=myLayout.findViewById(R.id.IDAllobject);

        // Initializing the ViewPagerAdapter
        ViewPagerAdapter mViewPagerAdapter = new ViewPagerAdapter(getActivity(), mArrayUri);
        // Adding the Adapter to the ViewPager
        mViewPager.setAdapter(mViewPagerAdapter);

        mViewPager.setCurrentItem(postion);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {

            }
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

            }

            public void onPageSelected(int position) {
                int myposition=position+1;
                IDAdress.setText(myposition+"/"+mArrayUri.size());
            }

        });

        String st="sdfsdf";
        st=mArrayUri.get(0);

        IDAdress.setText((postion+1)+"/"+mArrayUri.size());



        return builder
                .setView(myLayout)
                .create();
    }
}