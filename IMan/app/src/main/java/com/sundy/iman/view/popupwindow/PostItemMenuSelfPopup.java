package com.sundy.iman.view.popupwindow;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.sundy.iman.R;
import com.sundy.iman.utils.PopupWindowUtil;

/**
 * Created by sundy on 17/10/29.
 */

public class PostItemMenuSelfPopup extends PopupWindow implements View.OnClickListener {

    private Context mContext;
    private View view;

    private final int TYPE_SHARE_WECHAT = 1;
    private final int TYPE_SHARE_WX_CIRCLE = 2;
    private final int TYPE_SHARE_QQ = 3;
    private final int TYPE_SHARE_QQ_ZONE = 4;


    public PostItemMenuSelfPopup(Context context) {
        super(context);
        this.mContext = context;
        initPopup();
        setPopupWindow();
    }

    private void initPopup() {
        view = LayoutInflater.from(mContext).inflate(R.layout.popup_post_item_menu_self, null);
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        ImageView iv_wechat = (ImageView) view.findViewById(R.id.iv_wechat);
        ImageView iv_wx_circle = (ImageView) view.findViewById(R.id.iv_wx_circle);
        ImageView iv_qq = (ImageView) view.findViewById(R.id.iv_qq);
        ImageView iv_qq_zone = (ImageView) view.findViewById(R.id.iv_qq_zone);

        RelativeLayout rel_delete = (RelativeLayout) view.findViewById(R.id.rel_delete);

        iv_wechat.setOnClickListener(this);
        iv_wx_circle.setOnClickListener(this);
        iv_qq.setOnClickListener(this);
        iv_qq_zone.setOnClickListener(this);
        rel_delete.setOnClickListener(this);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    private void setPopupWindow() {
        ColorDrawable dw = new ColorDrawable(0x00000000);
        this.setBackgroundDrawable(dw);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.setTouchable(true);
        this.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        this.setHeight(view.getMeasuredHeight());

        this.setContentView(view);
    }

    public void showPopup(View anchorView) {
        int windowPos[] = PopupWindowUtil.calculatePopWindowPos(anchorView, view);
        int xOff = 20; // 可以自己调整偏移
        windowPos[0] -= xOff;
        this.showAtLocation(anchorView, Gravity.TOP | Gravity.START, windowPos[0], windowPos[1]);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rel_delete:
                if (onClickListener != null) {
                    onClickListener.deleteClick();
                }
                break;
            case R.id.iv_wechat:
                if (onClickListener != null) {
                    onClickListener.shareClick(TYPE_SHARE_WECHAT);
                }
                break;
            case R.id.iv_wx_circle:
                if (onClickListener != null) {
                    onClickListener.shareClick(TYPE_SHARE_WX_CIRCLE);
                }
                break;
            case R.id.iv_qq:
                if (onClickListener != null) {
                    onClickListener.shareClick(TYPE_SHARE_QQ);
                }
                break;
            case R.id.iv_qq_zone:
                if (onClickListener != null) {
                    onClickListener.shareClick(TYPE_SHARE_QQ_ZONE);
                }
                break;
        }
    }

    public OnClickListener onClickListener;

    public interface OnClickListener {
        void deleteClick();

        void shareClick(int type);

    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

}