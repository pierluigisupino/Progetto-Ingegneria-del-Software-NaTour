package com.ingsw2122_n_03.natour.presentation.support;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ingsw2122_n_03.natour.model.Message;
import com.ingsw2122_n_03.natour.model.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.ingsw2122_n_03.natour.R;

public class MessageAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private Context mContext;
    private List<Message> mMessageList;
    private User mUser;

    private LocalDate lastMessageDate;

    public MessageAdapter(Context context, List<Message> messageList, User currentUser) {
        mContext = context;
        mMessageList = messageList;
        mUser = currentUser;
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = mMessageList.get(position);

        if (message.getSender().getUid().equals(mUser.getUid())) {
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message message = mMessageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
        }
    }

    private void checkDate(LocalDate date, TextView dateTextView){

        @SuppressLint("SimpleDateFormat")
        DateTimeFormatter dateFormatter  = DateTimeFormatter.ofPattern("MMMM dd yyyy");
        String sentDate = dateFormatter.format(date);

        if(lastMessageDate != null){

            if(date.isAfter(lastMessageDate)){
                dateTextView.setVisibility(View.VISIBLE);
                dateTextView.setText(sentDate);
            }else{
                dateTextView.setVisibility(View.GONE);
            }

        }else{
            lastMessageDate = date;
            dateTextView.setVisibility(View.VISIBLE);
            dateTextView.setText(sentDate);
        }

    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, dateText, timeText;

        SentMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            dateText = (TextView) itemView.findViewById(R.id.text_message_date);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
        }

        void bind(Message message) {
            messageText.setText(message.getBody());

            checkDate(message.getSendDate(), dateText);

            @SuppressLint("SimpleDateFormat")
            DateTimeFormatter formatter  = DateTimeFormatter.ofPattern("hh:mm a");
            String sentTime = formatter.format(message.getSendTime());
            timeText.setText(sentTime);
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, dateText, timeText;

        ReceivedMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            dateText = (TextView) itemView.findViewById(R.id.text_message_date);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
        }

        void bind(Message message) {
            messageText.setText(message.getBody());

            checkDate(message.getSendDate(), dateText);

            @SuppressLint("SimpleDateFormat")
            DateTimeFormatter formatter  = DateTimeFormatter.ofPattern("hh:mm a");
            String sentTime = formatter.format(message.getSendTime());
            timeText.setText(sentTime);
        }
    }
}