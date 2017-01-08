package refresh.renyi.io.supperrefreshview.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import refresh.renyi.io.supperrefreshview.R;

/**
 * Created by renyi on 16/4/11.
 */
public class SuperFooterView extends UpdateSuperView {
    private int mCurrentHeight;  //出发加载的临界值，默认为 加载视图内容的height；
    private int mState = STATE_NORMAL;

    private View mContentView; //footer 根布局
    private View mCurrentView; //下拉刷新内容View
    private TextView mRefreshTextView;
    private ProgressBar mPbLoading;

    private LayoutParams ll;


    public SuperFooterView(Context context) {
        super(context);
        initView(context);
    }

    public SuperFooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        mContentView = View.inflate(context, R.layout.item_footer, null);
        mCurrentView = mContentView.findViewById(R.id.rl_footer);
        mRefreshTextView = (TextView) mContentView.findViewById(R.id.tv_refresh);
        mPbLoading = (ProgressBar) mContentView.findViewById(R.id.pb_loading);

        ll = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1000);
        addView(mContentView, ll);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mCurrentHeight = mCurrentView.getMeasuredHeight();
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public void updateHeight(int value) {
        if (Math.abs(value) >= mCurrentHeight && mState != STATE_LOADING) {
            setState(STATE_ALREADY);
        } else if (Math.abs(value) < mCurrentHeight && mState != STATE_LOADING) {
            setState(STATE_NORMAL);
        }
    }


    @Override
    public void reseatHeight() {
        if (mState == STATE_ALREADY) {
            mState = STATE_LOADING;
            showState(mState);
            onLoadMore();
        }
    }

    @Override
    public void setState(int state) {
        if (mState != state) {
            mState = state;
            showState(mState);
        }
    }

    @Override
    public int getState() {
        return mState;
    }

    @Override
    public int getVisableHeight() {
        return 0;
    }

    /**
     * 获取触发刷新的临界值
     *
     * @return
     */
    public int getCurHeight() {
        return mCurrentHeight;
    }

    /**
     * 根据状态显示对应视图， 自定义加载效果在这里控制自己想要的视图
     *
     * @param state
     */
    private void showState(int state) {
        switch (state) {
            case STATE_NORMAL:
                mRefreshTextView.setText("上拉加载更多");
                mRefreshTextView.setVisibility(View.VISIBLE);
                mPbLoading.setVisibility(View.INVISIBLE);
                break;
            case STATE_ALREADY:
                mRefreshTextView.setText("放手即可加载");
                mRefreshTextView.setVisibility(View.VISIBLE);
                mPbLoading.setVisibility(View.INVISIBLE);
                break;
            case STATE_LOADING:
                mRefreshTextView.setText("正在加载中");
                mRefreshTextView.setVisibility(View.INVISIBLE);
                mPbLoading.setVisibility(View.VISIBLE);
                break;

        }
    }

    private void onLoadMore() {
        if (mRefreshAndLoadListener != null) {
            mRefreshAndLoadListener.onLoadMore();
        }

    }
}
