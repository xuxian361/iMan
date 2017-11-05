package com.sundy.iman.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.implments.SwipeItemRecyclerMangerImpl;
import com.daimajia.swipe.interfaces.SwipeAdapterInterface;
import com.daimajia.swipe.interfaces.SwipeItemMangerInterface;
import com.daimajia.swipe.util.Attributes;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.model.EaseAtMessageHelper;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.EaseSmileUtils;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.widget.EaseImageView;
import com.hyphenate.util.DateUtils;
import com.orhanobut.logger.Logger;
import com.sundy.iman.R;
import com.sundy.iman.config.Constants;
import com.sundy.iman.entity.GetHomeListEntity;
import com.sundy.iman.entity.LocationEntity;
import com.sundy.iman.entity.MsgEvent;
import com.sundy.iman.greendao.ImUserInfo;
import com.sundy.iman.helper.DbHelper;
import com.sundy.iman.helper.UIHelper;
import com.sundy.iman.net.ParamHelper;
import com.sundy.iman.net.RetrofitCallback;
import com.sundy.iman.net.RetrofitHelper;
import com.sundy.iman.paperdb.LocationPaper;
import com.sundy.iman.paperdb.PaperUtils;
import com.sundy.iman.ui.activity.AddCommunityActivity;
import com.sundy.iman.ui.activity.ChatActivity;
import com.sundy.iman.ui.activity.ContactInfoActivity;
import com.sundy.iman.view.dialog.CommonDialog;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionNo;
import com.yanzhenjie.permission.PermissionYes;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import lombok.Data;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by sundy on 17/9/14.
 */

public class MsgFragment extends BaseFragment {

    private static final int REQUEST_CODE_PERMISSION_LOCATION = 100;
    private final static int MSG_REFRESH = 2;

