package sj.usual.lib.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import sj.usual.lib.R;

public class OnLoadingDialog extends Dialog {

	private static Context context; // 上下文

	/**
	 * LoadDialog
	 */
	private static OnLoadingDialog loadDialog;
	/**
	 * cancelable, the dialog dimiss or undimiss flag
	 */
	private boolean cancelable;
	/**
	 * if the dialog don't dimiss, what is the tips.
	 */
	private String tipMsg;

	private OnLoadingDialog(Context context) {
		this(context, 0);
		// TODO Auto-generated constructor stub
	}

	private OnLoadingDialog(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
		init();
	}

	/**
	 * the LoadDialog constructor
	 * @param context Context
	 * @param cancelable boolean
	 * @param tipMsg     String
	 */
	private OnLoadingDialog(final Context context, boolean cancelable, String tipMsg) {
		super(context);
		this.context = context;
		this.cancelable = cancelable;
		this.tipMsg = tipMsg;

		init();
	}
	
	private void init() {
//		this.getContext().setTheme(android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
		this.getContext().setTheme(R.style.UsualLib_NoDimEnabledDialogStyle);
		setContentView(R.layout.usuallib_dialog_onloading);
		// 必须放在加载布局后
		setparams();

		TextView tv = (TextView) findViewById(R.id.tv_loading_msg);
		if (!TextUtils.isEmpty(tipMsg)) {
			tv.setVisibility(View.VISIBLE);
			tv.setText(tipMsg);
		}
	}

	private void setparams() {
		this.setCancelable(cancelable);
		this.setCanceledOnTouchOutside(false);
		getWindow().getDecorView().getBackground().setAlpha(0);

		// Dialog宽度
//		WindowManager windowManager = getWindow().getWindowManager();
//		Display display = windowManager.getDefaultDisplay();
//		WindowManager.LayoutParams lp = this.getWindow().getAttributes();
//		Point point = new Point();
//		display.getSize(point);
//		lp.width = (int) (point.x * 0.4);
//		Window window = getWindow();
//		window.setAttributes(lp);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (!cancelable) {
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * show the dialog
	 *
	 * @param context
	 */
	public static void show(Context context) {
		show(context, null, true);
	}

	/**
	 * show the dialog
	 *
	 * @param context Context
	 * @param message String
	 */
	public static void show(Context context, String message) {
		show(context, message, true);
	}

	/**
	 * show the dialog
	 *
	 * @param context    Context
	 * @param resourceId resourceId
	 */
	public static void show(Context context, int resourceId) {
		show(context, context.getResources().getString(resourceId), true);
	}

	/**
	 * show the dialog
	 *
	 * @param context    Context
	 * @param message    String, show the message to user when isCancel is true.
	 * @param cancelable boolean, true is can't dimiss，false is can dimiss
	 */
	private static void show(Context context, String message, boolean cancelable) {
		/*if (loadDialog != null && loadDialog.isShowing()) {

		} else */
		if (context instanceof Activity) {
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
				if (((Activity) context).isFinishing() || ((Activity) context).isDestroyed()) {
					return;
				}
			} else {
				if (((Activity) context).isFinishing()) {
					return;
				}
			}
			dismissDialog();
			loadDialog = new OnLoadingDialog(context, cancelable, message);
			setListeners();
			loadDialog.show();
		} else { // 系统弹窗
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				if (!Settings.canDrawOverlays(context)) {
					return;
				}
			}
			dismissDialog();
			loadDialog = new OnLoadingDialog(context, cancelable, message);
			loadDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
			setListeners();
			loadDialog.show();
		}
	}

	private static void setListeners() {
		if(loadDialog == null) {
			return;
		}
		loadDialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				if(mOnDialogDismissListener != null) {
					mOnDialogDismissListener.onDismiss(dialog);
				}
			}
		});
		loadDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if(mOnKeyMyListener != null) {
					return mOnKeyMyListener.onKey(dialog, keyCode, event);
				}
				return false;
			}
		});
	}

	/**
	 * dismiss the dialog
	 */
	public static void dismissDialog() {
		if (loadDialog != null) {
			loadDialog.dismiss();
			loadDialog = null;
		}
	}

	private static OnDialogDismissListener mOnDialogDismissListener;

	public static OnDialogDismissListener getOnDialogDismissListener() {
		return mOnDialogDismissListener;
	}

	public static void setOnDialogDismissListener(OnDialogDismissListener onDialogDismissListener) {
		mOnDialogDismissListener = onDialogDismissListener;
	}

	public interface OnDialogDismissListener {
		void onDismiss(DialogInterface dialog);
	}

	private static OnKeyMyListener mOnKeyMyListener;

	public static OnKeyMyListener getOnKeyMyListener() {
		return mOnKeyMyListener;
	}

	public static void setOnKeyMyListener(OnKeyMyListener onKeyListener) {
		mOnKeyMyListener = onKeyListener;
	}

	public interface OnKeyMyListener {
		boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event);
	}
}
