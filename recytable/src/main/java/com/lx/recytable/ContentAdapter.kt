package com.lx.recytable

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.lx.recytable.databinding.TableItemCellTvBinding
import com.lx.recytable.databinding.TableItemScrollviewBinding

/**
 * @author: luoXiong
 * @date: 2019/4/29 17:30
 * @version: 1.0
 * @desc:
 */

class ContentAdapter(
    context: Context, private val tableView: RecycleTableView, val mContentCallBack: TableDataListener
) : RecyclerView.Adapter<ContentAdapter.TableBindingViewHolder<TableItemScrollviewBinding>>() {
    var mLayoutInflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): TableBindingViewHolder<TableItemScrollviewBinding> {
        val binding = DataBindingUtil.inflate<TableItemScrollviewBinding>(
            mLayoutInflater, R.layout.table_item_scrollview, parent, false
        )
        //把每个item里的scrollView添加到集合,通过遍历集合,实现同时滑动的效果
        tableView.addItemScrollViewToList(binding.hsScrollView)
        //动态创建每一行item里的单元格TextView
        tableView.createOneRowItemCellTv(binding.llFirstColumnContain, binding.llContainTv)

        println("onCreateViewHolder()....1111111.....")
        return TableBindingViewHolder(binding)
    }


    override fun onBindViewHolder(holder: TableBindingViewHolder<TableItemScrollviewBinding>, rowIndex: Int) {
        val binding = holder.binding
        val llContainTv = binding.llContainTv

        for (index in 0 until llContainTv.childCount) {
            //最左侧可固定的列设置数据
            if (index == 0) {
                val itemCellText = mContentCallBack.getItemCellText(rowIndex, 0)
                (binding.llFirstColumnContain.getChildAt(0) as TextView).text = itemCellText
            }
            //可滑动内容区域更新数据

            //可滑动内容区域更新数据
            val tv = llContainTv.getChildAt(index)
            // val itemCellText = mContentCallBack.getItemCellText(rowIndex, index + 1) //+1的作用是,index=0,默认就是第1列了
            //(tv as TextView).text = itemCellText

            val tvBinding = DataBindingUtil.getBinding<TableItemCellTvBinding>(tv)
            mContentCallBack.onBindViewHolderCallBack(tvBinding!!,rowIndex,index + 1)


//            val itemCellText = mContentCallBack.getItemCellText(rowIndex, index + 1)
//            (llContainTv.getChildAt(index) as TextView).text = itemCellText
//            mContentCallBack.onBindViewHolderCallBack(holder.binding, rowIndex, index)
        }
    }

    override fun getItemCount(): Int {
        println("getItemCount()....22222.....="+mContentCallBack.getRowSize())
        return mContentCallBack.getRowSize()
    }

    class TableBindingViewHolder<T : ViewDataBinding>(val binding: T) : RecyclerView.ViewHolder(binding.root)
}


//public class ContentAdapter<Group> extends GroupedRecyclerViewAdapter<Group> {
//    TableDataListener<Group> mContentCallBack;
//
//    public ContentAdapter(Context context, TableDataListener<Group> contentCallBack) {
//        super(context);
//        mContentCallBack = contentCallBack;
//    }
//
//    @Override
//    public int getChildrenCount(int groupPosition) {
//        return mContentCallBack.getChildrenCount(mDataList.get(groupPosition));
//    }
//
//    }
//
//    @Override
//    public Group getChildItem(Group groupEntity, int childPosition) {
//        return null;
//    public boolean hasHeader(int groupPosition) {
//        return false;
//    }
//
//    @Override
//    public boolean hasFooter(int groupPosition) {
//        return false;
//    }
//
//    @Override
//    public int getHeaderLayout(int viewType) {
//        return 0;
//    }
//
//    @Override
//    public int getFooterLayout(int viewType) {
//        return 0;
//    }
//
//    @Override
//    public int getChildLayout(int viewType) {
//        return 0;
//    }
//
//    @Override
//    public void onBindHeaderViewHolder(BindingViewHolder holder, int groupPosition) {
//
//    }
//
//    @Override
//    public void onBindFooterViewHolder(BindingViewHolder holder, int groupPosition) {
//
//    }
//}
