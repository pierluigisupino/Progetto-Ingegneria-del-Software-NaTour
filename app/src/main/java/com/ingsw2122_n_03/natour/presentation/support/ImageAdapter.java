package com.ingsw2122_n_03.natour.presentation.support;

import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ingsw2122_n_03.natour.R;

import java.util.List;


public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private final List<byte[]> bytes;
    private boolean wantsToDelete;

    public ImageAdapter(List<byte[]> bitmaps, Boolean wantsToDelete) {
        this.bytes = bitmaps;
        this.wantsToDelete = wantsToDelete;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ImageViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_photo,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        holder.setBitmap(bytes.get(position));
        holder.deleteButton.setOnClickListener(view -> {
            int pos = holder.getAdapterPosition();
            bytes.remove(pos);
            notifyItemRemoved(pos);
        });
    }

    @Override
    public int getItemCount() {
        return bytes.size();
    }

    protected class ImageViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        ImageButton deleteButton;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            deleteButton = itemView.findViewById(R.id.deleteButton);

            if(wantsToDelete){
                deleteButton.setClickable(true);
                deleteButton.setVisibility(View.VISIBLE);
            }

        }

        private void setBitmap(byte[] bytes){
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
        }

    }

}
