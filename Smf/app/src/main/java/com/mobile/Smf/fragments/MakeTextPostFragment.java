package com.mobile.Smf.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.Smf.R;
import com.mobile.Smf.activities.FeedActivity;
import com.mobile.Smf.database.DataInterface;
import com.mobile.Smf.model.Feed;
import com.mobile.Smf.util.PostContentHolder;

public class MakeTextPostFragment extends Fragment {

    private PostContentHolder postContentHolder;

    private TextView textViewHeader;
    private TextView textViewNumChars;
    private TextView textViewCarsAllowed;
    private EditText editTextInputText;
    private Button buttonUploadNewPost;

    private DataInterface dataInterface;

    final private int maxNumberOfCharsAllowed = 145; // one better than twitter!

    private Feed feed;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View makePostView = inflater.inflate(R.layout.fragment_maketextpost,container,false);

        postContentHolder = PostContentHolder.getPostContentHolderSingleton();
        dataInterface = DataInterface.getDataInterface(getContext());
        feed = Feed.getFeedSingleton(getContext());

        textViewHeader = (TextView) makePostView.findViewById(R.id.maketextpost_textview_header);
        textViewNumChars = (TextView) makePostView.findViewById(R.id.maketextpost_textview_numchars);
        textViewCarsAllowed = (TextView) makePostView.findViewById(R.id.maketextpost_textview_allowed_length);
        editTextInputText = (EditText) makePostView.findViewById(R.id.maketextpost_edittext_text);
        buttonUploadNewPost = (Button) makePostView.findViewById(R.id.maketextpost_button_uploadnewpostbutton);

        if (postContentHolder.getText().equals("")){
            editTextInputText.setText("");
        } else {
            editTextInputText.setText(postContentHolder.getText());
        }

        textViewHeader.setText(R.string.maketextpost_header);

        buttonUploadNewPost.setText(R.string.maketextpost_uploadbutton);
        textViewCarsAllowed.setText(" /" + maxNumberOfCharsAllowed + " characters.");
        updateNumberOfCharsWrittenTextView();

        editTextInputText.addTextChangedListener(new TextWatcher() {
                                                     @Override
                                                     public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                                     }

                                                     @Override
                                                     public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                         updateNumberOfCharsWrittenTextView();

                                                     }

                                                     @Override
                                                     public void afterTextChanged(Editable s) {

                                                     }
                                                 });

                buttonUploadNewPost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String text = editTextInputText.getText().toString();
                        postContentHolder.setText(text);

                        if (text.equals("")) {
                            Toast.makeText(getContext(), "Type something to post!", Toast.LENGTH_SHORT).show();
                        } else {
                            // upload the new post and respond accordingly
                            if (dataInterface.uploadTextPost(text)) {
                                // go back to feed after posting
                                postContentHolder.clearData();
                                Toast.makeText(getContext(), "Uploaded post!", Toast.LENGTH_LONG).show();
                                feed.updateWithNewerPosts();
                                Intent intent = new Intent(getContext(), FeedActivity.class);
                                startActivity(intent);
                            } else {
                                // else make a toast and let user try again?
                                Toast.makeText(getContext(), "Could not upload post, try again.", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });

        return makePostView;
    }

    private void updateNumberOfCharsWrittenTextView(){
        textViewNumChars.setText(""+editTextInputText.length());
        textViewNumChars.setTextColor(editTextInputText.getText().length() >= maxNumberOfCharsAllowed ? Color.RED : Color.BLACK);
    }
}
