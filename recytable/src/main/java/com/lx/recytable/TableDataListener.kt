package com.lx.recytable

import com.lx.recytable.databinding.TableItemCellTvBinding

/**
 * @author: luoXiong
 * @date: 2019/4/29 16:10
 * @version: 1.0
 * @desc:
 */
interface TableDataListener {


    fun getColumnSize(): Int

    fun getRowSize(): Int

    fun getItemCellText(rowIndex: Int, columnIndex: Int): String

    fun getTitleText(rowIndex: Int, columnIndex: Int): String

    fun clickedView(view: TableItemCellTvBinding, columnIndex: Int)

    fun onBindViewHolderCallBack(binding: TableItemCellTvBinding, rowIndex: Int, columnIndex: Int)
}
