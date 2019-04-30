package com.lx.recytable

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
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
public class RecycleTableView<T> : LinearLayout, TableItemHorizontalScrollView.HorizontalScrollListener {


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
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mDataCallBack: TableDataListener<T>

    private var mItemCellTvPaddingStart = 0
    private var mItemCellTvPaddingEnd = 0
    private var mItemCellTvPaddingTop = 0
    private var mItemCellTvPaddingBottom = 0
    private var mItemCellTvTextColor = 0
    private var mItemCellTvTextSize = 0
    private var mItemCellTvTextHeight = 200
    private var mDividerColor = 0
    private var mBgColor = 0
    private var mDividerWidth = 0

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


        if (attrs != null) {
            val blackColor = ContextCompat.getColor(mContext, android.R.color.holo_red_dark)
            val attr = resources.obtainAttributes(attrs, R.styleable.lx_recycle_table)
            mItemCellTvPaddingStart = attr.getDimensionPixelSize(R.styleable.lx_recycle_table_item_text_padding_start, 0)
            mItemCellTvPaddingEnd = attr.getDimensionPixelSize(R.styleable.lx_recycle_table_item_text_padding_end, 0)
            mItemCellTvPaddingTop = attr.getDimensionPixelSize(R.styleable.lx_recycle_table_item_text_padding_top, 0)
            mItemCellTvPaddingBottom = attr.getDimensionPixelSize(R.styleable.lx_recycle_table_item_text_padding_bottom, 0)
            mItemCellTvTextColor = attr.getColor(R.styleable.lx_recycle_table_item_text_color, blackColor)
            mItemCellTvTextSize = attr.getInt(R.styleable.lx_recycle_table_item_text_size, 14)
            mDividerWidth = attr.getDimensionPixelSize(R.styleable.lx_recycle_table_divider_width, dp2px(1f))
            mDividerColor = attr.getColor(R.styleable.lx_recycle_table_divider_color, blackColor)
            mBgColor = attr.getColor(R.styleable.lx_recycle_table_bg_color, ContextCompat.getColor(mContext, android.R.color.white))
        }
        //内部分割线
        showDividers = SHOW_DIVIDER_BEGINNING or SHOW_DIVIDER_MIDDLE or SHOW_DIVIDER_END
        val gd = GradientDrawable()
        gd.setColor(mDividerColor)
        gd.setSize(0, mDividerWidth)
        dividerDrawable = gd

        //外部四周分割线
        setSelfBorderDivider()
        println("mDividerWidth=" + mDividerWidth)
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
    private fun setSelfBorderDivider(){
        //图层第1级 item底部线的颜色
        val strokeGdOne = GradientDrawable()
        strokeGdOne.setStroke( mDividerWidth, mDividerColor )

        //图层第2级,背景色
        val solidGdTwo = GradientDrawable()
        solidGdTwo.setColor(mBgColor) //设置solid

        //创建图层
        val layers = arrayOf<Drawable>(strokeGdOne, solidGdTwo)
        val layerDrawable = LayerDrawable(layers)
        layerDrawable.setLayerInset(1,mDividerWidth,mDividerWidth,mDividerWidth,mDividerWidth)
         setBackground(layerDrawable)
    }

    /**
     * 设置adapter
     */
    // fun setAdapter(contentAdapter: VHBaseAdapter<T>, dataListener: TableDataListener<T>) {
    fun setAdapter(dataCallBack: TableDataListener<T>) {
        mDataCallBack = dataCallBack
        //清除原始数据
        cleanup()
        //载入标题行
        val titleRow = initTitle()
        //载入表格正文
        initContentList()

        addView(titleRow)
        addView(mRecyclerView, -1, -1)

        if (!mIsShowFirstRow) {
            //假如设置了不显示标题行，在这里隐藏掉
            getChildAt(0).visibility = View.GONE
        }
    }

    //private fun initContentList(contentAdapter: VHBaseAdapter<T>) {
    private fun initContentList() {
        mRecyclerView = RecyclerView(context)
        mRecyclerView.layoutManager = LinearLayoutManager(mContext)
        val adapter = ContentAdapter<T>(mContext, this, mDataCallBack)
        //创建分割线
        val itemDecoration = DividerItemDecoration(mContext, LinearLayout.VERTICAL)
        val gd = GradientDrawable()
        gd.setColor(mDividerColor)
        gd.setSize(0, mDividerWidth)
        itemDecoration.setDrawable(gd)
        mRecyclerView.addItemDecoration(itemDecoration)
        mRecyclerView.adapter = adapter
    }


