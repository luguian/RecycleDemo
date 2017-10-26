package com.nova.recycleviewdemo.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nova.recycleviewdemo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lu on 2017/10/25.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyHolder>{

    private RecyclerView mRecyclerView;
    private List<String> data = new ArrayList<>();
    private Context mContext;

    private View VIEW_FOOTER;
    private View VIEW_HEADER;

    //TYPE
    private int TYPE_NORMAL = 1000;
    private int TYPE_HEADER = 1001;
    private int TYPE_FOOTER = 1002;

    public MyAdapter(List<String> data,Context mContext){
        this.data = data;
        this.mContext = mContext;

    }


    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_FOOTER){
            return new MyHolder(VIEW_FOOTER);
        }else if(viewType == TYPE_HEADER){
            return new MyHolder(VIEW_HEADER);
        }else{
            return new MyHolder(getLayout(R.layout.item_list_layout));
        }
    }
    private View getLayout(int layoutId){
        return LayoutInflater.from(mContext).inflate(layoutId,null);

    }
    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        if(!isHeaderView(position) && !isFooterView(position)){
            if(haveHeaderView()) position--;
            TextView content = (TextView) holder.itemView.findViewById(R.id.item_list);
            TextView time = (TextView) holder.itemView.findViewById(R.id.item_time);
            content.setText(data.get(position));

        }

    }

    @Override
    public int getItemCount() {
        int count = (data == null ? 0 :data.size());
        if(VIEW_FOOTER != null){
          count++;
        }
        if(VIEW_HEADER != null){
            count++;
        }
        return count;
    }

    @Override
    public int getItemViewType(int position){
        if(isHeaderView(position)){
            return TYPE_HEADER;
        }else if(isFooterView(position)){
            return TYPE_FOOTER;
        } else{
            return TYPE_NORMAL;
        }

    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView){
        try{
            if(mRecyclerView == null && mRecyclerView != recyclerView){
                mRecyclerView =recyclerView;
            }
            ifGridLayoutManager();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static class MyHolder extends RecyclerView.ViewHolder{
        public MyHolder(View itemView){
            super(itemView);

        }
    }

    //是否有头部
    private boolean haveHeaderView(){
        return VIEW_HEADER != null;
    }
    //是否有尾部
    private boolean havaFooterView(){
        return VIEW_FOOTER != null;
    }
    //判断是否是头部
    private boolean isHeaderView(int position){
        return haveHeaderView() && position == 0;
    }
    //判断是否是尾部
    private boolean isFooterView(int position){
        return havaFooterView() && position == getItemCount() -1;
    }
    public void addHeaderView(View headerView){
        if(haveHeaderView()){
            throw new IllegalStateException("haveview has already exists!");
        }else{
            //避免出现宽度自适应
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            headerView.setLayoutParams(params);
            VIEW_HEADER = headerView;
            ifGridLayoutManager();
            notifyItemInserted(0);
        }

    }
    public void addFooterView(View footerView){
        if(havaFooterView()){
            throw new IllegalStateException("haveview has already exists!");
        }else{
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            footerView.setLayoutParams(params);
            VIEW_FOOTER = footerView;
            ifGridLayoutManager();
            notifyItemInserted(getItemCount() - 1);
        }

    }
    private void ifGridLayoutManager(){
        if(mRecyclerView == null) return;
        final RecyclerView.LayoutManager layoutManager =mRecyclerView.getLayoutManager();
        if(layoutManager instanceof GridLayoutManager){
            final GridLayoutManager.SpanSizeLookup originalSpanSizeLookup =
                    ((GridLayoutManager) layoutManager).getSpanSizeLookup();
            ((GridLayoutManager) layoutManager).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return (isHeaderView(position) || isFooterView(position) ?
                            ((GridLayoutManager) layoutManager).getSpanCount() : 1);
                }
            });

        }
    }
}
