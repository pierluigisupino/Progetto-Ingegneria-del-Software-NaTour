package com.ingsw2122_n_03.natour.presentation;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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
    private TextView topTextView;
    private TextView bottomTextView;
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;

    private boolean isChatUpdate = false;
    private boolean isChatUpdateOnError = false;
    private boolean isLoading = false;

    private ArrayList<User> chats = new ArrayList<>();

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

        swipeRefreshLayout.setOnRefreshListener(() -> {
            if(isChatUpdate || isChatUpdateOnError) {
                isLoading = true;
                messageController.updateChats();
            }else{
                swipeRefreshLayout.setRefreshing(false);
            }
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
        isChatUpdate = true;
        isChatUpdateOnError = false;
        if(this.isVisible()) updateUi();
    }

    private void updateUi(){

        if(isLoading){
            swipeRefreshLayout.setRefreshing(false);
            isLoading = false;
        }

        if(isChatUpdate && !isChatUpdateOnError) {
            if (chats.isEmpty()) {
                lottieAnimationView.setAnimation(R.raw.animation_empty);
                lottieAnimationView.setSpeed(1F);
                lottieAnimationView.playAnimation();
                bottomTextView.setText(getString(R.string.empty));
                bottomTextView.setTextColor(ContextCompat.getColor(requireActivity(), R.color.primary));
                bottomTextView.setVisibility(View.VISIBLE);
                topTextView.setVisibility(View.GONE);
            } else {
                lottieAnimationView.setVisibility(View.GONE);
                topTextView.setVisibility(View.GONE);
                bottomTextView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                chatAdapter = new ChatAdapter(requireActivity(), chats);
                chatAdapter.setClickListener(this);
                recyclerView.setAdapter(chatAdapter);
            }
        }

        if(isChatUpdateOnError){
            lottieAnimationView.setAnimation(R.raw.animation_error);
            lottieAnimationView.setSpeed(1F);
            lottieAnimationView.playAnimation();
            topTextView.setText(getString(R.string.resolvable_error));
            bottomTextView.setText(getString(R.string.resolve_error));
            bottomTextView.setTextColor(ContextCompat.getColor(requireActivity(), R.color.error));
            topTextView.setVisibility(View.VISIBLE);
            bottomTextView.setVisibility(View.VISIBLE);
        }
    }

    public void onResolvableError(){
        isChatUpdateOnError = true;

        if(this.isVisible()) {
            requireActivity().runOnUiThread(this::updateUi);
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        messageController.retrieveMessages(chatAdapter.getItem(position));
        //SHOW LOADING
    }
}