package sj.usual.lib.popupwindow;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;

/**
 * 定制的弹框样式
 * @author WuShengjun
 * @date 2017年11月06日
 */
public class CustomPopupWindow extends BasePopupWindow {

	public CustomPopupWindow(View contentView) {
		super(contentView);
	}

	public CustomPopupWindow(int width, int height) {
		super(width, height);
	}

	public CustomPopupWindow(View contentView, int width, int height) {
		super(contentView, width, height);
	}

	@Override
	public CustomPopupWindow setWindowBackground(Drawable drawable) {
		return super.setWindowBackground(drawable);
	}

	@Override
	public CustomPopupWindow setContentBackground(int resId) {
		return super.setContentBackground(resId);
	}

	@Override
	public CustomPopupWindow setOutsideTouchable(boolean outsideTouchable) {
		return super.setOutsideTouchable(outsideTouchable);
	}
}
