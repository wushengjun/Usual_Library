package sj.usual.lib.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author WuShengjun
 * @date 2017年3月7日
 */
public class WheelView extends ElasticScrollView {
	public static final String TAG = WheelView.class.getSimpleName();
	private String text_color = "#bbbbbb";// 默认字体颜色
	private String text_select_color = "#0288ce";// 默认选中字体颜色
	private String paint_color = "#83cde6";// 选中框的颜色
	private int text_size = 20;
	private int topbottom = 15;//选择器中间textview上下间距
	private int leftright = 15;//选择器中间textview左右间距

	private Bitmap blt;// 选中项背景图片

	/**
	 * 设置 选择器中间textview上下间距
	 * @param topbottom
	 */
	public void setTopbottom(int topbottom) {
		this.topbottom = topbottom;
	}

	/**
	 * 设置选择器中间textview左右间距
	 * @param leftright
	 */
	public void setLeftright(int leftright) {
		this.leftright = leftright;
	}

	/**
	 * 设置字体大小
	 * @param textsize
	 */
	public void setText_size(int textsize){
		this.text_size = textsize;
	}

	public void setBlt(Bitmap mblt) {
		this.blt = mblt;
	}

	public void setText_color(String text_color) {
		this.text_color = text_color;
	}

	public void setText_select_color(String text_select_color) {
		this.text_select_color = text_select_color;
	}

	public void setPaint_color(String paint_color) {
		this.paint_color = paint_color;
	}

	public static class OnWheelViewListener {
		public void onSelected(int selectedIndex, String item) {
		}
	}

	private Context context;
	// private ScrollView scrollView;

	private LinearLayout views;

	public WheelView(Context context) {
		super(context);
		init(context);
	}

	public WheelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public WheelView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	// String[] items;
	List<String> items;

	private List<String> getItems() {
		return items;
	}

	public void setItems(List<String> list) {
		if (null == items) {
			items = new ArrayList<String>();
		}
		items.clear();
		items.addAll(list);

		// 前面和后面补全
		for (int i = 0; i < offset; i++) {
			items.add(0, "");
			items.add("");
		}

		initData();

	}

	public static final int OFF_SET_DEFAULT = 1;
	int offset = OFF_SET_DEFAULT; // 偏移量（需要在最前面和最后面补全）

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	int displayItemCount; // 每页显示的数量

	int selectedIndex = 1;

	private void init(Context context) {
		this.context = context;
		Log.d(TAG, "parent: " + this.getParent());
		this.setVerticalScrollBarEnabled(false);

		views = new LinearLayout(context);
		views.setOrientation(LinearLayout.VERTICAL);
		this.addView(views);

		scrollerTask = new Runnable() {

			public void run() {

				int newY = getScrollY();
				if (initialY - newY == 0) { // stopped
					final int remainder = initialY % itemHeight;
					final int divided = initialY / itemHeight;
					if (remainder == 0) {
						selectedIndex = divided + offset;

						onSeletedCallBack();
					} else {
						if (remainder > itemHeight / 2) {
							WheelView.this.post(new Runnable() {
								@Override
								public void run() {
									WheelView.this.smoothScrollTo(0, initialY
											- remainder + itemHeight);
									selectedIndex = divided + offset + 1;
									onSeletedCallBack();
								}
							});
						} else {
							WheelView.this.post(new Runnable() {
								@Override
								public void run() {
									WheelView.this.smoothScrollTo(0, initialY
											- remainder);
									selectedIndex = divided + offset;
									onSeletedCallBack();
								}
							});
						}

					}

				} else {
					initialY = getScrollY();
					WheelView.this.postDelayed(scrollerTask, newCheck);
				}
			}
		};

	}

	int initialY;
	Runnable scrollerTask;
	int newCheck = 50;

	public void startScrollerTask() {
		initialY = getScrollY();
		this.postDelayed(scrollerTask, newCheck);
	}

	private void initData() {
		displayItemCount = offset * 2 + 1;
		for (String item : items) {
			views.addView(createView(item));
		}
		refreshItemView(0);
	}

