package com.moonsky.customflowlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义流式布局
 * Created by Nick on 2017/9/9.
 */

public class FlowLayout extends ViewGroup {

    /**
     * 用来保存每行views的列表
     */
    private List<List<View>> mViewLinesList = new ArrayList<>();
    /**
     * 用来保存行高的列表
     */
    private List<Integer> mLineHeights = new ArrayList<>();

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //决定该ViewGroup的LayoutParams
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        //建议的宽高
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        //测量后的宽高
        int measureWidth = 0;
        int measureHeight = 0;

        //测量后行的宽高
        int currentLineWidth = 0;
        int currentLineHeight = 0;

        //TODO:方案一 计算出所有的childView的宽和高
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        //onMeasure有可能被多次调用
        mViewLinesList.clear();
        mLineHeights.clear();

        int childWidth;
        int childHeight;
        List<View> childList = new ArrayList<>();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            //TODO:方案二 计算出childView的宽和高
//                measureChild(childView, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams mlp = (MarginLayoutParams) childView.getLayoutParams();

            childWidth = childView.getMeasuredWidth() + mlp.leftMargin + mlp.rightMargin;
            childHeight = childView.getMeasuredHeight() + mlp.topMargin + mlp.bottomMargin;

            //需要换行的处理
            if (currentLineWidth + childWidth > widthSize) {

                measureWidth = Math.max(measureWidth, currentLineWidth);
                measureHeight += currentLineHeight;

                mViewLinesList.add(childList);
                mLineHeights.add(currentLineHeight);

                //重新赋值新一行的宽、高
                currentLineWidth = childWidth;
                currentLineHeight = childHeight;

                //新建一行，添加新一行的view
                childList = new ArrayList<>();
                childList.add(childView);

            } else {
                //如果子View累加没有超过当前行的宽度---不换行的处理
                currentLineWidth += childWidth;
                currentLineHeight = Math.max(currentLineHeight, childHeight);
                childList.add(childView);
            }

            //最后一行的处理
            if (i == childCount - 1) {
                //记录当前行的最大宽度，高度累加
                measureWidth = Math.max(measureWidth, currentLineWidth);
                measureHeight += currentLineHeight;

                //将当前行的viewList添加至总的mViewsList，将行高添加至总的行高List
                mViewLinesList.add(childList);
                mLineHeights.add(currentLineHeight);
            }
        }

        if (widthMode == MeasureSpec.EXACTLY) {
            measureWidth = widthSize;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            measureHeight = heightSize;
        }

        setMeasuredDimension(measureWidth, measureHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int left, top, right, bottom;
        int currentLeft = 0;
        int currentTop = 0;

        int lineCount = mViewLinesList.size();
        for (int i = 0; i < lineCount; i++) {

            List<View> views = mViewLinesList.get(i);
            int lineViewSizes = views.size();

            for (int j = 0; j < lineViewSizes; j++) {
                View childView = views.get(j);
                MarginLayoutParams mlp = (MarginLayoutParams) childView.getLayoutParams();

                left = currentLeft + mlp.leftMargin;
                top = currentTop + mlp.topMargin;

                right = left + childView.getMeasuredWidth();
                bottom = top + childView.getMeasuredHeight();

                childView.layout(left, top, right, bottom);

                currentLeft += childView.getMeasuredWidth() + mlp.leftMargin + mlp.rightMargin;

            }
            currentLeft = 0;
            currentTop += mLineHeights.get(i);
        }
        mViewLinesList.clear();
        mLineHeights.clear();
    }


    /**
     * 对外开放的点击事件
     *
     * @param listener
     */
    public void setOnItemClickListener(final OnItemClickListener listener) {
        int childCount = getChildCount();

        for (int i = 0; i < childCount; i++) {
            final View childAtView = getChildAt(i);
            final int finalI = i;
            childAtView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(childAtView, finalI);
                }
            });
        }
    }
}
