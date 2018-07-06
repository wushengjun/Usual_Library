package sj.usual.lib.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import sj.usual.lib.R;
import sj.usual.lib.adapterhelper.CommonAdapter;
import sj.usual.lib.adapterhelper.ViewHolder;

/**
 * Created by WuShengjun on 2017/11/6.
 */

public abstract class ListDialog<E> extends BaseDialog {
    private OnDialogDismissListener onDialogDismissListener;
    private OnListItemClickListener<E> onListItemClickListener;

    private ListView mListView;
    private List<E> itemList;
    private int mListViewLayoutRes;
    private int mItemLayoutRes;

    /**
     * 该构造方法ListView的item默认为android.R.layout.simple_list_item_1，对应TextView的id为android.R.id.text1
     * @param context
     * @param itemList
     */
    public ListDialog(Context context, List<E> itemList) {
        this(context, itemList, 0, true);
    }

    public ListDialog(Context context, List<E> itemList, @LayoutRes int itemLayoutId) {
        this(context, itemList, itemLayoutId, true);
    }

    public ListDialog(Context context, List<E> itemList, @LayoutRes int listViewlayoutRes, @LayoutRes int itemLayoutRes) {
        super(context, true);
        this.itemList = itemList;
        this.mListViewLayoutRes = listViewlayoutRes;
        this.mItemLayoutRes = itemLayoutRes;
        init();
    }

    public ListDialog(Context context, List<E> itemList, @LayoutRes int itemLayoutRes, boolean cancelable) {
        super(context, cancelable);
        this.itemList = itemList;
        this.mItemLayoutRes = itemLayoutRes;
        init();
    }

    private void init() {
        mContentView = View.inflate(mContext, R.layout.usuallib_list_dialog_layout, null);
        mListView = getView(mContentView, R.id.lv_listDialog);
        if(mListViewLayoutRes != 0) {
            mContentView = View.inflate(mContext, mListViewLayoutRes, null);
            checkContentListView(mContentView);
        }
        setContentListView();
        // 获取当前Activity所在的窗体
        mDialogWindow = mDialog.getWindow();
        // 获得窗体的属性
        mDialogLp = mDialogWindow.getAttributes();
    }

    private void checkContentListView(View contentView) {
        if(!(contentView instanceof ListView)) {
            throw new IllegalStateException("The listViewLayoutRes must directly be a ListView!");
        }
        mListView = (ListView) mContentView;
    }

    private void setContentListView() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(onListItemClickListener != null) {
                    onListItemClickListener.onItemClick(adapterView, view, itemList.get(i), i);
                }
            }
        });
        if(mItemLayoutRes == 0) {
            mItemLayoutRes = android.R.layout.simple_list_item_1;
        }
        ListItemAdapter listAdapter = new ListItemAdapter(mContext, itemList, mItemLayoutRes);
        mListView.setAdapter(listAdapter);

        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if(onDialogDismissListener != null) {
                    onDialogDismissListener.onDismiss();
                }
            }
        });
        // 将布局设置给Dialog
        mDialog.setContentView(mContentView);
    }

    /**
     * 设置每个条目
     * @param holder ViewHolder对象
     * @param item 每个条目
     * @param position ListView的position
     */
    public abstract void setItems(ViewHolder holder, E item, int position);

    public ListDialog setContentView(View contentView) {
        mContentView = contentView;
        checkContentListView(contentView);
        setContentListView();
        return this;
    }

    public ListDialog setCancelable(boolean cancelable) {
        return super.setCancelable(cancelable);
    }

    public ListDialog setCanceledOnTouchOutside(boolean cancel) {
        return super.setCanceledOnTouchOutside(cancel);
    }

    public OnDialogDismissListener getOnDialogDismissListener() {
        return onDialogDismissListener;
    }

    public ListDialog setOnDialogDismissListener(OnDialogDismissListener onDialogDismissListener) {
        this.onDialogDismissListener = onDialogDismissListener;
        return this;
    }

    public OnListItemClickListener getOnListItemClickListener() {
        return onListItemClickListener;
    }

    public ListDialog setOnListItemClickListener(OnListItemClickListener<E> onListItemClickListener) {
        this.onListItemClickListener = onListItemClickListener;
        return this;
    }

    @Override
    public ListDialog setWindowBackground(Drawable drawable) {
        return super.setWindowBackground(drawable);
    }

    @Override
    public ListDialog setWindowBackground(@DrawableRes int resource) {
        return super.setWindowBackground(resource);
    }

    @Override
    public ListDialog setWidth(int width) {
        return super.setWidth(width);
    }

    @Override
    public ListDialog setHeight(int height) {
        return super.setHeight(height);
    }

    @Override
    public ListDialog setMinWidth(int minWidth) {
        return super.setMinWidth(minWidth);
    }

    @Override
    public ListDialog setMaxHeight(int maxHeight) {
        return super.setMaxHeight(maxHeight);
    }

    public interface OnDialogDismissListener {
        void onDismiss();
    }

    public interface OnListItemClickListener<I> {
        void onItemClick(AdapterView<?> adapterView, View view, I item, int position);
    }

    private class ListItemAdapter extends CommonAdapter<E> {

        public ListItemAdapter(Context context, E[] mDatas, int itemLayoutId) {
            super(context, mDatas, itemLayoutId);
        }

        public ListItemAdapter(Context context, List<E> mDatas, int itemLayoutId) {
            super(context, mDatas, itemLayoutId);
        }

        @Override
        public void convert(ViewHolder helper, E item, int position) {
            setItems(helper, item, position);
        }
    }
}
