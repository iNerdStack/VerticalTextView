package com.inerdstack.verticaltextview;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {

    VerticalTextView myTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myTextView = (VerticalTextView) findViewById(R.id.my_text);

        myTextView.setText("履行了");
        myTextView.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
        // 文本字体大小
        int textSize = DensityUtils.sp2px(this, 16);
        // 设置行间距倍数
        myTextView.setLineSpacingMultiplier(1);
        // 设置字体大小
        myTextView.setTextSize(textSize);
        // 设置高度
//        myTextView.setHeight(DensityUtils.dip2px(this, 150));
        // 设置最大行号
        myTextView.setMaxLines(3);
    }
}
