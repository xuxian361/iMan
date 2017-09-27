package com.sundy.iman.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sundy.iman.R;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 自定义Title Bar View
 * Created by sundy on 17/9/27.
 */

public class TitleBarView extends LinearLayout {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_left)
    TextView tvLeft;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.rel_title_bar)
    RelativeLayout relTitleBar;

    private OnClickListener clickListener;

    public TitleBarView(Context context) {
        this(context, null, 0);
    }

    public TitleBarView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView();
        obtainAttributes(context, attrs);

    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_title_bar, this, true);
    }

    private void obtainAttributes(Context context, AttributeSet attrs) {

    }

    @OnClick({R.id.iv_back, R.id.tv_left, R.id.tv_title, R.id.tv_right, R.id.iv_right, R.id.rel_title_bar})
    public void onViewClicked(View view) {
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

    public interface OnClickListener {
        void onLeftImgClick();

        void onLeftTxtClick();

        void onRightImgClick();

        void onRightTxtClick();

        void onTitleClick();
    }

    public void setOnClickListener(OnClickListener listener) {
        this.clickListener = listener;
    }
}
