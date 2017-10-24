package com.sundy.iman.view.popupwindow;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sundy.iman.R;
import com.sundy.iman.utils.SizeUtils;

/**
 * Created by sundy on 17/10/24.
 */

public class SelectExpiryTimePopup extends PopupWindow implements View.OnClickListener {

    private Context mContext;
    private View view;
    private TextView tv_hour_48;
    private TextView tv_hour_24;
    private TextView tv_hour_12;
    private TextView tv_hour_6;
    private TextView tv_hour_2;

    public SelectExpiryTimePopup(Context context) {
        super(context);
        this.mContext = context;
        initPopup();
        setPopupWindow();
    }

    private void initPopup() {
        view = LayoutInflater.from(mContext).inflate(R.layout.popup_select_expiry_time, null);

        TextView tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
        tv_hour_48 = (TextView) view.findViewById(R.id.tv_hour_48);
        tv_hour_24 = (TextView) view.findViewById(R.id.tv_hour_24);
        tv_hour_12 = (TextView) view.findViewById(R.id.tv_hour_12);
        tv_hour_6 = (TextView) view.findViewById(R.id.tv_hour_6);
        tv_hour_2 = (TextView) view.findViewById(R.id.tv_hour_2);

        LinearLayout rel_content = (LinearLayout) view.findViewById(R.id.rel_content);

        tv_hour_48.setOnClickListener(this);
        tv_hour_24.setOnClickListener(this);
        tv_hour_12.setOnClickListener(this);
        tv_hour_6.setOnClickListener(this);
        tv_hour_2.setOnClickListener(this);

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
            case R.id.tv_hour_48:
                if (onClickListener != null)
                    onClickListener.onTimeSelected(48, tv_hour_48.getText().toString().trim());
                break;
            case R.id.tv_hour_24:
                if (onClickListener != null)
                    onClickListener.onTimeSelected(24, tv_hour_24.getText().toString().trim());
                break;
            case R.id.tv_hour_12:
                if (onClickListener != null)
                    onClickListener.onTimeSelected(12, tv_hour_12.getText().toString().trim());
                break;
            case R.id.tv_hour_6:
                if (onClickListener != null)
                    onClickListener.onTimeSelected(6, tv_hour_6.getText().toString().trim());
                break;
            case R.id.tv_hour_2:
                if (onClickListener != null)
                    onClickListener.onTimeSelected(2, tv_hour_2.getText().toString().trim());
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
        void onTimeSelected(int hours, String name);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

}
