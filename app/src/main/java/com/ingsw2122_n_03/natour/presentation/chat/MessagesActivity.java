package com.ingsw2122_n_03.natour.presentation.chat;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.application.MessageController;
import com.ingsw2122_n_03.natour.databinding.ActivityMessagesBinding;
import com.ingsw2122_n_03.natour.model.Message;
import com.ingsw2122_n_03.natour.model.User;
import com.ingsw2122_n_03.natour.presentation.support.BaseActivity;
import com.ingsw2122_n_03.natour.presentation.support.MessageAdapter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;


public class MessagesActivity extends BaseActivity {

    private ConstraintLayout constraintLayout;
    private RecyclerView recyclerView;
    private ImageButton buttonSend;
    private Bundle bundle  = new Bundle();


    @SuppressLint("NewApi")
    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMessagesBinding binding = ActivityMessagesBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        MessageController.getInstance().setMessageActivity(this);

        constraintLayout = binding.layout;

        bundle = getIntent().getExtras();

        ArrayList<Message> messages = (ArrayList<Message>) bundle.getSerializable("messages");
        User currentUser = (User) bundle.getSerializable("currentUser");
        User endUser = (User) bundle.getSerializable("endUser");


        MaterialToolbar materialToolbar = binding.topAppBar;
        recyclerView = binding.recyclerView;
        EditText editMessage = binding.editMessage;
        buttonSend = binding.buttonSend;

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
                buttonSend.setEnabled(false);
            }
        });

        materialToolbar.setNavigationOnClickListener(v -> finish());

    }


    @SuppressWarnings("unchecked")
    public void updateChat(Message message){
        runOnUiThread(()-> {
            buttonSend.setEnabled(true);
            ArrayList<Message> messages = (ArrayList<Message>) bundle.getSerializable("messages");
            messages.add(message);
            Objects.requireNonNull(recyclerView.getAdapter()).notifyItemInserted(messages.size() - 1);
            recyclerView.scrollToPosition(messages.size() - 1);
        });
    }


    public String getCurrentSession() {
        return ((User) bundle.getSerializable("endUser")).getUid();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "MessagesActivity");
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "MessagesActivity");
        FirebaseAnalytics.getInstance(this).logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);
    }


    @Override
    public void onSuccess(String msg) {}


    @Override
    public void onFail(String msg) {
        runOnUiThread(() -> {
            buttonSend.setEnabled(true);
            Snackbar.make(constraintLayout, msg, Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(ContextCompat.getColor(MessagesActivity.this, R.color.error))
                    .show();
        });
    }

}