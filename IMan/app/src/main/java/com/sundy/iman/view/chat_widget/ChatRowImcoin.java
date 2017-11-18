package com.sundy.iman.view.chat_widget;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.sundy.iman.R;
import com.sundy.iman.greendao.ImUserInfo;
import com.sundy.iman.helper.DbHelper;
import com.sundy.iman.helper.UIHelper;
import com.sundy.iman.ui.activity.SendImcoinInfoActivity;

/**
 * Created by sundy on 17/11/10.
 */

public class ChatRowImcoin extends EaseChatRow {

    private TextView tv_amount;
    private TextView tv_note;

    public ChatRowImcoin(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        String type = message.getStringAttribute(EaseConstant.CONS_ATTR_TYPE, "");
        if (!TextUtils.isEmpty(type) && type.equals("imcoin")) {
            inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ?
                    R.layout.ease_row_received_imcoin : R.layout.ease_row_sent_imcoin, this);
        }
    }

    @Override
    protected void onFindViewById() {
        tv_amount = (TextView) findViewById(R.id.tv_amount);
        tv_note = (TextView) findViewById(R.id.tv_note);
    }

    @Override
    protected void onUpdateView() {
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onSetUpView() {
        String amount = message.getStringAttribute(EaseConstant.CONS_ATTR_IMCOIN, "");
        double amountDou = Double.parseDouble(amount);
        String value = String.format("%.2f", amountDou);
        tv_amount.setText(value);

        boolean isReceived = (message.direct() == EMMessage.Direct.RECEIVE);
        if (isReceived)
            tv_note.setText(context.getString(R.string.received));
        else
            tv_note.setText(context.getString(R.string.sent));

        handleTextMessage();
    }

    protected void handleTextMessage() {
        if (message.direct() == EMMessage.Direct.SEND) {
            setMessageSendCallback();
        }
    }

    @Override
    protected void onBubbleClick() {
        boolean isReceived = (message.direct() == EMMessage.Direct.RECEIVE);
        String amount = message.getStringAttribute(EaseConstant.CONS_ATTR_IMCOIN, "");
        String hxId = message.getUserName();
        ImUserInfo imUserInfo = DbHelper.getInstance().getUserInfoByHxId(hxId);
        if (isReceived) { //已接收
            goSendImcoinInfo(amount, imUserInfo.getUsername(), 1);
        } else { //已发送
            goSendImcoinInfo(amount, imUserInfo.getUsername(), 2);
        }
    }

    private void goSendImcoinInfo(String amount, String username, int type) {
        Bundle bundle = new Bundle();
        bundle.putString("amount", amount);
        bundle.putString("username", username);
        bundle.putInt("type", type);
        UIHelper.jump((Activity) context, SendImcoinInfoActivity.class, bundle);
    }

}
