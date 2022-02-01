package com.ingsw2122_n_03.natour.presentation;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.application.MessageController;
import com.ingsw2122_n_03.natour.databinding.FragmentChatBinding;
import com.ingsw2122_n_03.natour.model.User;
import com.ingsw2122_n_03.natour.presentation.support.ChatAdapter;
import com.ingsw2122_n_03.natour.presentation.support.ItineraryAdapter;

import java.util.ArrayList;

public class ChatFragment extends Fragment implements ChatAdapter.ItemClickListener {

    private FragmentChatBinding binding;
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;

    private final MessageController messageController = MessageController.getInstance();

    private ArrayList<User> chats = new ArrayList<>();

    public ChatFragment() { messageController.setMessagesFragment(this); }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);

        recyclerView = binding.chats;

        if(chats.isEmpty()){
            //SEMBRA CHE NON CI SIA NIENTE TEXT
            //recycle view invisible
        }else {
            //SEMBRA CHE NON CI SIA NIENTE INVISIBLE
            //recycle view visible
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        chatAdapter = new ChatAdapter(requireActivity(), chats);
        chatAdapter.setClickListener(this);
        recyclerView.setAdapter(chatAdapter);

        return binding.getRoot();
    }

    public void updateChats(ArrayList<User> chats) {
        this.chats = chats;

        if(this.isVisible()) {
            //SEMBRA CHE NON CI SIA NIENTE INVISIBLE
            
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