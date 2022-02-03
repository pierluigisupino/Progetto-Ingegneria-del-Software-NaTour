package com.ingsw2122_n_03.natour.presentation.chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

    private User currentUser;
    private User endUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMessagesBinding binding = ActivityMessagesBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Intent intent = getIntent();

        ArrayList<Message> messages = (ArrayList<Message>) intent.getSerializableExtra("messages");
        currentUser = (User) intent.getSerializableExtra("currentUser");
        endUser = (User) intent.getSerializableExtra("endUser");

        MaterialToolbar materialToolbar = binding.topAppBar;
        RecyclerView recyclerView = binding.recyclerView;
        EditText editMessage = binding.editMessage;
        ImageButton buttonSend = binding.buttonSend;

        materialToolbar.setTitle(endUser.getName());

        MessageAdapter messageAdapter = new MessageAdapter(messages, currentUser);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);
        recyclerView.scrollToPosition(messages.size() - 1);

        buttonSend.setOnClickListener(view1 -> {
            String body = editMessage.getText().toString();

            if(body.length() > 0){
                Message message = new Message(body, LocalDate.now(), LocalTime.now(), currentUser, endUser);
                MessageController.getInstance().sendMessage(message);

                editMessage.getText().clear();

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(buttonSend.getWindowToken(), 0);
            }
        });

        materialToolbar.setNavigationOnClickListener(v -> finish());

    }
}