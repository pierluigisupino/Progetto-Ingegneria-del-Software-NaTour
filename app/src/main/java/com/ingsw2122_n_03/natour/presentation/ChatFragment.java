package com.ingsw2122_n_03.natour.presentation;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ingsw2122_n_03.natour.application.MessageController;
import com.ingsw2122_n_03.natour.databinding.FragmentChatBinding;
import com.ingsw2122_n_03.natour.model.User;

import java.util.ArrayList;

public class ChatFragment extends Fragment {

    private FragmentChatBinding binding;
    private RecyclerView recyclerView;

    private final MessageController messageController = MessageController.getInstance();

    private ArrayList<User> chats = new ArrayList<>(); //utenti con cui ho avuto chat


    public ChatFragment() { messageController.setMessagesFragment(this); }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);

        //binding recycle view

        LinearLayoutManager layoutManager =  new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        if(chats.isEmpty()){
            //SEMBRA CHE NON CI SIA NIENTE TEXT
            //recycle view invisible
        }else {
            //SEMBRA CHE NON CI SIA NIENTE INVISIBLE
            //recycle view visible
        }

        //SET RECYCLE VIEW ADAPTER

        return binding.getRoot();
    }

    public void updateChats(ArrayList<User> chats) {
        this.chats = chats;
        if(this.isVisible()) {
            //SEMBRA CHE NON CI SIA NIENTE INVISIBLE
            //set recycle view adapter
        }
    }
}