	int itemHeight = 0;
	private TextView createView(String item) {
		TextView tv = new TextView(context);
		tv.setLayoutParams(new LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));
		tv.setSingleLine(true);
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, text_size);
		tv.setText(item);
		tv.setGravity(Gravity.CENTER);
		int padding_topbottom = dip2px(topbottom);
		int padding_leftright = dip2px(leftright);
		tv.setPadding(padding_leftright, padding_topbottom, padding_leftright, padding_topbottom);
		if (0 == itemHeight) {
			itemHeight = getViewMeasuredHeight(tv);
			Log.d(TAG, "itemHeight: " + itemHeight);
			views.setLayoutParams(new LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT, itemHeight
							* displayItemCount));
			LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this
					.getLayoutParams();
			this.setLayoutParams(new LinearLayout.LayoutParams(lp.width,
					itemHeight * displayItemCount));
		}
		return tv;
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		refreshItemView(t);
		if (t > oldt) {
			// Log.d(TAG, "向下滚动");
			scrollDirection = SCROLL_DIRECTION_DOWN;
		} else {
			// Log.d(TAG, "向上滚动");
			scrollDirection = SCROLL_DIRECTION_UP;
		}
	}

	private void refreshItemView(int y) {
		int position = y / itemHeight + offset;
		int remainder = y % itemHeight;
		int divided = y / itemHeight;

		if (remainder == 0) {
			position = divided + offset;
		} else {
			if (remainder > itemHeight / 2) {
				position = divided + offset + 1;
			}
		}

		int childSize = views.getChildCount();
		for (int i = 0; i < childSize; i++) {
			TextView itemView = (TextView) views.getChildAt(i);
			if (null == itemView) {
				return;
			}
			if (position == i) {
				itemView.setTextColor(Color.parseColor(text_select_color));
			} else {
				itemView.setTextColor(Color.parseColor(text_color));
			}
		}
	}

	/**
	 * 获取选中区域的边界
	 */
	int[] selectedAreaBorder;
	private int[] obtainSelectedAreaBorder() {
		if (null == selectedAreaBorder) {
			selectedAreaBorder = new int[2];
			selectedAreaBorder[0] = itemHeight * offset;
			selectedAreaBorder[1] = itemHeight * (offset + 1);
		}
		return selectedAreaBorder;
	}

	private int scrollDirection = -1;
	private static final int SCROLL_DIRECTION_UP = 0;
	private static final int SCROLL_DIRECTION_DOWN = 1;

	Paint paint;
	int viewWidth;
	@Override
	public void setBackgroundDrawable(Drawable background) {

		if (viewWidth == 0) {
			viewWidth = ((Activity) context).getWindowManager()
					.getDefaultDisplay().getWidth();
			Log.d(TAG, "viewWidth: " + viewWidth);
		}

		if (blt == null) {

			if (null == paint) {
				paint = new Paint();
				paint.setColor(Color.parseColor(paint_color));
				paint.setStrokeWidth(dip2px(1f));
			}

			background = new Drawable() {
				@Override
				public void draw(Canvas canvas) {
					canvas.drawLine(viewWidth * 1 / 6,
							obtainSelectedAreaBorder()[0], viewWidth * 5 / 6,
							obtainSelectedAreaBorder()[0], paint);
					canvas.drawLine(viewWidth * 1 / 6,
							obtainSelectedAreaBorder()[1], viewWidth * 5 / 6,
							obtainSelectedAreaBorder()[1], paint);
				}

				@Override
				public void setAlpha(int alpha) {

				}

				@Override
				public void setColorFilter(ColorFilter cf) {

				}

				@Override
				public int getOpacity() {
					return PixelFormat.UNKNOWN;
				}
			};
		} else {// 重新绘制图片

			// 获得图片的宽高
			int width = blt.getWidth();
			int height = blt.getHeight();
			// 计算缩放比例
			float scaleHeight = ((float) itemHeight) / height;
			float scaleWidth = ((float) viewWidth) / width;
			// 取得想要缩放的matrix参数
			Matrix matrix = new Matrix();
			matrix.postScale(scaleWidth, scaleHeight);
			// 得到新的图片 www.2cto.com
			blt = Bitmap.createBitmap(blt, 0, 0, width, height, matrix, true);

			if (viewWidth == 0) {
				viewWidth = ((Activity) context).getWindowManager()
						.getDefaultDisplay().getWidth();
				Log.d(TAG, "viewWidth: " + viewWidth);
			}

			if (null == paint) {
				paint = new Paint();
			}

			background = new Drawable() {
				@Override
				public void draw(Canvas canvas) {
					canvas.drawBitmap(blt, 0, itemHeight, paint); // 在新坐标处处开始绘制图片
				}

				@Override
				public void setAlpha(int alpha) {

				}

				@Override
				public void setColorFilter(ColorFilter cf) {

				}

				@Override
				public int getOpacity() {
					return PixelFormat.UNKNOWN;
				}
			};

		}

		super.setBackgroundDrawable(background);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		Log.d(TAG, "w: " + w + ", h: " + h + ", oldw: " + oldw + ", oldh: "
				+ oldh);
		viewWidth = w;
		setBackgroundDrawable(null);
	}

	/**
	 * 选中回调
	 */
	private void onSeletedCallBack() {
		if (null != onWheelViewListener) {
			onWheelViewListener.onSelected(selectedIndex,
					items.get(selectedIndex));
		}

	}

	public void setSeletion(int position) {
		final int p = position;
		selectedIndex = p + offset;
		this.post(new Runnable() {
			@Override
			public void run() {
				WheelView.this.smoothScrollTo(0, p * itemHeight);
			}
		});

	}

	public String getSeletedItem() {
		return items.get(selectedIndex);
	}

	public int getSeletedIndex() {
		return selectedIndex - offset;
	}

	@Override
	public void fling(int velocityY) {
		super.fling(velocityY / 3);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_UP) {

			startScrollerTask();
		}
		return super.onTouchEvent(ev);
	}

	private OnWheelViewListener onWheelViewListener;

	public OnWheelViewListener getOnWheelViewListener() {
		return onWheelViewListener;
	}

	public void setOnWheelViewListener(OnWheelViewListener onWheelViewListener) {
		this.onWheelViewListener = onWheelViewListener;
	}

	private int dip2px(float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	private int getViewMeasuredHeight(View view) {
		int width = MeasureSpec.makeMeasureSpec(0,
				MeasureSpec.UNSPECIFIED);
		int expandSpec = MeasureSpec.makeMeasureSpec(
				Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		view.measure(width, expandSpec);
		return view.getMeasuredHeight();
	}
}
