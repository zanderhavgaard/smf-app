package com.mobile.Smf.model;

import com.mobile.Smf.util.PostTypeInterface;

public abstract class Post implements PostTypeInterface {

    private int postID;
    private String userName;
    private long timeStamp;
    private String localTimeStamp;
    private String universalTimeStamp;
    private static int likes;
    private boolean clicked;
    private int updateVal = 0;

    public abstract int getPostType();

    //add likes to constructor of posts and subclasses
    public Post(int postID, String userName, long timeStamp, String localTime, String universalTime, int likes, boolean clicked){
        this.postID = postID;
        this.userName = userName;
        this.timeStamp = timeStamp;
        this.localTimeStamp = localTime;
        this.universalTimeStamp = universalTime;
        this.likes = likes;
        this.clicked = clicked;
        System.out.println("Inside post "+postID+" "+this.likes);
    }

    public int getPostID() {
        return postID;
    }

    public String getUserName() {
        return userName;
    }

    public long getTimeStamp() {
       return timeStamp;
   }

    public String getLocalTimeStamp() { return localTimeStamp; }

    public String getUniversalTimeStamp() {
        return universalTimeStamp;
    }

    public void likePost() {
        if (!clicked) {
            likes++;
            updateVal = 1;
            if(!clicked)
                clicked = true;
        }
    }

    public void unlikePost() {
        if (clicked) {
            likes-- ;
            updateVal = -1;
            if(clicked)
                clicked = false;
        }
    }

    public int getlikes() {return likes;}

    public void setLikes(int val){likes = val;}

    public boolean getClicked() {return clicked;}

    public int getUpdateVal() {return updateVal;}

    public void clearUpdateVal() {
        updateVal = 0;
    }

    public String getFormattedUniversalTimestamp() {
        StringBuilder sb = new StringBuilder();
        sb.append(universalTimeStamp.substring(0,4)+"-");
        sb.append(universalTimeStamp.substring(4,6)+"/");
        sb.append(universalTimeStamp.substring(6,8)+"\n ");
        sb.append(universalTimeStamp.substring(universalTimeStamp.indexOf(8) != '0' ? 8 : 9, 10)+"-");
        sb.append(universalTimeStamp.substring(universalTimeStamp.indexOf(10) != '0' ? 10 : 11, 12)+"-");
        sb.append(universalTimeStamp.substring(universalTimeStamp.indexOf(12) != '0' ? 12 : 13, 14)+"");
        return sb.toString();
    }

}
