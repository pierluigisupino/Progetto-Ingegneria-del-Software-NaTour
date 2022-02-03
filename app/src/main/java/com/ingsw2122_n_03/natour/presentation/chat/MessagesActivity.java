package com.ingsw2122_n_03.natour.presentation.chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.material.appbar.MaterialToolbar;
import com.ingsw2122_n_03.natour.application.MessageController;
import com.ingsw2122_n_03.natour.databinding.ActivityMessagesBinding;
import com.ingsw2122_n_03.natour.model.Message;
import com.ingsw2122_n_03.natour.model.User;
import com.ingsw2122_n_03.natour.presentation.support.MessageAdapter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class MessagesActivity extends AppCompatActivity {

    private ArrayList<Message> messages;
    private User currentUser;
    private User endUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMessagesBinding binding = ActivityMessagesBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Intent intent = getIntent();

        messages = (ArrayList<Message>) intent.getSerializableExtra("messages");
        currentUser = (User) intent.getSerializableExtra("currentUser");
        endUser = (User) intent.getSerializableExtra("endUser");

        MaterialToolbar materialToolbar = binding.topAppBar;
        RecyclerView recyclerView = binding.recyclerView;
        EditText editMessage = binding.editMessage;
        ImageButton buttonSend = binding.buttonSend;

        materialToolbar.setTitle(endUser.getName());

        MessageAdapter messageAdapter = new MessageAdapter(messages, currentUser);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);

        buttonSend.setOnClickListener(view1 -> {
            String body = editMessage.getText().toString();

            if(body.length() > 0){
                Message message = new Message(body, LocalDate.now(), LocalTime.now(), currentUser, endUser);
                MessageController.getInstance().sendMessage(message);
            }
        });

        materialToolbar.setNavigationOnClickListener(v -> finish());

    }
}