package sj.usual.lib.adapterhelper;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * ExpandableListView的通用设配器
 * K group的泛型
 * V child的泛型
 * Created by WuShengjun on 2017/8/22.
 */

public abstract class CommonExpandLvAdapter<K, V> extends BaseExpandableListAdapter {

    protected Context mContext;
    protected List<Map<K, List<V>>> mItems;
    protected final int groupLayoutId;
    protected final int childLayoutId;

    private boolean bgItemColorflag;
    private int evenColorId, oddColorId;
    private String defEvenColor = "#f0f0f0", defOddColor = "#ffffff";

    public CommonExpandLvAdapter (Context mContext, List<Map<K, List<V>>> mItems, int groupLayoutId, int childLayoutId) {
        this.mContext = mContext;
        this.mItems = mItems;
        this.groupLayoutId = groupLayoutId;
        this.childLayoutId = childLayoutId;

        checkNullData();
    }

    public void updateData(List<Map<K, List<V>>> mItems) {
        this.mItems = mItems;
        checkNullData();
        this.notifyDataSetChanged();
    }

    private void checkNullData() {
        if(mItems == null) {
            mItems = new ArrayList<>();
        }
    }

    @Override
    public int getGroupCount() {
        return mItems.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return getChildren(groupPosition).size();
    }

    @Override
    public Map<K, List<V>> getGroup(int groupPosition) {
        return mItems.get(groupPosition);
    }

    @Override
    public V getChild(int groupPosition, int childPosition) {
       return getChildren(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder = getViewHolder(groupLayoutId, groupPosition, convertView, parent);
        Map<K, List<V>> groupItem = getGroup(groupPosition);
        Set<K> keySet = groupItem.keySet();
        for(K key : keySet) {
            convertGroup(viewHolder, key, groupItem.get(key), groupPosition, isExpanded);
            break;
        }
        return viewHolder.getConvertView();
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder = getViewHolder(childLayoutId, childPosition, convertView, parent);
        convertChild(viewHolder, getChild(groupPosition, childPosition), groupPosition, childPosition, isLastChild);
        setItemColor(viewHolder, childPosition);
        return viewHolder.getConvertView();
    }

    @Override
    /**
     * 子条目是否可点击或选中
     * @param groupPosition
     * @param childPosition
     * @return if true can, or not
     */
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return isChildCanSelectOrClick(groupPosition, childPosition);
    }

    private K getGroupItem(int groupPosition) {
        Map<K, List<V>> childMap = mItems.get(groupPosition);
        Set<K> keySet = childMap.keySet();
        for(K key : keySet) {
            return key;
        }
        return null;
    }

    private List<V> getChildren(int groupPosition) {
        K groupItem = getGroupItem(groupPosition);
        if(groupItem != null) {
            return getGroup(groupPosition).get(groupItem);
        }
        return new ArrayList<>();
    }

    private ViewHolder getViewHolder(int layoutId, int position, View convertView, ViewGroup parent) {
        return ViewHolder.get(mContext, convertView, parent, layoutId, position);
    }

    private void setItemColor(ViewHolder viewHolder, int position) {
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

    public void setItemColors(int evenColorId, int oddColorId) {
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

    protected String getString(int resId) {
        return mContext.getString(resId);
    }

    protected String ifNull(String val) {
        return ifNull(val, "");
    }

    protected String ifNull(String val, String defVal) {
        return TextUtils.isEmpty(val) ? defVal : val;
    }

    public abstract void convertGroup(ViewHolder helper, K groupItem, List<V> childrenList, int groupPosition, boolean isExpanded);
    public abstract void convertChild(ViewHolder helper, V childItem, int groupPosition, int childPosition, boolean isLastChild);
    public abstract boolean isChildCanSelectOrClick(int groupPosition, int childPosition);
}
