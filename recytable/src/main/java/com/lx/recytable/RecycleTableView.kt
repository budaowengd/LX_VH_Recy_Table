package com.lx.recytable

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.Dimension
import androidx.annotation.Dimension.DP
import androidx.collection.SparseArrayCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lx.recytable.databinding.TableItemScrollviewBinding
import java.util.*

/**
 * @author: luoXiong
 * @date: 2019/4/29 16:28
 * @version: 1.0
 * @desc: 最外层表格布局
 */
class RecycleTableView : LinearLayout, TableItemHorizontalScrollView.HorizontalScrollListener {


    private var currentTouchView: TableItemHorizontalScrollView? = null

    private lateinit var mContext: Context
    private lateinit var mInflater: LayoutInflater

    /**
     * 是否显示第1行标题
     */
    private var mIsShowFirstRow: Boolean = true

    /**
     * 是否固定第1列不能滑动
     */
    private var mIsMovedFirstColumn: Boolean = false


    /**
     * 第1行, 也就是标题行中的每一列的宽度，
     * 所有的行里的每一列宽度都是等于标题行的每一列的宽度
     * key: 第1行每列的索引,0...N
     * value: 第1行每列宽度
     */
    private val mColumnWidthMap = SparseArrayCompat<Int>()

    //存放所有的HScrollView
    protected var mHScrollViews: MutableList<TableItemHorizontalScrollView> = ArrayList<TableItemHorizontalScrollView>()

    //用于显示表格正文内容
    private var mRecyclerView: RecyclerView? = null
    private var contextAdapter: ContentAdapter? = null
    private var titleBinding: TableItemScrollviewBinding? = null
    private lateinit var mDataCallBack: TableDataListener


    //    private var mItemCellTvPaddingTop = 0
    //    private var mItemCellTvPaddingBottom = 0

    private var mTitleCellTvPaddingStart = 0
    private var mTitleCellTvPaddingEnd = 0
    private var mTitleCellTvPaddingTop = 0
    private var mTitleCellTvPaddingBottom = 0

    private var mItemCellTvTextNormalColor = 0
    private var mItemCellTvTextSelectedColor = 0
    private var mItemCellTvTextSize = 0
    private var mItemCellTvTextHeight = 0
    private var mDividerColor = 0
    private var mBgColor = 0
    private var mDividerWidth = 0


    private var mItemCellTvBgSelectedColor = 0 //单元格背景选中颜色
    private var mItemCellTvBgNormalColor = 0  //单元格背景默认颜色

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        orientation = VERTICAL
        mContext = context
        this.mInflater = LayoutInflater.from(context)

        val leftDefaultPadding = dp2px(12f)
        val topDefaultPadding = dp2px(16f)

        if (attrs != null) {
            val blackColor = ContextCompat.getColor(mContext, android.R.color.holo_red_dark)
            // val tvCellBgNormalColor = ContextCompat.getColor(mContext, android.R.color.white)
            val attr = resources.obtainAttributes(attrs, R.styleable.lx_recycle_table)

            mTitleCellTvPaddingStart = attr.getDimensionPixelSize(R.styleable.lx_recycle_table_title_text_padding_start, leftDefaultPadding)
            mTitleCellTvPaddingEnd = attr.getDimensionPixelSize(R.styleable.lx_recycle_table_title_text_padding_end, leftDefaultPadding)
            mTitleCellTvPaddingTop = attr.getDimensionPixelSize(R.styleable.lx_recycle_table_title_text_padding_top, topDefaultPadding)
            mTitleCellTvPaddingBottom =
                attr.getDimensionPixelSize(R.styleable.lx_recycle_table_title_text_padding_bottom, topDefaultPadding)

            //mItemCellTvPaddingTop = attr.getDimensionPixelSize(R.styleable.lx_recycle_table_item_text_padding_top, topDefaultPadding)
            //mItemCellTvPaddingBottom = attr.getDimensionPixelSize(R.styleable.lx_recycle_table_item_text_padding_bottom, topDefaultPadding)


            mItemCellTvTextNormalColor = attr.getColor(R.styleable.lx_recycle_table_item_text_normal_color, blackColor)
            mItemCellTvTextSelectedColor = attr.getColor(R.styleable.lx_recycle_table_item_text_selected_color, 0)
            mItemCellTvBgSelectedColor = attr.getColor(R.styleable.lx_recycle_table_item_text_selected_bg_color, 0)
            mItemCellTvBgNormalColor = attr.getColor(R.styleable.lx_recycle_table_item_text_normal_bg_color, 0)
            mItemCellTvTextSize = attr.getInt(R.styleable.lx_recycle_table_item_text_size, 14)
            mDividerWidth = attr.getDimensionPixelSize(R.styleable.lx_recycle_table_divider_width, dp2px(1f))
            mItemCellTvTextHeight = attr.getDimensionPixelSize(R.styleable.lx_recycle_table_item_text_height, 0)
            mDividerColor = attr.getColor(R.styleable.lx_recycle_table_divider_color, blackColor)
            mBgColor = attr.getColor(R.styleable.lx_recycle_table_bg_color, ContextCompat.getColor(mContext, android.R.color.white))
        }
        //内部分割线
         createDivider(0)

