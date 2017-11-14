package com.sundy.iman.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sundy.iman.R;
import com.sundy.iman.view.PayPwdInputView;

/**
 * Created by sundy on 17/11/14.
 */

public class PayBillPasswordDialog extends Dialog implements View.OnClickListener {

    private ImageView iv_close;
    private TextView tv_amount;
    private PayPwdInputView viewPwd;


    public PayBillPasswordDialog(@NonNull Context context) {
        super(context, R.style.MyDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_pay_bill_pwd);
        getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        iv_close = (ImageView) findViewById(R.id.iv_close);
        tv_amount = (TextView) findViewById(R.id.tv_amount);
        viewPwd = (PayPwdInputView) findViewById(R.id.viewPwd);
        viewPwd.setPwdListener(new PayPwdInputView.onPasswordListener() {
            @Override
            public void verifyPwd(String pwd) {
                if (onPwdListener != null) {
                    onPwdListener.onPwdVerify(pwd);
                }
            }
        });
        iv_close.setOnClickListener(this);

    }

    public void setAmount(String amount) {
        tv_amount.setText(amount);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_close:
                dismiss();
                break;
        }
    }

    public PayPwdInputView getViewPwd() {
        return viewPwd;
    }

    private OnPwdListener onPwdListener;

    public interface OnPwdListener {
        void onPwdVerify(String pwd);
    }

    public void setOnPwdListener(OnPwdListener onPwdListener) {
        this.onPwdListener = onPwdListener;
    }


}
