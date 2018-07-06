package sj.usual.lib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.LayoutRes;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;

import java.util.Timer;
import java.util.TimerTask;

import sj.usual.lib.R;
import sj.usual.lib.log.MyLg;
import sj.usual.lib.tool.SystemTool;
import sj.usual.lib.util.ToastUtils;

/**
 * ListView的左滑菜单Itemlayout跟布局, 需和自定义的LeftSlideMenuListView配合使用（继承自HorizontalScrollView）
 * Created by WuShengjun on 2017/11/16.
 */

public class LeftSlideMenuItemLayout extends HorizontalScrollView {
    public static final long CLICK_TIME_MILLIS = 400; // 点击时间
    public static final long LONG_CLICK_TIME_MILLIS = 800; // 长按时间
    public static final int CLICK_DISTANCE = 20; // 点击时的移动误差
    private VelocityTracker mVelocityTracker; // 滑动速率计算

    private ViewGroup directChild; // HorizontalScrollView里的直隶孩子
    private View mContentView, mRightMenuView; // 内容View和右边隐藏的菜单View
    private int menuViewWidth; // 右边菜单的宽度
    private @LayoutRes int rightMenuViewLayoutId; // 左滑右边滑出的菜单LayoutId
    // 滑出比例多少时显示和隐藏菜单，默认是一半
    private float slideScaleToShowMenu, slideScaleToHideMenu;
    // 手势速率单位和速率大于多少像素时显示或隐藏菜单，默认单位是100毫秒，速率大于120像素时就滑出或隐藏
    private int currentVelocityUnit, xVelocityToShowOrHideMenu;

    public LeftSlideMenuItemLayout(Context context) {
        this(context, null);
    }

