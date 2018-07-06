package sj.usual.lib.tool;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WuShengjun on 2017/11/13.
 */

public class ViewTool {
    /**
     * 重置Picker控件的宽度
     * @param frameLayout
     * @param numberWidth
     */
    public static void resizePicker(FrameLayout frameLayout, int numberWidth) {
        resizePicker(frameLayout, -1, numberWidth);
    }

    /**
     * 重置Picker控件的宽度
     * @param frameLayout
     * @param yearWidth
     * @param numberWidth
     */
    public static void resizePicker(FrameLayout frameLayout, int yearWidth, int numberWidth) {
        List<NumberPicker> numberPickerList = findNumberPicker(frameLayout);
        for(int i=0; i<numberPickerList.size(); i++) {
            NumberPicker numberPicker = numberPickerList.get(i);
            if(i == 0 && yearWidth > 0) {
                resizeNumberPicker(numberPicker, yearWidth);
            } else {
                resizeNumberPicker(numberPicker, numberWidth);
            }
        }
    }

    /**
     * 得到viewGroup里面的numberpicker组件
     * @param viewGroup
     * @return
     */
    private static List<NumberPicker> findNumberPicker(ViewGroup viewGroup) {
        List<NumberPicker> npList = new ArrayList<>();
        View child = null;
        if(null != viewGroup) {
            for(int i = 0; i<viewGroup.getChildCount(); i++) {
                child = viewGroup.getChildAt(i);
                if(child instanceof NumberPicker) {
                    npList.add((NumberPicker) child);
                } else if(child instanceof LinearLayout) {
                    List<NumberPicker> result = findNumberPicker((ViewGroup)child);
                    if(result.size() > 0){
                        return result;
                    }
                }
            }
        }
        return npList;
    }

    /**
     * 调整numberpicker大小
     * @param np
     * @param width
     */
    private static void resizeNumberPicker(NumberPicker np, int width){
        resizeNumberPicker(np, width, 0, 0, 0, 0);
    }

    /**
     * 调整numberpicker大小
     * @param np
     * @param width
     * @param marginLeft
     * @param marginTop
     * @param margRight
     * @param marginBottom
     */
    private static void resizeNumberPicker(NumberPicker np, int width, int marginLeft, int marginTop, int margRight, int marginBottom){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(marginLeft, marginTop, margRight, marginBottom);
        np.setLayoutParams(params);
    }

    /**
     * 计算出来的位置，y方向就在anchorView的上面和下面对齐显示，x方向就是与屏幕右边对齐显示
     * 如果anchorView的位置有变化，就可以适当自己额外加入偏移来修正
     * @param anchorView  呼出window的view
     * @param contentView window的内容布局
     * @return window显示的左上角的xOff, yOff坐标
     */
    public static int[] calPopWindowRightPos(final View anchorView, final View contentView) {
        return calWhatViewPos(anchorView, contentView, Location.WindowRight);
    }

    /**
     * 计算出来的位置，y方向就在anchorView的上面和下面对齐显示，x方向就是与屏幕右边对齐显示
     * 如果anchorView的位置有变化，就可以适当自己额外加入偏移来修正
     * @param anchorView  呼出window的view
     * @param contentView window的内容布局
     * @return window显示的左上角的xOff, yOff坐标
     */
    public static int[] calPopWindowLeftPos(final View anchorView, final View contentView) {
        return calWhatViewPos(anchorView, contentView, Location.WindowLeft);
    }

    /**
     * 计算出来的位置，y方向就在anchorView的上面和下面对齐显示，x方向就是与屏幕右边对齐显示
     * 如果anchorView的位置有变化，就可以适当自己额外加入偏移来修正
     * @param anchorView  呼出window的view
     * @param contentView window的内容布局
     * @return window显示的左上角的xOff, yOff坐标
     */
    public static int[] calAtAnchorViewCenterPos(final View anchorView, final View contentView) {
        return calWhatViewPos(anchorView, contentView, Location.AnchorViewCenter);
    }

    /**
     * 计算出来的位置，y方向就在anchorView的上面和下面对齐显示，x方向就是与屏幕右边对齐显示
     * 如果anchorView的位置有变化，就可以适当自己额外加入偏移来修正
     * @param anchorView  呼出window的view
     * @param contentView window的内容布局
     * @return window显示的左上角的xOff, yOff坐标
     */
    public static int[] calAtAnchorViewLeftPos(final View anchorView, final View contentView) {
        return calWhatViewPos(anchorView, contentView, Location.AnchorViewLeft);
    }

    /**
     * 计算出来的位置，y方向就在anchorView的上面和下面对齐显示，x方向就是与屏幕右边对齐显示
     * 如果anchorView的位置有变化，就可以适当自己额外加入偏移来修正
     * @param anchorView  呼出window的view
     * @param contentView window的内容布局
     * @return window显示的左上角的xOff, yOff坐标
     */
    public static int[] calAtAnchorViewRightPos(final View anchorView, final View contentView) {
        return calWhatViewPos(anchorView, contentView, Location.AnchorViewRight);
    }

    private static int[] calWhatViewPos(final View anchorView, final View contentView, Location loc) {
        final int windowPos[] = new int[2];
        final int anchorLoc[] = new int[2];
        // 获取锚点View在屏幕上的左上角坐标位置
        anchorView.getLocationOnScreen(anchorLoc);
        final int anchorHeight = anchorView.getHeight();
        final int anchorWidth = anchorView.getWidth();
        // 获取屏幕的高宽
        final int screenHeight = SystemTool.getScreenHeight(anchorView.getContext());
        final int screenWidth = SystemTool.getScreenWidth(anchorView.getContext());
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        // 计算contentView的高宽
        final int windowHeight = contentView.getMeasuredHeight();
        final int windowWidth = contentView.getMeasuredWidth();
        // 判断需要向上弹出还是向下弹出显示
        final boolean isNeedShowUp = (screenHeight - anchorLoc[1] - anchorHeight < windowHeight);
        if(loc == Location.WindowLeft) {
            windowPos[0] = 0;
        } else if(loc == Location.WindowRight) {
            windowPos[0] = screenWidth - windowWidth;
        } else if(loc == Location.AnchorViewCenter) {
            windowPos[0] = anchorLoc[0] + anchorWidth/2 - windowWidth/2;
        } else if(loc == Location.AnchorViewLeft) {
            windowPos[0] = anchorLoc[0];
        } else if(loc == Location.AnchorViewRight) {
            windowPos[0] = anchorLoc[0] + anchorWidth;
        } else {
            windowPos[0] = anchorLoc[0];
        }
        if (isNeedShowUp) {
            windowPos[1] = anchorLoc[1] - windowHeight;
        } else {
            windowPos[1] = anchorLoc[1] + anchorHeight;
        }
        return windowPos;
    }

    private enum Location {
        WindowLeft, WindowRight, AnchorViewCenter, AnchorViewLeft, AnchorViewRight
    }
}
