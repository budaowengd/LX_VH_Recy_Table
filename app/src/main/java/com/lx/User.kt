package com.lx

import androidx.databinding.ObservableInt
import androidx.versionedparcelable.ParcelField

/**
 * @author: luoXiong
 * @date: 2019/5/8 10:29
 * @version: 1.0
 * @desc:
 */
data class User(
    var skuNum:Int =10,
    var selectSkuNum : ObservableInt= ObservableInt(skuNum)
){

    override fun toString(): String {
        return "User(skuNum=$skuNum, selectSkuNum=${selectSkuNum.get()})"
    }

}
