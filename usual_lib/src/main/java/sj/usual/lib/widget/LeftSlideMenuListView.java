package sj.usual.lib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import sj.usual.lib.R;
import sj.usual.lib.log.MyLg;

/**
 * Item可左滑出菜单的ListView（同一时间只会滑出一个item的菜单），需配合LeftSlideMenuItemLayout使用
 * Created by WuShengjun on 2017/11/17.
 */

public class LeftSlideMenuListView extends ListView {
    // 分隔线边距
    private int dividerMarginLeft, dividerMarginRight, dividerMarginTop, dividerMarginBottom;

    public LeftSlideMenuListView(Context context) {
        this(context, null);
    }

    public LeftSlideMenuListView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.listViewStyle);
    }

    public LeftSlideMenuListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initCustomAttrs(context, attrs);
    }

    private void initCustomAttrs(Context context, AttributeSet attrs) {
        if(attrs == null) return;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LeftSlideMenuListView);
        final int N = typedArray.getIndexCount();
        for (int i = 0; i < N; i++) {
            initAttr(typedArray.getIndex(i), typedArray);
        }
        typedArray.recycle();
        initCustomDivider(); // 分隔线（可设置左右边距的,暂时只支持默认的divider）
    }

    protected void initAttr(int attr, TypedArray typedArray) {
        if(attr == R.styleable.LeftSlideMenuListView_dividerMarginLeft) {
            dividerMarginLeft = typedArray.getDimensionPixelOffset(attr, 0);
        } else if (attr == R.styleable.LeftSlideMenuListView_dividerMarginRight) {
            dividerMarginRight = typedArray.getDimensionPixelOffset(attr, 0);
        } else if (attr == R.styleable.LeftSlideMenuListView_dividerMarginTop) {
            dividerMarginTop = typedArray.getDimensionPixelOffset(attr, 0);
        } else if (attr == R.styleable.LeftSlideMenuListView_dividerMarginBottom) {
            dividerMarginBottom = typedArray.getDimensionPixelOffset(attr, 0);
        }
    }

    private void initCustomDivider() {
        Drawable dividerDrawable = getDivider();
        if(dividerMarginLeft > 0 || dividerMarginRight > 0
                || dividerMarginTop > 0 || dividerMarginBottom > 0) {
            if(dividerDrawable != null) {
                InsetDrawable insetDrawable = new InsetDrawable(dividerDrawable, dividerMarginLeft, dividerMarginTop,
                        dividerMarginRight, dividerMarginBottom);
                setDivider(insetDrawable);
                setDividerHeight(getDividerHeight());
            }
        }
    }

    public void setDividerMargin(int marginLeft, int marginTop, int marginRight, int marginBottom) {
        this.dividerMarginLeft = marginLeft;
        this.dividerMarginTop = marginTop;
        this.dividerMarginRight = marginRight;
        this.dividerMarginBottom = marginBottom;
        initCustomDivider(); // 分隔线（可设置左右边距的,暂时只支持默认的divider）
    }

    private LeftSlideMenuItemLayout mOpenedItemView;
    private LeftSlideMenuItemLayout mTouchItemView;
    private int downPosition, openedItemMenuPosition = -1; // 按下的ItemPosition，打开Menu的ItemPosition
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(onItemClickListener != null || onItemLongClickListener != null) {
            setItemClickListener(); // 注册监听器
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downPosition = pointToPosition((int) ev.getX(), (int) ev.getY());
                mOpenedItemView = getOpenedItemView(); // 先判断可见界面有无菜单滑出
                mTouchItemView = getPointItemView(ev.getX(), ev.getY()); // 获取触碰时的ItemView
                if(mOpenedItemView != null && mTouchItemView != null && !mTouchItemView.isOpened()) {
                    mOpenedItemView.closeMenu();
                    openedItemMenuPosition = -1;
//                    return true;
                }
                if(mOpenedItemView == null && mTouchItemView != null) { // 没有弹出菜单
                    mTouchItemView.setPressed(true);
                }
                break;
            case MotionEvent.ACTION_MOVE:
//                MyLg.e("ListView", "dispatchTouchEvent>MOVE");
//                if(mOpenedItemView != null && mTouchItemView != null && !mTouchItemView.isOpened()) {
//                    return false;
//                }
                if(mOpenedItemView == null && mTouchItemView != null) {
                    mTouchItemView.setPressed(mTouchItemView.isSelected());
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
//                MyLg.e("ListView", "dispatchTouchEvent>UP");
                if(mOpenedItemView != null && mOpenedItemView.isOpened()) {
                    openedItemMenuPosition = downPosition;
                } else {
                    openedItemMenuPosition = -1;
                }
                if(mOpenedItemView == null && mTouchItemView != null) { // 没有弹出菜单
                    mTouchItemView.setPressed(false);
                }
                mTouchItemView = null;
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
//                MyLg.e("ListView", "onInterceptTouchEvent>MOVE");
//                if(mOpenedItemView != null && mTouchItemView != null && mTouchItemView.isOpened()) {
//                    return false;
//                }
                break;
            case MotionEvent.ACTION_UP:
//                MyLg.e("ListView", "onInterceptTouchEvent>UP");
//                if (mOpenedItemView != null && mTouchItemView != null && !mTouchItemView.isOpened()) {
//                    return true;
//                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
//                MyLg.e("ListView", "onTouchEvent>MOVE");
//                if(mOpenedItemView != null) {
//                    return true;
//                }
                break;
            case MotionEvent.ACTION_UP:
//                MyLg.e("ListView", "onTouchEvent>UP");
                break;
            case MotionEvent.ACTION_CANCEL:
//                MyLg.e("ListView", "onTouchEvent>CANCEL");
                break;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        mOpenedItemView = getOpenedItemView();
        if (mOpenedItemView != null && mOpenedItemView.isOpened()) {
            mOpenedItemView.closeMenu();
        }
    }

    /**
     * 获取手指按下时的ItemView
     * @param downX
     * @param downY
     * @return
     */
    public LeftSlideMenuItemLayout getPointItemView(float downX, float downY) {
        int firstVisiblePosition = getFirstVisiblePosition();
        int lastVisiblePosition = getLastVisiblePosition();
        int position = firstVisiblePosition;
        while (position <= lastVisiblePosition && position < getCount()) {
            View itemView = getChildAt(position - firstVisiblePosition); // 从第一个可见的View开始
            if (itemView instanceof LeftSlideMenuItemLayout) {
                LeftSlideMenuItemLayout itemLayout = (LeftSlideMenuItemLayout) itemView;
//                MyLg.e("getItemViewAt", "downY=" + downY + ", position=" + position);
                if (itemLayout.getLeft() <= downX && itemLayout.getRight() >= downX
                        && itemLayout.getTop() <= downY && itemLayout.getBottom() >= downY) {
                    return itemLayout;
                }
            }
            position++;
        }
        return null;
    }

    /**
     * 获取此时菜单打开时的ItemView
     * @return return opened item view or return null.
     */
    public LeftSlideMenuItemLayout getOpenedItemView() {
        int firstVisiblePosition = getFirstVisiblePosition();
        int lastVisiblePosition = getLastVisiblePosition();
        int position = firstVisiblePosition;
        LeftSlideMenuItemLayout openedItemLayout = null;
        while (position <= lastVisiblePosition && position < getCount()) {
            View itemView = getChildAt(position - firstVisiblePosition); // 从第一个可见的View开始
            if (itemView instanceof LeftSlideMenuItemLayout) {
                LeftSlideMenuItemLayout itemLayout = (LeftSlideMenuItemLayout) itemView;
                if (itemLayout.isOpened()) {
                    openedItemLayout = itemLayout;
                }
            }
            position++;
        }
        return openedItemLayout;
    }

    /**
     * 获取打开的菜单布局View
     * @return return opened menu view of item or return null.
     */
    public View getOpenedItemMenu() {
        LeftSlideMenuItemLayout openedItemView = getOpenedItemView();
        if(openedItemView == null)
            return null;
        return openedItemView.getRightMenuView();
    }

    /**
     * 获取此时打开菜单的ItemPosition
     * @return return opened item position or return -1.
     */
    public int getOpenedItemMenuPosition() {
        return openedItemMenuPosition;
    }

    private void setItemClickListener() {
        final int firstVisiblePosition = getFirstVisiblePosition();
        int lastVisiblePosition = getLastVisiblePosition();
        int position = firstVisiblePosition;
        while (position <= lastVisiblePosition && position < getCount()) {
            View itemView = getChildAt(position - firstVisiblePosition); // 从第一个可见的View开始
            if (itemView instanceof LeftSlideMenuItemLayout) {
                LeftSlideMenuItemLayout itemLayout = (LeftSlideMenuItemLayout) itemView;
                if(onItemClickListener != null && itemLayout.getOnClickListener() == null) {// 如果没有设置点击事件
                    itemLayout.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(mOpenedItemView == null)
                                onItemClickListener.onItemClick(v, getItemAtPosition(downPosition), downPosition);
                        }
                    });
                }
                if(onItemLongClickListener != null && itemLayout.getOnLongClickListener() == null) {
                    itemLayout.setOnLongClickListener(new OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            if(mOpenedItemView == null)
                                return onItemLongClickListener.onItemLongClick(v, getItemAtPosition(downPosition), downPosition);
                            else
                                return false;
                        }
                    });
                }
            } else {
                if(onItemClickListener != null && super.getOnItemClickListener() == null) {
                    super.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            onItemClickListener.onItemClick(view, getItemAtPosition(position), position);
                        }
                    });
                }
                if(onItemLongClickListener != null && super.getOnItemLongClickListener() == null) {
                    super.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                            return onItemLongClickListener.onItemLongClick(view, getItemAtPosition(position), position);
                        }
                    });
                }
            }
            position++;
        }
    }

    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    @Nullable
    public OnItemClickListener getMyOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnMyItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public OnItemLongClickListener getMyOnItemLongClickListener() {
        return onItemLongClickListener;
    }

    public void setOnMyItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, Object dataItem, int position);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(View itemView, Object dataItem, int position);
    }
}
