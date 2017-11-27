package top.renk.tvtopbox.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import top.renk.tvtopbox.R;
import top.renk.tvtopbox.adapter.HomeTvAdapter;
import top.renk.tvtopbox.widget.CustomRecyclerView;

public class MainActivity extends AppCompatActivity {

    private static final int LINE_NUM = 3;
    @BindView(R.id.list_arr_left)
    Button mLeftArr;
    @BindView(R.id.list_arr_recycler_view)
    CustomRecyclerView mRecyclerView;
    @BindView(R.id.list_arr_right)
    Button mRightArr;
    private List<Integer> mListData;
    private HomeTvAdapter mAdapter;
    private int totalWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initData();
        initListener();
    }

    private void initData() {
        //设置布局管理器
        StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(LINE_NUM, StaggeredGridLayoutManager.HORIZONTAL);
        mLayoutManager.setAutoMeasureEnabled(true);
        mListData = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            mListData.add(i);
        }
        mAdapter = new HomeTvAdapter(this, mListData);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        totalWidth= mRecyclerView.getMeasuredWidth();
    }

    private void initListener() {
        mRecyclerView.addOnScrollListener(new MyOnScrollListener());
    }


    @OnClick({R.id.list_arr_left, R.id.list_arr_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.list_arr_left:
                if (mRecyclerView.getScrollState() == RecyclerView.SCROLL_STATE_IDLE) {
                    mRecyclerView.smoothScrollBy(-totalWidth, 0);
                }
                break;
            case R.id.list_arr_right:
                if (mRecyclerView.getScrollState() == RecyclerView.SCROLL_STATE_IDLE) {
                    mRecyclerView.smoothScrollBy(totalWidth, 0);
                }
                break;
        }
    }

    private class MyOnScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            //在滚动的时候处理箭头的状态
            setLeftArrStatus();
            setRightArrStatus();
        }
    }

    /**
     * 设置左侧箭头的状态
     */
    private void setLeftArrStatus() {
        if (mRecyclerView.isFirstItemVisible()) {
            Log.e("renk", "fist can visit");
            mLeftArr.setVisibility(View.INVISIBLE);
        } else {
            mLeftArr.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置右侧箭头的状态
     */
    private void setRightArrStatus() {
        if (mRecyclerView.isLastItemVisible(LINE_NUM, mListData.size())) {
            Log.e("renk", "last can visit");
            mRightArr.setVisibility(View.INVISIBLE);
        } else {
            mRightArr.setVisibility(View.VISIBLE);
        }
    }
}
