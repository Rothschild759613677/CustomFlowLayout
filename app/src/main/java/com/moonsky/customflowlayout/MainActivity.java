package com.moonsky.customflowlayout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 该项目主要实现的功能：
 * 自定义流式布局
 * 支持点击事件
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FlowLayout flowLayout = (FlowLayout) findViewById(R.id.flowLayout);
        flowLayout.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int index) {
                Toast.makeText(getApplicationContext(), "当前点击的条目-" + ((TextView) view).getText() + "---" + index, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
