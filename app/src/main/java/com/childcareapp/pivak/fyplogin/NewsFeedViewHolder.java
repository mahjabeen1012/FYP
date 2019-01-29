package com.childcareapp.pivak.fyplogin;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.github.abdularis.civ.CircleImageView;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;

public class NewsFeedViewHolder extends RecyclerView.ViewHolder {

    View mView;
    TextView mUploaderName,mTimeStamp,mContent,mFileName,mLikes;
    ImageView mImg;
    VideoView videoView;
    LinearLayout personalInformation;
    LinearLayout fileInformation;
    LinearLayout contentInformation;
    LinearLayout showImg;
    LinearLayout showVid;
    LinearLayout lc;
    CircleImageView circleImageView;
    SimpleExoPlayerView exoPlayerView;
    SimpleExoPlayer exoPlayer;
    Button mLikeButton,mPostOptions,mCommentButton;


    public NewsFeedViewHolder(View itemView) {
        super(itemView);
        circleImageView = itemView.findViewById(R.id.newsfeed_profile_image);
        mUploaderName = itemView.findViewById(R.id.newsfeed_name);
        mTimeStamp = itemView.findViewById(R.id.newsfeed_timestamp);
        mContent = itemView.findViewById(R.id.newsfeed_content);
        mImg = itemView.findViewById(R.id.newsfeed_image);
        mFileName = itemView.findViewById(R.id.newsfeed_filename);
        exoPlayerView = itemView.findViewById(R.id.exo_palyer_view);
        mLikes = itemView.findViewById(R.id.noOfLikes);
        mLikeButton = itemView.findViewById(R.id.likeButton);
        mPostOptions = itemView.findViewById(R.id.post_options);
        mCommentButton = itemView.findViewById(R.id.commentButton);

        personalInformation  = itemView.findViewById(R.id.newsFeedPersonInformation);
        contentInformation = itemView.findViewById(R.id.newsFeedContent);
        fileInformation = itemView.findViewById(R.id.newsFeedFile);
        showImg = itemView.findViewById(R.id.newsFeedImage);
        showVid = itemView.findViewById(R.id.newsFeedVideo);
        lc = itemView.findViewById(R.id.likeComment);
    }

}