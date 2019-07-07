/*
* Singleton
* Manages feed_as_list data, fetches data from the data interface and serves it to the UI.
* */

package com.mobile.Smf.model;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;

import com.mobile.Smf.database.DataInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;


public class Feed extends Observable {

    private static Feed feedSingleton;
    private DataInterface datainterface;
    private List<Post> feed_as_list;


    private Feed(Context context){
        datainterface = DataInterface.getDataInterface(context);
        feed_as_list = new ArrayList<>();
         fillFeed();
    }

    public static Feed getFeedSingleton(Context context){
        if (feedSingleton == null) feedSingleton = new Feed(context);
        return feedSingleton;
    }

    public List<Post> getFeedAsList(){
        return feed_as_list;
    }

    public void fillFeed(){
        feed_as_list = datainterface.getFirstTenPosts();
    }

    public void updateWithNewerPosts(){
        List<Post> newItems = datainterface.getUpdatedListNewer();
        if (newItems != null){
            feed_as_list.addAll(0,newItems);
        }
        updateObservers();
    }

    public void updateWithOlderPosts(){
        List<Post> newItems = datainterface.getUpdatedListOlder();
        System.out.println("updateWithOlderPosts "+newItems);
        if (newItems != null){
            feed_as_list.addAll(newItems);
        }
        updateObservers();
    }

    public void updateObservers(){
        this.setChanged();
        this.notifyObservers();
        this.clearChanged();
    }

    public Post getPostByID(int ID){
        for (Post post : feed_as_list){
            if (post.getPostID() == ID){
                return post;
            }
        }
        return null;
    }

}
