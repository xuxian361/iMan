package com.sundy.iman.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.sundy.iman.R;
import com.sundy.iman.interfaces.OnTitleBarClickListener;
import com.sundy.iman.view.TitleBarView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sundy on 17/11/10.
 */

public class SendImcoinInfoActivity extends BaseActivity {

    @BindView(R.id.title_bar)
    TitleBarView titleBar;
    @BindView(R.id.tv_username)
    TextView tvUsername;
    @BindView(R.id.tv_amount)
    TextView tvAmount;
    private int type; //type:1 - 已接收；2 - 已发送
    private String amount;
    private String username;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_send_imcoin_info);
        ButterKnife.bind(this);

        initTitle();
        initData();
        init();
    }

    private void initTitle() {
        titleBar.setBackMode(getString(R.string.transfer_info));
        titleBar.setOnClickListener(new OnTitleBarClickListener() {
            @Override
            public void onLeftImgClick() {
                finish();
            }

            @Override
            public void onLeftTxtClick() {

            }

            @Override
            public void onRightImgClick() {

            }

            @Override
            public void onRightTxtClick() {

            }

            @Override
            public void onTitleClick() {

            }
        });
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            amount = bundle.getString("amount");
            username = bundle.getString("username");
            type = bundle.getInt("type");
        }
    }

    private void init() {
        if (type == 1)
            tvUsername.setText(username + " " + getString(R.string.received));
        else if (type == 2)
            tvUsername.setText(username + " " + getString(R.string.sent));

        tvAmount.setText(amount);
    }

}
