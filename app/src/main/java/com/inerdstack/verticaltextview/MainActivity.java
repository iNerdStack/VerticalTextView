package com.inerdstack.verticaltextview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    MyTextView myTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myTextView = (MyTextView) findViewById(R.id.my_text);
        myTextView.setText("测试哈奥垃圾费阿里山的风景阿里上飞机爱上当减肥拉丝粉案例看是否");
        myTextView.setMaxLines(2);
        myTextView.setTextSize(12);
    }
}
