package com.sundy.iman.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.orhanobut.logger.Logger;
import com.sundy.iman.R;
import com.sundy.iman.entity.MsgEvent;
import com.sundy.iman.entity.TabEntity;
import com.sundy.iman.helper.ChatHelper;
import com.sundy.iman.paperdb.PaperUtils;
import com.sundy.iman.ui.activity.MainActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by sundy on 17/9/27.
 */

public class MainFragment extends BaseFragment {

    @BindView(R.id.frameMain)
    FrameLayout frameMain;
    @BindView(R.id.view_menu)
    CommonTabLayout viewMenu;
    Unbinder unbinder;
    private String[] mTitles;
    private int[] mIconUnSelectIds = {
            R.mipmap.icons_msg_uncheck,
            R.mipmap.icons_me_uncheck};
    private int[] mIconSelectIds = {
            R.mipmap.icons_msg_checked,
            R.mipmap.icons_me_checked};
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();

    private Fragment msgFragment, meFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        unbinder = ButterKnife.bind(this, view);

        EventBus.getDefault().register(this);
        initFragment();
        initMenuTab();
        switchContent(msgFragment);
        return view;
    }

    private void initFragment() {
        msgFragment = new MsgFragment();
        meFragment = new MeFragment();
    }

    private void initMenuTab() {
        mTitles = new String[]{getString(R.string.message), getString(R.string.me)};

        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnSelectIds[i]));
        }

        viewMenu.setTabData(mTabEntities);
        viewMenu.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                Logger.e("----->position = " + position);
                switch (position) {
                    case 0:
                        switchContent(msgFragment);
                        break;
                    case 1:
                        switchContent(meFragment);
                        break;
                }
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
    }

    public void switchContent(Fragment fragment) {
        mCallback.switchContent(fragment, R.id.frameMain);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MsgEvent event) {
        if (event != null) {
            String msg = event.getMsg();
            switch (msg) {
                case MsgEvent.EVENT_UPDATE_UNREAD_MSG_COUNT:
                    updateUnreadMsgCount();
                    break;
            }
        }
    }

    //更新未读消息数
    private void updateUnreadMsgCount() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int count = getUnreadMsgCountTotal();
                Logger.e("------->更新未读消息数 ：" + count);
                if (count > 0) {
                    viewMenu.showMsg(0, count);
                    viewMenu.setMsgMargin(0, -5, 5);
                } else {
                    viewMenu.hideMsg(0);

                }

                //更新MsgFragment 的消息列表
                updateMsgList();
            }
        });
    }

    //更新消息列表
    private void updateMsgList() {
        try {
            if (MainActivity.mContent == null)
                return;
            if (MainActivity.mContent instanceof MsgFragment) {
                ((MsgFragment) msgFragment).refresh();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * get unread message count
     *
     * @return
     */
    public int getUnreadMsgCountTotal() {
        return EMClient.getInstance().chatManager().getUnreadMsgsCount();
    }

    EMMessageListener messageListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            // notify new message
            for (EMMessage message : messages) {
                ChatHelper.getInstance().getNotifier().onNewMsg(message);
            }
            updateUnreadMsgCount();
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            //red packet code : 处理红包回执透传消息
            for (EMMessage message : messages) {
                EMCmdMessageBody cmdMsgBody = (EMCmdMessageBody) message.getBody();
                final String action = cmdMsgBody.action();//获取自定义action
            }
            //end of red packet code
            updateUnreadMsgCount();
        }

        @Override
        public void onMessageRead(List<EMMessage> messages) {
        }

        @Override
        public void onMessageDelivered(List<EMMessage> message) {
        }

        @Override
        public void onMessageRecalled(List<EMMessage> messages) {
            updateUnreadMsgCount();
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (PaperUtils.isLogin() && ChatHelper.getInstance().isLoggedIn()) {
            updateUnreadMsgCount();

            // unregister this event listener when this activity enters the
            // background
            ChatHelper sdkHelper = ChatHelper.getInstance();
            sdkHelper.pushActivity(mContext);

            EMClient.getInstance().chatManager().addMessageListener(messageListener);
        } else {
            viewMenu.hideMsg(0);
        }
    }

    @Override
    public void onStop() {
        if (PaperUtils.isLogin() && ChatHelper.getInstance().isLoggedIn()) {
            EMClient.getInstance().chatManager().removeMessageListener(messageListener);
            ChatHelper sdkHelper = ChatHelper.getInstance();
            sdkHelper.popActivity(mContext);
        }
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
