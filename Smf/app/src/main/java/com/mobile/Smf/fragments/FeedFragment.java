package com.mobile.Smf.fragments;

import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.mobile.Smf.database.DataInterface;
import com.mobile.Smf.model.Post;

import com.mobile.Smf.util.PostRecyclerViewAdapter;
import com.mobile.Smf.R;
import com.mobile.Smf.model.Feed;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.atomic.AtomicBoolean;

import me.everything.android.ui.overscroll.IOverScrollDecor;
import me.everything.android.ui.overscroll.IOverScrollStateListener;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

import static com.mobile.Smf.model.Feed.getFeedSingleton;
import static me.everything.android.ui.overscroll.IOverScrollState.STATE_BOUNCE_BACK;
import static me.everything.android.ui.overscroll.IOverScrollState.STATE_DRAG_END_SIDE;
import static me.everything.android.ui.overscroll.IOverScrollState.STATE_DRAG_START_SIDE;
import static me.everything.android.ui.overscroll.IOverScrollState.STATE_IDLE;

public class FeedFragment extends Fragment implements Observer {

    private Feed feed;
    private RecyclerView feedRecyclerView;
    private PostRecyclerViewAdapter postRecyclerViewAdapter;
    private DataInterface dataInterface;

    private int scrollPosition;
    private int maxScrollPosition;
    private boolean hasIncreasedMaxScrollPosition = false;
    private boolean hasGottenOlderPosts = true;
    private boolean hasGottenNewerPosts = true;
    private AtomicBoolean likeFlag;

    private boolean debug = false;
    private String Tag = "FeedFragment";



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View feedView = inflater.inflate(R.layout.fragment_feed, container, false);

        feed = getFeedSingleton(getContext());
        dataInterface = DataInterface.getDataInterface(getContext());

        feed.addObserver(this);

        feedRecyclerView = (RecyclerView) feedView.findViewById(R.id.feed_recyclerview);
        feedRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        startRecyclerView();

        likeFlag = new AtomicBoolean(true);
        LikeUpdater likeUpdater = new LikeUpdater();
        likeUpdater.execute();

        return feedView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        likeFlag.compareAndSet(true,false);
    }


    private void startRecyclerView() {
        postRecyclerViewAdapter = new PostRecyclerViewAdapter(getContext(),feed.getFeedAsList());
        feedRecyclerView.setAdapter(postRecyclerViewAdapter);


        /*
        feedRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (scrollPosition == 0 && maxScrollPosition != 0 && !hasGottenNewerPosts){
                    getNewerPostsEvent();
                    hasGottenNewerPosts = true;
                } else if (scrollPosition == maxScrollPosition && !hasIncreasedMaxScrollPosition && !hasGottenOlderPosts) {
                    getOlderPostsEvent();
                    hasGottenOlderPosts = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                scrollPosition += dy;
                if (scrollPosition > maxScrollPosition){
                    maxScrollPosition = scrollPosition;
                    hasIncreasedMaxScrollPosition = true;
                }
                else if (scrollPosition == maxScrollPosition){
                    // do nothing ?
                }
                else {
                    hasIncreasedMaxScrollPosition = false;
                    hasGottenOlderPosts = false;
                    hasGottenNewerPosts = false;
                }
                if (debug){
                    Log.d("FeedFragment", "onScrolled dx: " + dx + " dy: " + dy);
                    Log.d("FeedFragment", "ScrollPosition " + scrollPosition + " / " + maxScrollPosition);
                }

            }

        });
        */


        /*
        * Uses the excellent iverscroll decor implementation by 'EverythingMe'
        * found @ https://github.com/EverythingMe/overscroll-decor
        * */
        OverScrollDecoratorHelper.setUpOverScroll(feedRecyclerView, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);

        IOverScrollDecor decor = OverScrollDecoratorHelper.setUpOverScroll(feedRecyclerView, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        decor.setOverScrollStateListener(new IOverScrollStateListener() {
                                             @Override
                                             public void onOverScrollStateChange(IOverScrollDecor decor, int oldState, int newState) {
                                                 switch (newState) {
                                                     case STATE_IDLE:
                                                         break;
                                                     case STATE_DRAG_START_SIDE:
                                                         getNewerPostsEvent();
                                                         break;
                                                     case STATE_DRAG_END_SIDE:
                                                         getOlderPostsEvent();
                                                         break;
                                                     case STATE_BOUNCE_BACK:
                                                         if (oldState == STATE_DRAG_START_SIDE) {
                                                         } else {
                                                         }
                                                         break;
                                                 }
                                             }
                                         });

    }


    private void getNewerPostsEvent(){
        Toast.makeText(getContext(),"Looking for newer posts...",Toast.LENGTH_SHORT).show();
        feed.updateWithNewerPosts();
        postRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void getOlderPostsEvent(){
        Toast.makeText(getContext(),"Looking for older posts...",Toast.LENGTH_SHORT).show();
        feed.updateWithOlderPosts();
        postRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void update(Observable o, Object arg) {
        postRecyclerViewAdapter.notifyDataSetChanged();
    }

    public void killLikeUpdating() {
        likeFlag.compareAndSet(true,false);
    }

    public void startLikeUpdating() {likeFlag.compareAndSet(false,true);}



    private class LikeUpdater extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... feeds) {

                while(likeFlag.get()) {

                    try{
                        Thread.sleep(30000);
                    } catch(InterruptedException e) {e.printStackTrace();}

                        List<Post> copiedList = ((List<Post>) ((ArrayList) feed.getFeedAsList()).clone());
                        List<Point> valuesToUpdate = new ArrayList<>();

                        for (Post p : copiedList) {
                            if (p.getUpdateVal() != 0)
                                valuesToUpdate.add(new Point(p.getUpdateVal(), p.getPostID()));
                        }

                        if (debug)
                            System.out.println("valuesToUpdate list: " + Arrays.toString(valuesToUpdate.toArray()));

                        if (!valuesToUpdate.isEmpty())
                            if (dataInterface.updateLikes(valuesToUpdate))
                                Log.d("likeUpdater", "likes committed to database");

                        /*try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }*/

                        List<Point> updatedLikes;
                        if (!valuesToUpdate.isEmpty()) {
                            updatedLikes = dataInterface.getUpdatedLikes(valuesToUpdate);

                            Log.d("likeUpdater", "updatedLikes returned " + updatedLikes);

                            List<Post> feed_as_list = feed.getFeedAsList();
                            for (Point p : updatedLikes) {
                                int id = p.x;
                                for (Post po : feed_as_list) {
                                    if (po.getPostID() == id) {
                                        po.setLikes(p.y);
                                        po.clearUpdateVal();
                                        break;
                                    }
                                }
                            }
                            publishProgress();
                            Log.d("likeUpdater", "likes updated in feed");
                        }
                    }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            feed.updateObservers();
            postRecyclerViewAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.d(Tag, "like updater terminated");
        }
    }

}
