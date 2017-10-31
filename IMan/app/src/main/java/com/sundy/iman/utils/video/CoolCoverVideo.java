package com.sundy.iman.utils.video;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer;
import com.sundy.iman.R;
import com.sundy.iman.helper.ImageHelper;

/**
 * Created by sundy on 17/10/31.
 */

public class CoolCoverVideo extends StandardGSYVideoPlayer {

    ImageView mCoverImage;

    String mUrl;

    int mDefaultRes;

    public CoolCoverVideo(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public CoolCoverVideo(Context context) {
        super(context);
    }

    public CoolCoverVideo(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        mCoverImage = (ImageView) findViewById(R.id.thumbImage);
    }

    @Override
    public int getLayoutId() {
        return R.layout.video_layout_cover;
    }

    public void loadCoverImage(String url, int res) {
        mUrl = url;
        mDefaultRes = res;
        ImageHelper.displayImageNet(getContext().getApplicationContext(),url,mCoverImage);
    }

    @Override
    public GSYBaseVideoPlayer startWindowFullscreen(Context context, boolean actionBar, boolean statusBar) {
        GSYBaseVideoPlayer gsyBaseVideoPlayer = super.startWindowFullscreen(context, actionBar, statusBar);
        CoolCoverVideo sampleCoverVideo = (CoolCoverVideo) gsyBaseVideoPlayer;
        sampleCoverVideo.loadCoverImage(mUrl, mDefaultRes);
        return gsyBaseVideoPlayer;
    }
}

