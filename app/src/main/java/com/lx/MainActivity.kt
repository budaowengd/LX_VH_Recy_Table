package com.lx

import android.content.res.Resources
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.widget.LinearLayout
import androidx.annotation.Dimension
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.lx.recytable.RecycleTableView
import com.lx.recytable.TableDataListener
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var activity: AppCompatActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = this
        setContentView(R.layout.activity_main)
        initAdapter()
        initTop()
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

    fun initAdapter() {
        val columnSize = 7
        val rowSize = 16
        val titleList = ArrayList<String>()
        for (columnIndex in 0 until columnSize) {
            if (columnIndex == 0) {
                titleList.add("编号")
            } else {
                titleList.add("第" + columnIndex + "列列列")
            }
        }

        val contentData = ArrayList<ArrayList<String>>()
        for (rowIndex in 0 until rowSize) {
            val rowList = ArrayList<String>()
            for (columnIndex in 0 until columnSize) {
                if (columnIndex == 0 && 3==3) {
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

        val tableView = findViewById<RecycleTableView<Grade>>(R.id.recycleTableView)
        tableView.setAdapter(object : TableDataListener<Grade> {
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
