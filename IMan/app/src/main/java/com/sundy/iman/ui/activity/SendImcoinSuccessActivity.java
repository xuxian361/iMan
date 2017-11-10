package com.sundy.iman.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.sundy.iman.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sundy on 17/11/10.
 */

public class SendImcoinSuccessActivity extends BaseActivity {

    @BindView(R.id.tv_username)
    TextView tvUsername;
    @BindView(R.id.tv_amount)
    TextView tvAmount;
    @BindView(R.id.btn_finish)
    TextView btnFinish;
    private String amount;
    private String username;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_send_imcoin_success);
        ButterKnife.bind(this);

        initData();
        init();

    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            amount = bundle.getString("amount");
            username = bundle.getString("username");
        }
    }

    private void init() {
        tvUsername.setText(username);
        tvAmount.setText(amount);
    }

    @OnClick(R.id.btn_finish)
    public void onViewClicked() {
        finish();
    }
}
