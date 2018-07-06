package sj.usual.lib.popupwindow;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

/**
 * Created by WuShengjun on 2017/11/6.
 */

public class BasePopupWindow {
    protected static final int CENTER = Gravity.CENTER;
    protected static final int BOTTOM = Gravity.BOTTOM;
    protected static final int TOP = Gravity.TOP;
    protected static final int LEFT = Gravity.LEFT;
    protected static final int RIGHT = Gravity.RIGHT;

    protected PopupWindow mPopupWindow;
    protected View mContentView;
    protected boolean mOutsideTouchable = false;

    protected int mWidth, mHeight;

    public BasePopupWindow(Context context, @LayoutRes int contentLayoutRes) {
        this(View.inflate(context, contentLayoutRes, null), 0, 0);
    }

    public BasePopupWindow(View contentView) {
        this(contentView, 0, 0);
    }

    public BasePopupWindow(View contentView, boolean outsideTouchable) {
        this(contentView, 0, 0);
    }

    public BasePopupWindow(int width, int height) {
        this(null, width, height);
    }

    public BasePopupWindow(View contentView, int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
        this.mContentView = contentView;
        init();
    }

    private void init() {
        if(mContentView == null) {
            mPopupWindow = new PopupWindow(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        } else {
            mPopupWindow = new PopupWindow(mContentView);
        }

        if(mWidth > 0 || mWidth == ViewGroup.LayoutParams.MATCH_PARENT) {
            mPopupWindow.setWidth(mWidth);
        } else {
            mPopupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        if(mHeight > 0 || mHeight == ViewGroup.LayoutParams.MATCH_PARENT) {
            mPopupWindow.setHeight(mHeight);
        } else {
            mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if(onPopupWindowDismissListener != null) {
                    onPopupWindowDismissListener.onDismiss();
                }
            }
        });

        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#f0f0f0")));
//         设置PopupWindow弹出窗体可点击
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(mOutsideTouchable);
        //软键盘不会挡着popupwindow  
        mPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    public void showDown(View view) {
        if (!mPopupWindow.isShowing()) {
            mPopupWindow.showAsDropDown(view);
        } else {
            mPopupWindow.dismiss();
        }
    }

    @TargetApi(19)
    public void showDown(View view, int gravity) {
        showDown(view, 0, 0, gravity);
    }

    @TargetApi(19)
    public void showDown(View view, int xff, int yff) {
        showDown(view, xff, yff, Gravity.LEFT);
    }

    @TargetApi(19)
    public void showDown(View view, int xff, int yff, int gravity) {
        if(!mPopupWindow.isShowing()) {
            mPopupWindow.showAsDropDown(view, xff, yff, gravity);
        } else {
            mPopupWindow.dismiss();
        }
    }

    public void showAt(View view, int gravity) {
        showAt(view, gravity, 0, 0);
    }

    public void showAt(View view, int gravity, int xff, int yff) {
        if(!mPopupWindow.isShowing()) {
            mPopupWindow.showAtLocation(view, gravity, xff, yff);
        } else {
            mPopupWindow.dismiss();
        }
    }

    public void dismiss() {
        if (mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }

    public boolean isShowing() {
        return mPopupWindow.isShowing();
    }

    public <T extends BasePopupWindow> T setWindowBackground(Drawable drawable) {
        mPopupWindow.setBackgroundDrawable(drawable);
        return (T) this;
    }

    public <T extends BasePopupWindow> T setContentBackground(int resId) {
        if(mContentView != null) {
            mContentView.setBackgroundResource(resId);
        }
        return (T) this;
    }

    public <T extends BasePopupWindow> T setContentBackground(Drawable drawable) {
        if(mContentView != null) {
            if (Build.VERSION.SDK_INT >= 16) {
                mContentView.setBackground(drawable);
            } else {
                mContentView.setBackgroundDrawable(drawable);
            }
        }
        return (T) this;
    }

    public <T extends BasePopupWindow> T setContentBackgroundColor(int color) {
        if(mContentView != null) {
            mContentView.setBackgroundResource(color);
        }
        return (T) this;
    }

    public <T extends BasePopupWindow> T setContentBackgroundColorRes(int colorId) {
        if(mContentView != null) {
            mContentView.setBackgroundColor(ContextCompat.getColor(mContentView.getContext(), colorId));
        }
        return (T) this;
    }

    public View getContentView() {
        return mContentView;
    }

    public <T extends BasePopupWindow> T setContentView(View contentView) {
        this.mContentView = contentView;
        mPopupWindow.setContentView(this.mContentView);
        contentViewSetOnKeyListen();
        return (T) this;
    }

    public boolean getOutsideTouchable() {
        return mOutsideTouchable;
    }

    public <T extends BasePopupWindow> T setOutsideTouchable(boolean outsideTouchable) {
        this.mOutsideTouchable = outsideTouchable;
        mPopupWindow.setOutsideTouchable(mOutsideTouchable);
        return (T) this;
    }

    public int getWidth() {
        return mWidth;
    }

    public <T extends BasePopupWindow> T setWidth(int width) {
        this.mWidth = width;
        mPopupWindow.setWidth(width);
        mContentView.setMinimumWidth(width);
        return (T) this;
    }

    public int getHeight() {
        return mHeight;
    }

    public <T extends BasePopupWindow> T setHeight(int height) {
        this.mHeight = height;
        mPopupWindow.setHeight(height);
        mContentView.setMinimumHeight(height);
        return (T) this;
    }

    protected <T extends View> T getView(View parent, int id) {
        return (T) parent.findViewById(id);
    }

    private OnKeyListener mOnKeyListener;

    private OnPopupWindowDismissListener onPopupWindowDismissListener;

    public OnKeyListener getOnKeyListener() {
        return mOnKeyListener;
    }

    public <T extends BasePopupWindow> T setOnKeyListener(OnKeyListener onKeyListener) {
        this.mOnKeyListener = onKeyListener;
        contentViewSetOnKeyListen();
        return (T) this;
    }

    public OnPopupWindowDismissListener getOnPopupWindowDismissListener() {
        return onPopupWindowDismissListener;
    }

    public <T extends BasePopupWindow> T setOnPopupWindowDismissListener(OnPopupWindowDismissListener onPopupWindowDismissListener) {
        this.onPopupWindowDismissListener = onPopupWindowDismissListener;
        return (T) this;
    }

    public interface OnKeyListener {
        boolean onKey(View contentView, int keyCode, KeyEvent event);
    }

    public interface OnPopupWindowDismissListener {
        void onDismiss();
    }

    public void contentViewSetOnKeyListen() {
        if(mContentView != null && mOnKeyListener != null) {
            mContentView.setFocusableInTouchMode(true);
            mContentView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    return mOnKeyListener.onKey(mContentView, keyCode, event);
                }
            });
        }
    }
}
