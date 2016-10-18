package com.inerdstack.verticaltextview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.text.Editable;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityEvent;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by wangjie on 2016/10/18.
 */
public class view extends View {

    // -默认值
    // 默认字体大小: 12sp
    private static final float DEFAULT_TEXT_SIZE = 12;
    // 默认字体颜色：#FF212121
    private static final int DEFAULT_TEXT_COLOR = 0xFF212121;
    // 默认字间距
    private static final int DEFAULT_LETTER_SPACING = 0;
    // 默认行倍数
    private static final float DEFAULT_LINE_SPACING_MULTIPLIER = 1.2f;
    // 默认文本内容
    private static final String DEFAULT_TEXT = "";
    // 其他默认
    private static final int DEFAULT_GLOBAL = Integer.MIN_VALUE;

    // -位置
    // 笔刷所在位置 X
    private int mPosX = DEFAULT_GLOBAL;
    // 笔刷所在位置 Y
    private int mPosY = DEFAULT_GLOBAL;

    // -文本
    // 文本内容
    private String mText = DEFAULT_TEXT;
    // 字体大小
    private float mTextSize = DEFAULT_TEXT_SIZE;
    // 字体颜色
    private int mTextColor = DEFAULT_TEXT_COLOR;
    // 最大字符数
    private int mMaxLength = DEFAULT_GLOBAL;

    // -间距
    // 字间距
    private float mLetterSpacing = DEFAULT_LETTER_SPACING;
    // 行间距尺寸
    private float mLineSpacingExtra = DEFAULT_GLOBAL;
    // 行间距倍数
    private float mLineSpacingMultiplier = DEFAULT_LINE_SPACING_MULTIPLIER;

    // -整体
    // 视图宽度
    private float mGlobalWidth = DEFAULT_GLOBAL;
    // 视图高度
    private float mGlobalHeight = DEFAULT_GLOBAL;
    // 最大行数
    private int mMaxLines = DEFAULT_GLOBAL;

    // -工具
    // 绘制文本的笔刷
    private Paint mPaint;
    // 矩阵
    private Matrix mMatrix;
    // 背景
    private BitmapDrawable mBackground = (BitmapDrawable) getBackground();

    public view(Context context) {
        this(context, null);
    }

    public view(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public view(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //初始化视图
        initView();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        // 初始化矩阵
        mMatrix = new Matrix();
        //初始化笔刷
        mPaint = new Paint();
        // 文字居中
        mPaint.setTextAlign(Paint.Align.CENTER);
        // 去锯齿
        mPaint.setAntiAlias(true);
    }

    /**
     * 视图大小
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 视图渲染
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 绘制背景
        if (mBackground != null) {
            // 画背景
//            Bitmap bitmap = Bitmap.createBitmap(mBackground.getBitmap(),
//                    0, 0;)
        }
    }

    /**
     * 设置文本内容
     * @param text
     */
    public void setText(String text) {
        if (!TextUtils.equals(text, mText)) {
            this.mText = text;
            // 刷新绘制
            validate();
        }
    }

    /**
     * 设置文本颜色
     * @param color
     */
    public void setTextColor(int color) {
        if (mTextColor != color) {
            this.mTextColor = color;
            // 刷新绘制
            validate();
        }
    }

    /**
     * 设置文字大小，单位：sp
     * @param textSize
     */
    public void setTextSize(float textSize) {
        if (mTextSize != textSize) {
            this.mTextSize = textSize;
            // 刷新绘制
            validate();
        }
    }

    /**
     * 设置文本最大长度
     * @param length
     */
    public void setMaxLenth(int length) {
        if (mMaxLength != length) {
            this.mMaxLength = length;
            // 刷新绘制
            validate();
        }
    }

    /**
     * 设置字符间距，单位:dp
     * @param spacing
     */
    public void setLetterSpacing(float spacing) {
        if (mLetterSpacing != spacing) {
            this.mLetterSpacing = spacing;
            // 刷新绘制
            validate();
        }
    }

    /**
     * 设置行间距，单位：dp
     * @param spacing
     */
    public void setLineSpacingExtra(float spacing) {
        if (mLineSpacingExtra != spacing) {
            this.mLineSpacingExtra = spacing;
            // 刷新绘制
            validate();
        }
    }

    /**
     * 设置行间距倍数
     * @param multiplier
     */
    public void setLineSpacingMultiplier(float multiplier) {
        if (mLineSpacingMultiplier != multiplier) {
            this.mLineSpacingMultiplier = multiplier;
            // 刷新绘制
            validate();
        }
    }

    /**
     * 设置视图宽度
     * @param width
     */
    public void setWidth(float width) {
        if (mGlobalWidth != width) {
            this.mGlobalWidth = width;
            // 刷新绘制
            validate();
        }
    }

    /**
     * 设置视图高度
     * @param height
     */
    public void setHeight(float height) {
        if (mGlobalHeight != height) {
            this.mGlobalHeight = height;
            // 刷新绘制
            validate();
        }
    }

    /**
     * 设置最大行数
     * @param lines
     */
    public void setMaxLines(int lines) {
        if (mMaxLines != lines) {
            this.mMaxLines = lines;
            // 刷新绘制
            validate();
        }
    }

    /**
     * 绘制文本
     */
    private void validate() {
        // 设置文字颜色
        mPaint.setColor(mTextColor);
    }
}
