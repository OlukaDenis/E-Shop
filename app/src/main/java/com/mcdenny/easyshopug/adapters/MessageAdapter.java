package com.mcdenny.easyshopug.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mcdenny.easyshopug.Common.Common;
import com.mcdenny.easyshopug.Model.Chat;
import com.mcdenny.easyshopug.R;
import com.mcdenny.easyshopug.Utils.Util;
import com.mcdenny.easyshopug.ViewHolder.MessageViewHolder;
import com.mcdenny.easyshopug.ViewHolder.UserViewHolder;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageViewHolder> {
    public static int MSG_TYPE_LEFT = 0;
    public static int MSG_TYPE_RIGHT = 1;

    private Context context;
    private List<Chat> chats;

    FirebaseUser firebaseUser;

    public MessageAdapter(Context context, List<Chat> list){
        this.chats = list;
        this.context = context;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_right, parent, false);
            return new MessageViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_left, parent, false);
            return new MessageViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder messageViewHolder, int position) {
        Chat chatData  = chats.get(position);

        messageViewHolder.showMessage.setText(chatData.getMessage());
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public void clear(){
        chats.clear();
        notifyDataSetChanged();

    }

    @Override
    public int getItemViewType(int position) {
        //firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String currUsr = Util.cleanEmailKey(Common.current_user_email);
        if(chats.get(position).getSender().equals(currUsr)){
            return MSG_TYPE_RIGHT;
        }
        else {
            return  MSG_TYPE_LEFT;
        }
    }
}
