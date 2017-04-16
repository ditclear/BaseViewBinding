package com.ditclear.app.base;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.DimenRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.InflateException;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ditclear.app.BR;
import com.ditclear.app.R;
import com.ditclear.app.state.StateModel;

/**
 * 页面描述：activity基类
 * <p>
 * Created by ditclear on 2017/4/16.
 */

public abstract class BaseBindActivity<VB extends ViewDataBinding> extends AppCompatActivity {

    protected VB mBinding;
    protected StateModel stateModel;

    protected Context mContext;
    protected Toolbar toolbar;
    protected SwipeRefreshLayout refreshLayout;
    protected RecyclerView recyclerView;
    protected int listOffset;//列表滑动偏移
    protected int page = 0;//分页

    private boolean loadMoreEnable = false; //是否可以加载更多

    public void setLoadMoreEnable(boolean loadMoreEnable) {
        this.loadMoreEnable = loadMoreEnable;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //上下文
        mContext = this;
        //
        View rootView = getLayoutInflater().inflate(this.getLayoutId(), null, false);
        mBinding = DataBindingUtil.bind(rootView);

        this.bindViewModel(getLayoutId(), rootView);
        if (mBinding == null) {
            if (getLayoutId() == 0) {
                throw new InflateException("布局没填充");
            } else {
                throw new NullPointerException("mBinding 不能为空");
            }
        }
        initToolBar();
        initRefreshLayout();
        initRecyclerView();
        initView();
        initEventHandler();
        //默认下拉刷新
        loadData(true);
    }


    /**
     * 初始化toolbar
     */
    private void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(null);
            setShowBackNavigation(true);
        }
    }


    /**
     * 初始化刷新布局,如果有刷新事件直接load
     */
    private void initRefreshLayout() {
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        if (refreshLayout != null) {
            refreshLayout.setEnabled(true);
            refreshLayout.setColorSchemeColors(
                    ContextCompat.getColor(mContext, R.color.theme));
            refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    loadData(true);
                }
            });
        }
    }

    /**
     * 加载更多
     */
    private void initRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        if (recyclerView != null && recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            ((LinearLayoutManager) recyclerView.getLayoutManager()).setInitialPrefetchItemCount(4);
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    listOffset += dy;
                    //表示是否能向上滚动，false表示已经滚动到底部
                    if (!recyclerView.canScrollVertically(1)) {
                        if (loadMoreEnable) {
                            loadData(false);
                        }
                    }
                }
            });
        }

    }


    public void bindViewModel(int layoutResID, View rootView) {
        if (stateModel == null) {
            stateModel = new StateModel();
        }
        mBinding.setVariable(BR.stateModel, stateModel);
        mBinding.executePendingBindings();
        super.setContentView(rootView);
    }

    @LayoutRes
    protected abstract int getLayoutId();

    //初始化视图
    protected abstract void initView();

    //事件监听，事件处理
    protected abstract void initEventHandler();

    //刷新数据
    protected abstract void loadData(boolean isRefresh);


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (-1 != getMenuId())
            getMenuInflater().inflate(getMenuId(), menu);
        return true;
    }

    /**
     * 获取toolbar中的menu
     *
     * @return
     */
    public int getMenuId() {
        return -1;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //默认返回
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    protected void setShowBackNavigation(boolean show) {
        if (show && toolbar != null) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        } else {
            if (toolbar != null) {
                toolbar.setNavigationIcon(null);
            }
        }
    }

    protected void setShowBackNavigation(boolean show, View.OnClickListener Listener) {
        if (show && toolbar != null) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
            toolbar.setNavigationOnClickListener(Listener);
        } else {
            if (toolbar != null) {
                toolbar.setNavigationIcon(null);
            }
        }
    }

    protected void setShowBackNavigation(boolean show, int drawableId) {
        if (show && toolbar != null) {
            toolbar.setNavigationIcon(drawableId);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        } else {
            if (toolbar != null) {
                toolbar.setNavigationIcon(null);
            }
        }
    }

    /**
     * 设置标题字体大小
     *
     * @param resId
     */
    protected void setBarTitleSize(@DimenRes int resId) {
        if (toolbar != null && toolbar.findViewById(R.id.title_tv) != null) {
            ((TextView) toolbar.findViewById(R.id.title_tv))
                    .setTextSize(mContext.getResources().getDimension(resId));
        }
    }

    /**
     * @return titletv
     */
    public TextView getTitleTv() {
        if (toolbar != null && toolbar.findViewById(R.id.title_tv) != null) {
            return (TextView) toolbar.findViewById(R.id.title_tv);
        }
        return null;
    }

    /**
     * 设置标题
     *
     * @param stringResId
     */
    protected void setBarTitle(@StringRes int stringResId) {
        setBarTitle(getString(stringResId));
    }

    /**
     * 设置标题
     *
     * @param title
     */
    public void setBarTitle(String title) {
        if (toolbar != null && toolbar.findViewById(R.id.title_tv) != null) {
            ((TextView) toolbar.findViewById(R.id.title_tv)).setText(title);
        }
    }

    /**
     * 统一提示
     *
     * @param msg
     */
    public void toast(String msg) {

        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }


}