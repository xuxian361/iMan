package com.sundy.iman.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hyphenate.chat.EMChatService;
import com.hyphenate.util.EMLog;

/**
 * Created by sundy on 17/11/9.
 */

public class StartServiceReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)
                && !intent.getAction().equals("android.intent.action.QUICKBOOT_POWERON")) {
            return;
        }
        EMLog.d("boot", "start IM service on boot");
        Intent startServiceIntent=new Intent(context, EMChatService.class);
        startServiceIntent.putExtra("reason", "boot");
        context.startService(startServiceIntent);
    }
}
