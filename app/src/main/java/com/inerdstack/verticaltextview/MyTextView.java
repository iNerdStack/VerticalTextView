package com.inerdstack.verticaltextview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by wangjie on 2016/10/18.
 */
public class MyTextView extends View {

    // -默认值
    // 默认字体大小: 12sp
    private static final float DEFAULT_TEXT_SIZE = 12;
    // 默认字体颜色：#FF212121
    private static final int DEFAULT_TEXT_COLOR = 0xFF212121;
    // 默认字间距
    private static final int DEFAULT_LETTER_SPACING = 0;
    // 默认行倍数
    private static final float DEFAULT_LINE_SPACING_MULTIPLIER = 0.5f;
    // 默认文本内容
    private static final String DEFAULT_TEXT = "";
    // 其他默认
    private static final int DEFAULT_GLOBAL = Integer.MIN_VALUE;

    // -位置
    // 笔刷所在位置 X
    private int mPosX = DEFAULT_GLOBAL;
    // 笔刷所在位置 Y
    private int mPosY = DEFAULT_GLOBAL;
    // 初始y轴位置
    private static final int INTIAL_Y = 0;

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
    private float mCharSpacing = DEFAULT_LETTER_SPACING;
    // 行间距尺寸
    private float mLineSpacingExtra = DEFAULT_GLOBAL;
    // 行间距倍数
    private float mLineSpacingMultiplier = DEFAULT_LINE_SPACING_MULTIPLIER;

    // -标记
    // 尺寸有效标记
    private static final int ENABLE_SPACING_EXTRA = 0x01;
    // 尺寸倍数有效
    private static final int ENABLE_SPACING_MULTIPLIER = 0x02;
    // 标记当前间距倍数有效还是尺寸有效，初始化倍数有效
//    private int mLineSpacing = ENABLE_SPACING_MULTIPLIER;

    // -整体
    // 视图宽度 px
    private float mGlobalWidth = DEFAULT_GLOBAL;
    // 视图高度 px
    private float mGlobalHeight = DEFAULT_GLOBAL;
    // 最大行数
    private int mMaxLines = DEFAULT_GLOBAL;

    // -非对外属性变量
    // 单个文字尺寸
    private int mTextDimen;
    // 一行显示的字符数
    private int mLengthPerLine = DEFAULT_GLOBAL;
    // 临时行号
    private int mTmpLines;
    // 临时宽度
    private float mTmpWidth;

    // -工具
    // 绘制文本的笔刷
    private Paint mPaint;
    // 矩阵
    private Matrix mMatrix;
    // 背景
    private BitmapDrawable mBackground;

    public MyTextView(Context context) {
        this(context, null);
    }

