package com.sundy.iman.ui.activity;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.implments.SwipeItemRecyclerMangerImpl;
import com.daimajia.swipe.interfaces.SwipeAdapterInterface;
import com.daimajia.swipe.interfaces.SwipeItemMangerInterface;
import com.daimajia.swipe.util.Attributes;
import com.orhanobut.logger.Logger;
import com.sundy.iman.MainApp;
import com.sundy.iman.R;
import com.sundy.iman.config.Constants;
import com.sundy.iman.entity.ContactItemEntity;
import com.sundy.iman.entity.ContactListEntity;
import com.sundy.iman.entity.DeleteContactEntity;
import com.sundy.iman.helper.ImageHelper;
import com.sundy.iman.helper.UIHelper;
import com.sundy.iman.interfaces.OnTitleBarClickListener;
import com.sundy.iman.net.ParamHelper;
import com.sundy.iman.net.RetrofitCallback;
import com.sundy.iman.net.RetrofitHelper;
import com.sundy.iman.paperdb.PaperUtils;
import com.sundy.iman.utils.NetWorkUtils;
import com.sundy.iman.view.CustomLoadMoreView;
import com.sundy.iman.view.DividerItemDecoration;
import com.sundy.iman.view.TitleBarView;
import com.sundy.iman.view.WrapContentLinearLayoutManager;
import com.sundy.iman.view.dialog.CommonDialog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import lombok.Data;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by sundy on 17/10/5.
 */

public class MyContactsActivity extends BaseActivity {

    @BindView(R.id.rel_search)
    RelativeLayout relSearch;
    @BindView(R.id.rv_contact)
    RecyclerView rvContact;
    @BindView(R.id.title_bar)
    TitleBarView titleBar;
    @BindView(R.id.iv_search)
    ImageView ivSearch;
    @BindView(R.id.et_search)
    EditText etSearch;

    private ContactAdapter contactAdapter;
    private List<ContactItemEntity> listContact = new ArrayList<>();