    public LeftSlideMenuItemLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LeftSlideMenuItemLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initDef();
        initCustomAttrs(context, attrs);
    }

    // 默认参数
    private void initDef() {
        rightMenuViewLayoutId = 0;
        slideScaleToShowMenu = 0.5f;
        slideScaleToHideMenu = 0.5f;
        currentVelocityUnit = 100;
        xVelocityToShowOrHideMenu = 50;

        setOverScrollMode(OVER_SCROLL_NEVER);
        setHorizontalScrollBarEnabled(false);
    }


    private void initCustomAttrs(Context context, AttributeSet attrs) {
        if(attrs == null) return;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LeftSlideMenuItemLayout);
        final int N = typedArray.getIndexCount();
        for (int i = 0; i < N; i++) {
            initAttr(typedArray.getIndex(i), typedArray);
        }
        typedArray.recycle();
    }

    protected void initAttr(int attr, TypedArray typedArray) {
        if(attr == R.styleable.LeftSlideMenuItemLayout_rightMenuView) {
            rightMenuViewLayoutId = typedArray.getResourceId(attr, 0);
            if(rightMenuViewLayoutId != 0) {
                mRightMenuView = View.inflate(getContext(), rightMenuViewLayoutId, null);
            }
        } else if (attr == R.styleable.LeftSlideMenuItemLayout_slideScaleToShowMenu) {
            slideScaleToShowMenu = typedArray.getFloat(attr, 0.5f);
        } else if (attr == R.styleable.LeftSlideMenuItemLayout_slideScaleToHideMenu) {
            slideScaleToHideMenu = typedArray.getFloat(attr, 0.5f);
        } else if (attr == R.styleable.LeftSlideMenuItemLayout_currentVelocityUnit) {
            currentVelocityUnit = typedArray.getInteger(attr, 100);
        } else if (attr == R.styleable.LeftSlideMenuItemLayout_xVelocityToShowOrHideMenu) {
            xVelocityToShowOrHideMenu = typedArray.getInteger(attr, 100);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if(getChildCount() > 0) {
            directChild = (ViewGroup) getChildAt(0); // 获取HorizontalScroll里面第一个也是唯一一个容器
            if(directChild.getChildCount() == 0) {
                mContentView = directChild;
            } else {
                mContentView = directChild.getChildAt(0); // 第一个算内容
            }
            if (mRightMenuView != null) {
                directChild.addView(mRightMenuView);
            } else if (directChild.getChildCount() >= 2) {
                mRightMenuView = directChild.getChildAt(1); // 若有第二个算隐藏的菜单的
            }
            if(mRightMenuView != null) {
                mRightMenuView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mContentView != null) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int screenWidth = SystemTool.getScreenWidth(getContext());
            if(width > screenWidth) {
                width = screenWidth;  // 最大宽度设置为屏幕宽度
            }
            directChild.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            ViewGroup.LayoutParams params = mContentView.getLayoutParams();
            params.width = width;
            params.height = directChild.getMeasuredHeight();
            mContentView.setLayoutParams(params);
            mContentView.setMinimumWidth(width);
        }
    }

    private float downX, downY; // 手指按下的位置
    private float startX, startY; // 记录上一刻手指位置
    private boolean isLeftSlide; // 是否是左滑
    private long downTimeMillis, touchTimeMillis; // 按下的时间点和触摸的总时间
    private boolean isClick = true; // 用来判断是否是点击动作
//    private boolean isDown; // 用来判断手指是否是按下触摸状态
    private boolean isLongClicked, longClickedReturn; // 是否已触发长按
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                MyLg.e("ItemLayout", "dispatchTouchEvent>>>DOWN");
                if(!isOpened) { // 菜单未打开状态下才进行设置，否则会妨碍菜单里的点击事件
                    setPressed(isClick || isSelected());
                }
                if(mRightMenuView != null && mRightMenuView.getVisibility() == View.GONE) {
                    mRightMenuView.setVisibility(View.VISIBLE);
                }
                downX = ev.getX();
                downY = ev.getY();
                downTimeMillis = System.currentTimeMillis();
                touchTimeMillis = 0;
                isClick = true;
                isLongClicked = false;
                break;
            case MotionEvent.ACTION_MOVE:
//                MyLg.e("ItemLayout", "dispatchTouchEvent>>>MOVE");
                if(!isOpened) { // 菜单未打开状态下才进行设置，否则会妨碍菜单里的点击事件
                    setPressed(isClick || isSelected());
                }
                if(isLongClicked) { //
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
//                MyLg.e("ItemLayout", "dispatchTouchEvent>>>UP");
                // 菜单打开状态下，是点击动作且点击除菜单布局外就收起菜单
                if(isOpened && isClick && downX <= getWidth()-getRightMenuViewWidth()) {
                    closeMenu();
                    return true;
                }
            case MotionEvent.ACTION_CANCEL:
                if (!isOpened) { // 菜单未打开状态下才进行设置，否则会妨碍菜单里的点击事件
                    setPressed(isSelected());
                    setSelected(isSelected());
                }
//                MyLg.e("dispatchTouchEvent>UP", isOpened + "," + isClick + ", " + (downX <= getWidth()-getRightMenuViewWidth()) + "," + touchTimeMillis);
                downX = 0;
                downY = 0;
                downTimeMillis = 0;
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                MyLg.e("ItemLayout", "onTouchEvent>>>DOWN");
                initVelocityTracker();
                break;
            case MotionEvent.ACTION_MOVE:
//                MyLg.e("ItemLayout", "onTouchEvent>>>MOVE");
                if(getParent() != null) {
                    if(getScrollX() > 0 || Math.abs(ev.getY()-startY) < Math.abs(ev.getX()-startX)) { // 若垂直滑动距离小于一半水平距离则不滑动ListView
                        // 不允许parentView拦截事件, parentView不滑动
                        getParent().requestDisallowInterceptTouchEvent(true);
                    } else if (!isClick) { // 若在点击或长按过程中不满足了点击或长按条件，也不允许滑动直到抬起动作
                        // 不允许parentView拦截事件, parentView不滑动
                        getParent().requestDisallowInterceptTouchEvent(true);
                    } else { // 没有滑出右边菜单且垂直滑动距离大于一半的水平滑动距离则ListView滑动
                        // parentView拦截事件
                        getParent().requestDisallowInterceptTouchEvent(false);
                    }
                }

                menuViewWidth = getRightMenuViewWidth();
                isLeftSlide = ev.getX() < startX; // 滑动过程中判断
                startX = ev.getX();
                startY = ev.getY();
                initVelocityTracker();
                mVelocityTracker.addMovement(ev); // 把触摸事件传入速率计算实例中
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
//                MyLg.e("ItemLayout", "onTouchEvent>>UP");
                initVelocityTracker();
                mVelocityTracker.computeCurrentVelocity(currentVelocityUnit); // 计算100毫秒滑动了多少像素
                float xVelocity = mVelocityTracker.getXVelocity(); // 获取水平滑动速率
                if(isLeftSlide) { // 向左滑
                    // 滑动距离大于一半或速率大于120像素每100毫秒就显示左滑菜单（可根据需要更改）
                    if(getScrollX() > menuViewWidth*slideScaleToShowMenu || Math.abs(xVelocity) > xVelocityToShowOrHideMenu) {
                        smoothScrollX(menuViewWidth);
                    } else {
                        smoothScrollX(0);
                    }
                } else { // 向右滑
                    // 滑动距离大于一半或速率大于120像素每100毫秒就隐藏左滑菜单（可根据需要更改）
                    if(getScrollX() < menuViewWidth*slideScaleToHideMenu || Math.abs(xVelocity) > xVelocityToShowOrHideMenu) {
                        smoothScrollX(0);
                    } else {
                        smoothScrollX(menuViewWidth);
                    }
                }
                mVelocityTracker.clear();
                break;
        }
        return clickAction(ev); // 点击动作
    }

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Timer longClickTimer;
    private TimerTask longClickTask;
    private Runnable longClickRunnable = new Runnable() {
        @Override
        public void run() {
            longClickedReturn = onLongClickListener.onLongClick(LeftSlideMenuItemLayout.this);
        }
    };
    private boolean clickAction(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 以下为执行延迟长按事件
                longClickTimer = new Timer();
                longClickTask = new TimerTask() {
                    @Override
                    public void run() {
//                        MyLg.e("longClickTask", "longClickTask>>>>run");
                        touchTimeMillis = System.currentTimeMillis() - downTimeMillis;
                        if(getScrollX() == 0 && onLongClickListener != null && touchTimeMillis >= LONG_CLICK_TIME_MILLIS && isClick) {
                            isLongClicked = true;
                            mHandler.post(longClickRunnable);
                        }
                    }
                };
                longClickTimer.schedule(longClickTask, LONG_CLICK_TIME_MILLIS);
                break;
            case MotionEvent.ACTION_MOVE:
                touchTimeMillis = System.currentTimeMillis() - downTimeMillis;
                if(isClick) { // 若这次触摸事件有一次为false就一直为false,不执行点击或长按事件了，直到抬起
                    isClick = Math.abs(ev.getX() - downX) <= CLICK_DISTANCE && Math.abs(ev.getY() - downY) <= CLICK_DISTANCE;
                }
                // 以下长按事件逻辑已用TimerTask来替代
//                if(getScrollX() == 0 && onLongClickListener != null && touchTimeMillis >= LONG_CLICK_TIME_MILLIS && isClick) {
//                    isLongClicked = true;
//                    longClickedReturn = onLongClickListener.onLongClick(this);
//                    return longClickedReturn;
//                }
                break;
            case MotionEvent.ACTION_UP:
                if(getScrollX() == 0 && onClickListener != null && isClick && !isLongClicked) { // 这里判断能否执行点击事件
                    if(!longClickedReturn) { // 若长按事件返回false,则抬起时照样触发点击
                        onClickListener.onClick(this);
                    } else if(touchTimeMillis < LONG_CLICK_TIME_MILLIS) { // 若长按事件不为null,但时间小于长按时间也算点击
                        onClickListener.onClick(this);
                    }
                }
            case MotionEvent.ACTION_CANCEL:
                cancelLongClick(); // 手指抬起就取消延迟长按事件了
                isClick = true;
                isLongClicked = false;
                break;
        }
        return super.onTouchEvent(ev);
    }

    private void cancelLongClick() {
        if(longClickTimer != null) {
            longClickTimer.cancel(); // 手指抬起就取消延迟长按事件了
            longClickTimer = null;
        }
        if(longClickTask != null) {
            longClickTask.cancel();
            longClickTask = null;
        }
    }

    /**
     * 获取手势速率计算实例
     */
    private void initVelocityTracker() {
        if(mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain(); // 获取计算手势速率实例
        }
    }

    private OnClickListener onClickListener;
    private OnLongClickListener onLongClickListener;

    @Override
    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public OnClickListener getOnClickListener() {
        return onClickListener;
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }

    public OnLongClickListener getOnLongClickListener() {
        return onLongClickListener;
    }

    private int getRightMenuViewWidth() {
        if (menuViewWidth == 0 && mRightMenuView != null) {
            menuViewWidth = mRightMenuView.getWidth();
        }
        return menuViewWidth;
    }

    private int mScrollX; // 滑动的水平位置
    private boolean isOpened;
    private void smoothScrollX(int x) { // 平滑到什么位置
        if (x > getRightMenuViewWidth()/2) {
            isOpened = true;
            if (onSlidedListener != null)
                onSlidedListener.opened();
        } else {
            isOpened = false;
            if (onSlidedListener != null)
                onSlidedListener.closed();
        }
        mScrollX = x;
        post(mRunable);
    }
    private Runnable mRunable = new Runnable() {
        @Override
        public void run() {
            smoothScrollTo(mScrollX, 0);
        }
    };

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(mVelocityTracker != null) {
//            MyLg.e("onDetachedFromWindow", "VelocityTracker recycle");
            mVelocityTracker.recycle(); // 回收
            mVelocityTracker = null;
        }
    }

    public View getRightMenuView() {
        return mRightMenuView;
    }

    public void setRightMenuView(int rightMenuViewLayoutId) {
        this.rightMenuViewLayoutId = rightMenuViewLayoutId;
        setRightMenuView(View.inflate(getContext(), rightMenuViewLayoutId, null));
    }

    public void setRightMenuView(View rightMenuView) {
        if (rightMenuView == null || directChild == null)
            return;
        if (mRightMenuView != null) {
            directChild.removeView(mRightMenuView);
        }
        mRightMenuView = rightMenuView;
        directChild.addView(mRightMenuView); // 添加左滑时需画出来的菜单布局
    }

    public int getMenuViewWidth() {
        if(mRightMenuView == null) {
            return 0;
        }
        return mRightMenuView.getWidth();
    }

    public float getSlideScaleToShowMenu() {
        return slideScaleToShowMenu;
    }

    public void setSlideScaleToShowMenu(float slideScaleToShowMenu) {
        this.slideScaleToShowMenu = slideScaleToShowMenu;
    }

    public float getSlideScaleToHideMenu() {
        return slideScaleToHideMenu;
    }

    public void setSlideScaleToHideMenu(float slideScaleToHideMenu) {
        this.slideScaleToHideMenu = slideScaleToHideMenu;
    }

    public int getCurrentVelocityUnit() {
        return currentVelocityUnit;
    }

    public void setCurrentVelocityUnit(int currentVelocityUnit) {
        this.currentVelocityUnit = currentVelocityUnit;
    }

    public int getxVelocityToShowOrHideMenu() {
        return xVelocityToShowOrHideMenu;
    }

    public void setxVelocityToShowOrHideMenu(int xVelocityToShowOrHideMenu) {
        this.xVelocityToShowOrHideMenu = xVelocityToShowOrHideMenu;
    }

    public boolean isOpened() {
        return isOpened;
    }

    public void openMenu() {
        smoothScrollX(menuViewWidth);
    }

    public void closeMenu() {
        smoothScrollX(0);
    }

    private OnSlidedListener onSlidedListener;

    public OnSlidedListener getOnSlidedListener() {
        return onSlidedListener;
    }

    public void setOnSlidedListener(OnSlidedListener onSlidedListener) {
        this.onSlidedListener = onSlidedListener;
    }

    public interface OnSlidedListener {
        void opened();
        void closed();
    }
}
