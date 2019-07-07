package com.mobile.Smf.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobile.Smf.R;
import com.mobile.Smf.model.Feed;
import com.mobile.Smf.model.Post;
import com.mobile.Smf.model.TextPost;

public class ViewPagerTextPostFragment extends Fragment {

    public static final String ARG_POSTID = "post_id";

    private TextPost post;

    private TextView textViewUsername;
    private TextView textViewTimestamp;
    private TextView textViewText;
    private ImageView like;
    private TextView numberOfLikes;

    public static ViewPagerTextPostFragment newInstance(int postID){
        Bundle args = new Bundle();
        args.putSerializable(ARG_POSTID,postID);
        ViewPagerTextPostFragment fragment = new ViewPagerTextPostFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        int postID = (int) getArguments().getSerializable(ARG_POSTID);
        post = (TextPost) Feed.getFeedSingleton(getContext()).getPostByID(postID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View postView = inflater.inflate(R.layout.view_post_textpost, container, false);

        textViewUsername = (TextView) postView.findViewById(R.id.view_textpost_textview_username);
        textViewTimestamp = (TextView) postView.findViewById(R.id.view_textpost_textview_timestamp);
        textViewText = (TextView) postView.findViewById(R.id.view_textpost_textview_text);
        like = (ImageView) postView.findViewById(R.id.view_post_imageview_like_not);
        numberOfLikes = (TextView) postView.findViewById(R.id.view_post_textview_numlikes);

        textViewUsername.setText(post.getUserName());
        textViewTimestamp.setText(post.getFormattedUniversalTimestamp());
//        textViewText.setText(post.getText());
        textViewText.setText("post id " + post.getPostID());

        // todo add like stuff



        return postView;
    }

}
