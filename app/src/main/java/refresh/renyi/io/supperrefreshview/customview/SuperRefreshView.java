package refresh.renyi.io.supperrefreshview.customview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;

import refresh.renyi.io.supperrefreshview.MainActivity;
import refresh.renyi.io.supperrefreshview.RefreshHolder;
import refresh.renyi.io.supperrefreshview.utils.customUtils;

/**
 * headerView 和 FooterView 处理地方不同
 * headerView负责处理自己的显示，滑动等实现逻辑
 * Footer在本类中统一处理
 * <p/>
 * Created by renyi on 16/4/8.
 */
public class SuperRefreshView extends LinearLayout {
    private UpdateSuperView mHeadView;
    private SuperFooterView mFooterView;
    private View mContentView;
    private MotionEvent mLastMoveEvent;
    private RefreshHolder mRefreshHolder;
    private Scroller mScroller;


    private boolean isShowHeader = false;
    private boolean isShowFooter = false;
    private boolean isIntercept = false;
    private boolean isLoadMore = true;
    private boolean isRefresh = true;


    private UpdateSuperView.RefreshAndLoadListener mListener;
    private int mTouchSlop;
    private float mLastY;
    private float mLastX;


    public SuperRefreshView(Context context) {
        super(context);
    }

    public SuperRefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData(context);
    }

    public SuperRefreshView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData(context);
    }

    private void initData(Context context) {
        mRefreshHolder = new RefreshHolder();
        mHeadView = new SuperHeadView(context);
        mFooterView = new SuperFooterView(context);
        mScroller = new Scroller(context, new DecelerateInterpolator());

        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

    }


    @Override
    protected void onFinishInflate() {
        addHeadView();
        mContentView = getChildAt(1);
        mRefreshHolder.setContentView(mContentView);
        addFooterView();
        super.onFinishInflate();
    }

    /**
     * 设置自动刷新
     *
     * @param isNeedAutoLoad
     */
    public void setAutoLoadMore(boolean isNeedAutoLoad) {
        if (isNeedAutoLoad) {
            mRefreshHolder.setRefreshView(this);
            mRefreshHolder.registerAutoLoadMore();
        }
    }

    public void setCanRefreshFlag(boolean flag) {
        this.isRefresh = flag;
    }

    public void setCanLoadMoreFlag(boolean flag) {
        this.isLoadMore = flag;
    }

    /**
     * 更改LinearLayout测量逻辑，height=childHeight*childCount
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int childCount = getChildCount();
        int finalHeight = 0;
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            LayoutParams margins = (LayoutParams) child.getLayoutParams();
            int leftMargin = margins.leftMargin;
            int rightMargin = margins.rightMargin;
            if (child.getVisibility() != View.GONE) {
                final int childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec, 0, width - leftMargin - rightMargin);
//                final int childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec, 0, width);
                final int childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec, 0, margins.height);
                measureChild(child, childWidthMeasureSpec, childHeightMeasureSpec);
                finalHeight += child.getMeasuredHeight();
            }
        }
        setMeasuredDimension(width, finalHeight);
    }


    private void addHeadView() {
        customUtils.removeViewFromParent(mHeadView);
        addView(mHeadView, 0);
    }

    private void addFooterView() {
        customUtils.removeViewFromParent(mFooterView);
        addView(mFooterView);
    }

    public void stopRefresh() {
        mHeadView.setState(UpdateSuperView.STATE_NORMAL);
        reseatHeaderHeight();
    }

    public void stopLoadMore() {
        mFooterView.setState(UpdateSuperView.STATE_NORMAL);
        reseatFooterHeight();
    }

    /**
     * 自动加载
     */
    public void autoLoadMore() {
        if (mFooterView.getState() != mFooterView.STATE_LOADING) {
            if (mListener != null) {
                mListener.onLoadMore();
            }
            mRefreshHolder.setOffsetY(-mFooterView.getCurHeight());
            mFooterView.offsetTopAndBottom(mRefreshHolder.getOffsetY());
            mContentView.offsetTopAndBottom(mRefreshHolder.getOffsetY());
            mFooterView.setState(UpdateSuperView.STATE_LOADING);
        }

    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                System.out.println("down");
                isIntercept = false;
                mLastY = ev.getRawY();
                mLastX = ev.getRawX();
                break;
            case MotionEvent.ACTION_MOVE:
                mLastMoveEvent = ev;
                int deltaY = (int) (ev.getRawY() - mLastY);
                int deltaX = (int) (ev.getRawX() - mLastX);
                //横向滑动 不做处理
//                if (isIntercept || Math.abs(deltaX) - Math.abs(deltaY) > mTouchSlop) {
//                    isIntercept = true;
//                    return super.dispatchTouchEvent(ev);
//                }
                //headView  处理滑动
                if ((mHeadView.getVisableHeight() > 0 || (deltaY > 0 && mRefreshHolder.isTop())) && isRefresh) {
                    if (!isShowHeader) {
                        isShowHeader = true;
                    }
                    sendCancelEvent();
                    updateHeaderHeight(deltaY);
                } else if ((mRefreshHolder.getOffsetY() < 0 || deltaY < 0 && mRefreshHolder.isBottom()) && isLoadMore) { //footerView 处理滑动
                    if (!isShowFooter) {
                        isShowFooter = true;
                    }
                    sendCancelEvent();
                    updateFooterHeight(deltaY / 2);
                }
                if (mHeadView.getVisableHeight() <= 0 && isShowHeader && deltaY < 0) {
                    isShowHeader = false;
                    sendDownEvent();
                } else if (mRefreshHolder.getOffsetY() >= 0 && isShowFooter && deltaY > 0) {
                    isShowFooter = false;
                    sendDownEvent();
                }
                mLastY = ev.getRawY();

                break;
            case MotionEvent.ACTION_UP:
                if (mHeadView.getVisableHeight() > 0) {
                    reseatHeaderHeight();
                }
                if (mRefreshHolder.getOffsetY() != 0) {
                    reseatFooterHeight();
                }
                break;

        }
        return super.dispatchTouchEvent(ev);
    }

    private void updateHeaderHeight(int value) {
        mHeadView.updateHeight(value);
    }

    private void updateFooterHeight(int value) {
        mRefreshHolder.setOffsetY(value);
        mFooterView.offsetTopAndBottom(value);
        mContentView.offsetTopAndBottom(value);
        mFooterView.updateHeight(mRefreshHolder.getOffsetY());
    }

    private void reseatHeaderHeight() {
        mHeadView.reseatHeight();
    }

    private void reseatFooterHeight() {
        if (mFooterView.getState() == mFooterView.STATE_ALREADY) {
            mScroller.startScroll(0, mRefreshHolder.getOffsetY(), 0, 0 - mRefreshHolder.getOffsetY() - mFooterView.getCurHeight(), 300);
        } else if (mFooterView.getState() == mFooterView.STATE_NORMAL) {
            mScroller.startScroll(0, mRefreshHolder.getOffsetY(), 0, 0 - mRefreshHolder.getOffsetY(), 300);
        } else if (mFooterView.getState() == mFooterView.STATE_LOADING && Math.abs(mRefreshHolder.getOffsetY()) > mFooterView.getCurHeight()) {
            mScroller.startScroll(0, mRefreshHolder.getOffsetY(), 0, 0 - mRefreshHolder.getOffsetY() - mFooterView.getCurHeight(), 300);
        }
        mFooterView.reseatHeight();
        postInvalidate();
    }

    /**
     * 模拟 down事件 用于分发到 内部子View
     */
    private void sendDownEvent() {
//        System.out.println("sendDownEvent");
        final MotionEvent last = mLastMoveEvent;
        if (last == null)
            return;
        MotionEvent e = MotionEvent.obtain(last.getDownTime(),
                last.getEventTime(), MotionEvent.ACTION_DOWN, last.getX(),
                last.getY(), last.getMetaState());
        dispatchTouchEventSupper(e);
    }

    /**
     * 模拟 cancel 用于分发到 内部子View
     */
    private void sendCancelEvent() {
//        System.out.println("sendCancelEvent");
        MotionEvent last = mLastMoveEvent;
        MotionEvent e = MotionEvent.obtain(
                last.getDownTime(),
                last.getEventTime()
                        + ViewConfiguration.getLongPressTimeout(),
                MotionEvent.ACTION_CANCEL, last.getX(), last.getY(),
                last.getMetaState());
        dispatchTouchEventSupper(e);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller != null && mScroller.computeScrollOffset()) {
            int temp = mRefreshHolder.getOffsetY() - mScroller.getCurrY();
            updateFooterHeight(-temp);
            postInvalidate();
        }
    }

    public boolean dispatchTouchEventSupper(MotionEvent e) {
        return super.dispatchTouchEvent(e);
    }

    public void setOnRefreshAndLoadListener(UpdateSuperView.RefreshAndLoadListener refreshAndLoadListener) {
        mListener = refreshAndLoadListener;
        if (mHeadView != null) {
            mHeadView.setRefeshAndLoarListener(refreshAndLoadListener);
        }
        if (mFooterView != null) {
            mFooterView.setRefeshAndLoarListener(refreshAndLoadListener);
        }
    }

    /**
     * 自定义headView  必须继承自 UpdateSuperView
     *
     * @param customView
     */
    private void setCustomHeaderView(UpdateSuperView customView) {
        this.mHeadView = customView;
    }
}
