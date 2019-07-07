package com.mobile.Smf.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.Smf.R;
import com.mobile.Smf.activities.FeedActivity;
import com.mobile.Smf.database.DataInterface;
import com.mobile.Smf.model.Feed;
import com.mobile.Smf.util.PostContentHolder;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;


public class MakePicturePostFragment extends Fragment {

    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int CAMERA_PERMISSION_GRANTED = 1;

    private TextView textViewHeader;
    private ImageView imageViewPicture;
    private Button buttonUploadNewPost;
    private Button buttonTakePicture;
    private Button rotatePicturePreviewButton;

    private PostContentHolder postContentHolder;
    private DataInterface dataInterface;

    private Bitmap imageToUploadAsBitmap;
    private String pictureToUploadFilePath;

    private Feed feed;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View makePicturePostView = inflater.inflate(R.layout.fragment_makepicturepost, container, false);

        //SqLite.syncProfileInfoFromMySql has not been called during login, and should be
        //then the date for getLoggedInUser can be uptained from SqLite

        postContentHolder = PostContentHolder.getPostContentHolderSingleton();
        dataInterface = DataInterface.getDataInterface(getContext());
        feed = Feed.getFeedSingleton(getContext());

        textViewHeader = (TextView) makePicturePostView.findViewById(R.id.makepicturepost_textview_header);
        imageViewPicture = (ImageView) makePicturePostView.findViewById(R.id.makepicturepost_imageview_picure);
        buttonUploadNewPost = (Button) makePicturePostView.findViewById(R.id.makepicturepost_button_uploadnewpostbutton);
        buttonTakePicture = (Button) makePicturePostView.findViewById(R.id.makepicturepost_button_takenewpicture);
        rotatePicturePreviewButton = (Button) makePicturePostView.findViewById(R.id.makepicturepost_button_rotatepicture);

        textViewHeader.setText(R.string.makepicturepost_header);
        buttonUploadNewPost.setText(R.string.makepicturepost_uploadbutton);
        buttonTakePicture.setText(R.string.makepicturepost_takepicturebutton);
        rotatePicturePreviewButton.setText(R.string.makepicturepost_rotatepicturebutton);

        // get pictyure to display
        if (postContentHolder.getPicture() == null){
            // set a placeholder image
            updatePreviewImageView(getPlaceHolderImage());
        } else {
            imageToUploadAsBitmap = postContentHolder.getPicture();
            updatePreviewImageView(imageToUploadAsBitmap);
        }

        buttonUploadNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageToUploadAsBitmap == null){
                    Toast.makeText(getContext(),"You must take a picture first",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (dataInterface.uploadPicturePost(imageToUploadAsBitmap)){
                    postContentHolder.clearData();
                    Toast.makeText(getContext(),"Successfully posted image",Toast.LENGTH_LONG).show();
                    feed.updateWithNewerPosts();
                    Intent intent = new Intent(getContext(), FeedActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(),"Failed to post picture.",Toast.LENGTH_LONG).show();
                }
            }
        });

        buttonTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkIfPermissionToTakePhoto()) {
                    startCameraIntent();
                }
            }
        });

        rotatePicturePreviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageToUploadAsBitmap != null){
                    imageToUploadAsBitmap = rotateBitmap(imageToUploadAsBitmap,90);
                    postContentHolder.setPicture(imageToUploadAsBitmap);
                    updatePreviewImageView(imageToUploadAsBitmap);
                }
            }
        });

        return makePicturePostView;
    }

    private void startCameraIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // create a temp file to store new image in
            File pictureFile = null;
            try {
                pictureFile = createImageFile();
            }
            catch (IOException e){
                Log.d("MPP","Could not create pictureFile");
            }
            if (pictureFile != null){
                Uri pictureURI = FileProvider.getUriForFile(getContext(),"com.mobile.Smf.fileprovider",pictureFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, pictureURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            imageToUploadAsBitmap = createBitmapFromFile(pictureToUploadFilePath);
            postContentHolder.setPicture(imageToUploadAsBitmap);
            Log.d("MPP","Bitmap size: "+imageToUploadAsBitmap.getAllocationByteCount());
            Log.d("MPP","Heigh: "+imageToUploadAsBitmap.getHeight()+" Width: "+imageToUploadAsBitmap.getHeight());
            updatePreviewImageView(imageToUploadAsBitmap);
        } else if (requestCode == Activity.RESULT_CANCELED){
            // user cancelled
        }
    }

    private Bitmap createBitmapFromFile(String filePath){
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        return bitmap;
    }

    private Bitmap rotateBitmap(Bitmap bitmapToBeRotatated, float angle){
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmapToBeRotatated, 0,0,
                bitmapToBeRotatated.getWidth(), bitmapToBeRotatated.getHeight(),
                matrix, true);
        return rotatedBitmap;
    }

    private File createImageFile() throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String pictureFileName = "IMG_"+timeStamp+"_";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File picture = File.createTempFile(pictureFileName,".jpg",storageDir);
        pictureToUploadFilePath = picture.getAbsolutePath();
        return picture;
    }

    private boolean checkIfPermissionToTakePhoto(){
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.CAMERA)) {
                Toast.makeText(getActivity(),"App needs permission to use camera to take icon_photos.", Toast.LENGTH_LONG).show();
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_GRANTED);
            }
            return false;
        } else {
            // Permission has already been granted
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION_GRANTED: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                } else {
                    // permission denied
                }
                return;
            }
        }
    }


    private void updatePreviewImageView(Bitmap image){
        imageViewPicture.setImageBitmap(image);
    }

    private Bitmap getPlaceHolderImage(){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 5;
        options.outWidth = 960;
        options.outHeight = 960;
        Bitmap placeHolder = BitmapFactory.decodeResource(getResources(),R.drawable.image_placeholder,options);
        return placeHolder;
    }










}
