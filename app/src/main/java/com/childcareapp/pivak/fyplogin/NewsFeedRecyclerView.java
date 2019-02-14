package com.childcareapp.pivak.fyplogin;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeInfoDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;
import com.childcareapp.pivak.fyplogin.Dialogs.CommentPostDialog;
import com.childcareapp.pivak.fyplogin.Dialogs.EditPostDialog;
import com.childcareapp.pivak.fyplogin.Models.CommentUsersList;
import com.childcareapp.pivak.fyplogin.Models.Images;
import com.childcareapp.pivak.fyplogin.Models.ModelNewsFeed;
import com.childcareapp.pivak.fyplogin.Models.NotificationsList;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
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

public class NewsFeedRecyclerView extends RecyclerView.Adapter<NewsFeedViewHolder> {

    //ImageView imageView;
    private Context context;
    Integer likes;
    String mName;
    String uName,status="";
    int check = 0;


    private List<NewsFeedList> newsFeedList = null;

    public NewsFeedRecyclerView(List<NewsFeedList> newsFeedList,Context context,String uName) {
        this.newsFeedList = newsFeedList;
        this.context = context;
        this.uName = uName;
    }

    @Override
    public NewsFeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.news_feed_recycler_view, parent, false);
        return new NewsFeedViewHolder(view);
    }
    public void refresh(List<NewsFeedList> items) {
        this.newsFeedList = items;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final NewsFeedViewHolder holder, final int position) {

        final NewsFeedList newsfeed = this.newsFeedList.get(position);


        holder.personalInformation.setVisibility(LinearLayout.VISIBLE);
        holder.lc.setVisibility(LinearLayout.VISIBLE);
        if(newsfeed.getFiletype().equals("img" ) && newsfeed.getContent() != "")
        {
            holder.showVid.setVisibility(LinearLayout.GONE);
            holder.fileInformation.setVisibility(LinearLayout.GONE);

            holder.showImg.setVisibility(LinearLayout.VISIBLE);
            holder.contentInformation.setVisibility(LinearLayout.VISIBLE);
            holder.mContent.setText(newsfeed.getContent());
            holder.showImg.setVisibility(LinearLayout.VISIBLE);
            Picasso.get().load(newsfeed.getDownloadUrl()).into(holder.mImg);
            holder.mImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("uri", newsfeed.getDownloadUrl());
                    bundle1.putString("fileName", newsfeed.getFileName());
                    ViewFullImage viewFulIImage = new ViewFullImage();
                    viewFulIImage.setArguments(bundle1);
                    viewFulIImage.show(((AppCompatActivity) context).getSupportFragmentManager(), "ShowImage");
                }
            });

        }
        if(newsfeed.getFiletype().equals("video" ) && newsfeed.getContent() != "")
        {
            holder.showImg.setVisibility(LinearLayout.GONE);
            holder.fileInformation.setVisibility(LinearLayout.GONE);

            holder.contentInformation.setVisibility(LinearLayout.VISIBLE);
            holder.showVid.setVisibility(LinearLayout.VISIBLE);
            holder.mContent.setText(newsfeed.getContent());
            holder.showVid.setVisibility(LinearLayout.VISIBLE);
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
            holder.exoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
            DefaultHttpDataSourceFactory dataSource = new DefaultHttpDataSourceFactory("exoplayer_video");
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            MediaSource mediaStore = new ExtractorMediaSource(Uri.parse(newsfeed.getDownloadUrl()), dataSource, extractorsFactory, null, null);
            holder.exoPlayerView.setPlayer(holder.exoPlayer);
            holder.exoPlayer.prepare(mediaStore);
            holder.exoPlayer.setPlayWhenReady(false);
//            holder.exoPlayerView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(context, "listening", Toast.LENGTH_SHORT).show();
//                    Bundle bundle3 = new Bundle();
//                    bundle3.putString("uri", newsfeed.getDownloadUrl());
//                    bundle3.putString("fileName", newsfeed.getFileName());
//                    ViewFullVideo viewFullVideo = new ViewFullVideo();
//                    viewFullVideo.setArguments(bundle3);
//                    viewFullVideo.show(((AppCompatActivity) context).getSupportFragmentManager(), "ShowVideo");
//                }
//            });

        }
        if(newsfeed.getFiletype().equals("doc") && newsfeed.getContent() != "")
        {
            holder.showVid.setVisibility(LinearLayout.GONE);
            holder.showImg.setVisibility(LinearLayout.GONE);

            holder.contentInformation.setVisibility(LinearLayout.VISIBLE);
            holder.fileInformation.setVisibility(LinearLayout.VISIBLE);
            holder.mContent.setText(newsfeed.getContent());
            holder.mFileName.setText(newsfeed.getFileName());
            holder.mFileName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        FileOutputStream fos = new FileOutputStream(newsfeed.getFileName());
                        fos.write(newsfeed.getDownloadUrl().getBytes());
                        fos.close();
                        Toast.makeText(context, "file saved", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

//            inputText.setText("");
//            response.setText("SampleFile.txt saved to External Storage...");

        }
        if(newsfeed.getFiletype().equals("") && newsfeed.getContent() != "")
        {
            holder.showVid.setVisibility(LinearLayout.GONE);
            holder.showImg.setVisibility(LinearLayout.GONE);
            holder.fileInformation.setVisibility(LinearLayout.GONE);

            holder.contentInformation.setVisibility(LinearLayout.VISIBLE);
            holder.mContent.setText(newsfeed.getContent());
        }
        if(newsfeed.getFiletype().equals("img") && newsfeed.getContent() == "")
        {
            holder.contentInformation.setVisibility(LinearLayout.GONE);
            holder.showVid.setVisibility(LinearLayout.GONE);
            holder.fileInformation.setVisibility(LinearLayout.GONE);

            holder.showImg.setVisibility(LinearLayout.VISIBLE);
            holder.showImg.setVisibility(LinearLayout.VISIBLE);
            Picasso.get().load(newsfeed.getDownloadUrl()).into(holder.mImg);
            holder.mImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Bundle bundle2 = new Bundle();
                    bundle2.putString("uri", newsfeed.getDownloadUrl());
                    bundle2.putString("fileName", newsfeed.getFileName());
                    ViewFullImage viewFulIImage = new ViewFullImage();
                    viewFulIImage.setArguments(bundle2);
                    viewFulIImage.show(((AppCompatActivity) context).getSupportFragmentManager(), "ShowImage");
                }
            });
        }
        if(newsfeed.getFiletype().equals("video" ) && newsfeed.getContent() == "")
        {
            holder.showImg.setVisibility(LinearLayout.GONE);
            holder.fileInformation.setVisibility(LinearLayout.GONE);
            holder.contentInformation.setVisibility(LinearLayout.GONE);

            holder.showVid.setVisibility(LinearLayout.VISIBLE);
            holder.showVid.setVisibility(LinearLayout.VISIBLE);

            try {
                BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
                TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
                holder.exoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
                DefaultHttpDataSourceFactory dataSource = new DefaultHttpDataSourceFactory("exoplayer_video");
                ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
                MediaSource mediaStore = new ExtractorMediaSource(Uri.parse(newsfeed.getDownloadUrl()), dataSource, extractorsFactory, null, null);
                holder.exoPlayerView.setPlayer(holder.exoPlayer);
                holder.exoPlayer.prepare(mediaStore);
                holder.exoPlayer.setPlayWhenReady(false);
            }
            catch (Exception e)
            {
                Toast.makeText(context, "Error playing video", Toast.LENGTH_SHORT).show();
            }
        }
        if(newsfeed.getFiletype().equals("doc") && newsfeed.getContent() == "")
        {
            holder.contentInformation.setVisibility(LinearLayout.GONE);
            holder.showVid.setVisibility(LinearLayout.GONE);
            holder.showImg.setVisibility(LinearLayout.GONE);

            holder.fileInformation.setVisibility(LinearLayout.VISIBLE);
            holder.mFileName.setText(newsfeed.getFileName());
        }

        holder.mUploaderName.setText(newsfeed.getUploaderName());
        String timeAgo = TimeAgo.getTimeAgo(newsfeed.getTimeInMillis());
        holder.mTimeStamp.setText(timeAgo);
        holder.mLikes.setText(Integer.toString(newsfeed.getLikes()) + " likes");

        holder.mLikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseFirestore post = FirebaseFirestore.getInstance();
                CollectionReference doc =   post.collection("Newsfeedlike").document("posts").collection(newsfeed.getPostId());
                doc.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task)
                    {
                        if (task.isSuccessful())
                        {
                            for (QueryDocumentSnapshot document : task.getResult())
                            {
                                if(document.exists())
                                {
                                    if ((document.getId()).equals(uName))
                                    {
                                        status = "liked";
                                        //Toast.makeText(context, "match", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        status = "unliked";
                                        //Toast.makeText(context, "no match", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else
                                {
                                    status = "unliked";
                                    //Toast.makeText(context, "no data", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        else
                        {
                            //Toast.makeText(context, "no successful", Toast.LENGTH_SHORT).show();
                        }
                    } });
                //Toast.makeText(context, status, Toast.LENGTH_SHORT).show();
                String parseLike = holder.mLikes.getText().toString();
                String[] tokens = parseLike.split(" ");
                likes = Integer.valueOf(tokens[0]);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(likes == 0)
                        {
                            status = "unliked";
                        }
                        if(status == "liked")
                        {
                            holder.mLikeButton.setBackgroundColor(Color.BLUE);
                            likes--;
                            holder.mLikes.setText(Integer.toString(likes) + " likes");
                            LikeButton(0,newsfeed.getPostId(),uName,likes,newsfeed.uploaderId);
                        }
                        if(status == "unliked")
                        {
                            holder.mLikeButton.setBackgroundColor(Color.WHITE);
                            likes++;
                            holder.mLikes.setText(Integer.toString(likes) + " likes");
                            LikeButton(1,newsfeed.getPostId(),uName,likes,newsfeed.uploaderId);

                        }
                    }
                }, 1000);



            }
        });

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document("Student").collection(newsfeed.getUploaderId()).document("Profile").collection("Image").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                if (documentSnapshots.isEmpty())
                {
                    //Toast.makeText(getActivity(), "Empty List", Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    for (DocumentSnapshot documentSnapshot : documentSnapshots)
                    {
                        if (documentSnapshot.exists())
                        {
                            String idd=documentSnapshot.getId();
                            ///////////// Nested query to get Image
                            //img=getActivity().findViewById(R.id.imgShareContent);
                            DocumentReference imgRef = db.collection("Users").document("Student").collection(newsfeed.getUploaderId()).document("Profile").collection("Image").document(idd);
                            imgRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful())
                                    {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists())
                                        {
                                            ///////////////
                                            Images imag=document.toObject(Images.class);
                                            URL url = null;
                                            try
                                            {
                                                url =new URL(imag.getUrl());
                                                //modelNewsFeed.setImg(url);
                                            }
                                            catch (MalformedURLException e) {
                                                e.printStackTrace();
                                            }
                                            Picasso.get().load(url.toString()).into(holder.circleImageView);
                                        }
                                        else
                                        {
                                            Log.i(TAG, "onComplete: Image doesn't exist");
                                        }
                                    }
                                    else
                                    {
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

        if(newsfeed.getUploaderId().equals(uName))
        {
            holder.mPostOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final PopupMenu popupMenu = new PopupMenu(context, holder.mPostOptions);
                    popupMenu.getMenuInflater().inflate(R.menu.post_menu, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getTitle().equals("Edit"))
                            {
                                openEditPostDialog(newsfeed.getUploaderName(),newsfeed.getUploaderId(),newsfeed.getContent(),newsfeed.getFileName(),newsfeed.getFiletype(),newsfeed.getPostId());
                            }
                            else if (item.getTitle().equals("Delete"))
                            {
                                showAlertDialog(newsfeed.getPostId(), position);
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                }
            });
        }
        else
        {
            holder.mPostOptions.setVisibility(LinearLayout.GONE);
        }

        holder.mLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle2 = new Bundle();
                bundle2.putString("postId", newsfeed.getPostId());
                bundle2.putString("uName",uName);
                ViewLikedUsers viewLikedUsers = new ViewLikedUsers();
                viewLikedUsers.setArguments(bundle2);
                viewLikedUsers.show(((AppCompatActivity) context).getSupportFragmentManager(), "All likes");
            }
        });
        holder.mCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle2 = new Bundle();
                bundle2.putString("postId", newsfeed.getPostId());
                bundle2.putString("uName",uName);
                bundle2.putString("uploaderId",newsfeed.getUploaderId());
                CommentPostDialog commentPostDialog = new CommentPostDialog();
                commentPostDialog.setArguments(bundle2);
                commentPostDialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "All Comments");
            }
        });

    }


    @Override
    public int getItemCount() {
        if(newsFeedList==null)
        {
            newsFeedList = new ArrayList<NewsFeedList>();
        }
        return newsFeedList.size();

    }
    public static class TimeAgo
    {
        private static final int SECOND_MILLIS = 1000;
        private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

        public static String getTimeAgo(long time) {
            if (time < 1000000000000L) {
                time *= 1000;
            }

            long now = System.currentTimeMillis();
            if (time > now || time <= 0) {
                return null;
            }


            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + " minutes ago";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + " hours ago";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else {
                return diff / DAY_MILLIS + " days ago";
            }
        }
    }
    private void LikeButton(int check, final String postId, final String LikeUser, final Integer updatedlikes, String uploaderId)
    {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (check == 0)
        {
            FirebaseFirestore post = FirebaseFirestore.getInstance();
            CollectionReference doc =   post.collection("Newsfeedlike").document("posts").collection(postId);
            doc.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task)
                {
                    if (task.isSuccessful())
                    {
                        for (QueryDocumentSnapshot document : task.getResult())
                        {
                            if ((document.getId()).equals(LikeUser))
                            {
                                db.collection("Newsfeedlike").document("posts").collection(postId).document(LikeUser).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                    }
                                });
                            }
                            else
                            {
                                Toast.makeText(context,"put here", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } });

        }
        else
        {
            final Map<String, Object> id = new HashMap<>();
            id.put("id",uName);
            db.collection("Newsfeedlike").document("posts").collection(postId).document(LikeUser).set(id).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }
            });
            if(!uName.equals(uploaderId))
            {
                setNotifications(postId,uploaderId);
            }

        }

        db.collection("NewsFeed").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                if (task.isSuccessful())
                {

                    for (final QueryDocumentSnapshot document : task.getResult())
                    {
                        final ModelNewsFeed model = document.toObject(ModelNewsFeed.class);
                        if(model.getPostId().equals(postId))
                        {
                            Map<String, Object> data = new HashMap<>();
                            data.put("likes",updatedlikes);
                            db.collection("NewsFeed").document(document.getId()).update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    //updateRecyclerView();

                                }
                            });

                            //refresh();
                        }

                    }
                }
                else
                {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });
    }
    private void deletePost(final String postId)
    {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestore post = FirebaseFirestore.getInstance();
        post.collection("NewsFeed").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                if (task.isSuccessful())
                {

                    for (final QueryDocumentSnapshot document : task.getResult())
                    {
                        final ModelNewsFeed model = document.toObject(ModelNewsFeed.class);
                        if(model.getPostId().equals(postId))
                        {
                            Map<String, Object> data = new HashMap<>();
                            db.collection("NewsFeed").document(document.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    //updateRecyclerView();

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
        post.collection("NewsfeedComment").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                if (task.isSuccessful())
                {

                    for (final QueryDocumentSnapshot document : task.getResult())
                    {
                        final CommentUsersList model = document.toObject(CommentUsersList.class);
                        if(model.getPostId().equals(postId))
                        {
                            Map<String, Object> data = new HashMap<>();
                            db.collection("NewsfeedComment").document(document.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    //updateRecyclerView();

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
//        post.collection("NewsfeedLike").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task)
//            {
//                if (task.isSuccessful())
//                {
//
//                    for (final QueryDocumentSnapshot document : task.getResult())
//                    {
//                        final LikeUsersList model = document.toObject(LikeUsersList.class);
//                        if(model.getPostId().equals(postId))
//                        {
//                            Map<String, Object> data = new HashMap<>();
//                            db.collection("NewsfeedLike").document(document.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task)
//                                {
//                                    //updateRecyclerView();
//
//                                }
//                            });
//
//                        }
//
//
//                    }
//                }
//                else
//                {
//                    Log.w(TAG, "Error getting documents.", task.getException());
//                }
//            }
//        });
    }
    public void showAlertDialog(final String postId, final int position)
    {
        new AwesomeInfoDialog(context)
                .setTitle(Html.fromHtml("<b>"+"DELETE POST"+"</b>", Html.FROM_HTML_MODE_LEGACY))
                .setMessage("Are you sure you want to delete this item?")
                .setColoredCircle(R.color.primaryDark)
                .setDialogIconAndColor(R.drawable.ic_dialog_warning, R.color.white)
                .setCancelable(true)
                .setPositiveButtonText("DELETE")
                .setPositiveButtonbackgroundColor(R.color.primaryDark)
                .setPositiveButtonTextColor(R.color.white)
                .setNegativeButtonText("CANCEL")
                .setNegativeButtonbackgroundColor(R.color.primaryDark)
                .setNegativeButtonTextColor(R.color.white)
                .setPositiveButtonClick(new Closure() {
                    @Override
                    public void exec() {
                        deletePost(postId);
                        Toast.makeText(context, Integer.toString(position), Toast.LENGTH_SHORT).show();
                        newsFeedList.get(position);
                        newsFeedList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position,newsFeedList.size());
                        Toast.makeText(context, "Post Deleted", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButtonClick(new Closure() {
                    @Override
                    public void exec() {
                        //cancel
                    }
                })
                .show();
    }
    private void loadNewsfeed()
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("feed").document("Posts").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                if (task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists())
                    {
                        String users=document.getData().get("users").toString();
                        if(!users.equals(""))
                        {
                            String[] tokens = users.split(",");
                            for (String t : tokens)
                            {
                                FirebaseFirestore post = FirebaseFirestore.getInstance();
                                CollectionReference docRef =   post.collection("feed").document("Posts").collection(t);
                                docRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task)
                                    {
                                        if (task.isSuccessful())
                                        {

                                            for (QueryDocumentSnapshot document : task.getResult())
                                            {
                                                ModelNewsFeed newsFeed=document.toObject(ModelNewsFeed.class);
                                                NewsFeedList newslist = new NewsFeedList(newsFeed.getContent(), newsFeed.getUploaderId(),newsFeed.getDownloadUrl(),newsFeed.getUploaderName(),newsFeed.getFiletype(),newsFeed.getTimeStamp(),newsFeed.getFileName(),newsFeed.getPostId(),newsFeed.getLikes(),newsFeed.getTimeInMillis());
                                                newsFeedList.add(newslist);


                                            }
                                        }
                                        else
                                        {

                                            Log.w(TAG, "Error getting documents.", task.getException());
                                        }
                                        int newMsgPosition = newsFeedList.size() - 1;
                                        notifyItemInserted(newMsgPosition);
                                    }
                                });
                            }
                        }
                        else
                        {
                            //users=null
                        }
                    }
                }
                else
                {
                    //no database connection
                }

            }});
    }
    private void updateRecyclerView()
    {
        final int size = newsFeedList.size();
        newsFeedList.clear();
        notifyItemRangeRemoved(0, size);
        loadNewsfeed();
    }
    public void openEditPostDialog(String name,String uName, String content, String filename, String filetype,String postId)
    {
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("id", uName);
        bundle.putString("content",content);
        bundle.putString("filename",filename);
        bundle.putString("filetype",filetype);
        bundle.putString("postId",postId);
        EditPostDialog editPostDialog = new EditPostDialog();
        editPostDialog.setArguments(bundle);
        editPostDialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "Edit Post");

    }
    private void setNotifications(String postId,String uploaderId)
    {
          FirebaseFirestore db = FirebaseFirestore.getInstance();
          NotificationsList notificationsList = new NotificationsList(uName,"liked your post.","",System.currentTimeMillis(),postId,"notRead",uploaderId);

        db.collection("NewsfeedNotify").add(notificationsList).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {

            }});
    }
    private void check1()
    {
        FirebaseFirestore post = FirebaseFirestore.getInstance();
        CollectionReference docRef =   post.collection("NewsFeed");
        docRef.orderBy("timeStamp", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots,
                                        @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e(TAG, "onEvent: Listen failed.", e);
                            return;
                        }

                        if(queryDocumentSnapshots != null)
                        {
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                                ModelNewsFeed newsFeed=doc.toObject(ModelNewsFeed.class);
                                NewsFeedList newslist = new NewsFeedList(newsFeed.getContent().toString(), newsFeed.getUploaderId().toString(),newsFeed.getDownloadUrl().toString(),newsFeed.getUploaderName().toString(),newsFeed.getFiletype().toString(),newsFeed.getTimeStamp(),newsFeed.getFileName().toString(),newsFeed.getPostId(),newsFeed.getLikes(),newsFeed.getTimeInMillis());
                                newsFeedList.add(newslist);
                            }
                            //Toast.makeText(getContext(), Integer.toString(newsFeedList.size()), Toast.LENGTH_SHORT).show();
                        }
                        else
                        {

                        }
                        int newMsgPosition = newsFeedList.size() - 1;
                        notifyItemInserted(newMsgPosition);
                    }
                });
    }

    private void refresh()
    {
        final int size = newsFeedList.size();
        newsFeedList.clear();
        notifyItemRangeRemoved(0, size);
        check1();
        //Toast.makeText(getContext(), "New Posts", Toast.LENGTH_SHORT).show();
    }
}

