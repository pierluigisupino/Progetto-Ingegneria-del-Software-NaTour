package com.ingsw2122_n_03.natour.presentation.support;
import com.ingsw2122_n_03.natour.R;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
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
    private final List<Bitmap> bitmaps;

    public ImageAdapter(TextView textView, List<Bitmap> bitmaps) {
        this.textView = textView;
        this.bitmaps = bitmaps;
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
        holder.setBitmap(bitmaps.get(position));
        holder.getDeleteButton().setOnClickListener(view -> {
            bitmaps.remove(holder.getAdapterPosition());
            notifyItemRemoved(holder.getAdapterPosition());
            setPhotoCount(holder);
        });
        setPhotoCount(holder);
    }

    @Override
    public int getItemCount() {
        return bitmaps.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        ImageButton deleteButton;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

        private void setBitmap(Bitmap bitmap){
            imageView.setImageBitmap(bitmap);
        }

        private ImageButton getDeleteButton(){
            return this.deleteButton;
        }
    }

    @SuppressLint("SetTextI18n")
    private void setPhotoCount(ImageViewHolder holder){
        if(bitmaps.size() == 0){
            textView.setText(holder.itemView.getContext().getString(R.string.no_photo_selected_text));
        }else{
            textView.setText(bitmaps.size() + holder.itemView.getContext().getString(R.string.photo_selected_text));
        }
    }
}