    //声明AMapLocationClient类对象
    public AMapLocationClient locationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption locationOption = null;
    Unbinder unbinder;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.collapsing)
    CollapsingToolbarLayout collapsing;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.scrollView)
    NestedScrollView scrollView;
    @BindView(R.id.btn_add)
    ImageView btnAdd;
    @BindView(R.id.rv_msg)
    RecyclerView rvMsg;
    private LayoutInflater mInflate;

    protected boolean hidden;
    protected boolean isConflict;
    private ConversationAdapter conversationAdapter;
    private List<EMConversation> conversationList = new ArrayList();

    protected Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    onConnectionDisconnected();
                    break;
                case 1:
                    onConnectionConnected();
                    break;
                case MSG_REFRESH: {
                    conversationList.clear();
                    conversationList.addAll(loadConversationList());
                    conversationAdapter.setNewData(conversationList);
                    conversationAdapter.notifyDataSetChanged();
                    break;
                }
                default:
                    break;
            }
        }
    };

    /**
     * connected to server
     */
    protected void onConnectionConnected() {

    }

    /**
     * disconnected with server
     */
    protected void onConnectionDisconnected() {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false))
            return;
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mInflate = inflater;
        View view = inflater.inflate(R.layout.fragment_msg, container, false);
        unbinder = ButterKnife.bind(this, view);

        init();
        setUpView();
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.hidden = hidden;
        if (!hidden && !isConflict) {
            refresh();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!hidden) {
            refresh();
        }
    }

    private void init() {
        rvMsg.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        conversationAdapter = new ConversationAdapter(R.layout.item_conversation, conversationList);
        View headerView = mInflate.inflate(R.layout.layout_msg_header_nearby, null);  //SUNDY
        conversationAdapter.addHeaderView(headerView);
        rvMsg.setAdapter(conversationAdapter);
    }

    private void setUpView() {
        if (PaperUtils.isLogin()) {
            conversationList.addAll(loadConversationList());
            conversationAdapter.setNewData(conversationList);
            conversationAdapter.notifyDataSetChanged();

            EMClient.getInstance().addConnectionListener(connectionListener);
        }
    }

    protected EMConnectionListener connectionListener = new EMConnectionListener() {

        @Override
        public void onDisconnected(int error) {
            if (error == EMError.USER_REMOVED
                    || error == EMError.USER_LOGIN_ANOTHER_DEVICE
                    || error == EMError.SERVER_SERVICE_RESTRICTED
                    || error == EMError.USER_KICKED_BY_CHANGE_PASSWORD
                    || error == EMError.USER_KICKED_BY_OTHER_DEVICE) {
                isConflict = true;
            } else {
                handler.sendEmptyMessage(0);
            }
        }

        @Override
        public void onConnected() {
            handler.sendEmptyMessage(1);
        }
    };

    /**
     * refresh ui
     */
    public void refresh() {
        if (PaperUtils.isLogin()) {
            if (!handler.hasMessages(MSG_REFRESH)) {
                handler.sendEmptyMessage(MSG_REFRESH);
            }
        }
    }

    /**
     * load conversation list
     *
     * @return
     */
    protected List<EMConversation> loadConversationList() {
        // get all conversations
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
        List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
        /**
         * lastMsgTime will change if there is new message during sorting
         * so use synchronized to make sure timestamp of last message won't change.
         */
        synchronized (conversations) {
            for (EMConversation conversation : conversations.values()) {
                if (conversation.getAllMessages().size() != 0) {
                    sortList.add(new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(), conversation));
                }
            }
        }
        try {
            // Internal is TimSort algorithm, has bug
            sortConversationByLastChatTime(sortList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<EMConversation> list = new ArrayList<EMConversation>();
        for (Pair<Long, EMConversation> sortItem : sortList) {
            list.add(sortItem.second);
        }
        return list;
    }

    /**
     * sort conversations according time stamp of last message
     *
     * @param conversationList
     */
    private void sortConversationByLastChatTime(List<Pair<Long, EMConversation>> conversationList) {
        Collections.sort(conversationList, new Comparator<Pair<Long, EMConversation>>() {
            @Override
            public int compare(final Pair<Long, EMConversation> con1, final Pair<Long, EMConversation> con2) {
                if (con1.first.equals(con2.first)) {
                    return 0;
                } else if (con2.first.longValue() > con1.first.longValue()) {
                    return 1;
                } else {
                    return -1;
                }
            }

        });
    }


    @Override
    public void onStart() {
        super.onStart();
        AndPermission.with(this)
                .requestCode(REQUEST_CODE_PERMISSION_LOCATION)
                .permission(Permission.LOCATION)
                .callback(this)
                // rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框；
                // 这样避免用户勾选不再提示，导致以后无法申请权限。
                // 你也可以不设置。
                .rationale(new RationaleListener() {
                    @Override
                    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                        // 这里的对话框可以自定义，只要调用rationale.resume()就可以继续申请。
                        AndPermission.rationaleDialog(mContext, rationale).show();
                    }
                })
                .start();
    }

    @PermissionYes(REQUEST_CODE_PERMISSION_LOCATION)
    private void getPermissionYes(@NonNull List<String> grantedPermissions) {
        Logger.e("位置权限申请成功!");
        initLocation();
        startLocation();
    }

    @PermissionNo(REQUEST_CODE_PERMISSION_LOCATION)
    private void getPermissionNo(@NonNull List<String> deniedPermissions) {
        Logger.e("位置权限申请失败!");
        //获取默认定位
        LocationEntity locationEntity = LocationPaper.getLocation();
    }

    //初始化定位
    private void initLocation() {
        //初始化client
        locationClient = new AMapLocationClient(mContext);
        locationOption = getDefaultOption();
        //设置定位参数
        locationClient.setLocationOption(locationOption);
        // 设置定位监听
        locationClient.setLocationListener(locationListener);
    }

    //默认的定位参数
    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        return mOption;
    }

    //开始定位
    private void startLocation() {
        if (locationClient != null)
            locationClient.startLocation();
    }

    //停止定位
    private void stopLocation() {
        if (locationClient != null)
            locationClient.stopLocation();
    }

    //销毁定位
    private void destroyLocation() {
        if (null != locationClient) {
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }

    //定位监听
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            if (null != location) {
                //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
                if (location.getErrorCode() == 0) {
                    Logger.i("定位成功");
                    String address = location.getAddress();
                    if (!TextUtils.isEmpty(address)) {
                        Logger.i("获取定位信息成功");
                        stopLocation();
                        saveLocation(location);
                        getHomePostList(location.getLatitude(), location.getLongitude());
                    } else {
                        Logger.w("获取定位信息失败");
                    }
                } else {
                    //定位失败
                    Logger.e("定位失败");
                }
            } else {
                Logger.e("定位失败，loc is null");
                //获取默认定位
                LocationEntity locationEntity = LocationPaper.getLocation();
            }
        }
    };

    //保存定位信息
    private void saveLocation(AMapLocation location) {
        LocationEntity locationEntity = new LocationEntity();
        locationEntity.setAddress(location.getAddress());
        locationEntity.setCountry(location.getCountry());
        locationEntity.setProvince(location.getProvince());
        locationEntity.setCity(location.getCity());
        locationEntity.setDistrict(location.getDistrict());
        locationEntity.setLat(location.getLatitude());
        locationEntity.setLng(location.getLongitude());

        LocationPaper.saveLocation(locationEntity);
    }

    //首页列表
    private void getHomePostList(double latitude, double longitude) {
        Map<String, String> param = new HashMap<>();
        param.put("mid", PaperUtils.getMId());
        param.put("session_key", PaperUtils.getSessionKey());
        param.put("latitude", latitude + "");
        param.put("longitude", longitude + "");
        Call<GetHomeListEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .getHomeList(ParamHelper.formatData(param));
        call.enqueue(new RetrofitCallback<GetHomeListEntity>() {
            @Override
            public void onSuccess(Call<GetHomeListEntity> call, Response<GetHomeListEntity> response) {
                GetHomeListEntity getHomeListEntity = response.body();
                if (getHomeListEntity != null) {
                    int code = getHomeListEntity.getCode();
                    String msg = getHomeListEntity.getMsg();
                    if (code == Constants.CODE_SUCCESS) {

                    }
                }
            }

            @Override
            public void onAfter() {

            }

            @Override
            public void onFailure(Call<GetHomeListEntity> call, Throwable t) {

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocation();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        destroyLocation();
        unbinder.unbind();
        EMClient.getInstance().removeConnectionListener(connectionListener);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (isConflict) {
            outState.putBoolean("isConflict", true);
        }
    }

    @OnClick(R.id.btn_add)
    public void onViewClicked() {
        goAddCommunity();
    }

    //跳转添加社区
    private void goAddCommunity() {
        UIHelper.jump(mContext, AddCommunityActivity.class);
    }

    private class ConversationAdapter extends BaseQuickAdapter<EMConversation, BaseViewHolder>
            implements SwipeItemMangerInterface, SwipeAdapterInterface, View.OnClickListener {

        public SwipeItemRecyclerMangerImpl mItemManger = new SwipeItemRecyclerMangerImpl(this);

        public ConversationAdapter(@LayoutRes int layoutResId, @Nullable List<EMConversation> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, EMConversation conversation) {
            RelativeLayout rel_item = helper.getView(R.id.rel_item);
            RelativeLayout avatar_container = helper.getView(R.id.avatar_container);
            EaseImageView avatar = helper.getView(R.id.avatar);
            TextView unread_msg_number = helper.getView(R.id.unread_msg_number);
            TextView name = helper.getView(R.id.name);
            TextView time = helper.getView(R.id.time);
            ImageView msg_state = helper.getView(R.id.msg_state);
            TextView mentioned = helper.getView(R.id.mentioned);
            TextView message = helper.getView(R.id.message);

            // get conversation
            if (conversation != null) {
                String hxId = conversation.conversationId();

                EaseUserUtils.setUserAvatar(getContext(), hxId, avatar);
                EaseUserUtils.setUserNick(hxId, name);
                mentioned.setVisibility(View.GONE);

                avatar.setShapeType(1);
                avatar.setBorderWidth(0);

                if (conversation.getUnreadMsgCount() > 0) {
                    // show unread message count
                    //unread_msg_number.setText(String.valueOf(conversation.getUnreadMsgCount()));
                    unread_msg_number.setVisibility(View.VISIBLE);
                } else {
                    unread_msg_number.setVisibility(View.INVISIBLE);
                }

                if (conversation.getAllMsgCount() != 0) {
                    // show the content of latest message
                    EMMessage lastMessage = conversation.getLastMessage();
                    message.setText(EaseSmileUtils.getSmiledText(getContext(), EaseCommonUtils.getMessageDigest(lastMessage, mContext)),
                            TextView.BufferType.SPANNABLE);
                    time.setText(DateUtils.getTimestampString(new Date(lastMessage.getMsgTime())));
                    if (lastMessage.direct() == EMMessage.Direct.SEND && lastMessage.status() == EMMessage.Status.FAIL) {
                        msg_state.setVisibility(View.VISIBLE);
                    } else {
                        msg_state.setVisibility(View.GONE);
                    }
                }
            }

            mItemManger.bindView(helper.getConvertView(), helper.getLayoutPosition());

            ItemData itemData = new ItemData();
            itemData.setPosition(helper.getLayoutPosition());
            itemData.setItem(conversation);

            helper.setOnClickListener(R.id.tv_item_del, this);
            helper.setTag(R.id.tv_item_del, R.id.item_tag, itemData);

            helper.setOnClickListener(R.id.ll_item, this);
            helper.setTag(R.id.ll_item, R.id.item_tag, itemData);

            helper.setOnClickListener(R.id.avatar_container, this);
            helper.setTag(R.id.avatar_container, R.id.item_tag, itemData);

        }

        @Override
        public int getSwipeLayoutResourceId(int position) {
            return R.id.swipe;
        }

        @Override
        public void openItem(int position) {
            mItemManger.openItem(position);
        }

        @Override
        public void closeItem(int position) {
            mItemManger.closeItem(position);
        }

        @Override
        public void closeAllExcept(SwipeLayout layout) {
            mItemManger.closeAllExcept(layout);
        }

        @Override
        public void closeAllItems() {
            mItemManger.closeAllItems();
        }

        @Override
        public List<Integer> getOpenItems() {
            return mItemManger.getOpenItems();
        }

        @Override
        public List<SwipeLayout> getOpenLayouts() {
            return mItemManger.getOpenLayouts();
        }

        @Override
        public void removeShownLayouts(SwipeLayout layout) {
            mItemManger.removeShownLayouts(layout);
        }

        @Override
        public boolean isOpen(int position) {
            return mItemManger.isOpen(position);
        }

        @Override
        public Attributes.Mode getMode() {
            return mItemManger.getMode();
        }

        @Override
        public void setMode(Attributes.Mode mode) {
            mItemManger.setMode(mode);
        }


        @Override
        public void onClick(View view) {
            ItemData itemData = (ItemData) view.getTag(R.id.item_tag);
            switch (view.getId()) {
                case R.id.tv_item_del:
                    Logger.e("----->删除Item");
                    //删除社区
                    if (itemData != null) {
                        showDeleteMsgDialog(itemData);
                    }
                    break;
                case R.id.ll_item:
                    Logger.e("----->点击Item");
                    if (isOpen(itemData.getPosition())) {
                        closeItem(itemData.getPosition());
                        return;
                    }
                    String hxId = itemData.getItem().conversationId();
                    if (!TextUtils.isEmpty(hxId)) {
                        ImUserInfo user = DbHelper.getInstance().getUserInfoByHxId(hxId);
                        if (user != null) {
                            goChat(user.getEasemob_account());
                        }
                    }
                    break;
                case R.id.avatar_container:
                    Logger.e("----->点击头像");
                    if (isOpen(itemData.getPosition())) {
                        closeItem(itemData.getPosition());
                        return;
                    }
                    String hxId2 = itemData.getItem().conversationId();
                    if (!TextUtils.isEmpty(hxId2)) {
                        ImUserInfo user = DbHelper.getInstance().getUserInfoByHxId(hxId2);
                        if (user != null) {
                            goUserDetail(user.getUserId());
                        }
                    }
                    break;
            }
        }

        @Data
        public class ItemData implements Serializable {

            private int position;
            private EMConversation item;

        }
    }

    //删除个人聊天消息
    private void showDeleteMsgDialog(final ConversationAdapter.ItemData itemData) {
        final CommonDialog dialog = new CommonDialog(mContext);
        dialog.getTitle().setVisibility(View.GONE);
        dialog.getContent().setText(getString(R.string.delete_conversation_messages_tips));
        dialog.getBtnOk().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                deleteConversation(itemData);
            }
        });
    }

    //删除聊天会话
    private void deleteConversation(ConversationAdapter.ItemData itemData) {
        EMConversation tobeDeleteCons = itemData.getItem();
        if (tobeDeleteCons == null) {
            return;
        }
        if (tobeDeleteCons.getType() == EMConversation.EMConversationType.GroupChat) {
            EaseAtMessageHelper.get().removeAtMeGroup(tobeDeleteCons.conversationId());
        }
        try {
            // delete conversation
            EMClient.getInstance().chatManager().deleteConversation(tobeDeleteCons.conversationId(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        refresh();

        // update unread count
        updateUnreadLabel();
    }

    //发Event 通知更新未读消息数
    private void updateUnreadLabel() {
        MsgEvent msgEvent = new MsgEvent();
        msgEvent.setMsg(MsgEvent.EVENT_UPDATE_UNREAD_MSG_COUNT);
        EventBus.getDefault().post(msgEvent);
    }

    //跳转聊天页面
    private void goChat(String easemod_id) {
        Bundle bundle = new Bundle();
        bundle.putString("easemod_id", easemod_id);
        UIHelper.jump(mContext, ChatActivity.class, bundle);
    }

    //跳转用户详情
    private void goUserDetail(String profile_id) {
        Logger.e("----->profile_id =" + profile_id);
        Bundle bundle = new Bundle();
        bundle.putString("profile_id", profile_id);
        bundle.putString("type", "1");
        bundle.putString("goal_id", "");
        UIHelper.jump(mContext, ContactInfoActivity.class, bundle);
    }

}
