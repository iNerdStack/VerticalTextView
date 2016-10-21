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
        myTextView.setText("测试哈asf说法是否打算暗室逢灯阿萨斯发顺丰asdf asS fa");
        myTextView.setMaxLines(2);
        myTextView.setTextSize(24);
        myTextView.setLineSpacingMultiplier(1.3f);
    }
}