    private fun initTitle(): View {
        val titleBinding = DataBindingUtil.inflate<TableItemScrollviewBinding>(
            mInflater, R.layout.table_item_scrollview, this, false
        )
        addItemScrollViewToList(titleBinding.hsScrollView)
        val llFirstColumnContain = titleBinding.llFirstColumnContain
        val llContainTv = titleBinding.llContainTv
        createOneRowTitleItemCellTv(llFirstColumnContain, llContainTv)
        return titleBinding.root
    }

    /**
     * 创建第1行标题的单元格
     */
    fun createOneRowTitleItemCellTv(llFirstColumnContain: LinearLayout, llContainTv: LinearLayout) {
        //绘制分割线
        setHorizontalDivider(llFirstColumnContain, (SHOW_DIVIDER_BEGINNING or SHOW_DIVIDER_END))
        setHorizontalDivider(llContainTv, (SHOW_DIVIDER_MIDDLE or SHOW_DIVIDER_END))
        for (columnIndex in 0 until mDataCallBack.getColumnSize()) {
            val tvTitleCell = TextView(mContext)

            tvTitleCell.setPadding(50, 40, 50, 40)
            tvTitleCell.text = mDataCallBack.getTitleText(0, columnIndex)
            tvTitleCell.gravity = Gravity.CENTER
            tvTitleCell.setTextSize(TypedValue.COMPLEX_UNIT_SP, mItemCellTvTextSize.toFloat())
            tvTitleCell.setTextColor(mItemCellTvTextColor)
            tvTitleCell.measure(0, 0)
           // tvTitleCell.setBackgroundColor(ContextCompat.getColor(mContext, android.R.color.holo_orange_light))
            val tvWidth = tvTitleCell.measuredWidth
            if (columnIndex == 0) {
                llFirstColumnContain.addView(tvTitleCell, tvWidth, -2)
            } else {
                llContainTv.addView(tvTitleCell, tvWidth, -2)
            }
            mColumnWidthMap.put(columnIndex, tvWidth)
            println("标题行...每个itme的..宽度=" + tvWidth)
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
            val tvCell = TextView(mContext)
            // tvCell.setPadding(mItemCellTvPaddingStart, mItemCellTvPaddingTop, mItemCellTvPaddingEnd, mItemCellTvPaddingBottom)
            //  tvCell.setBackgroundColor(ContextCompat.getColor(mContext, android.R.color.holo_purple))
            tvCell.gravity = Gravity.CENTER
            tvCell.setTextColor(mItemCellTvTextColor)
            tvCell.setTextSize(TypedValue.COMPLEX_UNIT_SP, mItemCellTvTextSize.toFloat())
            val tvCellWidth = mColumnWidthMap.get(columnIndex) ?: -2
            println("内容行....每个单元格..宽度 =" + tvCellWidth + "  列=" + columnIndex)
            if (columnIndex == 0) {
                llFirstColumnContain.addView(tvCell, tvCellWidth, mItemCellTvTextHeight)
            } else {
                llContainTv.addView(tvCell, tvCellWidth, mItemCellTvTextHeight)
            }
        }

    }


    fun addItemScrollViewToList(hScrollView: TableItemHorizontalScrollView) {
        if (mHScrollViews.isNotEmpty()) {
            val size = mHScrollViews.size
            val scrollView = mHScrollViews[size - 1]
            val scrollX = scrollView.scrollX
            //这是给第一次满屏，或者快速下滑等情况时，新创建的会再创建一个convertView的时候，
            // 把这个新进入的convertView里的HListViewScrollView移到对应的位置
            if (scrollX != 0) {
                mRecyclerView.post(Runnable {
                    //在主线程中去移动到对应的位置
                    hScrollView.scrollTo(scrollX, 0)
                })
            }
        }
        hScrollView.setHorizontalScrollListener(this)
        mHScrollViews.add(hScrollView)
    }

    private fun cleanup() {
        removeAllViews()
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
