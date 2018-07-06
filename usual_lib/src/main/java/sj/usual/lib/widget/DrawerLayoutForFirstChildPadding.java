package sj.usual.lib.widget;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * 子布局padding
 * Created by WuShengjun on 2017/6/28.
 */

public class DrawerLayoutForFirstChildPadding extends DrawerLayout {
    public DrawerLayoutForFirstChildPadding(Context context) {
        super(context);
    }

    public DrawerLayoutForFirstChildPadding(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawerLayoutForFirstChildPadding(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    private View firstChildView;
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        firstChildView = getChildAt(0);
        Log.e("DL>onFinishInflate", "firstChildView=" + firstChildView);
    }

    public void setFirstChildViewPaddingLeft(int paddlingLeft) {
        if(firstChildView != null) {
            firstChildView.setPadding(firstChildView.getPaddingLeft() + paddlingLeft, firstChildView.getPaddingTop(),
                    firstChildView.getPaddingRight(), firstChildView.getPaddingBottom());
        }
    }

    public void setFirstChildViewPaddingTop(int paddlingTop) {
        if(firstChildView != null) {
            firstChildView.setPadding(firstChildView.getPaddingLeft(), firstChildView.getPaddingTop() + paddlingTop,
                    firstChildView.getPaddingRight(), firstChildView.getPaddingBottom());
        }
    }

    public void setFirstChildViewPaddingRight(int paddingRight) {
        if(firstChildView != null) {
            firstChildView.setPadding(firstChildView.getPaddingLeft(), firstChildView.getPaddingTop(),
                    firstChildView.getPaddingRight() + paddingRight, firstChildView.getPaddingBottom());
        }
    }

    public void setFirstChildViewPaddingBottom(int paddingBottom) {
        if(firstChildView != null) {
            firstChildView.setPadding(firstChildView.getPaddingLeft(), firstChildView.getPaddingTop(),
                    firstChildView.getPaddingRight(), firstChildView.getPaddingBottom() + paddingBottom);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        return super.onTouchEvent(ev);
    }
}
