package com.sundy.iman.helper;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sundy.iman.R;

import java.io.File;

/**
 * 图片显示Helper
 * Created by sundy on 17/9/21.
 */

public class ImageHelper {

    //显示本地图片
    public static void displayImageLocal(Context context, String localPath, ImageView imageView) {
        Glide.with(context)
                .load(localPath)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.default_image)
                .into(imageView);
    }

    //显示本地图片
    public static void displayImageLocal(Context context, File file, ImageView imageView) {
        Glide.with(context)
                .load(file)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.default_image)
                .into(imageView);
    }

    //显示网络图片
    public static void displayImageNet(Context context, String path, ImageView imageView) {
        displayImageNet(context, path, imageView, R.drawable.default_image);
    }

    //显示头像
    public static void displayPortrait(Context context, String path, ImageView imageView) {
        displayImageNet(context, path, imageView, R.mipmap.icon_default_portrait);
    }

    public static void displayImageNet(Context context, String path, ImageView imageView, @DrawableRes int defaultRes) {
        Glide.with(context)
                .load(path)
                .dontAnimate()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(defaultRes)
                .into(imageView);
    }

}
