package com.mobile.Smf.model;

import android.graphics.Bitmap;

import com.mobile.Smf.util.PostTypeInterface;

public class PicturePost extends Post implements PostTypeInterface {

    private static final int postType = 1;

    private Bitmap picture;

    public PicturePost(int postID, String userName, long timeStamp, Bitmap picture,String localTime, String universalTime, int likes, boolean clicked){
        super(postID,userName,timeStamp,localTime, universalTime, likes, clicked);
        this.picture = picture;
    }

    public Bitmap getPicture(){
        return picture;
    }

    @Override
    public int getPostType(){return postType;}

}
