package com.sundy.iman.view.popupwindow;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.sundy.iman.R;

/**
 * Created by sundy on 17/10/23.
 */

public class CommunityMenuPopup extends PopupWindow implements View.OnClickListener {

    private Context mContext;
    private View view;

    public CommunityMenuPopup(Context context) {
        super(context);
        this.mContext = context;
        initPopup();
        setPopupWindow();
    }

    private void initPopup() {
        view = LayoutInflater.from(mContext).inflate(R.layout.popup_community_menu, null);

        ImageView iv_close = (ImageView) view.findViewById(R.id.iv_close);
        LinearLayout ll_post_msg = (LinearLayout) view.findViewById(R.id.ll_post_msg);
        LinearLayout ll_community_info = (LinearLayout) view.findViewById(R.id.ll_community_info);
        LinearLayout ll_share_community = (LinearLayout) view.findViewById(R.id.ll_share_community);
        LinearLayout ll_quit_community = (LinearLayout) view.findViewById(R.id.ll_quit_community);

        iv_close.setOnClickListener(this);
        ll_post_msg.setOnClickListener(this);
        ll_community_info.setOnClickListener(this);
        ll_share_community.setOnClickListener(this);
        ll_quit_community.setOnClickListener(this);

    }

    private void setPopupWindow() {
        this.setContentView(view);
        ColorDrawable dw = new ColorDrawable(0x00000000);
        this.setBackgroundDrawable(dw);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        this.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        this.setAnimationStyle(R.style.popWindow_anim_style_top_in_out);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_post_msg:
                if (onClickListener != null)
                    onClickListener.postClick();
                break;
            case R.id.ll_community_info:
                if (onClickListener != null)
                    onClickListener.infoClick();
                break;
            case R.id.ll_share_community:
                if (onClickListener != null)
                    onClickListener.shareClick();
                break;
            case R.id.ll_quit_community:
                if (onClickListener != null)
                    onClickListener.quitClick();
                break;
            case R.id.iv_close:
                dismiss();
                break;
        }
    }

    public OnClickListener onClickListener;

    public interface OnClickListener {
        void quitClick();

        void postClick();

        void infoClick();

        void shareClick();
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

}