    private String keyword = "";
    private int page = 1; //当前页码
    private int perpage = 10; //每页显示条数
    private boolean canLoadMore = true;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_contacts);
        ButterKnife.bind(this);

        initTitle();
        init();
        page = 1;
        if (listContact != null)
            listContact.clear();
        getContactList();
    }

    private void init() {
        etSearch.addTextChangedListener(textWatcher);

        rvContact.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvContact.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        contactAdapter = new ContactAdapter(R.layout.item_contact, listContact);
        contactAdapter.setLoadMoreView(new CustomLoadMoreView());
        contactAdapter.setPreLoadNumber(perpage);
        contactAdapter.setOnLoadMoreListener(onLoadMoreListener, rvContact);
        View view_community = getLayoutInflater().inflate(R.layout.view_my_community_header, null);
        view_community.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goMyCommunity();
            }
        });

        contactAdapter.addHeaderView(view_community);
        rvContact.setAdapter(contactAdapter);
    }

    //跳转我的社区列表
    private void goMyCommunity() {
        UIHelper.jump(this, MyCommunityActivity.class);
    }

    private void initTitle() {
        titleBar.setBackMode(getString(R.string.my_contacts));
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

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            keyword = etSearch.getText().toString().trim();
            page = 1;
            if (listContact != null)
                listContact.clear();
            contactAdapter.notifyDataSetChanged();
            getContactList();
        }
    };

    //获取联系人列表
    private void getContactList() {
        if (NetWorkUtils.isNetAvailable(this)) {
            Map<String, String> param = new HashMap<>();
            param.put("mid", PaperUtils.getMId());
            param.put("session_key", PaperUtils.getSessionKey());
            param.put("page", page + "");
            param.put("perpage", perpage + "");
            param.put("keyword", keyword);
            Call<ContactListEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                    .getContactList(ParamHelper.formatData(param));
            call.enqueue(new RetrofitCallback<ContactListEntity>() {
                @Override
                public void onSuccess(Call<ContactListEntity> call, Response<ContactListEntity> response) {
                    ContactListEntity contactListEntity = response.body();
                    if (contactListEntity != null) {
                        int code = contactListEntity.getCode();
                        String msg = contactListEntity.getMsg();
                        if (code == Constants.CODE_SUCCESS) {
                            ContactListEntity.DataEntity dataEntity = contactListEntity.getData();
                            if (dataEntity != null) {
                                showData(dataEntity.getList());
                            }
                        }
                    }
                }

                @Override
                public void onAfter() {

                }

                @Override
                public void onFailure(Call<ContactListEntity> call, Throwable t) {

                }
            });
        } else {
            contactAdapter.loadMoreEnd();
        }
    }

    private void showData(List<ContactItemEntity> listData) {
        try {
            if (listData.size() < perpage) {
                canLoadMore = false;
                contactAdapter.loadMoreEnd();
            } else {
                page = page + 1;
                canLoadMore = true;
                contactAdapter.loadMoreComplete();
            }
            for (int i = 0; i < listData.size(); i++) {
                ContactItemEntity item = listData.get(i);
                if (item != null) {
                    listContact.add(item);
                }
            }
            contactAdapter.setNewData(listContact);
            contactAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class ContactAdapter extends BaseQuickAdapter<ContactItemEntity, BaseViewHolder>
            implements SwipeItemMangerInterface, SwipeAdapterInterface, View.OnClickListener {

        public SwipeItemRecyclerMangerImpl mItemManger = new SwipeItemRecyclerMangerImpl(this);

        public ContactAdapter(@LayoutRes int layoutResId, @Nullable List<ContactItemEntity> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, ContactItemEntity item) {
            CircleImageView iv_header = helper.getView(R.id.iv_header);
            TextView tv_username = helper.getView(R.id.tv_username);

            String username = item.getUsername();
            String profileImg = item.getProfile_image();
            ImageHelper.displayPortrait(MyContactsActivity.this, profileImg, iv_header);

            tv_username.setText(username);

            mItemManger.bindView(helper.getConvertView(), helper.getLayoutPosition());

            ItemData itemData = new ItemData();
            itemData.setPosition(helper.getLayoutPosition());
            itemData.setItem(item);

            helper.setOnClickListener(R.id.tv_item_del, this);
            helper.setTag(R.id.tv_item_del, R.id.item_tag, itemData);

            helper.setOnClickListener(R.id.ll_item, this);
            helper.setTag(R.id.ll_item, R.id.item_tag, itemData);
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
                    //删除联系人
                    if (NetWorkUtils.isNetAvailable(MyContactsActivity.this)) {
                        if (itemData != null) {
                            showDeleteContactDialog(itemData);
                        }
                    } else {
                        MainApp.getInstance().showToast(getString(R.string.network_not_available));
                    }
                    break;
                case R.id.ll_item:
                    Logger.e("----->点击Item");
                    if (isOpen(itemData.getPosition())) {
                        closeItem(itemData.getPosition());
                        return;
                    }
                    goUserDetail(itemData.getItem().getId());
                    break;
            }
        }

        @Data
        public class ItemData implements Serializable {

            private int position;
            private ContactItemEntity item;

        }
    }

    //显示删除联系人弹框
    private void showDeleteContactDialog(final ContactAdapter.ItemData itemData) {
        final CommonDialog dialog = new CommonDialog(this);
        dialog.getTitle().setVisibility(View.GONE);
        dialog.getContent().setText(getString(R.string.delete_contact_tips));
        dialog.getBtnOk().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                deleteContact(itemData);
            }
        });
    }

    //删除联系人
    private void deleteContact(final ContactAdapter.ItemData itemData) {
        Map<String, String> param = new HashMap<>();
        param.put("mid", PaperUtils.getMId());
        param.put("session_key", PaperUtils.getSessionKey());
        param.put("contact_id", itemData.getItem().getId());
        Call<DeleteContactEntity> call = RetrofitHelper.getInstance().getRetrofitServer()
                .deleteContact(ParamHelper.formatData(param));
        call.enqueue(new RetrofitCallback<DeleteContactEntity>() {
            @Override
            public void onSuccess(Call<DeleteContactEntity> call, Response<DeleteContactEntity> response) {
                DeleteContactEntity deleteContactEntity = response.body();
                if (deleteContactEntity != null) {
                    int code = deleteContactEntity.getCode();
                    String msg = deleteContactEntity.getMsg();
                    if (code == Constants.CODE_SUCCESS) {
                        try {
                            listContact.remove(itemData.getItem());
                            contactAdapter.notifyDataSetChanged();
                            contactAdapter.closeAllItems();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        MainApp.getInstance().showToast(msg);
                    }
                }
            }

            @Override
            public void onAfter() {

            }

            @Override
            public void onFailure(Call<DeleteContactEntity> call, Throwable t) {

            }
        });
    }

    //跳转用户详情
    private void goUserDetail(String profile_id) {
        Logger.e("----->profile_id =" + profile_id);
        Bundle bundle = new Bundle();
        bundle.putString("profile_id", profile_id);
        bundle.putString("type", "1");
        bundle.putString("goal_id", "");
        UIHelper.jump(this, ContactInfoActivity.class, bundle);
    }

    private BaseQuickAdapter.RequestLoadMoreListener onLoadMoreListener = new BaseQuickAdapter.RequestLoadMoreListener() {
        @Override
        public void onLoadMoreRequested() {
            if (canLoadMore) {
                Logger.e("----->onLoadMoreRequested ");
                Logger.e("--->page = " + page);
                getContactList();
            } else {
                contactAdapter.loadMoreEnd();
            }
        }
    };
}
