package com.childcareapp.pivak.fyplogin;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.childcareapp.pivak.fyplogin.Models.Images;
import com.childcareapp.pivak.fyplogin.Models.ModelNewsFeed;
import com.childcareapp.pivak.fyplogin.Models.NotificationsList;
import com.github.abdularis.civ.CircleImageView;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class ViewNotification extends Fragment {

    String postId, uploaderId, status = "", uName;
    CircleImageView circleImageView;
    TextView mUploaderName, mTimeStamp, mContent, mFileName, mLikes;
    ImageView mImg;
    VideoView videoView;
    LinearLayout personalInformation;
    LinearLayout fileInformation;
    LinearLayout contentInformation;
    LinearLayout showImg;
    LinearLayout showVid;
    LinearLayout lc;
    SimpleExoPlayerView exoPlayerView;
    SimpleExoPlayer exoPlayer;
    Button mLikeButton, mPostOptions, mCommentButton;
    Integer likes;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View itemView = inflater.inflate(R.layout.news_feed_recycler_view, container, false);

        uName = getArguments().getString("user");

        postId = getArguments().getString("postId");
        uploaderId = getArguments().getString("uploaderId");

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

        personalInformation = itemView.findViewById(R.id.newsFeedPersonInformation);
        contentInformation = itemView.findViewById(R.id.newsFeedContent);
        fileInformation = itemView.findViewById(R.id.newsFeedFile);
        showImg = itemView.findViewById(R.id.newsFeedImage);
        showVid = itemView.findViewById(R.id.newsFeedVideo);
        lc = itemView.findViewById(R.id.likeComment);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        final FirebaseFirestore post = FirebaseFirestore.getInstance();
        post.collection("NewsfeedNotify").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {


                    for (QueryDocumentSnapshot document : task.getResult())
                    {
                        NotificationsList find = document.toObject(NotificationsList.class);
                        if(find.getPostId().equals(postId))
                        {
                            final Map<String, Object> id = new HashMap<>();
                            id.put("status","read");
                            post.collection("NewsfeedNotify").document(document.getId()).update(id).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                }
                            });
                        }

                    }
                }
                else
                {

                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });



        personalInformation.setVisibility(LinearLayout.VISIBLE);
        db.collection("NewsFeed").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    List<NotificationsList> mUsersList = new ArrayList<>();

                    int check = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        final ModelNewsFeed newsfeed = document.toObject(ModelNewsFeed.class);

                        if (newsfeed.getPostId().equals(postId))
                        {
                            check = 1;
                            if (newsfeed.getFiletype().equals("img") && newsfeed.getContent() != "") {
                                showVid.setVisibility(LinearLayout.GONE);
                                fileInformation.setVisibility(LinearLayout.GONE);

                                contentInformation.setVisibility(LinearLayout.VISIBLE);
                                showImg.setVisibility(LinearLayout.VISIBLE);
                                mContent.setText(newsfeed.getContent());
                                Picasso.get().load(newsfeed.getDownloadUrl()).into(mImg);
                                mImg.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Bundle bundle1 = new Bundle();
                                        bundle1.putString("uri", newsfeed.getDownloadUrl());
                                        bundle1.putString("fileName", newsfeed.getFileName());
                                        ViewFullImage viewFulIImage = new ViewFullImage();
                                        viewFulIImage.setArguments(bundle1);
                                        viewFulIImage.show(((AppCompatActivity) getContext()).getSupportFragmentManager(), "ShowImage");
                                    }
                                });

                            }
                            if (newsfeed.getFiletype().equals("video") && newsfeed.getContent() != "") {
                                showImg.setVisibility(LinearLayout.GONE);
                                fileInformation.setVisibility(LinearLayout.GONE);

                                contentInformation.setVisibility(LinearLayout.VISIBLE);
                                showVid.setVisibility(LinearLayout.VISIBLE);
                                mContent.setText(newsfeed.getContent());
                                try {
                                    BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
                                    TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
                                    exoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);
                                    DefaultHttpDataSourceFactory dataSource = new DefaultHttpDataSourceFactory("exoplayer_video");
                                    ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
                                    MediaSource mediaStore = new ExtractorMediaSource(Uri.parse(newsfeed.getDownloadUrl()), dataSource, extractorsFactory, null, null);
                                    exoPlayerView.setPlayer(exoPlayer);
                                    exoPlayer.prepare(mediaStore);
                                    exoPlayer.setPlayWhenReady(false);
                                }catch (Exception e) {
                                    Toast.makeText(getContext(), "Error playing video", Toast.LENGTH_SHORT).show();
                                }

                            }
                            if (newsfeed.getFiletype().equals("doc") && newsfeed.getContent() != "") {
                                showVid.setVisibility(LinearLayout.GONE);
                                showImg.setVisibility(LinearLayout.GONE);

                                contentInformation.setVisibility(LinearLayout.VISIBLE);
                                fileInformation.setVisibility(LinearLayout.VISIBLE);
                                mContent.setText(newsfeed.getContent());
                                mFileName.setText(newsfeed.getFileName());
                                mFileName.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        try {
                                            FileOutputStream fos = new FileOutputStream(newsfeed.getFileName());
                                            fos.write(newsfeed.getDownloadUrl().getBytes());
                                            fos.close();
                                            Toast.makeText(getContext(), "file saved", Toast.LENGTH_SHORT).show();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                            }
                            if (newsfeed.getFiletype().equals("") && newsfeed.getContent() != "") {
                                showVid.setVisibility(LinearLayout.GONE);
                                showImg.setVisibility(LinearLayout.GONE);
                                fileInformation.setVisibility(LinearLayout.GONE);

                                contentInformation.setVisibility(LinearLayout.VISIBLE);
                                mContent.setText(newsfeed.getContent());
                            }
                            if (newsfeed.getFiletype().equals("img") && newsfeed.getContent() == "") {
                                contentInformation.setVisibility(LinearLayout.GONE);
                                showVid.setVisibility(LinearLayout.GONE);
                                fileInformation.setVisibility(LinearLayout.GONE);

                                showImg.setVisibility(LinearLayout.VISIBLE);
                                Picasso.get().load(newsfeed.getDownloadUrl()).into(mImg);
                                mImg.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Bundle bundle2 = new Bundle();
                                        bundle2.putString("uri", newsfeed.getDownloadUrl());
                                        bundle2.putString("fileName", newsfeed.getFileName());
                                        ViewFullImage viewFulIImage = new ViewFullImage();
                                        viewFulIImage.setArguments(bundle2);
                                        viewFulIImage.show(((AppCompatActivity) getContext()).getSupportFragmentManager(), "ShowImage");
                                    }
                                });
                            }
                            if (newsfeed.getFiletype().equals("video") && newsfeed.getContent() == "") {
                                showImg.setVisibility(LinearLayout.GONE);
                                fileInformation.setVisibility(LinearLayout.GONE);
                                contentInformation.setVisibility(LinearLayout.GONE);
                                showVid.setVisibility(LinearLayout.VISIBLE);

                                try {
                                    BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
                                    TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
                                    exoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);
                                    DefaultHttpDataSourceFactory dataSource = new DefaultHttpDataSourceFactory("exoplayer_video");
                                    ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
                                    MediaSource mediaStore = new ExtractorMediaSource(Uri.parse(newsfeed.getDownloadUrl()), dataSource, extractorsFactory, null, null);
                                    exoPlayerView.setPlayer(exoPlayer);
                                    exoPlayer.prepare(mediaStore);
                                    exoPlayer.setPlayWhenReady(false);
                                } catch (Exception e) {
                                    Toast.makeText(getContext(), "Error playing video", Toast.LENGTH_SHORT).show();
                                }
                            }
                            if (newsfeed.getFiletype().equals("doc") && newsfeed.getContent() == "") {
                                contentInformation.setVisibility(LinearLayout.GONE);
                                showVid.setVisibility(LinearLayout.GONE);
                                showImg.setVisibility(LinearLayout.GONE);

                                fileInformation.setVisibility(LinearLayout.VISIBLE);
                                mFileName.setText(newsfeed.getFileName());
                            }

                            mUploaderName.setText(newsfeed.getUploaderName());
                            String timeAgo = NewsFeedRecyclerView.TimeAgo.getTimeAgo(newsfeed.getTimeInMillis());
                            mTimeStamp.setText(timeAgo);

                            final FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("Users").document("Student").collection(newsfeed.getUploaderId()).document("Profile").collection("Image").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot documentSnapshots) {
                                    if (documentSnapshots.isEmpty()) {
                                        //Toast.makeText(getActivity(), "Empty List", Toast.LENGTH_SHORT).show();
                                        return;
                                    } else {
                                        for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                                            if (documentSnapshot.exists()) {
                                                String idd = documentSnapshot.getId();
                                                ///////////// Nested query to get Image
                                                //img=getActivity().findViewById(R.id.imgShareContent);
                                                DocumentReference imgRef = db.collection("Users").document("Student").collection(newsfeed.getUploaderId()).document("Profile").collection("Image").document(idd);
                                                imgRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            DocumentSnapshot document = task.getResult();
                                                            if (document.exists()) {
                                                                ///////////////
                                                                Images imag = document.toObject(Images.class);
                                                                URL url = null;
                                                                try {
                                                                    url = new URL(imag.getUrl());
                                                                    //modelNewsFeed.setImg(url);
                                                                } catch (MalformedURLException e) {
                                                                    e.printStackTrace();
                                                                }
                                                                Picasso.get().load(url.toString()).into(circleImageView);
                                                            } else {
                                                                Log.i(TAG, "onComplete: Image doesn't exist");
                                                            }
                                                        } else {
                                                            Log.i(TAG, "onComplete: Failed to get Image");
                                                        }
                                                    }
                                                });
                                                //// end of nested query

                                            }
                                        }
                                    }

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.i(TAG, "onFailure: Failed to get Image");

                                }
                            });


                            mPostOptions.setVisibility(LinearLayout.GONE);
                            lc.setVisibility(LinearLayout.GONE);

                        }

                    }
                    if(check == 0)
                    {
                        personalInformation.setVisibility(LinearLayout.GONE);
                        fileInformation.setVisibility(LinearLayout.GONE);
                        showVid.setVisibility(LinearLayout.GONE);
                        showImg.setVisibility(LinearLayout.GONE);
                        lc.setVisibility(LinearLayout.GONE);
                        contentInformation.setVisibility(LinearLayout.VISIBLE);

                        mContent.setText("The post is no longer available.You might have deleted it.");

                    }
                } else {

                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });

        return itemView;
    }

}