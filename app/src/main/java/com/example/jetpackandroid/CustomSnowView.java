package com.example.jetpackandroid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CustomSnowView extends View {

    public CustomSnowView(Context context) {
        this(context, null);
    }

    public CustomSnowView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    //画笔
    Paint mPaint;

    //保存点的集合
    List<BobbleBean> mBobbleBeanList;

    public CustomSnowView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mBobbleBeanList = new ArrayList<>();
    }

    //测量
    private int mDefaultWidth = dp2px(100);
    private int mDefaultHeight = dp2px(100);
    //测量过后View的大小也就是画布的大小
    private int mMeasureWidth = 0;
    private int mMeasureHeight = 0;

    /**
     * view 在绘制之前，先要进行测量，会回调方法onMeasure()
     * MeasureSpec类     它是Android系统为我们设计的一个短小而又强悍的一个类，通过它来帮助我们测量View。它是一个32位的int值，
     * 其中前两位代表测量的模式，后30位代表测量出的大小。
     * 测量模式分为三种：
     * EXACTLY 精确值模式，即当我们在布局文件中为View指定了具体的大小，例如：android:layout_width=“100dp”,
     * 或者当我们将View的大小指定为充满父布局，即为match_parent时，此时，该View的测量模式即为EXACTLY模式。
     * (View的默认测量模式为EXACTLY模式)
     * <p>
     * AT_MOST 最大值模式，此时View的尺寸不得大于父控件允许的最大尺寸即可。即对应我们给View的宽或高指定为wrap_content时。
     * <p>
     * UPSPECIFIED 不指定测量模式，即父视图没有限制其大小，子View可以是任何尺寸。该模式一般用于系统内部，平时的Android开发基本用不到。
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取计算相关内容
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthSpecMode == MeasureSpec.EXACTLY) {
            //当specMode = EXACTLY时，精确值模式，即当我们在布局文件中为View指定了具体的大小
            mMeasureWidth = widthSpecSize;
        } else {
            //指定默认大小
            mMeasureWidth = mDefaultWidth;
            if (widthSpecMode == MeasureSpec.AT_MOST) {
                mMeasureWidth = Math.min(mMeasureWidth, widthSpecSize);
            }
        }
        //测量计算View的高
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        if (heightSpecMode == MeasureSpec.EXACTLY) {
            //当specMode = EXACTLY时，精确值模式，即当我们在布局文件中为View指定了具体的大小
            mMeasureHeight = heightSpecSize;
        } else {
            //指定默认大小
            mMeasureHeight = mDefaultHeight;
            if (heightSpecMode == MeasureSpec.AT_MOST) {
                mMeasureHeight = Math.min(mMeasureHeight, heightSpecSize);
            }
        }
        mMeasureHeight = mMeasureHeight - getPaddingBottom() - getPaddingTop();
        mMeasureWidth = mMeasureWidth - getPaddingLeft() - getPaddingBottom();
        //重新测量
        setMeasuredDimension(mMeasureWidth, mMeasureHeight);
    }

    //一个 dp 转 像素的计算
    private int dp2px(int dp) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }
    //第二部排版

    //这里面创建点
    Random mRandom = new Random();

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        for (int i = 0; i < mMeasureWidth / 3; i++) {
            BobbleBean lBobbleBean = new BobbleBean();
            //生成位置信息 随机
            //取值范围 0~ mMeasureWidth
            int x = mRandom.nextInt(mMeasureWidth);
            int y = mRandom.nextInt(mMeasureHeight);

            //绘制使用的位置
            lBobbleBean.postion = new Point(x, y);
            //重置位置
            lBobbleBean.origin = new Point(x, 0);
            //随机的半径 1~4
            lBobbleBean.radius = mRandom.nextFloat() * 3 + dp2px(1);
            //随机速度 3~6
            lBobbleBean.speed = 1 + mRandom.nextInt(3);
            //随机透明度的白色
            lBobbleBean.color = randomWhiteColor();
            mBobbleBeanList.add(lBobbleBean);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制时重新计算位置
        for (BobbleBean lBobbleBean : mBobbleBeanList) {
            Point lPostion = lBobbleBean.postion;
            lPostion.y += lBobbleBean.speed;
            //在竖直方向上增加偏移
            float randValue = mRandom.nextFloat() * 2 - 0.5f;
            lPostion.x += randValue;
            //边界控制
            if (lPostion.y > mMeasureHeight) {
                lPostion.y = 0;
            }
        }
        //现将这些点全部绘制出来
        for (BobbleBean lBobbleBean : mBobbleBeanList) {
            //修改画笔的颜色
            mPaint.setColor(lBobbleBean.color);
            //绘制
            //参数一 二 圆点位置
            //参数三  半径
            //参数 四 画笔
            canvas.drawCircle(lBobbleBean.postion.x, lBobbleBean.postion.y, lBobbleBean.radius, mPaint);
        }
        //循环刷新10 毫秒刷新一次
        postInvalidateDelayed(10L);
    }

    public static int randomWhiteColor() {
        Random random = new Random();
        int a = random.nextInt(200);
        return Color.argb(a, 255, 255, 255);
    }
}
