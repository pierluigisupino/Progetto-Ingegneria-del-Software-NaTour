package com.ingsw2122_n_03.natour.presentation.support;
import com.ingsw2122_n_03.natour.R;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;


public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private final TextView textView;
    private final List<byte[]> bytes;

    public ImageAdapter(TextView textView, List<byte[]> bitmaps) {
        this.textView = textView;
        this.bytes = bitmaps;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ImageViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.photo_item,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        holder.setBitmap(bytes.get(position));
        holder.getDeleteButton().setOnClickListener(view -> {
            int pos = holder.getAdapterPosition();
            bytes.remove(pos);
            notifyItemRemoved(pos);
            setPhotoCount(holder);
        });
        setPhotoCount(holder);
    }

    @Override
    public int getItemCount() {
        return bytes.size();
    }

    protected static class ImageViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        ImageButton deleteButton;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

        private void setBitmap(byte[] bytes){
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
        }

        private ImageButton getDeleteButton(){
            return this.deleteButton;
        }
    }

    @SuppressLint("SetTextI18n")
    private void setPhotoCount(ImageViewHolder holder){
        if(bytes.size() == 0){
            textView.setText(holder.itemView.getContext().getString(R.string.no_photo_selected_text));
        }else{
            textView.setText(bytes.size() + holder.itemView.getContext().getString(R.string.photo_selected_text));
        }
    }
}
