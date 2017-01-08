package refresh.renyi.io.supperrefreshview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import refresh.renyi.io.supperrefreshview.customview.SuperRefreshView;
import refresh.renyi.io.supperrefreshview.customview.UpdateSuperView;
import refresh.renyi.io.supperrefreshview.utils.SecondActivity;


public class MainActivity extends Activity {

    private SuperRefreshView sr;
    private Context context;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sr = (SuperRefreshView) findViewById(R.id.sr);
        lv = (ListView) findViewById(R.id.listview);
        context = this;
        lv.setAdapter(new Myadapter());

        findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sr.stopRefresh();
            }
        });

        findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sr.stopLoadMore();
            }
        });
        findViewById(R.id.btn3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                startActivity(intent);
            }
        });
//        sr.setAutoLoadMore(true);

        sr.setOnRefreshAndLoadListener(new UpdateSuperView.RefreshAndLoadListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(MainActivity.this, "refresh", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLoadMore() {
                Toast.makeText(MainActivity.this, "loadmore", Toast.LENGTH_SHORT).show();
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("listView item onClick");
            }
        });
    }


    public class ScrollOffsetTransformer implements ViewPager.PageTransformer {
        /**
         * position参数指明给定页面相对于屏幕中心的位置。它是一个动态属性，会随着页面的滚动而改变。
         * 当一个页面（page)填充整个屏幕时，positoin值为0；
         * 当一个页面（page)刚刚离开屏幕右(左）侧时，position值为1（-1）；
         * 当两个页面分别滚动到一半时，其中一个页面是-0.5，另一个页面是0.5。
         * 基于屏幕上页面的位置，通过诸如setAlpha()、setTranslationX()或setScaleY()方法来设置页面的属性，创建自定义的滑动动画。
         */
        @Override
        public void transformPage(View page, float position) {
            if (position > 0) {
                //右侧的缓存页往左偏移100
                page.setTranslationX(-100 * position);
            }
        }
    }

    private class Myadapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 50;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv = new TextView(MainActivity.this);
            tv.setText("" + position);
            return tv;
        }
    }

}
