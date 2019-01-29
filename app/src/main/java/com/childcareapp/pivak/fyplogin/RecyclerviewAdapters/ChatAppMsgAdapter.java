package com.childcareapp.pivak.fyplogin.RecyclerviewAdapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.childcareapp.pivak.fyplogin.Models.ChatDataModel;
import com.childcareapp.pivak.fyplogin.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ChatAppMsgAdapter extends RecyclerView.Adapter<ChatAppMsgAdapter.ChatAppMsgViewHolder> {

    private List<ChatDataModel> msgDataList = null;

    public ChatAppMsgAdapter(List<ChatDataModel> msgDtoList) {
        this.msgDataList = msgDtoList;
    }

    @Override
    public void onBindViewHolder(ChatAppMsgViewHolder holder, int position) {
        ChatDataModel msgData = this.msgDataList.get(position);
        // If the message is a received message.
        if(msgData.getStatus().equals("Received"))
        {
            // Show received message in left linearlayout.
            holder.leftMsgLayout.setVisibility(LinearLayout.VISIBLE);
            holder.leftMsgTextView.setText(msgData.getMessage());
            //holder.rigtMsgImageView.setImageBitmap(msgData.getBitmapImage());
            Picasso.get().load(msgData.getImage()).into(holder.rigtMsgImageView);
            // Remove left linearlayout.The value should be GONE, can not be INVISIBLE
            // Otherwise each iteview's distance is too big.
            holder.rightMsgLayout.setVisibility(LinearLayout.GONE);
        }
        // If the message is a sent message.
        else if(msgData.getStatus().equals("Sent"))
        {
            // Show sent message in right linearlayout.
            holder.rightMsgLayout.setVisibility(LinearLayout.VISIBLE);
            holder.rightMsgTextView.setText(msgData.getMessage());
            // Remove left linearlayout.The value should be GONE, can not be INVISIBLE
            // Otherwise each iteview's distance is too big.
            holder.leftMsgLayout.setVisibility(LinearLayout.GONE);
        }
    }

    @Override
    public ChatAppMsgViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.activity_chat_app_item_view, parent, false);
        return new ChatAppMsgViewHolder(view);
    }

    @Override
    public int getItemCount() {
        if(msgDataList==null)
        {
            msgDataList = new ArrayList<ChatDataModel>();
        }
        return msgDataList.size();
    }

    public class ChatAppMsgViewHolder extends RecyclerView.ViewHolder {

        LinearLayout leftMsgLayout;

        LinearLayout rightMsgLayout;

        TextView leftMsgTextView;

        TextView rightMsgTextView;

        ImageView rigtMsgImageView;

        public ChatAppMsgViewHolder(View itemView) {
            super(itemView);

            if(itemView!=null) {
                leftMsgLayout = (LinearLayout) itemView.findViewById(R.id.chat_left_msg_layout);
                rightMsgLayout = (LinearLayout) itemView.findViewById(R.id.chat_right_msg_layout);
                leftMsgTextView = (TextView) itemView.findViewById(R.id.chat_left_msg_text_view);
                rightMsgTextView = (TextView) itemView.findViewById(R.id.chat_right_msg_text_view);
                rigtMsgImageView = (ImageView) itemView.findViewById(R.id.picture_chat_message_other);
            }
        }
    }
}