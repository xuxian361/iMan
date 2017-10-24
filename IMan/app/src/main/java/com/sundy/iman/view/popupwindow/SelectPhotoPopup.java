package com.sundy.iman.view.popupwindow;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.sundy.iman.R;

/**
 * Created by sundy on 17/10/7.
 */

public class SelectPhotoPopup extends PopupWindow implements View.OnClickListener {

    private Context mContext;
    private View view;

    public SelectPhotoPopup(Context context) {
        super(context);
        this.mContext = context;
        initPopup();
        setPopupWindow();
    }

    private void initPopup() {
        view = LayoutInflater.from(mContext).inflate(R.layout.popup_select_photo, null);

        TextView tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
        TextView tv_album = (TextView) view.findViewById(R.id.tv_album);
        TextView tv_camera = (TextView) view.findViewById(R.id.tv_camera);
        LinearLayout rel_content = (LinearLayout) view.findViewById(R.id.rel_content);

        tv_camera.setOnClickListener(this);
        tv_album.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
        rel_content.setOnClickListener(this);
    }

    private void setPopupWindow() {
        this.setContentView(view);
        ColorDrawable dw = new ColorDrawable(0x00000000);
        this.setBackgroundDrawable(dw);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        this.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        this.setAnimationStyle(R.style.popWindow_anim_style);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_camera:
                if (onClickListener != null)
                    onClickListener.clickCamera();
                Logger.i("Camera Click");
                break;
            case R.id.tv_album:
                if (onClickListener != null)
                    onClickListener.clickAlbum();
                Logger.i("Album Click");
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
        void clickCamera();

        void clickAlbum();
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

}
