package refresh.renyi.io.supperrefreshview.customview;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.Scroller;
import android.widget.TextView;

import refresh.renyi.io.supperrefreshview.R;


/**
 * Created by renyi on 16/4/11.
 */
public class SuperHeadView extends UpdateSuperView {
    private int mContentHeight; //内容高度  为自动刷新的默认值
    private int mCurrentHeight;
    private int mState = STATE_NORMAL;
    private AnimationDrawable animationDrawable;

    private View mContainerView; //header 根布局
    private View mRlContentView; //内容布局
    private ImageView mRefreshView;
    private TextView mRefreshTextView;
    private Scroller mScroller;

    private LayoutParams ll;

    public SuperHeadView(Context context) {
        super(context);
        initView(context);
    }

    public SuperHeadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        mContainerView = View.inflate(context, R.layout.item_layout, null);
        mRefreshView = (ImageView) mContainerView.findViewById(R.id.iv);
        mRefreshTextView = (TextView) mContainerView.findViewById(R.id.tv_refresh);
        mRlContentView = mContainerView.findViewById(R.id.rl_content);
        mRefreshView.setImageDrawable(getResources().getDrawable(R.drawable.iconrefresh1_2x));

        ll = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        addView(mContainerView, ll);

        mScroller = new Scroller(context, new DecelerateInterpolator());
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //初始化触发刷新的临界值   这个值可自己根据实际情况指定
        mContentHeight = mRlContentView.getMeasuredHeight();
        super.onSizeChanged(w, h, oldw, oldh);
    }


    @Override
    public void updateHeight(int value) {
        mCurrentHeight = getVisableHeight();
        if (value + mCurrentHeight < 0) {
            mCurrentHeight = 0;
        } else {
            mCurrentHeight = value / 2 + mCurrentHeight;
        }
        setVisableHeight(mCurrentHeight);
        if (getVisableHeight() < mContentHeight && mState != STATE_REFRESHING) {
            setState(STATE_NORMAL);
        } else if (getVisableHeight() >= mContentHeight && mState != STATE_REFRESHING) {
            setState(STATE_ALREADY);
        }
    }

    @Override
    public int getVisableHeight() {
        return ll.height;
    }

    private void setVisableHeight(int value) {
        ll.height = value;
        mContainerView.setLayoutParams(ll);
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
    public void reseatHeight() {
        mCurrentHeight = getVisableHeight();
        if (mCurrentHeight <= 0) { //当正在加载中 CurrentHeight为<0 这时候移动会造成BUG，所以屏蔽掉
            return;
        }
        if (mState == STATE_NORMAL) {
            mScroller.startScroll(0, mCurrentHeight, 0, -mCurrentHeight, 300);
        } else if (mState == STATE_ALREADY) {
            mScroller.startScroll(0, mCurrentHeight, 0, mContentHeight - mCurrentHeight, 300);
            mState = STATE_REFRESHING;
            onRefresh();
            showState(mState);
        } else if (mCurrentHeight > mContentHeight) {
            mScroller.startScroll(0, mCurrentHeight, 0, mContentHeight - mCurrentHeight, 300);
        }
        invalidate();
    }

    /**
     * 根据状态显示对应视图， 自定义加载效果在这里控制自己想要的视图
     *
     * @param state
     */
    private void showState(int state) {
        switch (state) {
            case STATE_NORMAL:
                mRefreshTextView.setText("下拉刷新");
                if (mRefreshView.getDrawable() instanceof AnimationDrawable) {
                    animationDrawable = (AnimationDrawable) mRefreshView.getDrawable();
                    if (animationDrawable != null && animationDrawable.isRunning()) {
                        animationDrawable.stop();
                    }
                    mRefreshView.setImageDrawable(getResources().getDrawable(R.drawable.iconrefresh1_2x));
                }
                break;
            case STATE_ALREADY:
                mRefreshTextView.setText("放手即可刷新");
                if (!(mRefreshView.getDrawable() instanceof AnimationDrawable)) {
                    mRefreshView.setImageDrawable(getResources().getDrawable(R.drawable.pull_refresh_loading));
                }
                break;
            case STATE_REFRESHING:
                mRefreshTextView.setText("正在刷新中");
                if (mRefreshView.getDrawable() instanceof AnimationDrawable) {
                    animationDrawable = (AnimationDrawable) mRefreshView.getDrawable();
                    if (animationDrawable != null && !animationDrawable.isRunning()) {
                        animationDrawable.start();
                    }
                }
                break;

        }
    }

    @Override
    public void computeScroll() {
        if (mScroller != null && mScroller.computeScrollOffset()) {
            setVisableHeight(mScroller.getCurrY());
            postInvalidate();
        }
        super.computeScroll();
    }

    private void onRefresh() {
        if (mRefreshAndLoadListener != null) {
            mRefreshAndLoadListener.onRefresh();
        }
    }


}
