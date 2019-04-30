package com.lx.recytable

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.HorizontalScrollView

/**
 * @author: luoXiong
 * @date: 2019/4/29 16:10
 * @version: 1.0
 * @desc: 表格内容布局的每个item
 */
class TableItemHorizontalScrollView : HorizontalScrollView {
    private var mScrollListener: HorizontalScrollListener? = null

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context) : super(context) {}

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        mScrollListener?.setCurrentTouchView(this)
        return super.onTouchEvent(ev)
    }


    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        //触摸1个item,联动其他的item一起滑动
        if (mScrollListener != null && mScrollListener!!.getCurrentTouchView() != null && mScrollListener!!.getCurrentTouchView() == this) {
            mScrollListener!!.onSingleItemScrollChanged(l, t, oldl, oldt)
        } else {
            super.onScrollChanged(l, t, oldl, oldt)
        }
    }

    fun setHorizontalScrollListener(mScrollListener: HorizontalScrollListener) {
        this.mScrollListener = mScrollListener
    }

    interface HorizontalScrollListener {

        fun getCurrentTouchView(): TableItemHorizontalScrollView?

        fun setCurrentTouchView(scrollView: TableItemHorizontalScrollView)

        fun onSingleItemScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int)
    }
}

