package sj.usual.lib.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

import sj.usual.lib.interfac.Pullable;
import sj.usual.lib.log.MyLg;

public class ElasticScrollView extends ScrollView implements Pullable {
	private int mMaxOverScrollY = 0;

	public ElasticScrollView(Context context) {
		super(context);
	}

	public ElasticScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ElasticScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
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
				MyLg.e("overScrollBy", "getScrollY=" + getScrollY() + ", yOffset=" + yOffset);
				if(getScrollY() < 0) { // 顶部OverScroll
					smoothScrollToY(0);
					MyLg.e("overScrollBy", "return true");
					return true; // 消费掉事件
				} else if (canPullUp()) { // 底部OverScroll
					if (getScrollY() > yOffset) {
						smoothScrollToY(getHeight());
						MyLg.e("overScrollBy", "return true");
						return true; // 消费掉事件
					}
				}
				break;
		}
		return super.onTouchEvent(ev);
	}

	private int mScrollY;
	private void smoothScrollToY(int y) {
		mScrollY = y;
		post(mRunnable);
	}

	private Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			smoothScrollTo(0, mScrollY);
		}
	};

	@Override
	public boolean canPullDown() {
		// TODO Auto-generated method stub
		return getScrollY() == 0;
	}

	private int yOffset;
	@Override
	public boolean canPullUp() {
		// TODO Auto-generated method stub
		if(getChildCount() > 0) {
			yOffset = getChildAt(0).getMeasuredHeight() - getHeight();
			return getScrollY() >= yOffset;
		}
		return true;
	}
}