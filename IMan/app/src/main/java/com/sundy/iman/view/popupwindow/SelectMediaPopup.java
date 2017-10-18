package com.sundy.iman.view.popupwindow;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.sundy.iman.R;

/**
 * Created by sundy on 17/10/18.
 */

public class SelectMediaPopup extends PopupWindow implements View.OnClickListener {

    private Context mContext;
    private View view;

    public SelectMediaPopup(Context context) {
        super(context);
        this.mContext = context;
        initPopup();
        setPopupWindow();
    }

    private void initPopup() {
        view = LayoutInflater.from(mContext).inflate(R.layout.popup_select_media, null);

        TextView tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
        TextView tv_video = (TextView) view.findViewById(R.id.tv_video);
        TextView tv_photo = (TextView) view.findViewById(R.id.tv_photo);
        RelativeLayout rel_content = (RelativeLayout) view.findViewById(R.id.rel_content);

        tv_photo.setOnClickListener(this);
        tv_video.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
        rel_content.setOnClickListener(this);
    }

    private void setPopupWindow() {
        ColorDrawable dw = new ColorDrawable(0x00000000);
        this.setBackgroundDrawable(dw);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        this.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        this.setAnimationStyle(R.style.popWindow_anim_style);
        this.setContentView(view);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_video:
                if (onClickListener != null)
                    onClickListener.clickVideo();
                Logger.i("Video Click");
                break;
            case R.id.tv_photo:
                if (onClickListener != null)
                    onClickListener.clickPhoto();
                Logger.i("Photo Click");
                break;
            case R.id.tv_cancel:
                dismiss();
                break;
            case R.id.rel_content:
                dismiss();
                break;
        }
    }

    public OnClickListener onClickListener;

    public interface OnClickListener {
        void clickPhoto();

        void clickVideo();
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
}
