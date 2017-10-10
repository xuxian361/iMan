package com.sundy.iman.view;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sundy.iman.R;
import com.sundy.iman.interfaces.OnTitleBarClickListener;

import butterknife.OnClick;

/**
 * 自定义Title Bar View
 * Created by sundy on 17/9/27.
 */

public class TitleBarView extends LinearLayout implements View.OnClickListener {

    private ImageView ivBack;
    private TextView tvLeft;
    private TextView tvTitle;
    private TextView tvRight;
    private ImageView ivRight;
    private RelativeLayout relTitleBar;

    private OnTitleBarClickListener clickListener;

    public TitleBarView(Context context) {
        this(context, null, 0);
    }

    public TitleBarView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_title_bar, this, true);
        ivBack = (ImageView) this.findViewById(R.id.iv_back);
        tvLeft = (TextView) this.findViewById(R.id.tv_left);
        tvTitle = (TextView) this.findViewById(R.id.tv_title);
        tvRight = (TextView) this.findViewById(R.id.tv_right);
        ivRight = (ImageView) this.findViewById(R.id.iv_right);
        relTitleBar = (RelativeLayout) this.findViewById(R.id.rel_title_bar);

        ivBack.setOnClickListener(this);
        tvLeft.setOnClickListener(this);
        tvTitle.setOnClickListener(this);
        tvRight.setOnClickListener(this);
        ivRight.setOnClickListener(this);
    }

    //Title Bar: 返回模式
    public void setBackMode() {
        setLeftIvVisibility(View.VISIBLE);
        setLeftTvTextVisibility(View.GONE);
        setTitleTvVisibility(View.GONE);
        setRightTvVisibility(View.GONE);
        setRightIvVisibility(View.GONE);
    }

    //Title Bar: 返回模式
    public void setBackMode(String title) {
        setLeftIvVisibility(View.VISIBLE);
        setLeftTvTextVisibility(View.GONE);
        setTitleTvVisibility(View.VISIBLE);
        setTitleTvText(title);
        setRightTvVisibility(View.GONE);
        setRightIvVisibility(View.GONE);
    }

    //--------------Bar Bg------------------
    public void setBgColor(int color) {
        relTitleBar.setBackgroundColor(color);
    }

    public void setBgColor(String color) {
        relTitleBar.setBackgroundColor(Color.parseColor(color));
    }

    //--------------Left ImageView------------------
    public void setLeftIvBg(int resourceBg) {
        ivBack.setImageResource(resourceBg);
    }

    public void setLeftIvVisibility(int visibility) {
        ivBack.setVisibility(visibility);
    }

    //--------------Left TextView------------------
    public void setLeftTvText(String text) {
        tvLeft.setText(text);
    }

    public void setLeftTvTextColor(int color) {
        tvLeft.setTextColor(color);
    }

    public void setLeftTvTextColor(String color) {
        tvLeft.setTextColor(Color.parseColor(color));
    }

    public void setLeftTvTextSize(int textSize) {
        tvLeft.setTextSize(textSize);
    }

    public void setLeftTvTextVisibility(int visibility) {
        tvLeft.setVisibility(visibility);
    }

    //--------------Title TextView------------------
    public void setTitleTvText(String text) {
        tvTitle.setText(text);
    }

    public void setTitleTvTextColor(int color) {
        tvTitle.setTextColor(color);
    }

    public void setTitleTvTextColor(String color) {
        tvTitle.setTextColor(Color.parseColor(color));
    }

    public void setTitleTvVisibility(int visibility) {
        tvTitle.setVisibility(visibility);
    }

    //--------------Right TextView------------------
    public void setRightTvText(String text) {
        tvRight.setText(text);
    }

    public void setRightTvTextSize(int textSize) {
        tvRight.setTextSize(textSize);
    }

    public void setRightTvTextColor(int color) {
        tvRight.setTextColor(color);
    }

    public void setRightTvTextColor(String color) {
        tvRight.setTextColor(Color.parseColor(color));
    }

    public void setRightTvVisibility(int visibility) {
        tvRight.setVisibility(visibility);
    }

    //--------------Right ImageView------------------
    public void setRightIvBg(int resourceBg) {
        ivRight.setImageResource(resourceBg);
    }

    public void setRightIvVisibility(int visibility) {
        ivRight.setVisibility(visibility);
    }

    @OnClick
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                if (clickListener != null)
                    clickListener.onLeftImgClick();
                break;
            case R.id.tv_left:
                if (clickListener != null)
                    clickListener.onLeftTxtClick();
                break;
            case R.id.tv_title:
                if (clickListener != null)
                    clickListener.onTitleClick();
                break;
            case R.id.tv_right:
                if (clickListener != null)
                    clickListener.onRightTxtClick();
                break;
            case R.id.iv_right:
                if (clickListener != null)
                    clickListener.onRightImgClick();
                break;
            case R.id.rel_title_bar:
                break;
        }
    }

    public void setOnClickListener(OnTitleBarClickListener listener) {
        this.clickListener = listener;
    }
}
