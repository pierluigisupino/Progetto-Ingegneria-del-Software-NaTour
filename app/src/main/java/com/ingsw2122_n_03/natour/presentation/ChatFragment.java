package com.ingsw2122_n_03.natour.presentation;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.ingsw2122_n_03.natour.application.MessageController;
import com.ingsw2122_n_03.natour.databinding.FragmentChatBinding;
import com.ingsw2122_n_03.natour.model.User;
import com.ingsw2122_n_03.natour.presentation.support.ChatAdapter;

import java.util.ArrayList;

import com.ingsw2122_n_03.natour.R;

public class ChatFragment extends Fragment implements ChatAdapter.ItemClickListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private LottieAnimationView lottieAnimationView;
    private TextView textView;
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;

    private ArrayList<User> chats = new ArrayList<>();

    private boolean isChatUpdate = false;
    private boolean isUiUpdate = false;

    public ChatFragment() {
        MessageController messageController = MessageController.getInstance();
        messageController.setMessagesFragment(this); }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        com.ingsw2122_n_03.natour.databinding.FragmentChatBinding binding = FragmentChatBinding.inflate(inflater, container, false);

        swipeRefreshLayout = binding.update;
        lottieAnimationView = binding.loadingAnimation;
        textView = binding.emptyText;
        recyclerView = binding.chats;

        swipeRefreshLayout.setOnRefreshListener(() -> {
            Toast.makeText(requireActivity() ,"Getting messages", Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
        });

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

    public void updateChats(ArrayList<User> chats) {
        this.chats = chats;
    }

    private void updateUi(){

            if(chats.isEmpty()){
                lottieAnimationView.setAnimation(R.raw.animation_empty);
                textView.setVisibility(View.VISIBLE);
            }else{
                lottieAnimationView.setVisibility(View.GONE);
                textView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                chatAdapter = new ChatAdapter(requireActivity(), chats);
                chatAdapter.setClickListener(this);
                recyclerView.setAdapter(chatAdapter);
            }
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(view.getContext(),"Uid: " + chatAdapter.getItem(position).getUid() + " Name: " + chatAdapter.getItem(position).getName(), Toast.LENGTH_SHORT).show();
    }
}