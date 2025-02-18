package com.example.krishiyog.adapters;

import android.transition.ChangeTransform;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.krishiyog.databinding.ChatBotCardviewBinding;
import com.example.krishiyog.databinding.TestBinding;
import com.example.krishiyog.models.ChatBotModel;

import java.util.ArrayList;
import java.util.List;

public class ChatBotAdapter extends RecyclerView.Adapter<ChatBotAdapter.ViewHolder> {

    private ArrayList<ChatBotModel> chatBotModelList;

    public ChatBotAdapter(ArrayList<ChatBotModel> chatBotModelList) {
        this.chatBotModelList = chatBotModelList;
    }

    @NonNull
    @Override
    public ChatBotAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ChatBotCardviewBinding binding = ChatBotCardviewBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatBotAdapter.ViewHolder holder, int position) {
        ChatBotModel chatBotModel = chatBotModelList.get(position);
        if (chatBotModel.isUser()){
            holder.binding.userMessage.setVisibility(View.VISIBLE);
            holder.binding.botMessage.setVisibility(View.GONE);
            holder.binding.userMessage.setText(chatBotModel.getMessage());
        }
        else {
            holder.binding.botMessage.setVisibility(View.VISIBLE);
            holder.binding.userMessage.setVisibility(View.GONE);
            holder.binding.botMessage.setText(chatBotModel.getMessage());

        }
    }

    @Override
    public int getItemCount() {
        return chatBotModelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private final ChatBotCardviewBinding binding;

        public ViewHolder(ChatBotCardviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
