package refresh.renyi.io.supperrefreshview;

import android.support.v4.view.ViewCompat;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ScrollView;

import refresh.renyi.io.supperrefreshview.customview.CustomScrollView;
import refresh.renyi.io.supperrefreshview.customview.SuperRefreshView;


/**
 * 统一管理内容View， 适配所有View的 top bottom 临界值判断
 * 已做常规View的判断， 如需添加自定义View， 自行到 isBottom isTop 方法里添加
 * Created by renyi on 16/5/20.
 */
public class RefreshHolder implements AbsListView.OnScrollListener {
    private View mChild;
    private int mOffsetY; //子内容Vertical 方向上的偏移量
    private SuperRefreshView mRefreshView;


    public void setContentView(View view) {
        this.mChild = view;

    }

    public void setRefreshView(SuperRefreshView refreshView) {
        this.mRefreshView = refreshView;
    }

    /**
     * 达到顶部监听
     *
     * @return
     */
    public boolean isTop() {
        if (mChild instanceof AbsListView) {
            AbsListView absListView = (AbsListView) mChild;
            return !canScrollVertically(mChild, -1)
                    || absListView.getChildCount() > 0
                    && (absListView.getFirstVisiblePosition() == 0 && absListView.getChildAt(0).getTop() == 0);
        } else if (mChild instanceof ScrollView) {
            ScrollView scrollView = (ScrollView) mChild;
            return scrollView.getScrollY() == 0;
        } else {
            return canScrollVertically(mChild, -1) || mChild.getScrollY() > 0;
        }
    }

    /**
     * 到达底部监听
     *
     * @return
     */
    public boolean isBottom() {
        if (mChild instanceof AbsListView) {
            AbsListView absListView = (AbsListView) mChild;
            return !canScrollVertically(mChild, 1);
        } else if (mChild instanceof ScrollView) {
            ScrollView scrollView = (ScrollView) mChild;
            View childView = scrollView.getChildAt(0);
            if (childView != null) {
                return !canScrollVertically(mChild, 1)
                        || childView.getMeasuredHeight() <= scrollView.getHeight() + scrollView.getScrollY();
            }
        }
        return false;
    }

    public void setOffsetY(int value) {
        mOffsetY += value;
    }

    public int getOffsetY() {
        return mOffsetY;
    }

    /**
     * 注册到达底部监听
     */
    public void registerAutoLoadMore() {
        if (mChild instanceof AbsListView) { //ListView GridView
            final AbsListView absListView = (AbsListView) mChild;
            absListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                        if (absListView.getLastVisiblePosition() == absListView.getCount() - 1) {
                            if (mRefreshView != null) {
                                mRefreshView.autoLoadMore();
                            }
                        }
                    }
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                }
            });
        } else if (mChild instanceof ScrollView) {
            if (mChild instanceof CustomScrollView) { //必须使用自定义CustomScrollView  才能使用自动加载功能
                CustomScrollView scrollView = (CustomScrollView) mChild;
                scrollView.registerOnBottomListener(new CustomScrollView.OnScrollBottomListener() {
                    @Override
                    public void srollToBottom() {
                        if (mRefreshView != null) {
                            mRefreshView.autoLoadMore();
                        }
                    }
                });
            } else {
                throw new RuntimeException("please use CustomScrollView instead of ScrollView");
            }

        }
    }


    /**
     * 用来判断view在竖直方向上能不能向上或者向下滑动
     *
     * @param view      v
     * @param direction 方向 负数代表向上滑动 ，正数则反之
     * @return
     */
    public boolean canScrollVertically(View view, int direction) {
        return ViewCompat.canScrollVertically(view, direction);
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }
}
