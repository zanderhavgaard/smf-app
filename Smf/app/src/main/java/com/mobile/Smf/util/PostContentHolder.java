package com.mobile.Smf.util;

import android.graphics.Bitmap;

public class PostContentHolder {

    private static PostContentHolder postContentHolderSingleton;

    private static String text;
    private static Bitmap picture;

    private PostContentHolder(){
        text = "";
        picture = null;
    };

    public static PostContentHolder getPostContentHolderSingleton(){
        if (postContentHolderSingleton == null) postContentHolderSingleton = new PostContentHolder();
        return postContentHolderSingleton;
    }

    public static String getText(){
        return text;
    }

    public static void setText(String newText){
        text = newText;
    }

    public static void setPicture(Bitmap newPicture){
        picture = newPicture;
    }

    public static Bitmap getPicture(){
        return picture;
    }

    public static void clearData(){
        picture = null;
        text = "";
    }

}
