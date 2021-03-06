package com.ingsw2122_n_03.natour.presentation.support;

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

    private final Context mContext;
    private final List<Message> mMessageList;
    private final User mUser;

    public MessageAdapter(Context contex, List<Message> messageList, User currentUser) {
        mContext = contex;
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
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
                ((SentMessageHolder) holder).bind(message, position);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message, position);
        }
    }

    private void handleDate(TextView dateTextView, LocalDate sendDate, int itemPosition){

        if (itemPosition != 0) {
            processDate(dateTextView, sendDate, mMessageList.get(itemPosition - 1).getTime().toLocalDate(), false);
        } else {
            processDate(dateTextView, sendDate, null, true);
        }
    }

    private void processDate(@NonNull TextView tv, LocalDate date1, LocalDate date2, boolean isFirstItem) {

        DateTimeFormatter dateFormatter  = DateTimeFormatter.ofPattern(mContext.getResources().getString(R.string.pattern_long_date));
        String sentDate = dateFormatter.format(date1);

        if (isFirstItem) {
            if (date1.equals(LocalDate.now()))
                tv.setText(R.string.today);
            else if (date1.equals(LocalDate.now().minusDays(1)))
                tv.setText(R.string.yesterday);
            else
                tv.setText(sentDate);

            tv.setPadding(0, 0, 0,0);
            tv.setVisibility(View.VISIBLE);

        } else if (!date1.equals(date2)) {
            if (date1.equals(LocalDate.now()))
                tv.setText(R.string.today);
            else if (date1.equals(LocalDate.now().minusDays(1)))
                tv.setText(R.string.yesterday);
            else
                tv.setText(sentDate);

            tv.setPadding(0, 32, 0, 0);
            tv.setVisibility(View.VISIBLE);
        } else {
            tv.setVisibility(View.GONE);
        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, dateText, timeText;

        SentMessageHolder(View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.text_message_body);
            dateText = itemView.findViewById(R.id.text_message_date);
            timeText = itemView.findViewById(R.id.text_message_time);
        }

        void bind(Message message, int position) {
            messageText.setText(message.getBody());

            handleDate(dateText, message.getTime().toLocalDate(), position);

            DateTimeFormatter formatter  = DateTimeFormatter.ofPattern(mContext.getResources().getString(R.string.pattern_time));
            String sentTime = formatter.format(message.getTime());
            timeText.setText(sentTime);
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, dateText, timeText;

        ReceivedMessageHolder(View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.text_message_body);
            dateText = itemView.findViewById(R.id.text_message_date);
            timeText = itemView.findViewById(R.id.text_message_time);
        }

        void bind(Message message, int position) {
            messageText.setText(message.getBody());

            handleDate(dateText, message.getTime().toLocalDate(), position);

            DateTimeFormatter formatter  = DateTimeFormatter.ofPattern(mContext.getResources().getString(R.string.pattern_time));
            String sentTime = formatter.format(message.getTime());
            timeText.setText(sentTime);
        }
    }
}