        //外部四周分割线
        // setSelfBorderDivider()
        println("mDividerWidth=" + mDividerWidth)
    }

    /**
     * flag = 0 : 设置当前TableView布局的内部Divider
     */
    private fun createDivider(flag: Int) {
        if (flag == 0) {
            showDividers = SHOW_DIVIDER_BEGINNING or SHOW_DIVIDER_MIDDLE
            val gd = GradientDrawable()
            gd.setColor(mDividerColor)
            gd.setSize(0, mDividerWidth)
            dividerDrawable = gd
        }
    }


    private fun setHorizontalDivider(tvContain: LinearLayout, showDividers: Int) {
        //绘制分割线
        tvContain.showDividers = showDividers
        val gd = GradientDrawable()
        gd.setColor(mDividerColor)
        gd.setSize(mDividerWidth, 0)
        tvContain.dividerDrawable = gd
    }


    private fun dp2px(@Dimension(unit = DP) dp: Float): Int {
        val scale = Resources.getSystem().displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

    /**
     * 设置当前布局四周的分割线
     */
    //    private fun setSelfBorderDivider() {
    //        //图层第1级 item底部线的颜色
    //        val strokeGdOne = GradientDrawable()
    //        strokeGdOne.setStroke(mDividerWidth, mDividerColor)
    //        //图层第2级,背景色
    //        val solidGdTwo = GradientDrawable()
    //        solidGdTwo.setColor(mBgColor) //设置solid
    //        //创建图层
    //        val layers = arrayOf<Drawable>(strokeGdOne, solidGdTwo)
    //        val layerDrawable = LayerDrawable(layers)
    //        layerDrawable.setLayerInset(1, mDividerWidth, mDividerWidth, mDividerWidth, mDividerWidth)
    //        setBackground(layerDrawable)
    //    }

    /**
     * 设置adapter
     */
    // fun setAdapter(contentAdapter: VHBaseAdapter, dataListener: TableDataListener) {
    fun setAdapter(dataCallBack: TableDataListener) {
        mDataCallBack = dataCallBack
        //清除原始数据
        cleanup()
        if (mRecyclerView == null) {
            //载入标题行
            initTitle()
            //载入表格正文
            initContentList()

            addView(titleBinding!!.root)
            addView(mRecyclerView, -1, -1)
        } else {
            println("复用了当前表格View....111111111.......")
            createOneRowTitleItemCellTv(titleBinding!!.llFirstColumnContain, titleBinding!!.llContainTv)
            contextAdapter = ContentAdapter(mContext, this, mDataCallBack)
            mRecyclerView!!.adapter = contextAdapter
        }


        //        if (!mIsShowFirstRow) {
        //            //假如设置了不显示标题行，在这里隐藏掉
        //            getChildAt(0).visibility = View.GONE
        //        }
    }

    //private fun initContentList(contentAdapter: VHBaseAdapter) {
    private fun initContentList() {
        mRecyclerView = RecyclerView(context)
        mRecyclerView!!.layoutManager = LinearLayoutManager(mContext)
        contextAdapter = ContentAdapter(mContext, this, mDataCallBack)
        //创建分割线
        val itemDecoration = DividerItemDecoration(mContext, LinearLayout.VERTICAL)
        val gd = GradientDrawable()
        gd.setColor(mDividerColor)
        gd.setSize(0, mDividerWidth)
        itemDecoration.setDrawable(gd)
        mRecyclerView!!.addItemDecoration(itemDecoration)
        mRecyclerView!!.adapter = contextAdapter
    }


    private fun initTitle() {
        titleBinding = DataBindingUtil.inflate<TableItemScrollviewBinding>(
            mInflater, R.layout.table_item_scrollview, this, false
        )
        addItemScrollViewToList(titleBinding!!.hsScrollView)
        val llFirstColumnContain = titleBinding!!.llFirstColumnContain
        val llContainTv = titleBinding!!.llContainTv
        createOneRowTitleItemCellTv(llFirstColumnContain, llContainTv)
    }

    /**
     * 创建第1行标题的单元格
     */
    fun createOneRowTitleItemCellTv(llFirstColumnContain: LinearLayout, llContainTv: LinearLayout) {
        llFirstColumnContain.removeAllViews()
        llContainTv.removeAllViews()

        //绘制分割线
        setHorizontalDivider(llFirstColumnContain, (SHOW_DIVIDER_BEGINNING or SHOW_DIVIDER_END))
        setHorizontalDivider(llContainTv, (SHOW_DIVIDER_MIDDLE or SHOW_DIVIDER_END))
        for (columnIndex in 0 until mDataCallBack.getColumnSize()) {
            val tvTitleCell = TextView(mContext)
            tvTitleCell.setPadding(mTitleCellTvPaddingStart, mTitleCellTvPaddingTop, mTitleCellTvPaddingEnd, mTitleCellTvPaddingBottom)

            tvTitleCell.text = mDataCallBack.getTitleText(0, columnIndex)
            tvTitleCell.gravity = Gravity.CENTER
            tvTitleCell.setTextSize(TypedValue.COMPLEX_UNIT_SP, mItemCellTvTextSize.toFloat())
            tvTitleCell.setTextColor(mItemCellTvTextNormalColor)
            tvTitleCell.measure(0, 0)
            // tvTitleCell.setBackgroundColor(ContextCompat.getColor(mContext, android.R.color.holo_orange_light))
            val tvWidth = tvTitleCell.measuredWidth + mTitleCellTvPaddingStart + mTitleCellTvPaddingEnd
            if (columnIndex == 0) {
                llFirstColumnContain.addView(tvTitleCell, tvWidth, -2)
            } else {
                llContainTv.addView(tvTitleCell, tvWidth, -2)
            }
            mColumnWidthMap.put(columnIndex, tvWidth)

            if (mItemCellTvTextHeight == 0) {
                mItemCellTvTextHeight = tvTitleCell.measuredHeight + mTitleCellTvPaddingTop + mTitleCellTvPaddingBottom
            }
            println("标题行...每个itme的..宽度=" + tvWidth + "  measuredWidth=" + tvTitleCell.measuredWidth)
        }

    }

    /**
     * 创建第2..N行的单元格
     * 内容布局的宽度: 和第1行标题的宽度一致
     * 高度:固定高度, 为了更好的体验,避免在onBindHolder里频繁计算每一行的高度.
     */
    fun createOneRowItemCellTv(llFirstColumnContain: LinearLayout, llContainTv: LinearLayout) {
        //绘制分割线
        setHorizontalDivider(llFirstColumnContain, (SHOW_DIVIDER_BEGINNING or SHOW_DIVIDER_END))
        setHorizontalDivider(llContainTv, (SHOW_DIVIDER_MIDDLE or SHOW_DIVIDER_END))

        for (columnIndex in 0 until mDataCallBack.getColumnSize()) {
            //设置padding没用,因为高度必须固定,宽度是跟随标题行的
            //tvCell.setPadding(0, mItemCellTvPaddingTop, 0, mItemCellTvPaddingBottom)

            val tvCellWidth = mColumnWidthMap.get(columnIndex) ?: -2
            if (columnIndex == 0) {
                val tvCell = TextView(mContext)
                tvCell.gravity = Gravity.CENTER
                tvCell.setTextSize(TypedValue.COMPLEX_UNIT_SP, mItemCellTvTextSize.toFloat())
                tvCell.setTextColor(mItemCellTvTextNormalColor)
                llFirstColumnContain.addView(tvCell, tvCellWidth, mItemCellTvTextHeight)
            } else {
                val binding = DataBindingUtil.inflate<com.lx.recytable.databinding.TableItemCellTvBinding>(
                    mInflater, R.layout.table_item_cell_tv, llContainTv, false
                )
                val tvCell = binding.root as TextView

                llContainTv.addView(tvCell, tvCellWidth, mItemCellTvTextHeight)
                tvCell.setOnClickListener {
                    mDataCallBack.clickedView(binding, columnIndex)
                }

                //设置背景选择器
                if (mItemCellTvBgSelectedColor != 0 && mItemCellTvBgNormalColor != 0) {
                    tvCell.background = createTvCellBackgroundResource()
                }
                //设置文字颜色选择器
                if (mItemCellTvTextSelectedColor == 0) {
                    tvCell.setTextColor(mItemCellTvTextNormalColor)
                } else {
                    tvCell.setTextColor(createTvCellTextColor())
                }
            }
        }
    }

    /**
     * 设置单元格文字颜色选择器
     */
    private fun createTvCellTextColor(): ColorStateList {
        val colors = intArrayOf(mItemCellTvTextNormalColor, mItemCellTvTextSelectedColor)
        val states = arrayOfNulls<IntArray>(2)
        states[0] = intArrayOf(-android.R.attr.state_selected)
        states[1] = intArrayOf(android.R.attr.state_selected)
        return ColorStateList(states, colors)
    }

    /**
     * 设置单元格背景选择器
     */
    private fun createTvCellBackgroundResource(): StateListDrawable {
        //创建选择器集合
        val drawableList = StateListDrawable()
        //默认效果
        drawableList.addState(intArrayOf(-android.R.attr.state_selected), ColorDrawable(mItemCellTvBgNormalColor))
        //选中效果
        drawableList.addState(intArrayOf(android.R.attr.state_selected), ColorDrawable(mItemCellTvBgSelectedColor))
        return drawableList
    }


    fun addItemScrollViewToList(hScrollView: TableItemHorizontalScrollView) {
        if (mHScrollViews.isNotEmpty()) {
            val size = mHScrollViews.size
            val scrollView = mHScrollViews[size - 1]
            val scrollX = scrollView.scrollX
            //这是给第一次满屏，或者快速下滑等情况时，新创建的会再创建一个convertView的时候，
            // 把这个新进入的convertView里的HListViewScrollView移到对应的位置
            if (scrollX != 0) {
                //                titleBinding?.root?.post(Runnable {
                //                    //在主线程中去移动到对应的位置
                //                    hScrollView.scrollTo(scrollX, 0)
                //                })
            }
        }
        hScrollView.setHorizontalScrollListener(this)
        mHScrollViews.add(hScrollView)
    }

    private fun cleanup() {
       // removeAllViews()
        mColumnWidthMap.clear()
        mHScrollViews.clear()
    }

    override fun onSingleItemScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        for (scrollView in mHScrollViews) {
            //防止重复滑动
            if (currentTouchView !== scrollView) scrollView.smoothScrollTo(l, t)
        }
    }

    override fun getCurrentTouchView(): TableItemHorizontalScrollView? {
        return currentTouchView
    }

    override fun setCurrentTouchView(scrollView: TableItemHorizontalScrollView) {
        currentTouchView = scrollView
    }


}
