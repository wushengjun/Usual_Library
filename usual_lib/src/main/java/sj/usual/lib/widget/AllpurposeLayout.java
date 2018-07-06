package sj.usual.lib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;

import sj.usual.lib.R;
import sj.usual.lib.log.MyLg;

/**
 * 万能的layout控件
 * Created by WuShengjun on 2017/10/09.
 */
public class AllpurposeLayout extends SwipeRefreshLayout {
    public static final int DATA = 0;
    public static final int ON_LOADING = 1;
    public static final int NO_DATA = -1;
    public static final int NO_NETWORK = -2;
    public static final int FAIL = -3;
    public static final int ERROR = -4;

    private Context mContext;
    private int faultTolerantX; // 水平容错距离
    private boolean canRefresh; // 是否可以设置刷新
    private boolean refreshViewScale;
    private int refreshViewOffsetStart, refreshViewOffsetEnd;

    private ViewGroup mViewGroup;
    private List<View> dataViews = new ArrayList<>();
    private View noDataView, onLoadingView, noNetworkView, failView, errorView;
    private int mShowViewAs = NO_DATA;
    private boolean allViewsInitFinish;

    public AllpurposeLayout(Context context) {
        this(context, null);
    }

    public AllpurposeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initDefaultAttrs();
        initCustomAttrs(context, attrs);
        initRefreshLayout();
    }

    private void initDefaultAttrs() {
        faultTolerantX = 20;
        canRefresh = true;
        refreshViewScale = false;
        refreshViewOffsetStart = 0;
        refreshViewOffsetEnd = 80;
    }

    private void initCustomAttrs(Context context, AttributeSet attrs) {
        if(attrs == null) return;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AllpurposeLayout);
        final int N = typedArray.getIndexCount();
        for (int i = 0; i < N; i++) {
            initAttr(typedArray.getIndex(i), typedArray);
        }
        typedArray.recycle();
    }

    protected void initAttr(int attr, TypedArray typedArray) {
        if (attr == R.styleable.AllpurposeLayout_faultTolerantX) {
            faultTolerantX = typedArray.getInt(attr, faultTolerantX);
        } else if (attr == R.styleable.AllpurposeLayout_canRefresh) {
            canRefresh = typedArray.getBoolean(attr, canRefresh);
        }
    }

    private void initRefreshLayout() {
        setColorSchemeColors(Color.parseColor("#45cbe6"), Color.parseColor("#ff9800"));
        setProgressBackgroundColorSchemeColor(Color.parseColor("#f4f4f4"));
        setProgressViewOffset(refreshViewScale, refreshViewOffsetStart, refreshViewOffsetEnd); // true则表示启用缩放模式
        setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(onRefreshCallBack != null) {
                    onRefreshCallBack.onRefresh();
                }
            }
        });
    }

    /**
     * 显示某内容View
     * @param viewAs 0：正常数据，1：正在加载，-1：没有数据，-2：网络出错，-3：获取失败，-4：发生错误
     */
    public void showViewAs(int viewAs) {
        if(!allViewsInitFinish) {
            mShowViewAs = viewAs;
            return;
        }
        showDataViews(false); // 隐藏所有布局中的dataView
        if (noDataView != null) noDataView.setVisibility(View.GONE);
        if (onLoadingView != null) onLoadingView.setVisibility(View.GONE);
        if (noNetworkView != null) noNetworkView.setVisibility(View.GONE);
        if (failView != null) failView.setVisibility(View.GONE);
        if (errorView != null) errorView.setVisibility(View.GONE);
        switch (viewAs) {
            case DATA: // 正常数据
                setRefreshEnabled(true); // 调用这个而不调用setEnabled,防止之前是不可刷新的变成了可刷新
                setRefreshing(false);
                showDataViews(true);
                break;
            case NO_DATA: // 没有数据
                setRefreshEnabled(true); // 调用这个而不调用setEnabled,防止之前是不可刷新的变成了可刷新
                setRefreshing(false);
                if (noDataView != null) noDataView.setVisibility(View.VISIBLE);
                break;
            case ON_LOADING: // 正在加载
                setRefreshEnabled(false); // 调用这个而不调用setEnabled,防止之前是不可刷新的变成了可刷新
                setRefreshing(false);
                if (onLoadingView != null) onLoadingView.setVisibility(View.VISIBLE);
                break;
            case NO_NETWORK: // 网络出错
                setRefreshEnabled(true); // 调用这个而不调用setEnabled,防止之前是不可刷新的变成了可刷新
                setRefreshing(false);
                if (noNetworkView != null) noNetworkView.setVisibility(View.VISIBLE);
                break;
            case FAIL: // 获取失败
                setRefreshEnabled(true); // 调用这个而不调用setEnabled,防止之前是不可刷新的变成了可刷新
                setRefreshing(false);
                if (failView != null) failView.setVisibility(View.VISIBLE);
                break;
            case ERROR: // 发生错误
                setRefreshEnabled(true); // 调用这个而不调用setEnabled,防止之前是不可刷新的变成了可刷新
                setRefreshing(false);
                if (errorView != null) errorView.setVisibility(View.VISIBLE);
                break;
            default:
                setRefreshEnabled(true); // 调用这个而不调用setEnabled,防止之前是不可刷新的变成了可刷新
                setRefreshing(false);
                showDataViews(true);
                break;
        }
    }

    private void showDataViews(boolean show) {
        for(View view : dataViews) {
            if(show) {
                view.setVisibility(View.VISIBLE);
            } else {
                view.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
//        Log.e("onFinishInflate", "0=" + getChildAt(0) + ", 1=" + getChildAt(1));
        initAllViews();
    }

    private int contentHeight;
    private int contentWidth;
    private void initAllViews() {
        if(mViewGroup != null) { //
            return;
        } else if(getChildCount() < 2) { // 若没有子View（除了本身的加载的圆圈ImageView）
            mViewGroup = initDefViewGroup();
            addView(mViewGroup);
        } else if(getChildCount() == 2) {
            View view = getChildAt(1); // 0为加载的圆圈
            if (view instanceof AdapterView) {
                mViewGroup = initDefViewGroup();
                removeView(view); // 先移除DataView，才能add，否则子View就不是单个了
                addView(mViewGroup); // 把新的ViewGroup添加进总容器
                mViewGroup.addView(view); // 把DataView添加进新的ViewGroup里
            } else if (view instanceof ScrollView) {
                ScrollView scrollView = (ScrollView) view;
                scrollView.setFillViewport(true); // 让子布局占满ScrollView
                if(scrollView.getChildCount() > 0) {
                    mViewGroup = (ViewGroup) scrollView.getChildAt(0);
                } else {
                    mViewGroup = initDefViewGroup();
                    scrollView.addView(mViewGroup);
                }
            } else {
                mViewGroup = (ViewGroup) view;
            }
        } else {
            throw new IllegalStateException("AllpurposeLayout can host only one direct child");
        }
        initViewGroupChildren(); // 初始化里面的noDataView, noNetworkView, onLoadingView, failView, errorView
    }

    // 若无子View，默认子View容器
    private ViewGroup initDefViewGroup() {
        ViewGroup viewGroup = new LinearLayout(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        viewGroup.setLayoutParams(params);
        return viewGroup;
    }

    /**
     * 初始化里面的noDataView, noNetworkView, onLoadingView, failView, errorView
     */
    private void initViewGroupChildren() {
        for (int i = 0; i < mViewGroup.getChildCount(); i++) {
            dataViews.add(mViewGroup.getChildAt(i));
        }
        setNoDataView(R.layout.allpurposelayout_view_nodata);
        setOnLoadingView(R.layout.allpurposelayout_view_loading);
        setNoNetworkView(R.layout.allpurposelayout_view_no_network);
        setFailView(R.layout.allpurposelayout_view_fail);
        setErrorView(R.layout.allpurposelayout_view_error);
        mViewGroup.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                contentWidth = mViewGroup.getWidth() - mViewGroup.getPaddingLeft() - mViewGroup.getPaddingRight();
                contentHeight = mViewGroup.getHeight() - mViewGroup.getPaddingTop() - mViewGroup.getPaddingBottom();
                if(noDataView != null && contentHeight > 0) {
                    LayoutParams params = noDataView.getLayoutParams();
                    params.width = contentWidth;
                    params.height = contentHeight;
//                        Log.e("noDataView", "contentHeight=" + contentHeight);
                    noDataView.setLayoutParams(params);
                }
                if(onLoadingView != null && contentHeight > 0) {
                    LayoutParams params = onLoadingView.getLayoutParams();
                    params.width = contentWidth;
                    params.height = contentHeight;
//                        Log.e("onLoadingView", "contentHeight=" + contentHeight);
                    onLoadingView.setLayoutParams(params);
                }
                if(noNetworkView != null && contentHeight > 0) {
                    LayoutParams params = noNetworkView.getLayoutParams();
                    params.width = contentWidth;
                    params.height = contentHeight;
//                        Log.e("noNetworkView", "contentHeight=" + contentHeight);
                    noNetworkView.setLayoutParams(params);
                }
                if(failView != null && contentHeight > 0) {
                    LayoutParams params = failView.getLayoutParams();
                    params.width = contentWidth;
                    params.height = contentHeight;
//                        Log.e("failView", "contentHeight=" + contentHeight);
                    failView.setLayoutParams(params);
                }
                if(errorView != null && contentHeight > 0) {
                    LayoutParams params = errorView.getLayoutParams();
                    params.width = contentWidth;
                    params.height = contentHeight;
//                        Log.e("errorView", "contentHeight=" + contentHeight);
                    errorView.setLayoutParams(params);
                }
                if(contentHeight > 0) {
                    allViewsInitFinish = true;
                    showViewAs(mShowViewAs);
                    mViewGroup.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
    }

    public View getNoDataView() {
        return noDataView;
    }

    public void setNoDataView(int layoutId) {
        setNoDataView(View.inflate(getContext(), layoutId, null));
    }

    public void setNoDataView(View noDataView) {
        removeViewAs(this.noDataView);
        this.noDataView = noDataView;
        addViewAs(this.noDataView);
    }

    public View getOnLoadingView() {
        return onLoadingView;
    }

    public void setOnLoadingView(int layoutId) {
        setOnLoadingView(View.inflate(getContext(), layoutId, null));
    }

    public void setOnLoadingView(View onLoadingView) {
        removeViewAs(this.onLoadingView);
        this.onLoadingView = onLoadingView;
        addViewAs(this.onLoadingView);
    }

    public View getNoNetworkView() {
        return noNetworkView;
    }

    public void setNoNetworkView(int layoutId) {
        setNoNetworkView(View.inflate(getContext(), layoutId, null));
    }

    public void setNoNetworkView(View noNetworkView) {
        removeViewAs(this.noNetworkView);
        this.noNetworkView = noNetworkView;
        addViewAs(this.noNetworkView);
    }

    public View getFailView() {
        return failView;
    }

    public void setFailView(int layoutId) {
        setFailView(View.inflate(getContext(), layoutId, null));
    }

    public void setFailView(View failView) {
        removeViewAs(this.failView);
        this.failView = failView;
        addViewAs(this.failView);
    }

    public View getErrorlView() {
        return errorView;
    }

    public void setErrorView(int layoutId) {
        setErrorView(View.inflate(getContext(), layoutId, null));
    }

    public void setErrorView(View errorView) {
        removeViewAs(this.errorView);
        this.errorView = errorView;
        addViewAs(this.errorView);
    }
    
    private void removeViewAs(View view) {
        if(view != null && mViewGroup != null) {
            mViewGroup.removeView(view);
        }
    }

    private void addViewAs(int viewLayoutId) {
        View view = View.inflate(mContext, viewLayoutId, null);
        addViewAs(view);
    }

    private void addViewAs(View view) {
        if(view != null && mViewGroup != null) {
            view.setVisibility(View.GONE);
            mViewGroup.addView(view);
        }
    }

    public void setRefreshEnabled(boolean enable) {
        if(canRefresh) {
            setEnabled(enable);
        } else {
            setEnabled(false);
        }
    }

    private float downX;
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        setChildrenListen(); // 解决里面子布局不是直接ScrollView或ListView时的滑动冲突
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                final float eventX = event.getX();
                float offsetX = Math.abs(eventX - downX);
                if (offsetX > faultTolerantX || event.getPointerCount() > 1) { // 水平距离若大于设定的容错距离，则返回false触摸向子控件传递
                    return false;
                }
                break;
            default:
                break;
        }
        return super.onInterceptTouchEvent(event);
    }

    private boolean first = true;
    private void setChildrenListen() {
        if (first) {
            first = false;
            for (int i = 0; i < getChildCount(); i++) {
                View childView = getChildAt(i);
                if (childView instanceof ViewGroup) { // 若为容器则需要遍历，否则直接是ScrollView或ListView不需解决滑动冲突
                    ViewGroup viewGroup = (ViewGroup) childView;
                    bianliViewGroup(viewGroup);
                }
            }
        }
    }

    public void bianliViewGroup(ViewGroup viewGroup) {
        for (int j = 0; j < viewGroup.getChildCount(); j++) {
            View child = viewGroup.getChildAt(j);
            if (child instanceof ScrollView) {
                ScrollView scrollView = (ScrollView) child;
                setRefreshEnabled(scrollView.getScrollY() == 0);
            } else if (child instanceof AbsListView) {
                AbsListView absListView = (AbsListView) child;
                absListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) { }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                        boolean enable = true;
                        if (view.getChildCount() > 0) {
                            boolean a = view.getFirstVisiblePosition() == 0;
                            boolean b = view.getChildAt(0).getTop() >= view.getPaddingTop();
                            enable = a && b;
                        }
//                        MyLg.e("onScroll", "enable=" + enable + ", paddingTop=" + view.getPaddingTop());
                        setRefreshEnabled(enable);
                    }
                });
            } else if(child instanceof ViewGroup) {
                ViewGroup childGroup = (ViewGroup) child;
                bianliViewGroup(childGroup); // 递归遍历里面所有的View
            }
        }
    }

    public boolean isCanRefresh() {
        return canRefresh;
    }

    public void setCanRefresh(boolean canRefresh) {
        this.canRefresh = canRefresh;
    }

    public int getFaultTolerantX() {
        return faultTolerantX;
    }

    /**
     * 设置下拉刷新时水平容错距离
     * @param faultTolerantX
     */
    public void setFaultTolerantX(int faultTolerantX) {
        this.faultTolerantX = faultTolerantX;
    }

    /**
     * 刷新回调接口
     */
    public interface OnRefreshCallBack {
        void onRefresh();
    }

    private OnRefreshCallBack onRefreshCallBack;

    public OnRefreshCallBack getOnRefreshCallBack() {
        return onRefreshCallBack;
    }

    public void setOnRefreshCallBack(OnRefreshCallBack onRefreshCallBack) {
        this.onRefreshCallBack = onRefreshCallBack;
    }

    private <T extends View> T getView(View parentView, int id) {
        return (T) parentView.findViewById(id);
    }

    @Override
    public void addView(View child) {
        addView(child, -1);
    }

    @Override
    public void addView(View child, int index) {
        checkChild();
        super.addView(child, index);
    }

    @Override
    public void addView(View child, LayoutParams params) {
        addView(child, -1, params);
    }

    @Override
    public void addView(View child, int index, LayoutParams params) {
        checkChild();
        super.addView(child, index, params);
    }

    @Override
    public void addView(View child, int width, int height) {
        checkChild();
        super.addView(child, width, height);
    }

    private void checkChild() {
        if(getChildCount() > 1) { // 其实有两个，第一个为加载的圆圈，第二个才是我们加进去的控件
            throw new IllegalStateException("AllpurposeLayout can host only one direct child");
        }
    }
}
