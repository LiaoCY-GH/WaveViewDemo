package com.example.waveviewdemo;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

public class WaveView extends View {

    private Paint PaintWave;//波浪画笔
    private Paint PaintProgress;//进度画笔
    private float ItemWaveLength;//波浪长度
    private float Radius;//圆半径
    private float WaveHeight = 30;//波浪高度
    private float ProgressTextSize = 60;//进度文字大小
    private Path PathWave;//波浪路径(不透明)
    private Path PathWaveA;//波浪路径(透明)
    private Path WaveCircle;//圆形球的路径
    private float Wave = 0;//波浪的偏移(实现波浪效果的关键)
    private float Progress = 0;//进度
    private Paint.FontMetricsInt FontMetricsInt;//字体度量整型
    private ObjectAnimator WaveobjectAnimator;//属性动画

    public WaveView(Context context) {
        this(context, null);
    }

    public WaveView(Context context, @Nullable AttributeSet attr) {
        this(context, attr, 0);
    }

    public WaveView(Context context, @Nullable AttributeSet attr, int defStyleAttr) {
        super(context, attr, defStyleAttr);

        init();
    }

    //初始化
    private void init() {
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        //波浪画布
        PaintWave = new Paint(Paint.ANTI_ALIAS_FLAG);
        PaintWave.setColor(Color.parseColor("#FFFFFFFF"));//设置画布颜色
        PaintWave.setStyle(Paint.Style.FILL);//设置样式

        //进度画布
        PaintProgress = new Paint(Paint.ANTI_ALIAS_FLAG);
        PaintProgress.setStrokeWidth(10);//设置高
        PaintProgress.setStrokeCap(Paint.Cap.ROUND);//设置笔帽为圆形
        PaintProgress.setStrokeJoin(Paint.Join.ROUND);//设置拐角为圆形
        PaintProgress.setColor(Color.WHITE);//进度颜色设置为白色
        PaintProgress.setTextAlign(Paint.Align.CENTER);//文字设置为居中

        //初始化
        PathWave = new Path();
        PathWaveA = new Path();
        WaveCircle = new Path();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWith(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }


    private int measureWith(int widthMeasureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = 200;
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    //测量高度
    private int measureHeight(int heightMeasureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(heightMeasureSpec);
        int specSize = MeasureSpec.getSize(heightMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = 200;
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Radius = (float) (Math.min(w, h) * 0.9 / 2);
        ItemWaveLength = Radius * 2;//一段波浪长度
        WaveCircle.addCircle(w / 2, h / 2, Radius, Path.Direction.CW);
        ProgressTextSize = Radius * 0.7f;//当控件大小发生改变时，动态修改文字大小
        PaintProgress.setTextSize(ProgressTextSize);
        FontMetricsInt = PaintProgress.getFontMetricsInt();
        WaveHeight = Radius / 8;//当控件大小发生改变时，动态修改画布高度
        invalidate();
        StartWaveAnim();
    }

    //开始画布动画
    @SuppressLint("ObjectAnimatorBinding")
    public void StartWaveAnim() {
        WaveobjectAnimator = ObjectAnimator.ofFloat(this, "wave", 0, ItemWaveLength).setDuration(4000);//设置时长
        WaveobjectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        WaveobjectAnimator.setInterpolator(new LinearInterpolator());
        WaveobjectAnimator.start();
    }

    //设置波浪偏移量（波浪的动画效果核心是靠这个实现的）
    public void setWave(float wave) {
        Wave = wave;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.clipPath(WaveCircle);//将画布裁剪为圆形
        canvas.drawColor(Color.parseColor("#FFFFFF"));
        PathWave.reset();
        PathWaveA.reset();
        //将波浪路径起始点移至圆形球的左侧一段波浪长度（即上下起伏为一段）的位置处
        PathWave.moveTo(getWidth() / 2 - Radius - ItemWaveLength + Wave, getHeight() / 2 + Radius + WaveHeight - Progress * (Radius * 2 + WaveHeight * 2));
        PathWaveA.moveTo(getWidth() / 2 - Radius - ItemWaveLength + Wave + ItemWaveLength / 8, getHeight() / 2 + Radius + WaveHeight - Progress * (Radius * 2 + WaveHeight * 2));

        float half = ItemWaveLength / 4;
        for (float x = -ItemWaveLength; x < getWidth() + ItemWaveLength; x += ItemWaveLength) {
            PathWave.rQuadTo(half / 2, -WaveHeight, half, 0);//贝赛尔曲线实现波浪
            PathWave.rQuadTo(half / 2, WaveHeight, half, 0);
            PathWaveA.rQuadTo(half / 2, -WaveHeight, half, 0);//贝赛尔曲线实现波浪
            PathWaveA.rQuadTo(half / 2, WaveHeight, half, 0);
        }
        PathWave.lineTo(getWidth(), getHeight());
        PathWave.lineTo(0, getHeight());
        PathWave.close();//制造闭合路径
        PathWaveA.lineTo(getWidth(), getHeight());
        PathWaveA.lineTo(0, getHeight());
        PathWaveA.close();//制造闭合路径
        PaintWave.setColor(Color.parseColor("#00FF38"));//设置后面的波浪为半透明
        canvas.drawPath(PathWaveA, PaintWave);
        PaintWave.setColor(Color.parseColor("#CF09FC"));//设置前面的波浪为不透明
        canvas.drawPath(PathWave, PaintWave);
        canvas.drawText((int) (Progress * 100) + "%", getWidth() / 2, getHeight() / 2 + ((FontMetricsInt.bottom - FontMetricsInt.top) / 2 - FontMetricsInt.bottom), PaintProgress);
        canvas.restore();
    }

    //设置进度(不带动画)
    public void setProgress(float progress) {
        Progress = progress;
        invalidate();
    }

    //设置进度(带动画)
    @SuppressLint("ObjectAnimatorBinding")
    public void setProgressWithAnim(float progress) {
        ObjectAnimator.ofFloat(this, "progress", 0, progress).setDuration(5000).start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (WaveobjectAnimator != null) {
            WaveobjectAnimator.cancel();
        }
    }
}
