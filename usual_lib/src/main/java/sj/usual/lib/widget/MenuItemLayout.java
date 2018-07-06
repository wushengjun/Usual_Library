package sj.usual.lib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import sj.usual.lib.R;

/**
 * Created by WuShengjun on 2017/10/17.
 */

public class MenuItemLayout extends LinearLayout {
    private static final String TAG = MenuItemLayout.class.getSimpleName();

    private int itemPadding;
    private int itemPaddingLeft;
    private int itemPaddingRight;
    private int itemPaddingTop;
    private int itemPaddingBottom;
    private Drawable startIcon;
    private int startIconPadding;
    private int startIconPaddingLeft;
    private int startIconPaddingRight;
    private int startIconPaddingTop;
    private int startIconPaddingBottom;
    private Drawable endIcon;
    private int endIconPadding;
    private int endIconPaddingLeft;
    private int endIconPaddingRight;
    private int endIconPaddingTop;
    private int endIconPaddingBottom;
    private CharSequence text;
    private int textSize;
    private int textColor;
    private CharSequence describtion;
    private int describtionTextSize;
    private int describtionTextColor;
    private int lineColor;
    private int lineHeight;
    private int lineMarginStart;
    private int lineMarginEnd;

    public MenuItemLayout(Context context) {
        this(context, null);
    }

