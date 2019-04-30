package com.lx.recytable

/**
 * @author: luoXiong
 * @date: 2019/4/29 16:10
 * @version: 1.0
 * @desc:
 */
interface TableDataListener<Group> {

    fun getColumnSize(): Int

    fun getRowSize(): Int

    fun getItemCellText(rowIndex: Int, columnIndex: Int): String

    fun getTitleText(rowIndex: Int, columnIndex: Int): String
}
