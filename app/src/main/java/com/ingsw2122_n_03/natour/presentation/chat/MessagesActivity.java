package com.ingsw2122_n_03.natour.presentation.chat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.ingsw2122_n_03.natour.application.MessageController;
import com.ingsw2122_n_03.natour.databinding.ActivityMessagesBinding;
import com.ingsw2122_n_03.natour.model.Message;
import com.ingsw2122_n_03.natour.model.User;
import com.ingsw2122_n_03.natour.presentation.support.MessageAdapter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

import com.ingsw2122_n_03.natour.R;



public class MessagesActivity extends AppCompatActivity {

    private User currentUser;
    private User endUser;

    private RecyclerView recyclerView;

    private ArrayList<Message> messages;

    @SuppressLint("NewApi")
    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMessagesBinding binding = ActivityMessagesBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        MessageController.getInstance().setMessageActivity(this);

        Intent intent = getIntent();

        messages = (ArrayList<Message>) intent.getSerializableExtra("messages");
        currentUser = (User) intent.getSerializableExtra("currentUser");
        endUser = (User) intent.getSerializableExtra("endUser");

        MaterialToolbar materialToolbar = binding.topAppBar;
        recyclerView = binding.recyclerView;
        EditText editMessage = binding.editMessage;
        ImageButton buttonSend = binding.buttonSend;

        materialToolbar.setTitle(endUser.getName());

        MessageAdapter messageAdapter = new MessageAdapter(this, messages, currentUser);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(messageAdapter);
        recyclerView.scrollToPosition(messages.size() - 1);

        buttonSend.setOnClickListener(view1 -> {
            String body = editMessage.getText().toString();

            if(body.length() > 0){
                Message message = new Message(body, LocalDateTime.now(), currentUser, endUser);
                MessageController.getInstance().sendMessage(message);

                editMessage.getText().clear();
            }
        });

        materialToolbar.setNavigationOnClickListener(v -> finish());

    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateChat(Message message){

        runOnUiThread(()->{
            messages.add(message);
            Objects.requireNonNull(recyclerView.getAdapter()).notifyDataSetChanged();
            recyclerView.scrollToPosition(messages.size() - 1);
        });
    }

    public String getCurrentSession() { return endUser.getUid(); }

}