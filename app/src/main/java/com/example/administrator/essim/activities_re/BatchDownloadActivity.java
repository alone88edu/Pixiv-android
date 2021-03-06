package com.example.administrator.essim.activities_re;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.administrator.essim.R;
import com.example.administrator.essim.activities_re.PixivApp;
import com.example.administrator.essim.adapters.BatchSelectAdapter;
import com.example.administrator.essim.interf.OnItemClickListener;
import com.example.administrator.essim.response.Reference;
import com.example.administrator.essim.response_re.IllustsBean;
import com.example.administrator.essim.utils.Common;
import com.example.administrator.essim.utils.DensityUtil;
import com.example.administrator.essim.utils.GridItemDecoration;
import com.example.administrator.essim.utils_re.FileDownload;
import com.example.administrator.essim.utils_re.LocalData;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class BatchDownloadActivity extends BaseActivity {

    private int startPosition;
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;

    @Override
    void initLayout() {
        mLayoutID = R.layout.activity_batch_download;
    }

    @Override
    void initView() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(view -> finish());
        startPosition = getIntent().getIntExtra("scroll dist", 0);
        mToolbar.setTitle("选择了0个项目");
        mRecyclerView = findViewById(R.id.recy_list);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new GridItemDecoration(
                2, DensityUtil.dip2px(mContext, 4.0f), false));
    }

    @Override
    void initData() {
        BatchSelectAdapter adapter = new BatchSelectAdapter(PixivApp.sIllustsBeans, mContext);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NotNull View view, int position, int viewType) {
                if (viewType == 0) {
                    PixivApp.downloadList.remove(PixivApp.sIllustsBeans.get(position));
                } else {
                    PixivApp.downloadList.add(PixivApp.sIllustsBeans.get(position));
                }
                mToolbar.setTitle("选择了" + PixivApp.downloadList.size() + "个项目");
            }

            @Override
            public void onItemLongClick(@NotNull View view, int position) {

            }
        });
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.scrollToPosition(startPosition);
    }

    @Override
    void getFirstData() {

    }

    @Override
    void getNextData() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PixivApp.downloadList.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.batch_download, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_select_all:
                for (int i = 0; i < PixivApp.sIllustsBeans.size(); i++) {
                    if (!PixivApp.sIllustsBeans.get(i).isSelected()) {
                        PixivApp.sIllustsBeans.get(i).setSelected(true);
                        PixivApp.downloadList.add(PixivApp.sIllustsBeans.get(i));
                    }
                }
                mRecyclerView.getAdapter().notifyDataSetChanged();
                mToolbar.setTitle("选择了" + PixivApp.downloadList.size() + "个项目");
                break;
            case R.id.action_not_select:
                for (int i = 0; i < PixivApp.sIllustsBeans.size(); i++) {
                    if (PixivApp.sIllustsBeans.get(i).isSelected()) {
                        PixivApp.sIllustsBeans.get(i).setSelected(false);
                    }
                }
                PixivApp.downloadList.clear();
                mRecyclerView.getAdapter().notifyDataSetChanged();
                mToolbar.setTitle("选择了" + PixivApp.downloadList.size() + "个项目");
                break;
            case R.id.action_start:
                if (PixivApp.downloadList.size() == 0) {
                    Common.showToast("你要下个什么？！");
                } else {
                    if (LocalData.getDownloadPath().contains("emulated")) {
                        FileDownload.downloadIllust(PixivApp.downloadList);
                        for (int i = 0; i < PixivApp.sIllustsBeans.size(); i++) {
                            if (PixivApp.sIllustsBeans.get(i).isSelected()) {
                                PixivApp.sIllustsBeans.get(i).setSelected(false);
                            }
                        }
                        mRecyclerView.getAdapter().notifyDataSetChanged();
                        PixivApp.downloadList.clear();
                        mToolbar.setTitle("选择了" + PixivApp.downloadList.size() + "个项目");
                    } else {
                        Snackbar.make(mRecyclerView, "暂时不支持下载到外置SD卡~", Snackbar.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
