package sj.usual.lib.adapterhelper;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.List;

/**
 * Created by WuShengjun on 2017/9/16.
 */

public class CommonPagerAdapter<V extends View> extends PagerAdapter {
    protected List<V> mPagers;
    protected List<String> mTitles;

    public CommonPagerAdapter(List<V> mPagers) {
        this.mPagers = mPagers;
    }

    public CommonPagerAdapter(List<V> mPagers, List<String> mTitles) {
        this.mPagers = mPagers;
        this.mTitles = mTitles;
    }

    public CommonPagerAdapter(List<V> mPagers, String[] mTitles) {
        this(mPagers, Arrays.asList(mTitles));
    }

    public void updateData(List<V> mPagers, List<String> mTitles) {
        this.mPagers = mPagers;
        this.mTitles = mTitles;
        this.notifyDataSetChanged();
    }

    public void updateData(List<V> mPagers, String[] mTitles) {
        updateData(mPagers, Arrays.asList(mTitles));
    }

    @Override
    public int getCount() {
        return mPagers.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mPagers.get(position));
        return mPagers.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if(getCount() == 0) { // 数据为空时移除所有界面
            container.removeAllViews();
        } else {
            container.removeView(mPagers.get(position));
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(mTitles == null || mTitles.isEmpty()) {
            return super.getPageTitle(position);
        }
        return mTitles.get(position);
    }

    @Override
    public int getItemPosition(Object object) {
        if(getCount() == 0) {
            return POSITION_NONE; // 为了调用nitifyDataSetChanged方法达到刷新界面效果
        }
        return super.getItemPosition(object);
    }
}
