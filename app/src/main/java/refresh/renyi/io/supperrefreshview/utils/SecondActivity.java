package refresh.renyi.io.supperrefreshview.utils;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import refresh.renyi.io.supperrefreshview.R;
import refresh.renyi.io.supperrefreshview.customview.SuperRefreshView;
import refresh.renyi.io.supperrefreshview.customview.UpdateSuperView;

/**
 * Created by renyi on 16/4/21.
 */
public class SecondActivity extends Activity {
    private SuperRefreshView sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        sp = (SuperRefreshView) findViewById(R.id.sr_second);

        Button btn = (Button) findViewById(R.id.btn_second);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.stopRefresh();
            }
        });

        findViewById(R.id.btn_second1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.stopLoadMore();
            }
        });

        sp.setOnRefreshAndLoadListener(new UpdateSuperView.RefreshAndLoadListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(SecondActivity.this, "scrollView refresh", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLoadMore() {
                Toast.makeText(SecondActivity.this, "scrollView Load", Toast.LENGTH_SHORT).show();
            }
        });


    }
}
