package com.hins.smartband.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.hins.smartband.R;

/**
 * 程序员： Hins on 2016/4/30.
 * 描述：获取自定义属性的值并生成TabBootm
 */
public class Main_TabBottom extends View {
    private static final String INSTANCE_STATUS = "instance_status";
    private static final String STATUS_ALPHA = "status_alpha";
    private int mColor = 0xFF5677FC;
    private Bitmap mIcon;
    private String mText;
    private int mTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
            12, getResources().getDisplayMetrics());

    private Canvas mCanvas;
    private Paint mPaint;
    private Bitmap mBitmap;
    private float mAlpha;//透明度
    private Rect mIconRect;//icon的范围
    private Rect mTextRect;//text的范围
    private Paint mTextPaint;

    public Main_TabBottom(Context context) {
        this(context, null);
    }

    public Main_TabBottom(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    //在构造函数中传入参数
    public Main_TabBottom(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //获取传入参数数组
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Main_TabBottom);
        //遍历参数数组
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.Main_TabBottom_tab_icon:
                    BitmapDrawable drawable = (BitmapDrawable) a.getDrawable(attr);
                    mIcon = drawable.getBitmap();
                    break;
                case R.styleable.Main_TabBottom_tab_color:
                    mColor = a.getColor(attr, 0xFF5677FC);
                    break;
                case R.styleable.Main_TabBottom_tab_text:
                    mText = a.getString(attr);
                    break;
                case R.styleable.Main_TabBottom_tab_text_size:
                    mTextSize = (int) a.getDimension(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                            12, getResources().getDisplayMetrics()));
            }
        }
        a.recycle();//TypeArray用完回收

        mTextRect = new Rect();
        mTextPaint = new Paint();
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mTextPaint.getTextBounds(mText, 0, mText.length(), mTextRect);//?
    }

    //获取当前icon的宽和高
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //icon的边长=min(View的宽度-左右内边距,View的高度-上下内边距-文本的高度)
        int iconWidth = Math.min(getMeasuredWidth() - getPaddingLeft() - getPaddingRight()
                , getMeasuredHeight() - getPaddingTop() - getPaddingBottom() - mTextRect.height());
        //获取icon绘制中心点的left
        int left = getMeasuredWidth() / 2 - iconWidth / 2;
        //获取icon绘制中心点的top
        int top = (getMeasuredHeight() - mTextRect.height()) / 2 - iconWidth / 2;
        //获取icon的范围
        mIconRect = new Rect(left, top, left + iconWidth, top + iconWidth);
    }

    //icon绘制过程
    @Override
    protected void onDraw(Canvas canvas) {
        //绘制icon
        canvas.drawBitmap(mIcon, null, mIconRect, null);
        //透明度
        int alpha = (int) Math.ceil(255 * mAlpha);
        //绘制text
        drawTabText(canvas, alpha);
        //绘制主题色的text
        drawTabColorText(canvas, alpha);
        //在内存里准备mBitmap，setAlpha，纯色，xfermode，图标
        setUpTagetBitmap(alpha);
        //在icon上面绘制一个有色的mBitmap
        canvas.drawBitmap(mBitmap, 0, 0, null);
    }

    //绘制主题色的text
    private void drawTabColorText(Canvas canvas, int alpha) {
        mTextPaint.setColor(mColor);
        mTextPaint.setAlpha(alpha);
        mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        int x = mIconRect.left + mIconRect.width() / 2 - mTextRect.width() / 2;
        int y = mIconRect.bottom + mTextRect.height();
        canvas.drawText(mText, x, y, mTextPaint);
    }

    //绘制text
    public void drawTabText(Canvas canvas, int alpha) {
        mTextPaint.setColor(0xFFCFCFCF);
        mTextPaint.setAlpha(255 - alpha);
        int x = mIconRect.left + mIconRect.width() / 2 - mTextRect.width() / 2;
        int y = mIconRect.bottom + mTextRect.height();
        canvas.drawText(mText, x, y, mTextPaint);
    }

    //在内存里准备mBitmap，setAlpha，纯色，xfermode，图标
    private void setUpTagetBitmap(int alpha) {
        mBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mPaint = new Paint();
        mPaint.setColor(mColor);
        mPaint.setAntiAlias(true);//去锯齿
        mPaint.setDither(true);
        mPaint.setAlpha(alpha);
        mCanvas.drawRect(mIconRect, mPaint);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mPaint.setAlpha(255);
        mCanvas.drawBitmap(mIcon, null, mIconRect, mPaint);
    }

    //从外面直接修改icon的透明度
    public void setIconAlpha(float alpha) {
        this.mAlpha = alpha;
        invalidateView();//重绘

    }

    //重绘
    private void invalidateView() {
        if (Looper.getMainLooper() == Looper.myLooper()) {//判断是否为ui线程
            invalidate();
        } else {
            postInvalidate();
        }
    }

    //保存tabBottom的状态
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATUS, super.onSaveInstanceState());
        bundle.putFloat(STATUS_ALPHA, mAlpha);
        return bundle;
    }

    //返回到原先的状态
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mAlpha = bundle.getFloat(STATUS_ALPHA);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATUS));
        }
        super.onRestoreInstanceState(state);
    }
}