    public MyTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Log.i("wdd", "constructor");
        //初始化
        init();
    }

    /**
     * 初始化视图
     */
    private void init() {
        Log.i("wdd", "init()");
        // 初始化工具
        initTools();
        // 同步默认数据数据
//        syncParams();
    }

    /**
     * 初始化工具
     */
    private void initTools() {
        Log.i("wdd", "initTools");
        // 初始化矩阵
        mMatrix = new Matrix();
        // 初始化背景
        mBackground = (BitmapDrawable) getBackground();
        //初始化笔刷
        mPaint = new Paint();
        // 文字居中
//        mPaint.setTextAlign(Paint.Align.CENTER);
        // 去锯齿
        mPaint.setAntiAlias(true);
    }

    /**
     * Very Important Method: 同步参数
     */
    private void syncParams() {
        Log.i("wdd", "syncParams()");
        // 设置笔刷颜色
        mPaint.setColor(mTextColor);
        
        // 计算单个字符覆盖边长
        measureCharDimen();

        // 计算行间距
        measureLineSpacing();

        // 计算文本宽度
        mesureWidth();

        String output = toString();
        Log.i("wdd", output);

    }

    /**
     * 计算行间距
     */
    private void measureLineSpacing() {
        Log.i("wdd", "measureLineSpacing");
        // 如果行间距未设定，则以行间距倍数来计算
        if (mLineSpacingExtra == DEFAULT_GLOBAL) {
            Log.i("wdd", "line spacing is not set");
            mLineSpacingExtra = mLineSpacingMultiplier * mTextDimen;
        }
        Log.i("wdd", "line spacing is " + mLineSpacingExtra);
    }

    /**
     * 以汉字“汉”为例，计算一个汉字覆盖的边长
     */
    private void measureCharDimen() {
        Log.i("wdd", "measureCharDimen");
        // 单位转换：sp转px
        int size = DensityUtils.sp2px(getContext(), mTextSize);
        // 设置字体大小
        mPaint.setTextSize(size);
        // 初始化字体矩阵
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        // 获取字体高度
        mTextDimen = (int) (fontMetrics.descent - fontMetrics.ascent);
        Log.i("wdd", "text dimen is " + mTextDimen);
    }

    /**
     * 计算宽度
     *
     * @return
     */
    private void mesureWidth() {
        Log.i("wdd", "measureWidth()");
        if (TextUtils.isEmpty(mText)) {
            // 临时标记文本宽度
            mTmpWidth = 0;
            mTmpLines = 0;
            return;
        }
        // 初始化第一行文本的宽度
        mTmpWidth = mTextDimen + mLineSpacingExtra;
        // 临时标记行数
        mTmpLines = 0;
        // 如果高度未设置，则说明高度是wrap_content
        if (mGlobalHeight == DEFAULT_GLOBAL) {

            Log.i("wdd", "height default");
            // -此时文本的高度取决于换行符的个数
            // 计算换行符的个数
            int num = containNum(mText, "\n");
            Log.i("wdd", "n is " + num);
            // 计算行号
            mTmpLines = num + 1;
            // 计算文本宽度
            mTmpWidth = (mTextDimen + mLineSpacingExtra) * mTmpLines;
            Log.i("wdd", "text dimen is " + mTextDimen + "; linespacingextra is " + mLineSpacingExtra + ";tmp lines is " + mTmpLines);
            Log.i("wdd", "width = " + mTmpWidth);
        } else {

            Log.i("wdd", "height seted");
            // 如果高度有设定，则预先试试每个字排版，测得宽度，先不考虑最大行数和宽度
            // 计算每一列可以排版的字数
            mLengthPerLine = (int) ((mGlobalHeight - 0) / mTextDimen); // 2 * INTIAL_Y
            Log.i("wdd", "mGlobalHeight=" + mGlobalHeight + ";mTextDimen" + mTextDimen);
            Log.i("wdd", "length per line is " + mLengthPerLine);
            // 每行的第j个字，标记
            int j = 0;
            // 遍历文本内容
            for (int i = 0; i < mText.length(); i++) {
                // 获取单字符
                char ch = mText.charAt(i);
                Log.i("wdd", "position -- " + i + "; char is -- " + ch);
                // 如果遇到换行符
                if (ch == '\n') {
                    Log.i("wdd", "换行");
                    // 且不是第一个字符就是换行符
                    if (j > 0) {
                        Log.i("wdd", "不是第一个字");
                        // 重置标记，改行第0个字
                        j = 0;
                        // 宽度增加(一个文字宽度+行间距)
                        mTmpWidth += mTextDimen + mLineSpacingExtra;
                        Log.i("wdd", "tmp width in " + i + " is " + mTmpWidth);
                        // 行号+1
                        mTmpLines++;
                        Log.i("wdd", "当前行--" + mTmpLines);
                    }
                } else {
                    Log.i("wdd", "文本字符" + i);
                    // 新增加一个字
                    j++;
                    Log.i("wdd", "当前行第" + j + "个字");
                    // 如果该行满了，则换行
                    if (j == mLengthPerLine) {
                        Log.i("wdd", "第" + j + "行满了");
                        // 换行，重置标记
                        j = 0;
                        Log.i("wdd", "j重置");
                        // 宽度增加(一个文字宽度+行间距)
                        mTmpWidth += mTextDimen + mLineSpacingExtra;
                        Log.d("wdd", "当前宽度" + mTmpWidth);
                        // 行数+1
                        mTmpLines++;
                    }
                }
                Log.i("wdd", "当前下标" + i);
            }
            Log.i("wdd", "tmp width is " + mTmpWidth);
        }

        Log.i("wdd", "line width is " + (mTextDimen + mLineSpacingExtra));
        Log.i("wdd", "tmp lines is " + mTmpLines + "---max lines is " + mMaxLines);
        // -计算最终可以显示的行数，去最终的文本宽度
        // 与最大行数比较，取较小值
        if (mTmpLines >= mMaxLines) {
            Log.i("wdd", "line more than max");
            // 如果行数超过最大行数，则以最大行数为准
            mTmpLines = mMaxLines;
            // 计算相应的宽度
            mTmpWidth = (mTextDimen + mLineSpacingExtra) * mTmpLines;
        }

        // 与设定的高度相比较，取较小值
        if (mGlobalWidth > 0) { // 排除match_parent(-1)，match_parent的值在onMeasure中计算
            Log.i("wdd", "global("+mGlobalWidth+") width more than zero");
            // 以文字宽度+行高作为一行的宽度，取得完整行宽的行数
            int l = (int) (mGlobalWidth / (mTextDimen + mLineSpacingExtra));
            Log.i("wdd", "---" + mGlobalWidth);
            // 最终剩余的宽度与（文字宽度+一般的行高）比较，即看是否可以再完整显示一行
            float remain = mGlobalWidth % (mTextDimen + mLineSpacingExtra);
            Log.i("wdd", "---" + mGlobalWidth);
            Log.i("wdd", "remain is " + remain);
            // 如果剩余宽度可以容纳一行文字，则多加一行
            if (remain > mTextDimen + mLineSpacingExtra / 2) {
                l++;
                Log.i("wdd", "line plus--" + l);
            }
            Log.i("wdd", "最多可以输入" + l + "行");

            // 如果行数超过设定的宽度计算得到的行数
            if (mTmpLines > l) {
                Log.i("wdd", "line more than got");
                // 则以设定宽度下得到的行数为准
                mTmpLines = l;
                // 文本宽度以设定的宽度为准
                mTmpWidth = mGlobalWidth;
                Log.i("wdd", "width is " + mGlobalWidth);
            }
        }

    }

    /**
     * 一个字符串中包含另一个字符串的个数
     *
     * @param container
     * @param str
     * @return
     */
    private int containNum(String container, String str) {
        Log.i("wdd", "containNum");
        // 子字符串长度
        int lenth = str.length();
        int index = container.indexOf(str);
        // 如果不存在，则返回0
        if (index == -1) {
            Log.i("wdd", "got 0");
            return 0;
        } else {
            Log.i("wdd", "迭代");
            // 如果存在，则迭代
            return 1 + containNum(container.substring(index + lenth), str);
        }
    }


    /**
     * 视图大小
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.i("wdd", "onMeasure()");

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        this.mGlobalWidth = width;
        this.mGlobalHeight = height;

        Log.i("wdd", "width " + mGlobalWidth + ";height " + mGlobalHeight);
//        syncParams();
        Log.i("wdd", "after syncParams()");
        Log.i("wdd", "width=" + width + ";height=" + height);
    }

    /**
     * 视图渲染
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Log.i("wdd", "onDraw");
        // 绘制背景
        if (mBackground != null) {
            Log.i("wdd", "background not null");
            // 画背景
            Bitmap bitmap = Bitmap.createBitmap(mBackground.getBitmap(),
                    0, 0, mTextDimen, mTextDimen);
            canvas.drawBitmap(bitmap, mMatrix, mPaint);
        }
        Log.i("wdd", "draw()");
        // 同步参数
        syncParams();
        // 画文字
        draw(canvas, this.mText);
    }

    /**
     * 画文字
     *
     * @param canvas
     * @param text
     */
    private void draw(Canvas canvas, String text) {
        Log.i("wdd", "enter draw()");

        // 得到最终的宽度
        // TODO: 对MATCH_PARENT、WRAP_CONTENT进行分析
        if (mGlobalWidth < mTmpWidth) {
            mGlobalWidth = mTmpWidth;
        }
        // 得到最终行数
        mMaxLines = mTmpLines;

        // 当前字符
        char ch;
        // 当前行号
        int curLine = 1;
        // 行间距的一般
        float halfLineSpacing = mLineSpacingExtra / 2;
        // 初始化笔刷X轴坐标
        mPosX = (int) (mGlobalWidth - mTextDimen - halfLineSpacing);
        Log.i("wdd", "---" + mGlobalWidth);
        // 初始化笔刷Y轴坐标
        mPosY = INTIAL_Y;

        // -绘制文字
        // 计算字符串长度
        int length = mText.length();
        // 如果高度未设定
        if (mGlobalHeight == DEFAULT_GLOBAL) {
            // 遍历字符串
            for (int i = 0; i < length; i++) {
                Log.i("wdd", "draw:x=" + mPosX + ";y=" + mPosY);
                // 获取当前字符
                ch = mText.charAt(i);

                // 如果遇到换行符
                if (ch == '\n') {
                    // 笔刷左移
                    mPosX -= (mTextDimen + mLineSpacingExtra);
                    // 高度重置
                    mPosY = INTIAL_Y;
                    // 行号+1
                    curLine++;
                } else {
                    // 绘制当前文字
                    canvas.drawText(String.valueOf(ch), mPosX, mPosY + mTextDimen, mPaint);
                    // 笔刷下移
                    mPosY += mTextDimen;
                }
            }

        } else { // 如果高度设定
            // 当前行的第几个字符
            int j = 0;
            // 遍历字符串
            for (int i = 0; i < length; i++) {
                Log.i("wdd", "draw:x = " + mPosX + "; y = " + mPosY);
                // 获取当前字符
                ch = mText.charAt(i);
                // 如果遇到换行符
                if (ch == '\n') {
                    // 且当前行有文字内容
                    if (j > 0) {
                        // 行号+1
                        curLine++;
                        // 字符+1
                        j++;
                        // -根据是不是最后一行，判断要不要加省略号
                        // 如果是最后一行
                        if (curLine >= mMaxLines) {
                            /* 换行符不会出现在文本结尾，说明如果遇到换行符，则文本内容没有结束，
                            所以需要绘制省略号
                             */
                            canvas.drawText("...", mPosX, mPosY + mTextDimen, mPaint);
                            // 跳出循环
                            break;
                        } else { // 如果不是最后一行
                            // 重置位置标记为0
                            j = 0;
                            // 笔刷左移
                            mPosX -= (mTextDimen + mLineSpacingExtra);
                            // 高度重置
                            mPosY = INTIAL_Y;
                        }
                    } else { // 不做任何操作
                        // 继续遍历
                    }
                } else { // 如果没有遇到换行符
                    // 新增一个文字
                    j++;
                    // -判断是不是最后一行
                    // 如果是最后一行
                    if (curLine >= mMaxLines) {
                        // -判断有没有到行尾
                        // 如果到行尾
                        if (j == mLengthPerLine) {
                            // -判断文本内容有没有结束
                            // 如果文本内容结束
                            if (i == length - 1) {
                                // 绘制完内容并退出
                                canvas.drawText(String.valueOf(ch), mPosX, mPosY + mTextDimen, mPaint);
                                break;
                            } else { // 如果文本内容没有结束
                                // 绘制省略号并退出
                                canvas.drawText("...", mPosX, mPosY + mTextDimen, mPaint);
                                break;
                            }
                        } else { // 如果不是行尾
                            // 继续绘制
                            canvas.drawText(String.valueOf(ch), mPosX, mPosY + mTextDimen, mPaint);
                            // 笔刷下移
                            mPosY += mTextDimen;
                        }
                    } else { // 如果不是最后一行
                        // -判断有没有达到行尾
                        // 如果到达行尾
                        if (j == mLengthPerLine) {
                            // 绘制文字
                            canvas.drawText(String.valueOf(ch), mPosX, mPosY + mTextDimen, mPaint);
                            // 笔刷左移
                            mPosX -= (mTextDimen + mLineSpacingExtra);
                            // 高度重置
                            mPosY = INTIAL_Y;
                            // 重置位置标记
                            j = 0;
                            // 行号+1
                            curLine++;
                        } else { // 如果未到达行尾
                            // 继续绘制
                            canvas.drawText(String.valueOf(ch), mPosX, mPosY + mTextDimen, mPaint);
                            // 笔刷下移
                            mPosY += mTextDimen;
                        }
                    }
                }
            }
        }
    }

    /**
     * 设置文本内容
     *
     * @param text
     */
    public void setText(String text) {
        Log.i("wdd", "setText()");
        if (!TextUtils.equals(text, mText)) {
            this.mText = text;
            // 刷新绘制
//            syncParams();
        }
    }

    /**
     * 设置文本颜色
     *
     * @param color
     */
    public void setTextColor(int color) {
        Log.i("wdd", "setTextColor");
        if (mTextColor != color) {
            this.mTextColor = color;
            // 刷新绘制
//            syncParams();
        }
    }

    /**
     * 设置文字大小，单位：sp
     *
     * @param textSize
     */
    public void setTextSize(float textSize) {
        Log.i("wdd", "setTextSize");
        if (mTextSize != textSize && textSize > 0) {
            this.mTextSize = textSize;
            // 刷新绘制
//            syncParams();
        }
    }

    /**
     * 设置文本最大长度
     *
     * @param length
     */
    public void setMaxLenth(int length) {
        Log.i("wdd", "setMaxLength");
        if (mMaxLength != length && length > 0) {
            this.mMaxLength = length;
            // 刷新绘制
//            syncParams();
        }
    }

    /**
     * 设置字符间距，单位:dp
     *
     * @param spacing
     */
    public void setLetterSpacing(float spacing) {
        Log.i("wdd", "setLetterSpacing");
        if (mCharSpacing != spacing && spacing > 0) {
            this.mCharSpacing = spacing;
            // 刷新绘制
//            syncParams();
        }
    }

    /**
     * 设置行间距，单位：dp
     *
     * @param spacing
     */
    public void setLineSpacingExtra(float spacing) {
        Log.i("wdd", "setLineSpacingExtra");
        if (mLineSpacingExtra != spacing && spacing > 0) {
            this.mLineSpacingExtra = spacing;
            // 刷新绘制
//            syncParams();
        }
    }

    /**
     * 设置行间距倍数
     *
     * @param multiplier
     */
    public void setLineSpacingMultiplier(float multiplier) {
        Log.i("wdd", "setLIneSpacingMult");
        if (mLineSpacingMultiplier != multiplier && multiplier > 0) {
            this.mLineSpacingMultiplier = multiplier;
            this.mLineSpacingExtra = DEFAULT_GLOBAL;
            // 刷新绘制
//            syncParams();
        }
    }

    /**
     * 设置视图宽度
     *
     * @param width
     */
    public void setWidth(float width) {
        Log.i("wdd", "setWidth");
        if (mGlobalWidth != width && width > 0) {
            Log.i("wdd", "---" + mGlobalWidth);
            // 单位转换：dp转px
            width = DensityUtils.dip2px(getContext(), width);
            this.mGlobalWidth = width;
            Log.i("wdd", "---" + mGlobalWidth);
            // 刷新绘制
//            syncParams();
        }
    }

    /**
     * 设置视图高度
     *
     * @param height
     */
    public void setHeight(float height) {
        Log.i("wdd", "setHeight");
        if (mGlobalHeight != height && height > 0) {
            // 单位转换：dp转px
            height = DensityUtils.dip2px(getContext(), height);
            this.mGlobalHeight = height;
            // 刷新绘制
//            syncParams();
        }
    }

    /**
     * 设置最大行数
     *
     * @param lines
     */
    public void setMaxLines(int lines) {
        Log.i("wdd", "setMaxLines");
        if (mMaxLines != lines && lines > 0) {
            this.mMaxLines = lines;
            // 刷新绘制
//            syncParams();
        }
    }

    @Override
    public String toString() {
        String output = "width:" + mGlobalWidth + "; height:" + mGlobalHeight + ";text size:" + mTextSize
                + ";text color:" + mTextColor + ";text dimen:" + mTextDimen + ";max lines:" + mMaxLines
                + ";x:" + mPosX + ";y:" + mPosY + ";length per line:" + mLengthPerLine + ";line spacing:"
                + mLineSpacingExtra + ";text:" + mText;
        return output;
    }
}
