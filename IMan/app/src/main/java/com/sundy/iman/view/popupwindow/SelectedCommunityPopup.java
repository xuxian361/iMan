package com.sundy.iman.view.popupwindow;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sundy.iman.R;
import com.sundy.iman.entity.CommunityItemEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sundy on 17/10/17.
 */

public class SelectedCommunityPopup extends PopupWindow {

    private Context mContext;
    private View view;
    private int popupWidth;
    private int popupHeight;

    private RecyclerView rv_community;
    private CommunityAdapter communityAdapter;
    private ArrayList<CommunityItemEntity> listCommunity = new ArrayList<>();

    public SelectedCommunityPopup(Context context) {
        super(context);
        this.mContext = context;
        initPopup();
        setPopupWindow();
    }

    private void initPopup() {
        view = LayoutInflater.from(mContext).inflate(R.layout.popup_selected_community, null);
        rv_community = (RecyclerView) view.findViewById(R.id.rv_community);
        rv_community.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));

        communityAdapter = new CommunityAdapter(R.layout.item_selected_community, listCommunity);
        rv_community.setAdapter(communityAdapter);
    }

    private void setPopupWindow() {
        ColorDrawable dw = new ColorDrawable(0x00000000);
        this.setBackgroundDrawable(dw);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        this.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        this.setAnimationStyle(R.style.popWindow_anim_style);
        this.setContentView(view);
        //获取自身的长宽高
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        popupHeight = view.getMeasuredHeight();
        popupWidth = view.getMeasuredWidth();
    }

    public void setData(ArrayList<CommunityItemEntity> list) {
        if (list != null) {
            listCommunity = list;
            communityAdapter.setNewData(listCommunity);
            communityAdapter.notifyDataSetChanged();
        }
    }

    private class CommunityAdapter extends BaseQuickAdapter<CommunityItemEntity, BaseViewHolder> {

        public CommunityAdapter(@LayoutRes int layoutResId, @Nullable List<CommunityItemEntity> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(final BaseViewHolder helper, final CommunityItemEntity item) {
            TextView tv_community_name = helper.getView(R.id.tv_community_name);
            tv_community_name.setText(item.getName() + " (" + item.getMembers() + ")");

            ImageView iv_delete = helper.getView(R.id.iv_delete);
            iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listCommunity != null) {
                        if (listCommunity.contains(item)) {
                            listCommunity.remove(item);
                            notifyItemRemoved(helper.getPosition());

                            if (onDataChangeListener != null) {
                                onDataChangeListener.onDataChange(listCommunity);
                            }
                        }
                    }
                }
            });
        }
    }

    public void showUp(View v) {
        //获取需要在其上方显示的控件的位置信息
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        //在控件上方显示
        int x = (location[0] + v.getWidth() / 2) - popupWidth / 2;
        int y = location[1] - popupHeight;
        showAtLocation(v, Gravity.NO_GRAVITY, x, y);
    }

    public interface OnDataChangeListener {
        void onDataChange(ArrayList<CommunityItemEntity> list);
    }

    public OnDataChangeListener onDataChangeListener;

    public void setOnDataChangeListener(OnDataChangeListener onDataChangeListener) {
        this.onDataChangeListener = onDataChangeListener;
    }

}
