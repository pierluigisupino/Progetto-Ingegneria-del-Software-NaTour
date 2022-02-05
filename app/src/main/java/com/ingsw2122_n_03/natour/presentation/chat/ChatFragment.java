package com.ingsw2122_n_03.natour.presentation.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.application.MessageController;
import com.ingsw2122_n_03.natour.databinding.FragmentChatBinding;
import com.ingsw2122_n_03.natour.model.Message;
import com.ingsw2122_n_03.natour.model.User;
import com.ingsw2122_n_03.natour.presentation.support.ChatAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatFragment extends Fragment implements ChatAdapter.ItemClickListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private LottieAnimationView lottieAnimationView;
    private TextView topTextView;
    private TextView bottomTextView;
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;

    private boolean isOnError = false;
    private boolean isLoading = true;

    private HashMap<User, ArrayList<Message>> chats = new HashMap<>();

    private final MessageController messageController = MessageController.getInstance();


    public ChatFragment() { messageController.setChatFragment(this); }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentChatBinding binding = FragmentChatBinding.inflate(inflater, container, false);

        swipeRefreshLayout = binding.update;
        lottieAnimationView = binding.loadingAnimation;
        topTextView = binding.topTexView;
        bottomTextView = binding.bottomTextView;
        recyclerView = binding.chats;

        swipeRefreshLayout.setOnRefreshListener(messageController::updateChats);

        lottieAnimationView.setSpeed(0.5F);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        chatAdapter = new ChatAdapter(requireActivity(), chats);
        chatAdapter.setClickListener(this);
        recyclerView.setAdapter(chatAdapter);

        return binding.getRoot();
    }


    @Override
    public void onResume() {
        super.onResume();
        updateUi();
    }


    private void updateUi(){

        requireActivity().runOnUiThread(() -> {

            if(isLoading) {
                swipeRefreshLayout.setEnabled(false);
                return;
            }

            swipeRefreshLayout.setEnabled(true);
            swipeRefreshLayout.setRefreshing(false);

            if(isOnError) {
                lottieAnimationView.setAnimation(R.raw.animation_error);
                lottieAnimationView.setMaxFrame(70);
                lottieAnimationView.setSpeed(1F);
                lottieAnimationView.playAnimation();
                topTextView.setText(ChatFragment.this.getString(R.string.resolvable_error));
                bottomTextView.setText(ChatFragment.this.getString(R.string.resolve_error));
                bottomTextView.setTextColor(ContextCompat.getColor(ChatFragment.this.requireActivity(), R.color.error));
                topTextView.setVisibility(View.VISIBLE);
                bottomTextView.setVisibility(View.VISIBLE);
                return;
            }

            if (chats.isEmpty()) {
                lottieAnimationView.setAnimation(R.raw.animation_empty);
                lottieAnimationView.setMaxFrame(150);
                lottieAnimationView.setSpeed(1F);
                lottieAnimationView.playAnimation();
                bottomTextView.setText(getString(R.string.empty));
                bottomTextView.setTextColor(ContextCompat.getColor(requireActivity(), R.color.primary));
                bottomTextView.setVisibility(View.VISIBLE);
                topTextView.setVisibility(View.GONE);
                return;
            }

            lottieAnimationView.setVisibility(View.GONE);
            topTextView.setVisibility(View.GONE);
            bottomTextView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            chatAdapter = new ChatAdapter(requireActivity(), chats);
            chatAdapter.setClickListener(this);
            recyclerView.setAdapter(chatAdapter);

        });

    }


    public void updateChats(HashMap<User, ArrayList<Message>> chats) {
        this.chats = chats;
        isOnError = false;
        isLoading = false;
        if(this.isVisible()) updateUi();
    }


    public void onError(){
        isOnError = true;
        isLoading = false;
        if(this.isVisible()) updateUi();
    }


    @Override
    public void onItemClick(View view, int position) {
        messageController.retrieveMessages(chatAdapter.getItem(position));
    }

}