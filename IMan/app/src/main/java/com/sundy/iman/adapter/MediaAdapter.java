package com.sundy.iman.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.sundy.iman.R;
import com.sundy.iman.entity.SelectMediaEntity;
import com.sundy.iman.helper.ImageHelper;
import com.sundy.iman.view.SquareItemLayout;

import java.util.ArrayList;


/**
 * Created by sundy on 17/10/24.
 */

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.PhotoViewHolder> {

    private ArrayList<SelectMediaEntity> selectedMediaEntities = new ArrayList<>();
    private LayoutInflater inflater;

    private Context mContext;

    public final static int TYPE_ADD = 1;
    final static int TYPE_PHOTO = 2;

    public int MAX = 1;

    public MediaAdapter(Context mContext, ArrayList<SelectMediaEntity> selectedMediaEntities) {
        this.selectedMediaEntities = selectedMediaEntities;
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
    }

    public MediaAdapter(Context mContext, ArrayList<SelectMediaEntity> selectedMediaEntities, int maxSize) {
        this.selectedMediaEntities = selectedMediaEntities;
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
        this.MAX = maxSize;
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        switch (viewType) {
            case TYPE_ADD:
                itemView = inflater.inflate(R.layout.item_media_add, parent, false);
                break;
            case TYPE_PHOTO:
                itemView = inflater.inflate(R.layout.item_media_picker, parent, false);
                break;
        }
        return new PhotoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PhotoViewHolder holder, final int position) {
        try {
            if (getItemViewType(position) == TYPE_PHOTO) {
                SelectMediaEntity selectImageEntity = selectedMediaEntities.get(position);
                if (selectImageEntity != null) {
                    ImageHelper.displayImageLocal(mContext, selectImageEntity.getLocalImagePath(), holder.ivPhoto);
                    String localVideoPath = selectImageEntity.getLocalVideoPath();
                    if (TextUtils.isEmpty(localVideoPath)) {
                        holder.iv_video.setVisibility(View.GONE);
                    } else {
                        holder.iv_video.setVisibility(View.VISIBLE);
                    }
                }
                holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (onItemClickListener != null) {
                            onItemClickListener.onImageClick(holder, position);
                        }
                    }
                });
                holder.vSelected.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (onItemClickListener != null) {
                            onItemClickListener.onDeleteClick(holder, position);
                        }
                    }
                });
            } else {
                holder.iv_add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (onItemClickListener != null) {
                            onItemClickListener.onAddClick(holder, position);
                        }
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        int count = selectedMediaEntities.size() + 1;
        if (count > MAX) {
            count = MAX;
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == selectedMediaEntities.size() && position != MAX) ? TYPE_ADD : TYPE_PHOTO;
    }

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivPhoto;
        public ImageView vSelected;
        public ImageView iv_add;
        public SquareItemLayout view_content;
        public ImageView iv_video;
        public NumberProgressBar v_progress;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            iv_add = (ImageView) itemView.findViewById(R.id.iv_add);
            ivPhoto = (ImageView) itemView.findViewById(R.id.iv_photo);
            vSelected = (ImageView) itemView.findViewById(R.id.v_selected);
            iv_video = (ImageView) itemView.findViewById(R.id.iv_video);
            view_content = (SquareItemLayout) itemView.findViewById(R.id.view_content);
            v_progress = (NumberProgressBar) itemView.findViewById(R.id.v_progress);
        }
    }

    public interface OnItemClickListener {
        void onAddClick(PhotoViewHolder holder, int position);

        void onDeleteClick(PhotoViewHolder holder, int position);

        void onImageClick(PhotoViewHolder holder, int position);

    }

    public OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


}