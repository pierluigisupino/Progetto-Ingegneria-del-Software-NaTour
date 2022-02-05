package com.ingsw2122_n_03.natour.presentation.support;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.model.Message;
import com.ingsw2122_n_03.natour.model.User;

import java.util.ArrayList;
import java.util.HashMap;


public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private final HashMap<User, ArrayList<Message>> mChats;
    private final ArrayList<User> mUsers;
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    public ChatAdapter(Context context, HashMap<User, ArrayList<Message>> chats) {
        this.mInflater = LayoutInflater.from(context);
        this.mChats = chats;
        mUsers = new ArrayList<>(mChats.keySet());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_chat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User iUser = mUsers.get(position);
        String user =  iUser.getName();
        holder.myTextView.setText(user);
        ArrayList<Message> messages = mChats.get(iUser);
        holder.lastMessage.setText(messages.get(messages.size()-1).getBody());
    }

    @Override
    public int getItemCount() {
        return mChats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView myTextView;
        TextView lastMessage;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.chat_user);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    public User getItem(int id) {
        return mUsers.get(id);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}