    public MenuItemLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MenuItemLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initDefaultAttrs(context);
        if(attrs != null) {
            initCustomAttrs(context, attrs);
        }
        addContent(context);
    }

    private void initDefaultAttrs(Context context) {
        itemPadding = dp2px(context, 0);
        itemPaddingLeft = dp2px(context, 10);
        itemPaddingRight = dp2px(context, 10);
        itemPaddingTop = dp2px(context, 10);
        itemPaddingBottom = dp2px(context, 10);
        startIconPadding = dp2px(context, 0);
        startIconPaddingLeft = dp2px(context, 0);
        startIconPaddingRight = dp2px(context, 0);
        startIconPaddingTop = dp2px(context, 0);
        startIconPaddingBottom = dp2px(context, 0);
        endIconPadding = dp2px(context, 0);
        endIconPaddingLeft = dp2px(context, 12);
        endIconPaddingRight = dp2px(context, 0);
        endIconPaddingTop = dp2px(context, 0);
        endIconPaddingBottom = dp2px(context, 0);
        text = "";
        textColor = Color.parseColor("#404040");
        textSize = sp2px(context, 14);
        describtion = "";
        describtionTextColor = Color.parseColor("#808080");
        describtionTextSize = sp2px(context, 14);
        lineColor = Color.parseColor("#d0d0d0");
        lineHeight = 1; // px
        lineMarginStart = dp2px(context, 12);
        lineMarginEnd = dp2px(context, 0);
    }

    private void initCustomAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MenuItemLayout);
        final int count = typedArray.getIndexCount();
        for (int i = 0; i < count; i++) {
            initAttr(typedArray.getIndex(i), typedArray);
        }
        typedArray.recycle();
    }

    protected void initAttr(int attr, TypedArray typedArray) {

        if (attr == R.styleable.MenuItemLayout_itemPadding) {
            if (typedArray.hasValue(attr)) {
                itemPadding = typedArray.getDimensionPixelOffset(attr, itemPadding);
                itemPaddingLeft = itemPadding;
                itemPaddingTop = itemPadding;
                itemPaddingRight = itemPadding;
                itemPaddingBottom = itemPadding;
            }
        } else if (attr == R.styleable.MenuItemLayout_itemPaddingLeft) {
            itemPaddingLeft = typedArray.getDimensionPixelOffset(attr, itemPaddingLeft);
        } else if (attr == R.styleable.MenuItemLayout_itemPaddingRight) {
            itemPaddingRight = typedArray.getDimensionPixelOffset(attr, itemPaddingRight);
        } else if (attr == R.styleable.MenuItemLayout_itemPaddingTop) {
            itemPaddingTop = typedArray.getDimensionPixelOffset(attr, itemPaddingTop);
        } else if (attr == R.styleable.MenuItemLayout_itemPaddingBottom) {
            itemPaddingBottom = typedArray.getDimensionPixelOffset(attr, itemPaddingBottom);
        } else if (attr == R.styleable.MenuItemLayout_startIcon) {
            startIcon = typedArray.getDrawable(attr);
        } else if (attr == R.styleable.MenuItemLayout_startIconPadding) {
            if (typedArray.hasValue(attr)) {
                startIconPadding = typedArray.getDimensionPixelOffset(attr, startIconPadding);
                startIconPaddingLeft = startIconPadding;
                startIconPaddingTop = startIconPadding;
                startIconPaddingRight = startIconPadding;
                startIconPaddingBottom = startIconPadding;
            }
        } else if (attr == R.styleable.MenuItemLayout_startIconPaddingLeft) {
            startIconPaddingLeft = typedArray.getDimensionPixelOffset(attr, startIconPaddingLeft);
        } else if (attr == R.styleable.MenuItemLayout_startIconPaddingRight) {
            startIconPaddingRight = typedArray.getDimensionPixelOffset(attr, startIconPaddingRight);
        } else if (attr == R.styleable.MenuItemLayout_startIconPaddingTop) {
            startIconPaddingTop = typedArray.getDimensionPixelOffset(attr, startIconPaddingTop);
        } else if (attr == R.styleable.MenuItemLayout_startIconPaddingBottom) {
            startIconPaddingBottom = typedArray.getDimensionPixelOffset(attr, startIconPaddingBottom);
        } else if (attr == R.styleable.MenuItemLayout_endIcon) {
            endIcon = typedArray.getDrawable(attr);
        } else if (attr == R.styleable.MenuItemLayout_endIconPadding) {
            if (typedArray.hasValue(attr)) {
                endIconPadding = typedArray.getDimensionPixelOffset(attr, endIconPadding);
                endIconPaddingLeft = endIconPadding;
                endIconPaddingTop = endIconPadding;
                endIconPaddingRight = endIconPadding;
                endIconPaddingBottom = endIconPadding;
            }
        } else if (attr == R.styleable.MenuItemLayout_endIconPaddingLeft) {
            endIconPaddingLeft = typedArray.getDimensionPixelOffset(attr, endIconPaddingLeft);
        } else if (attr == R.styleable.MenuItemLayout_endIconPaddingRight) {
            endIconPaddingRight = typedArray.getDimensionPixelOffset(attr, endIconPaddingRight);
        } else if (attr == R.styleable.MenuItemLayout_endIconPaddingTop) {
            endIconPaddingTop = typedArray.getDimensionPixelOffset(attr, endIconPaddingTop);
        } else if (attr == R.styleable.MenuItemLayout_endIconPaddingBottom) {
            endIconPaddingBottom = typedArray.getDimensionPixelOffset(attr, endIconPaddingBottom);
        } else if (attr == R.styleable.MenuItemLayout_mainText) {
            text = typedArray.getString(attr);
        } else if (attr == R.styleable.MenuItemLayout_mainTextSize) {
            textSize = typedArray.getDimensionPixelSize(attr, textSize);
        } else if (attr == R.styleable.MenuItemLayout_mainTextColor) {
            textColor = typedArray.getColor(attr, textColor);
        } else if (attr == R.styleable.MenuItemLayout_describtion) {
            describtion = typedArray.getString(attr);
        } else if (attr == R.styleable.MenuItemLayout_describtionTextSize) {
            describtionTextSize = typedArray.getDimensionPixelSize(attr, describtionTextSize);
        } else if (attr == R.styleable.MenuItemLayout_describtionTextColor) {
            describtionTextColor = typedArray.getColor(attr, describtionTextColor);
        } else if (attr == R.styleable.MenuItemLayout_lineColor) {
            lineColor = typedArray.getColor(attr, lineColor);
        } else if (attr == R.styleable.MenuItemLayout_lineHeight) {
            lineHeight = typedArray.getDimensionPixelOffset(attr, lineHeight);
        } else if (attr == R.styleable.MenuItemLayout_lineMarginStart) {
            lineMarginStart = typedArray.getDimensionPixelOffset(attr, lineMarginStart);
        } else if (attr == R.styleable.MenuItemLayout_lineMarginEnd) {
            lineMarginEnd = typedArray.getDimensionPixelOffset(attr, lineMarginEnd);
        }
    }

    private LinearLayout linearLayout;
    private ImageView startIconView, endIconView;
    private TextView textView, describtionView;
    private View lineView;
    private void addContent(Context context) {
        setOrientation(LinearLayout.VERTICAL);
        if(getBackground() == null) {
            // 获取波纹点击效果resourceId
            int resourceId = getResources().getIdentifier("item_background_material", "drawable", "android");
            setBackgroundResource(resourceId); // 默认背景
        }

        linearLayout = new LinearLayout(context);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(params);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER_VERTICAL);
        setLinearLayoutPadding();

        startIconView = new ImageView(context);
        setStartIconView();

        textView = new TextView(context);
        setMainText();

        describtionView = new TextView(context);
        describtionView.setGravity(Gravity.RIGHT);
        LayoutParams describParams = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        describtionView.setLayoutParams(describParams);
        setDescribtionView();

        endIconView = new ImageView(context);
        setEndIconView();

        linearLayout.addView(startIconView);
        linearLayout.addView(textView);
        linearLayout.addView(describtionView);
        linearLayout.addView(endIconView);

        lineView = new View(context);
        setLineView();

        addView(linearLayout);
        addView(lineView);
    }

    private void setLinearLayoutPadding() {
        if(linearLayout != null)
            linearLayout.setPadding(itemPaddingLeft, itemPaddingTop, itemPaddingRight, itemPaddingBottom);
    }

    private void setStartIconView() {
        if(startIconView != null) {
            startIconView.setImageDrawable(startIcon);
            startIconView.setPadding(startIconPaddingLeft, startIconPaddingTop, startIconPaddingRight, startIconPaddingBottom);
        }
    }

    private void setMainText() {
        if (textView != null) {
            textView.setText(text);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            textView.setTextColor(textColor);
        }
    }

    private void setDescribtionView() {
        if(describtionView != null) {
            describtionView.setText(describtion);
            describtionView.setTextSize(TypedValue.COMPLEX_UNIT_PX, describtionTextSize);
            describtionView.setTextColor(describtionTextColor);
        }
    }

    private void setEndIconView() {
        if(endIconView != null) {
            endIconView.setImageDrawable(endIcon);
            endIconView.setPadding(endIconPaddingLeft, endIconPaddingTop, endIconPaddingRight, endIconPaddingBottom);
        }
    }

    private void setLineView() {
        if(lineView != null) {
            lineView.setBackgroundColor(lineColor);
            LayoutParams lineParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, lineHeight);
            lineParams.setMargins(lineMarginStart, 0, lineMarginEnd, 0);
            lineView.setLayoutParams(lineParams);
        }
    }

    public void setItemPadding(int itemPaddingLeft, int itemPaddingTop, int itemPaddingRight, int itemPaddingBottom) {
        this.itemPaddingLeft = itemPaddingLeft;
        this.itemPaddingTop = itemPaddingTop;
        this.itemPaddingRight = itemPaddingRight;
        this.itemPaddingBottom = itemPaddingBottom;
        setLinearLayoutPadding();
    }

    public int getItemPaddingLeft() {
        return itemPaddingLeft;
    }

    public int getItemPaddingRight() {
        return itemPaddingRight;
    }

    public int getItemPaddingTop() {
        return itemPaddingTop;
    }

    public int getItemPaddingBottom() {
        return itemPaddingBottom;
    }

    public Drawable getStartIcon() {
        return startIcon;
    }

    public void setStartIcon(Drawable startIcon) {
        this.startIcon = startIcon;
        setStartIconView();
    }

    public void setStartIconPadding(int startIconPaddingLeft, int startIconPaddingTop, int startIconPaddingRight, int startIconPaddingBottom) {
        this.startIconPaddingLeft = startIconPaddingLeft;
        this.startIconPaddingTop = startIconPaddingTop;
        this.startIconPaddingRight = startIconPaddingRight;
        this.startIconPaddingBottom = startIconPaddingBottom;
        setStartIconView();
    }

    public int getStartIconPaddingLeft() {
        return startIconPaddingLeft;
    }

    public int getStartIconPaddingRight() {
        return startIconPaddingRight;
    }

    public int getStartIconPaddingTop() {
        return startIconPaddingTop;
    }

    public int getStartIconPaddingBottom() {
        return startIconPaddingBottom;
    }

    public Drawable getEndIcon() {
        return endIcon;
    }

    public void setEndIcon(Drawable endIcon) {
        this.endIcon = endIcon;
        setEndIconView();
    }

    public int getEndIconPadding() {
        return endIconPadding;
    }

    public void setEndIconPadding(int endIconPaddingLeft, int endIconPaddingTop, int endIconPaddingRight, int endIconPaddingBottom) {
        this.endIconPaddingLeft = endIconPaddingLeft;
        this.endIconPaddingTop = endIconPaddingTop;
        this.endIconPaddingRight = endIconPaddingRight;
        this.endIconPaddingBottom = endIconPaddingBottom;
        setEndIconView();
    }

    public int getEndIconPaddingLeft() {
        return endIconPaddingLeft;
    }

    public int getEndIconPaddingRight() {
        return endIconPaddingRight;
    }

    public int getEndIconPaddingTop() {
        return endIconPaddingTop;
    }

    public int getEndIconPaddingBottom() {
        return endIconPaddingBottom;
    }

    public CharSequence getText() {
        return text;
    }

    public void setText(int resId) {
        setText(getContext().getString(resId));
    }

    public void setText(CharSequence text) {
        this.text = text;
        setMainText();
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
        setMainText();
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        setMainText();
    }

    public CharSequence getDescribtion() {
        return describtion;
    }

    public void setDescribtion(int resId) {
        setDescribtion(getContext().getString(resId));
    }

    public void setDescribtion(CharSequence describtion) {
        this.describtion = describtion;
        setDescribtionView();
    }

    public int getDescribtionTextSize() {
        return describtionTextSize;
    }

    public void setDescribtionTextSize(int describtionTextSize) {
        this.describtionTextSize = describtionTextSize;
        setDescribtionView();
    }

    public int getDescribtionTextColor() {
        return describtionTextColor;
    }

    public void setDescribtionTextColor(int describtionTextColor) {
        this.describtionTextColor = describtionTextColor;
        setDescribtionView();
    }

    public int getLineColor() {
        return lineColor;
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
        setLineView();
    }

    public int getLineHeight() {
        return lineHeight;
    }

    public void setLineHeight(int lineHeight) {
        this.lineHeight = lineHeight;
        setLineView();
    }

    public void setLineMargin(int lineMarginStart, int lineMarginEnd) {
        this.lineMarginStart = lineMarginStart;
        this.lineMarginEnd = lineMarginEnd;
        setLineView();
    }

    public int getLineMarginStart() {
        return lineMarginStart;
    }

    public int getLineMarginEnd() {
        return lineMarginEnd;
    }

    public static int dp2px(Context context, float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
    }

    public static int sp2px(Context context, float spValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, context.getResources().getDisplayMetrics());
    }
}
