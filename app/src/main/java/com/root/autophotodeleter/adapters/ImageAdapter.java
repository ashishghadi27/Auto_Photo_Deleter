package com.root.autophotodeleter.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.root.autophotodeleter.R;
import com.root.autophotodeleter.vo.FileInfoVO;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder>{

    private List<FileInfoVO> fileInfoList;
    private Context context;

    public ImageAdapter(List<FileInfoVO> fileInfoList, Context context) {
        this.fileInfoList = fileInfoList;
        this.context = context;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView photo;
        private LinearLayout imageContainer;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            photo = itemView.findViewById(R.id.photo);
            imageContainer = itemView.findViewById(R.id.imageContainer);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.imageview_template, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        FileInfoVO fileInfo = fileInfoList.get(position);
        Glide.with(context)
                .load(Uri.fromFile(fileInfo.getFile()))
                .into(holder.photo);

    }

    @Override
    public int getItemCount() {
        return fileInfoList.size();
    }

}
