package sj.usual.lib.adapterhelper;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by WuShengjun on 2017-12-11.
 */

public abstract class CommonLvContainsExpandLvAdapter<K, V> extends BaseAdapter {
    protected LayoutInflater mInflater;
    protected Context mContext;
    protected List<List<Map<K, List<V>>>> mDatas;
    protected final @LayoutRes int mItemLayoutId;
    protected final @LayoutRes int mExpandableListViewGroupLayoutId;
    protected final @LayoutRes int mExpandableListViewChildLayoutId;
    protected final @IdRes int mExpandableListViewId;

    protected View mConvertView;

    public CommonLvContainsExpandLvAdapter(Context context, List<List<Map<K, List<V>>>> mDatas, int itemLayoutId,
                                           int expandableListViewId, int expandableListViewGroupLayoutId, int expandableListViewChildLayoutId) {
        this.mContext = context;
        this.mDatas = initNullDatas(mDatas);
        this.mItemLayoutId = itemLayoutId;
        this.mExpandableListViewId = expandableListViewId;
        this.mExpandableListViewGroupLayoutId = expandableListViewGroupLayoutId;
        this.mExpandableListViewChildLayoutId = expandableListViewChildLayoutId;
    }

    public void updateData(List<List<Map<K, List<V>>>> mDatas) {
        this.mDatas = initNullDatas(mDatas);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public List<Map<K, List<V>>> getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder = getViewHolder(position, convertView,
                parent);
        List<Map<K, List<V>>> itemChildList = getItem(position);
        if(itemChildList != null) {
            K key = getItemGroup(position, 0);
            if (key != null) {
                convertItem(viewHolder, key, position);
            }

            ExpandableListView expandableListView = viewHolder.getView(mExpandableListViewId);
            expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                    K key = getItemGroup(position, groupPosition);
                    if (key != null) {
                        return onItemGroupClick(parent, v, position, key, groupPosition, id);
                    }
                    return false;
                }
            });
            expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                    V value = getItemChild(position, groupPosition, childPosition);
                    if (value != null) {
                        return onItemChildClick(parent, v, position, groupPosition, value, childPosition, id);
                    }
                    return false;
                }
            });
            expandableListView.setAdapter(new CommonExpandLvAdapter<K, V>(mContext, itemChildList, mExpandableListViewGroupLayoutId, mExpandableListViewChildLayoutId) {
                @Override
                public void convertGroup(ViewHolder helper, K groupItem, List<V> childrenList, int groupPosition, boolean isExpanded) {
                    convertItemGroup(helper, groupItem, childrenList, groupPosition, isExpanded);
                }

                @Override
                public void convertChild(ViewHolder helper, V childItem, int groupPosition, int childPosition, boolean isLastChild) {
                    convertItemChild(helper, childItem, groupPosition, childPosition, isLastChild);
                    setItemChildChildColor(helper, childPosition);
                }

                @Override
                public boolean isChildCanSelectOrClick(int groupPosition, int childPosition) {
                    return true;
                }
            });

            if (isExpandAllChildGroup()) {
                for (int i = 0; i < itemChildList.size(); i++) {
                    expandableListView.expandGroup(i);
                }
            }
        }
        return viewHolder.getConvertView();
    }

    private K getItemGroup(int itemPosition, int itemGrouPosition) {
        Map<K, List<V>> childGroupItem = getItemGroupMap(itemPosition, itemGrouPosition);
        if (childGroupItem != null) {
            Set<K> keySet = childGroupItem.keySet();
            for (K key : keySet) {
                return key;
            }
        }
        return null;
    }

    private V getItemChild(int itemPosition, int itemGrouPosition, int itemChildPosition) {
        Map<K, List<V>> childGroupMap = getItemGroupMap(itemPosition, itemGrouPosition);
        K itemGroup = getItemGroup(itemPosition, itemGrouPosition);
        if(childGroupMap != null && itemGroup != null) {
            return childGroupMap.get(itemGroup).get(itemChildPosition);
        }
        return null;
    }

    private Map<K, List<V>> getItemGroupMap(int itemPosition, int itemGrouPosition) {
        List<Map<K, List<V>>> itemChildList = getItem(itemPosition);
        if(!itemChildList.isEmpty() && itemGrouPosition < itemChildList.size()) {
            Map<K, List<V>> childGroupItem = itemChildList.get(itemGrouPosition);
            return childGroupItem;
        }
        return null;
    }

    private ViewHolder getViewHolder(int position, View convertView, ViewGroup parent) {
        if(mConvertView != null) {
            return ViewHolder.get(mContext, convertView, parent, mConvertView, position);
        }
        return ViewHolder.get(mContext, convertView, parent, mItemLayoutId, position);
    }

    private List<List<Map<K, List<V>>>> initNullDatas(List<List<Map<K, List<V>>>> datas) {
        if(datas == null) {
            datas = new ArrayList<>();
        }
        return datas;
    }

    protected String getString(int resId) {
        return mContext.getString(resId);
    }

    protected String ifNull(String val) {
        return ifNull(val, "");
    }

    protected String ifNull(String val, String defVal) {
        return TextUtils.isEmpty(val) ? defVal : val;
    }

    private boolean bgItemColorflag;
    private int evenColorId, oddColorId;
    private String defEvenColor = "#f0f0f0", defOddColor = "#ffffff";
    private void setItemChildChildColor(ViewHolder viewHolder, int position) {
        if(bgItemColorflag) {
            View converView = viewHolder.getConvertView();
            try {
                if(position % 2 == 0) {
                    if (evenColorId == 0)
                        converView.setBackgroundColor(Color.parseColor(defEvenColor));
                    else
                        converView.setBackgroundColor(ContextCompat.getColor(mContext, evenColorId));
                } else {
                    if(oddColorId == 0)
                        converView.setBackgroundColor(Color.parseColor(defOddColor));
                    else
                        converView.setBackgroundColor(ContextCompat.getColor(mContext, oddColorId));
                }
            } catch (Exception e) {
                if (position % 2 == 0) {
                    converView.setBackgroundColor(evenColorId);
                } else {
                    converView.setBackgroundColor(oddColorId);
                }
            }
        }
    }

    public void setItemChildChildColors(int evenColorId, int oddColorId) {
        bgItemColorflag = true;
        this.evenColorId = evenColorId;
        this.oddColorId = oddColorId;
    }

    public boolean isBgItemColorflag() {
        return bgItemColorflag;
    }

    public void setBgItemColorflag(boolean bgItemColorflag) {
        this.bgItemColorflag = bgItemColorflag;
    }

    public abstract void convertItem(ViewHolder helper, K item, int position);
    public abstract void convertItemGroup(ViewHolder helper, K groupItem, List<V> childrenList, int groupPosition, boolean isExpanded);
    public abstract void convertItemChild(ViewHolder helper, V childItem, int groupPosition, int childPosition, boolean isLastChild);
    public abstract boolean isExpandAllChildGroup();
    public abstract boolean onItemGroupClick(ExpandableListView parent, View v, int itemPosition, K groupItem, int groupPosition, long id);
    public abstract boolean onItemChildClick(ExpandableListView parent, View v, int itemPosition, int groupPosition, V childItem, int childPosition, long id);
}
