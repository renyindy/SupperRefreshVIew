package refresh.renyi.io.supperrefreshview.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by renyi on 16/4/11.
 */
public abstract class UpdateSuperView extends LinearLayout {
    public static final int STATE_NORMAL = 0;  //常规状态
    public static final int STATE_ALREADY = 1;   //已可触发刷新
    public static final int STATE_REFRESHING = 2;  //正在刷新
    public static final int STATE_LOADING = 3; //正在加载

    protected RefreshAndLoadListener mRefreshAndLoadListener;

    public UpdateSuperView(Context context) {
        super(context);
    }

    public UpdateSuperView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 用来更改头部或底部的可见高度
     * @param value
     */
    public abstract void updateHeight(int value);

    /**
     * 重置头部或底部的可见高度，这个取决于当前state以决定重置为什么高度，不一定是不可见
     */
    public abstract void reseatHeight();

    /**
     * 设置当前状态
     * @param state
     */
    public abstract void setState(int state);

    /**
     * 获取当前状态
     * @return
     */
    public abstract int getState();

    /**
     * 获取当前头部或底部的可见高度
     * @return
     */
    public abstract int getVisableHeight();

    /**
     * 上拉加载和下拉刷新触发回调监听
     */
    public interface RefreshAndLoadListener{
        void onRefresh();
        void onLoadMore();
    }

    protected void setRefeshAndLoarListener(RefreshAndLoadListener refeshAndLoarListener){
        this.mRefreshAndLoadListener = refeshAndLoarListener;
    }
}
