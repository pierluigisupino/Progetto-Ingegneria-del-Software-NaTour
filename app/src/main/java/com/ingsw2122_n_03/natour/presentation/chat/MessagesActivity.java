package com.ingsw2122_n_03.natour.presentation.chat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.appbar.MaterialToolbar;
import com.ingsw2122_n_03.natour.databinding.ActivityMessagesBinding;
import com.ingsw2122_n_03.natour.model.Message;
import com.ingsw2122_n_03.natour.model.User;

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

        materialToolbar.setTitle(endUser.getName());

        materialToolbar.setNavigationOnClickListener(v -> finish());

    }
}