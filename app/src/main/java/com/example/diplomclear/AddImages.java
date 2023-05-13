package com.example.diplomclear;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AddImages extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser user;

    private String IdUser;

    ActivityResultLauncher<PickVisualMediaRequest> pickMultipleMedia;

    ArrayList<Uri> ImageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_images);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        IdUser=user.getUid();
        ImageList=new ArrayList<>();

        pickMultipleMedia =  registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia(9), uris -> {
                    // Callback is invoked after the user selects media items or closes the
                    // photo picker.
                    if (!uris.isEmpty()) {
                        Log.d("PhotoPicker", "Number of items selected: " + uris.size());

                        for (Uri u:uris) {
                            Log.e("uris", u.getPath());

                            ImageView IDImageView=findViewById(R.id.IDImageView);
                            IDImageView.setImageURI(u);

                            ImageList.add(u);

                        }
                        ShowImages();
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                });

        ActivityResultLauncher<Intent> activityResultLauncher;

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        Log.e("result", result.getResultCode()+"result.getResultCode()");
                        if (data != null){
                            Log.e("data", data.toString()+"data");
                            Uri selectedImageUri = data.getClipData().getItemAt(0).getUri();
                            int countImage=data.getClipData().getItemCount();
                            Log.e("selectedImageUri", data.getClipData().getItemAt(0).getUri().getPath());
                            Log.e("CountImage", data.getClipData().getItemCount()+"");


                            if (selectedImageUri != null){
                                Log.e("selectedImageUri", selectedImageUri.getPath()+"selectedImageUri");
                                try {
                                    Log.e("ImageList", ImageList.toString());

                                        for (int i=0; i<countImage;i++)
                                        {
                                            Uri ImageUri = data.getClipData().getItemAt(i).getUri();
                                            ImageList.add(ImageUri);
                                        }

                                    ImageView IDImageView=findViewById(R.id.IDImageView);
                                    IDImageView.setImageURI(selectedImageUri);

                                    ImageList.add(selectedImageUri);
                                    Log.e("ImageList", ImageList.toString());
                                    ShowImages();

                                }catch (Exception e){
                                }
                            }
                        }
                    }
                });

        Button select=findViewById(R.id.IDNewImage);

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_PICK,
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//                activityResultLauncher.launch(intent);
                try {
                    pickMultipleMedia.launch(new PickVisualMediaRequest.Builder()
                            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                            .build());
                }
                catch (Exception e){}

            }
        });
    }


//    int PICK_IMAGE_MULTIPLE = 1;
//    String imageEncoded;
//    List<String> imagesEncodedList;


    void imageChooser() {

//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMAGE_MULTIPLE);
    }

    // this function is triggered when user
    // selects the image from the imageChooser
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        try {
//            // When an Image is picked
//            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK
//                    && null != data) {
//                // Get the Image from data
//
//                String[] filePathColumn = { MediaStore.Images.Media.DATA };
//                imagesEncodedList = new ArrayList<String>();
//                if(data.getData()!=null){
//
//                    Uri mImageUri=data.getData();
//
//
//                } else {
//                    if (data.getClipData() != null) {
//                        ClipData mClipData = data.getClipData();
//                        ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
//                        for (int i = 0; i < mClipData.getItemCount(); i++) {
////                            Uri mImageUri=data.getData();
//                        }
//                        Log.v("LOG_TAG", "Selected Images" + mArrayUri.size());
//                    }
//                }
//            } else {
//                Toast.makeText(this, "You haven't picked Image",
//                        Toast.LENGTH_LONG).show();
//            }
//        } catch (Exception e) {
//            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
//                    .show();
//        }
//
//        super.onActivityResult(requestCode, resultCode, data);
//    }

    public void ShowImages() {
        GridView IDgridview = findViewById(R.id.IDgridview);
        IDgridview.setAdapter(new AddImages.ImageAdapterGridView(this, ImageList));
    }

    public class ImageAdapterGridView extends BaseAdapter {
        private Context mContext;
        ArrayList<Uri> ImageList;

        public ImageAdapterGridView(Context c, ArrayList<Uri> ImageList) {
            mContext = c;
            this.ImageList = ImageList;
        }

        public int getCount() {
            return ImageList.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        public View getView(int position, View convertView, ViewGroup parent) {

            Uri nameImage = ImageList.get(position);

            LayoutInflater inflater = getLayoutInflater();
            View myLayout = inflater.inflate(R.layout.image_gallery, null, false);

            ImageView IDImageView = myLayout.findViewById(R.id.IDImageView);

//            File file = new File(nameImage.getPath());
//            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

            IDImageView.setImageURI(nameImage);

            return myLayout;
        }
    }
}


