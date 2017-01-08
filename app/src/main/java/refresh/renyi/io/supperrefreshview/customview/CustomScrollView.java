package refresh.renyi.io.supperrefreshview.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

/**
 * Created by renyi on 16/4/28.
 */
public class CustomScrollView extends ScrollView {

    private OnScrollBottomListener _listener;
    private int _calCount;
    private Context mContext;

    public CustomScrollView(Context context) {
        super(context);
    }

    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public CustomScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }


    private void init() {
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        View view = this.getChildAt(0);
        if (this.getHeight() + this.getScrollY() == view.getHeight()) {
            _calCount++;
            if (_calCount == 1) {
                if (_listener != null) {
                    _listener.srollToBottom();
                }
            }
        } else {
            _calCount = 0;
        }
    }

    public interface OnScrollBottomListener {
        void srollToBottom();
    }

    public void unRegisterOnBottomListener() {
        _listener = null;
    }

    public void registerOnBottomListener(OnScrollBottomListener l) {
        _listener = l;
    }
}
