package com.mobile.Smf.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mobile.Smf.R;
import com.mobile.Smf.fragments.ViewPagerPicturePostFragment;
import com.mobile.Smf.fragments.ViewPagerTextPostFragment;
import com.mobile.Smf.model.Feed;
import com.mobile.Smf.model.Post;

import java.util.List;

public class PostViewPagerActivity extends AppCompatActivity {

    private ViewPager postViewPager;
    private List<Post> feedList;
    private int postID;

    public static Intent newIntent(Context packageContext, int postID){
        Intent intent = new Intent(packageContext, PostViewPagerActivity.class);
        intent.putExtra(ViewPagerTextPostFragment.ARG_POSTID, postID);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_view_pager);

        postID = (int) getIntent().getSerializableExtra(ViewPagerPicturePostFragment.ARG_POSTID);

        Feed feed = Feed.getFeedSingleton(getApplicationContext());
        feedList = feed.getFeedAsList();

        postViewPager = (ViewPager) findViewById(R.id.post_view_pager);

        FragmentManager fragmentManager = getSupportFragmentManager();

        postViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Post post = feedList.get(position);
                if (post.getPostType() == 0){
                    return ViewPagerTextPostFragment.newInstance(post.getPostID());
                }
                else if (post.getPostType() == 1){
                    return ViewPagerPicturePostFragment.newInstance(post.getPostID());
                }
                return null;
            }

            @Override
            public int getCount() {
                return feedList.size();
            }
        });

        // open pageviewer on the correct post
        for (int i=0; i<feedList.size(); i++){
            if (postID == feedList.get(i).getPostID()){
                postViewPager.setCurrentItem(i);
                break;
            }
        }

    }
}
