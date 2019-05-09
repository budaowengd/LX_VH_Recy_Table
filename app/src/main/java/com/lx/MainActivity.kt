package com.lx

import android.content.res.Resources
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.Dimension
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.ObservableInt
import com.lx.recytable.RecycleTableView
import com.lx.recytable.TableDataListener
import com.lx.recytable.databinding.TableItemCellTvBinding
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var activity: AppCompatActivity
    lateinit var tableView: RecycleTableView
    var skuNum=10
    var selectSkuNum= ObservableInt(skuNum)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("selectSkuNum..1111...="+selectSkuNum.get()+"  skuNum="+skuNum)
        println("selectSkuNum...2222..="+User())
        activity = this
        setContentView(R.layout.activity_main)
        tableView = findViewById<RecycleTableView>(R.id.recycleTableView)

        initData(5,5)
        setAp()
        initTop()
    }

    fun click1(v: View) {
        initData(3,3)
        setAp()
    }

    fun click2(v: View) {
        initData(33,6)
        setAp()

    }
    fun click3(v: View) {
        initData(22,7)
        setAp()
    }

    private fun initTop() {
        val layout = findViewById<LinearLayout>(R.id.ll_top)
        setHorizontalDivider(layout, 100)
        tv1.post {
            //dp1 =3.5
            //dp10 =35
            println("tv1...宽度=" + tv1.width + "  tv2=" + tv2.width + "  layout=" + layout.width)
        }
    }

    private fun dp2px(@Dimension(unit = Dimension.DP) dp: Float): Int {
        val scale = Resources.getSystem().displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }


    private fun setHorizontalDivider(tvContain: LinearLayout, dividerWidth: Int) {
        //绘制分割线
        tvContain.showDividers = LinearLayout.SHOW_DIVIDER_BEGINNING or LinearLayout.SHOW_DIVIDER_END or LinearLayout.SHOW_DIVIDER_MIDDLE
        val gd = GradientDrawable()
        gd.setColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
        gd.setSize(dividerWidth, 0)
        tvContain.dividerDrawable = gd
    }
    val titleList = ArrayList<String>()
    val contentData = ArrayList<ArrayList<String>>()
    fun initData(columnSize:Int,rowSize:Int) {
        titleList.clear()
        contentData.clear()

        for (columnIndex in 0 until columnSize) {
            if (columnIndex == 0) {
                titleList.add("编号")
            } else {
                titleList.add("第" + columnIndex + "列")
            }
        }


        for (rowIndex in 0 until rowSize) {
            val rowList = ArrayList<String>()
            for (columnIndex in 0 until columnSize) {
                if (columnIndex == 0 && 3 == 2) {
                    rowList.add("编号")
                } else {
                    rowList.add("" + columnIndex + "列" + rowIndex + "行")
                }
            }
            contentData.add(rowList)
        }
        if (3 == 3) {
            println("整体的数据  行总数=" + contentData.size + "  列总数=" + titleList.size)
            println("整体的数据 titleList=" + contentData.size + "   list=" + titleList)
            println("整体的数据  contentData=" + contentData)
            // return
        }

    }

    fun setAp(){
        tableView.setAdapter(object : TableDataListener {
            override fun onBindViewHolderCallBack(tvBinding: TableItemCellTvBinding, rowIndex: Int, tvChildIndex: Int) {
                tvBinding.setItem(contentData[rowIndex][tvChildIndex])
            }

            private var lastClickedView: TextView? = null
            override fun clickedView(binding: TableItemCellTvBinding, columnIndex: Int) {
                //view.setTextIsSelectable()
                binding.tv1.isSelected = true
                lastClickedView?.isSelected = false
                lastClickedView = binding.tv1
            }

            override fun getTitleText(rowIndex: Int, columnIndex: Int): String {
                return titleList.get(columnIndex)
            }

            override fun getRowSize(): Int {
                return contentData.size
            }

            override fun getItemCellText(rowIndex: Int, columnIndex: Int): String {
                println("getItemCellText()....rowIndex=" + rowIndex + "  columnIndex=" + columnIndex + "  text=")
                //                if (rowIndex == 0) {
                //                    return titleList.get(columnIndex)
                //                }
                val text = contentData.get(rowIndex).get(columnIndex)
                return text
            }

            override fun getColumnSize(): Int {
                return titleList.size
            }


        })

    }
}
