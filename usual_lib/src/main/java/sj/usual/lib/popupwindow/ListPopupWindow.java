package sj.usual.lib.popupwindow;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;

import java.util.List;

import sj.usual.lib.adapterhelper.CommonAdapter;
import sj.usual.lib.adapterhelper.ViewHolder;

/**
 * Created by WuShengjun on 2017/11/6.
 */

public abstract class ListPopupWindow<E> extends BasePopupWindow {
    private ListView listView;
    private List<E> itemList;
    private @IdRes int listViewId;
    private @LayoutRes int itemLayoutRes;
    private ListItemAdapter listAdapter;

    private OnListItemClickListener<E> onListItemClickListener;

    public ListPopupWindow(Context context, List<E> itemList, @LayoutRes int contentLayoutRes, @IdRes int listViewId) {
        this(itemList, View.inflate(context, contentLayoutRes, null), listViewId, 0);
    }

    public ListPopupWindow(List<E> itemList, View contentView, @IdRes int listViewId) {
        this(itemList, contentView, listViewId, 0);
    }

    public ListPopupWindow(Context context, List<E> itemList, @LayoutRes int contentLayoutRes, int listViewId, @LayoutRes int itemLayoutRes) {
        this(itemList, View.inflate(context, contentLayoutRes, null), listViewId, itemLayoutRes);
    }

    public ListPopupWindow(List<E> itemList, View contentView, int listViewId, @LayoutRes int itemLayoutRes) {
        super(contentView);
        this.itemList = itemList;
        this.listViewId = listViewId;
        this.itemLayoutRes = itemLayoutRes;
        init();
    }

    private void init() {
        listView = getView(mContentView, listViewId);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(onListItemClickListener != null) {
                    onListItemClickListener.onItemClick(adapterView, view, itemList.get(i), i);
                }
            }
        });
        if(itemLayoutRes == 0) {
            itemLayoutRes = android.R.layout.simple_list_item_1;
        }
        updateItems(itemList); // 设置数据
    }

    public void updateItems(List<E> itemList) {
        this.itemList = itemList;
        if(listAdapter == null) {
            listAdapter = new ListItemAdapter(mContentView.getContext(), this.itemList, itemLayoutRes);
            listView.setAdapter(listAdapter);
        } else {
            listAdapter.updateData(this.itemList);
        }
    }

    public abstract void setItems(ViewHolder holder, E item, int position);

    public OnListItemClickListener getOnListItemClickListener() {
        return onListItemClickListener;
    }

    public ListPopupWindow setOnListItemClickListener(OnListItemClickListener<E> onListItemClickListener) {
        this.onListItemClickListener = onListItemClickListener;
        return this;
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

    @Override
    public ListPopupWindow setWindowBackground(Drawable drawable) {
        return super.setWindowBackground(drawable);
    }

    @Override
    public ListPopupWindow setWidth(int width) {
        return super.setWidth(width);
    }

    @Override
    public ListPopupWindow setHeight(int height) {
        return super.setHeight(height);
    }
}
