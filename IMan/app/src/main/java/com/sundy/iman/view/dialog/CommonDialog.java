package com.sundy.iman.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sundy.iman.R;

/**
 * Created by sundy on 17/9/14.
 */

public class CommonDialog extends Dialog implements View.OnClickListener {

    private TextView tv_dialog_title, tv_dialog_content;
    private Button dialog_cancel, dialog_ok;

    public CommonDialog(@NonNull Context context) {
        super(context, R.style.MyDialog);
        this.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_common);
        getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        tv_dialog_title = (TextView) findViewById(R.id.tv_dialog_title);
        tv_dialog_content = (TextView) findViewById(R.id.tv_dialog_content);
        dialog_cancel = (Button) findViewById(R.id.dialog_cancel);
        dialog_ok = (Button) findViewById(R.id.dialog_ok);

        dialog_cancel.setOnClickListener(this);
        dialog_ok.setOnClickListener(this);
    }

    public TextView getTitle() {
        return tv_dialog_title;
    }

    public TextView getContent() {
        return tv_dialog_content;
    }

    public Button getBtnOk() {
        return dialog_ok;
    }

    public Button getBtnCancel() {
        return dialog_cancel;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dialog_cancel:
                dismiss();
                break;
            case R.id.dialog_ok:
                if (onBtnClick != null)
                    onBtnClick.onOkClick();
                break;
        }
    }

    public OnBtnClick onBtnClick;

    public interface OnBtnClick {
        void onOkClick();
    }

    public void setOnBtnClick(OnBtnClick onBtnClick) {
        this.onBtnClick = onBtnClick;
    }

}
