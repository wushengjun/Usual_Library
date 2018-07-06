package sj.usual.lib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

import sj.usual.lib.R;

/**
 * Created by WuShengjun on 2017/10/11.
 */

public class BGAProgressBar extends ProgressBar {
    private static final String TAG = BGAProgressBar.class.getSimpleName();

    private Paint mPaint;
    private Mode mMode;
    private int mTextColor;
    private int mTextSize;
    private int mTextMargin;
    private int mReachedColor;
    private int mReachedHeight;
    private int mUnReachedColor;
    private int mUnReachedHeight;
    private boolean mIsHiddenText;
    private boolean mIsCapRounded;
    private RoundMode mRoundMode;

    private int mRadius;

    private int mMaxUnReachedEndX;
    private int mMaxStrokeWidth;

    private int mTextHeight;
    private int mTextWidth;

    private RectF mArcRectF;
    private Rect mTextRect = new Rect();

    private float mProgress, mMax;
    private String mText;

    public BGAProgressBar(Context context) {
        this(context, null);
    }

    public BGAProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.progressBarStyle);
    }

    public BGAProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initDefaultAttrs(context);
        initCustomAttrs(context, attrs);

        mMaxStrokeWidth = Math.max(mReachedHeight, mUnReachedHeight);
    }

    private void initDefaultAttrs(Context context) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        mMode = Mode.System;
        mRoundMode = RoundMode.None;
        mTextColor = Color.parseColor("#70A800");
        mTextSize = BGAProgressBar.sp2px(context, 10);
        mTextMargin = BGAProgressBar.dp2px(context, 4);
        mReachedColor = Color.parseColor("#70A800");
        mReachedHeight = BGAProgressBar.dp2px(context, 2);
        mUnReachedColor = Color.parseColor("#CCCCCC");
        mUnReachedHeight = BGAProgressBar.dp2px(context, 1);
        mIsCapRounded = false;
        mIsHiddenText = false;

        mRadius = BGAProgressBar.dp2px(context, 16);
    }

    private void initCustomAttrs(Context context, AttributeSet attrs) {
        if(attrs == null) return;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BGAProgressBar);
        final int N = typedArray.getIndexCount();
        for (int i = 0; i < N; i++) {
            initAttr(typedArray.getIndex(i), typedArray);
        }
        typedArray.recycle();
    }

    protected void initAttr(int attr, TypedArray typedArray) {
        if (attr == R.styleable.BGAProgressBar_bga_pb_mode) {
            int ordinal = typedArray.getInt(attr, Mode.System.ordinal());
            mMode = Mode.values()[ordinal];
        } else if (attr == R.styleable.BGAProgressBar_bga_pb_roundMode) {
            int ordinal = typedArray.getInt(attr, RoundMode.None.ordinal());
            mRoundMode = RoundMode.values()[ordinal];
            setProgressDrawable();
        } else if (attr == R.styleable.BGAProgressBar_bga_pb_textColor) {
            mTextColor = typedArray.getColor(attr, mTextColor);
        } else if (attr == R.styleable.BGAProgressBar_bga_pb_textSize) {
            mTextSize = typedArray.getDimensionPixelOffset(attr, mTextSize);
        } else if (attr == R.styleable.BGAProgressBar_bga_pb_textMargin) {
            mTextMargin = typedArray.getDimensionPixelOffset(attr, mTextMargin);
        } else if (attr == R.styleable.BGAProgressBar_bga_pb_reachedColor) {
            mReachedColor = typedArray.getColor(attr, mReachedColor);
        } else if (attr == R.styleable.BGAProgressBar_bga_pb_reachedHeight) {
            mReachedHeight = typedArray.getDimensionPixelOffset(attr, mReachedHeight);
        } else if (attr == R.styleable.BGAProgressBar_bga_pb_unReachedColor) {
            mUnReachedColor = typedArray.getColor(attr, mUnReachedColor);
        } else if (attr == R.styleable.BGAProgressBar_bga_pb_unReachedHeight) {
            mUnReachedHeight = typedArray.getDimensionPixelOffset(attr, mUnReachedHeight);
        } else if (attr == R.styleable.BGAProgressBar_bga_pb_isCapRounded) {
            mIsCapRounded = typedArray.getBoolean(attr, mIsCapRounded);
            if (mIsCapRounded) {
                mPaint.setStrokeCap(Paint.Cap.ROUND);
            }
        } else if (attr == R.styleable.BGAProgressBar_bga_pb_isHiddenText) {
            mIsHiddenText = typedArray.getBoolean(attr, mIsHiddenText);
        } else if (attr == R.styleable.BGAProgressBar_bga_pb_radius) {
            mRadius = typedArray.getDimensionPixelOffset(attr, mRadius);
        }
    }

    private void setProgressDrawable() {
        if(mRoundMode == RoundMode.None) {

        } else if(mRoundMode == RoundMode.Left) {
            setProgressDrawable(ContextCompat.getDrawable(getContext(), R.drawable.usuallib_layer_list_bgaprogressbar_bg_left_round));
        } else if(mRoundMode == RoundMode.Right) {
            setProgressDrawable(ContextCompat.getDrawable(getContext(), R.drawable.usuallib_layer_list_bgaprogressbar_bg_right_round));
        } else if(mRoundMode == RoundMode.Left_Right) {
            setProgressDrawable(ContextCompat.getDrawable(getContext(), R.drawable.usuallib_layer_list_bgaprogressbar_bg_left_right_round));
        }
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mMode == Mode.System) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else if (mMode == Mode.Horizontal) {
            calculateTextWidthAndHeight();

            int width = MeasureSpec.getSize(widthMeasureSpec);

            int expectHeight = getPaddingTop() + getPaddingBottom();
            if (mIsHiddenText) {
                expectHeight += Math.max(mReachedHeight, mUnReachedHeight);
            } else {
                expectHeight += Math.max(mTextHeight, Math.max(mReachedHeight, mUnReachedHeight));
            }
            int height = resolveSize(expectHeight, heightMeasureSpec);
            setMeasuredDimension(width, height);

            mMaxUnReachedEndX = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        } else if (mMode == Mode.Circle) {
            int expectSize = mRadius * 2 + mMaxStrokeWidth + getPaddingLeft() + getPaddingRight();
            int width = resolveSize(expectSize, widthMeasureSpec);
            int height = resolveSize(expectSize, heightMeasureSpec);
            expectSize = Math.min(width, height);

            mRadius = (expectSize - getPaddingLeft() - getPaddingRight() - mMaxStrokeWidth) / 2;
            if (mArcRectF == null) {
                mArcRectF = new RectF();
            }
            mArcRectF.set(0, 0, mRadius * 2, mRadius * 2);

            setMeasuredDimension(expectSize, expectSize);
        } else if (mMode == Mode.Comet) {
            // TODO
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else if (mMode == Mode.Wave) {
            // TODO
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        if (mMode == Mode.System) {
            super.onDraw(canvas);
        } else if (mMode == Mode.Horizontal) {
            onDrawHorizontal(canvas);
        } else if (mMode == Mode.Circle) {
            onDrawCircle(canvas);
        } else if (mMode == Mode.Comet) {
            // TODO
            super.onDraw(canvas);
        } else if (mMode == Mode.Wave) {
            // TODO
            super.onDraw(canvas);
        }
    }

    private void onDrawHorizontal(Canvas canvas) {
        canvas.save();
        canvas.translate(getPaddingLeft(), getMeasuredHeight() / 2);

        float reachedRatio = getProgress() * 1.0f / getMax();
        float reachedEndX = reachedRatio * mMaxUnReachedEndX;

        if (mIsHiddenText) {
            if (reachedEndX > mMaxUnReachedEndX) {
                reachedEndX = mMaxUnReachedEndX;
            }
            if (reachedEndX > 0) {
                mPaint.setColor(mReachedColor);
                mPaint.setStrokeWidth(mReachedHeight);
                mPaint.setStyle(Paint.Style.STROKE);
                canvas.drawLine(0, 0, reachedEndX, 0, mPaint);
                /*if(mRoundMode == RoundMode.None) {
                    canvas.drawLine(0, 0, reachedEndX, 0, mPaint);
                } else if(mRoundMode == RoundMode.Left) {
                    canvas.drawLine(mReachedHeight, 0, reachedEndX, 0, mPaint);
                    RectF ovalReached = new RectF(0, 0, mReachedHeight, mReachedHeight); // 设置个新的长方形，扫描测量
                    // 画弧，第一个参数是RectF：该类是第二个参数是角度的开始，第三个参数是多少度，
                    // 第四个参数是true的时候画扇形，是false的时候画弧线
                    mPaint.setColor(Color.RED);
                    canvas.drawArc(ovalReached, 90, 180, true, mPaint); // 绘制左边圆角
                } else if(mRoundMode == RoundMode.Right) {
                    canvas.drawLine(mReachedHeight, 0, reachedEndX, 0, mPaint);
                    RectF ovalReached = new RectF(0, 0, mReachedHeight, mReachedHeight); // 设置个新的长方形，扫描测量
                    // 画弧，第一个参数是RectF：该类是第二个参数是角度的开始，第三个参数是多少度，
                    // 第四个参数是true的时候画扇形，是false的时候画弧线
                    mPaint.setColor(Color.RED);
                    canvas.drawArc(ovalReached, 90, 180, true, mPaint); // 绘制左边圆角
                } else if(mRoundMode == RoundMode.Left_Right) {
                    if(mMaxUnReachedEndX - reachedEndX <= mReachedHeight/2) {
                        mPaint.setStrokeCap(Paint.Cap.ROUND);
                    }

                    canvas.drawLine(mReachedHeight, 0, reachedEndX, 0, mPaint);
                    RectF ovalReached = new RectF(0, 0, mReachedHeight, mReachedHeight); // 设置个新的长方形，扫描测量
                    // 画弧，第一个参数是RectF：该类是第二个参数是角度的开始，第三个参数是多少度，
                    // 第四个参数是true的时候画扇形，是false的时候画弧线
                    mPaint.setColor(Color.RED);
                    canvas.drawArc(ovalReached, 90, 180, true, mPaint); // 绘制左边圆角
                }*/
            }

            float unReachedStartX = reachedEndX;
            if (mIsCapRounded) {
                unReachedStartX += (mReachedHeight + mUnReachedHeight) * 1.0f / 2;
            }
            if (unReachedStartX < mMaxUnReachedEndX) {
                mPaint.setColor(mUnReachedColor);
                mPaint.setStrokeWidth(mUnReachedHeight);
                mPaint.setStyle(Paint.Style.STROKE);
                canvas.drawLine(unReachedStartX, 0, mMaxUnReachedEndX, 0, mPaint);
                /*if(mRoundMode == RoundMode.None) {
                    canvas.drawLine(unReachedStartX, 0, mMaxUnReachedEndX, 0, mPaint);
                } else if(mRoundMode == RoundMode.Left) {

                } else if(mRoundMode == RoundMode.Right) {

                } else if(mRoundMode == RoundMode.Left_Right) {
                    RectF ovalUnReached = new RectF(mMaxUnReachedEndX - mUnReachedHeight, 0, mUnReachedHeight, mUnReachedHeight); // 设置个新的长方形，扫描测量
                    canvas.drawLine(unReachedStartX, 0, mMaxUnReachedEndX, 0, mPaint);
                    // 画弧，第一个参数是RectF：该类是第二个参数是角度的开始，第三个参数是多少度，
                    // 第四个参数是true的时候画扇形，是false的时候画弧线
                    mPaint.setColor(Color.RED);
                    canvas.drawArc(ovalUnReached, 270, 180, true, mPaint); // 绘制右边圆角
                }*/
            }
        } else {
            calculateTextWidthAndHeight();
            int maxReachedEndX = mMaxUnReachedEndX - mTextWidth - mTextMargin;
            if (reachedEndX > maxReachedEndX) {
                reachedEndX = maxReachedEndX;
            }
            if (reachedEndX > 0) {
                mPaint.setColor(mReachedColor);
                mPaint.setStrokeWidth(mReachedHeight);
                mPaint.setStyle(Paint.Style.STROKE);

                canvas.drawLine(0, 0, reachedEndX, 0, mPaint);
            }

            mPaint.setTextAlign(Paint.Align.LEFT);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(mTextColor);
            float textStartX = reachedEndX > 0 ? reachedEndX + mTextMargin : reachedEndX;
            canvas.drawText(mText, textStartX, mTextHeight / 2, mPaint);

            float unReachedStartX = textStartX + mTextWidth + mTextMargin;
            if (unReachedStartX < mMaxUnReachedEndX) {
                mPaint.setColor(mUnReachedColor);
                mPaint.setStrokeWidth(mUnReachedHeight);
                mPaint.setStyle(Paint.Style.STROKE);
                canvas.drawLine(unReachedStartX, 0, mMaxUnReachedEndX, 0, mPaint);
            }
        }

        canvas.restore();
    }

    private void onDrawCircle(Canvas canvas) {
        canvas.save();
        canvas.translate(getPaddingLeft() + mMaxStrokeWidth / 2, getPaddingTop() + mMaxStrokeWidth / 2);

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mUnReachedColor);
        mPaint.setStrokeWidth(mUnReachedHeight);
        canvas.drawCircle(mRadius, mRadius, mRadius, mPaint);

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mReachedColor);
        mPaint.setStrokeWidth(mReachedHeight);
        float sweepAngle = getBGAProgress() / getBGAMax() * 360;
        canvas.drawArc(mArcRectF, 0, sweepAngle, false, mPaint);

        if (!mIsHiddenText) {
            calculateTextWidthAndHeight();
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(mTextColor);
            mPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(mText, mRadius, mRadius + mTextHeight / 2, mPaint);
        }

        canvas.restore();
    }

    public synchronized float getBGAProgress() {
        return mProgress;
    }

    public synchronized BGAProgressBar setBGAProgress(float progress) {
        super.setProgress((int) progress);
        this.mProgress = progress;
        postInvalidate();
        return this;
    }

    public float getBGAMax() {
        return mMax;
    }

    public BGAProgressBar setBGAMax(float max) {
        super.setMax((int) max);
        this.mMax = max;
        return this;
    }

    @Override
    public synchronized void setProgress(int progress) {
        setBGAProgress(progress);
    }

    @Override
    public synchronized int getProgress() {
        return super.getProgress();
    }

    @Override
    public synchronized void setMax(int max) {
        setBGAMax(max);
    }

    @Override
    public synchronized int getMax() {
        return super.getMax();
    }

    public Mode getMode() {
        return mMode;
    }

    public BGAProgressBar setMode(Mode mMode) {
        this.mMode = mMode;
        postInvalidate();
        return this;
    }

    private void calculateTextWidthAndHeight() {
        mText = String.format("%d", (int) (getBGAProgress() * 100 / getBGAMax())) + "%";
        mPaint.setTextSize(mTextSize);
        mPaint.setStyle(Paint.Style.FILL);

        mPaint.getTextBounds(mText, 0, mText.length(), mTextRect);
        mTextWidth = mTextRect.width();
        mTextHeight = mTextRect.height();
    }

    public enum Mode {
        System,
        Horizontal,
        Circle,
        Comet,
        Wave
    }

    public enum RoundMode {
        None,
        Left,
        Right,
        Left_Right
    }

    public static int dp2px(Context context, float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
    }

    public static int sp2px(Context context, float spValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, context.getResources().getDisplayMetrics());
    }
}
