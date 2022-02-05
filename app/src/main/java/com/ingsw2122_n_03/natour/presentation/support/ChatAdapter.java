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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;


public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private final Context mContext;
    private final HashMap<User, ArrayList<Message>> mChats;
    private final ArrayList<User> mUsers;
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    public ChatAdapter(Context context, HashMap<User, ArrayList<Message>> chats) {
        this.mInflater = LayoutInflater.from(context);
        this.mChats = chats;
        mContext = context;
        mUsers = new ArrayList<>(mChats.keySet());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_chat, parent, false);
        return new ViewHolder(view);
    }

    private void processDate(@NonNull TextView tv, LocalDate date, LocalDateTime time) {

        DateTimeFormatter dateFormatter  = DateTimeFormatter.ofPattern(mContext.getResources().getString(R.string.pattern_date));
        String sentDate = dateFormatter.format(date);

        if (date.equals(LocalDate.now()))
            tv.setText(time.format(DateTimeFormatter.ofPattern(mContext.getResources().getString(R.string.pattern_time))));
        else if (date.equals(LocalDate.now().minusDays(1)))
            tv.setText(R.string.yesterday);
        else
            tv.setText(sentDate);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User iUser = mUsers.get(position);
        String user =  iUser.getName();
        holder.chatUser.setText(user);
        ArrayList<Message> messages = mChats.get(iUser);
        assert messages != null;
        holder.chatLastMessage.setText(messages.get(messages.size()-1).getBody());

        LocalDate localDate = messages.get(messages.size()-1).getTime().toLocalDate();
        LocalDateTime localDateTime = messages.get(messages.size()-1).getTime();
        processDate(holder.chatTime, localDate, localDateTime);
    }

    @Override
    public int getItemCount() {
        return mChats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView chatUser;
        TextView chatTime;
        TextView chatLastMessage;


        ViewHolder(View itemView) {
            super(itemView);
            chatUser = itemView.findViewById(R.id.chat_user);
            chatTime = itemView.findViewById(R.id.chat_time);
            chatLastMessage = itemView.findViewById(R.id.chat_last_message);
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