package sj.usual.lib.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

import sj.usual.lib.interfac.Pullable;
import sj.usual.lib.log.MyLg;

/**
 * 具有弹性的GridView
 * @author WuShengjun
 * @date 2017年2月22日
 */
public class ElasticGridView extends GridView implements Pullable {
	private int mMaxOverScrollY = 0; //

	public ElasticGridView(Context context) {
		super(context);
	}

	public ElasticGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ElasticGridView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
//		MyLg.e("overScrollBy", "scrollY=" + scrollY + ", getScrollY=" + getScrollY());
		return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, mMaxOverScrollY, isTouchEvent);
	}

	private float downY;
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				setOverScrollMode(OVER_SCROLL_ALWAYS);
				downY = ev.getY();
				break;
			case MotionEvent.ACTION_MOVE:
				mMaxOverScrollY = (int) (Math.abs(ev.getY()-downY)*0.2f);
				break;
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				setOverScrollMode(OVER_SCROLL_NEVER);
				MyLg.e("overScrollBy", "getScrollY=" + getScrollY());
				if (getCount() > 0) { // 顶部
					if (getScrollY() < 0) { // 表明已OverScroll
						scrollTo(0, 0); // 回顶部
						MyLg.e("overScrollBy", "return true");
						return true; // 消费掉事件
					} else if (getScrollY() < getMeasuredHeight()-getHeight()) { // 底部
						scrollTo(0, getHeight()); // 回底部
						return true; // 消费掉事件
					}
				}
				break;
		}
		return super.onTouchEvent(ev);
	}

	@Override
	public boolean canPullDown() {
		if (getCount() == 0) {
			// 没有item的时候也可以下拉刷新
			return true;
		} else if (getFirstVisiblePosition() == 0
				&& getChildAt(0).getTop() >= 0) {
			// 滑到ListView的顶部了
			return true;
		} else
			return false;
	}

	@Override
	public boolean canPullUp() {
		if (getCount() == 0) {
			// 没有item的时候也可以上拉加载
			return true;
		} else if (getLastVisiblePosition() == (getCount() - 1)) {
			// 滑到底部了
			if (getChildAt(getLastVisiblePosition() - getFirstVisiblePosition()) != null
					&& getChildAt(getLastVisiblePosition()
					- getFirstVisiblePosition()).getBottom() <= getMeasuredHeight())
				return true;
		}
		return false;
	}
}
