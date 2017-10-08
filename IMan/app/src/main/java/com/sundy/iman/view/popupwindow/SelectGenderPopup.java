package com.sundy.iman.view.popupwindow;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sundy.iman.R;

/**
 * 性别选择PopupWindow
 * Created by sundy on 17/10/8.
 */

public class SelectGenderPopup extends PopupWindow implements View.OnClickListener {

    private Context mContext;
    private View view;
    private TextView tv_female, tv_male;
    private OnGenderClickListener onGenderClickListener;

    public SelectGenderPopup(Context context) {
        super(context);
        this.mContext = context;
        initPopup();
        setPopupWindow();
    }

    private void initPopup() {
        view = LayoutInflater.from(mContext).inflate(R.layout.popup_select_gender, null);

        TextView tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
        tv_female = (TextView) view.findViewById(R.id.tv_female);
        tv_male = (TextView) view.findViewById(R.id.tv_male);

        RelativeLayout rel_content = (RelativeLayout) view.findViewById(R.id.rel_content);

        tv_cancel.setOnClickListener(this);
        tv_female.setOnClickListener(this);
        tv_male.setOnClickListener(this);
        rel_content.setOnClickListener(this);

    }

    private void setPopupWindow() {
        this.setContentView(view);
        ColorDrawable dw = new ColorDrawable(0x00000000);
        this.setBackgroundDrawable(dw);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        this.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        this.setAnimationStyle(R.style.popWindow_anim_style);
    }

    //设置性别选定
    public void setGenderSelected(int gender) //1: male; 2:female
    {
        if (gender == 1) {
            tv_male.setSelected(true);
            tv_female.setSelected(false);
        } else {
            tv_male.setSelected(false);
            tv_female.setSelected(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                dismiss();
                break;
            case R.id.rel_content:
                dismiss();
                break;
            case R.id.tv_male:
                setGenderSelected(1);
                if (onGenderClickListener != null)
                    onGenderClickListener.onGenderClick(1);
                dismiss();
                break;
            case R.id.tv_female:
                setGenderSelected(2);
                if (onGenderClickListener != null)
                    onGenderClickListener.onGenderClick(2);
                dismiss();
                break;
        }
    }

    public interface OnGenderClickListener {
        void onGenderClick(int gender);
    }

    public void setOnGenderClickListener(OnGenderClickListener onGenderClickListener) {
        this.onGenderClickListener = onGenderClickListener;
    }

}
