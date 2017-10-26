# RecycleDemo
给recycleView添加头部
ListView 与 RecycleView两者非常相似，都是依赖适配器。
RecycleView不像ListView拥有addHeaderView()与addFooterView()的方法简单添加头部尾部，而且RecycleView没有像ListView的列表点击监听方法（setItemOnclicjListener）.
那么如何给recycleView添加头部和尾部?
RecycleView中有getItemViewType(int position)这个方法是在执行onCreateViewHolder（ViewGroup parent,int viewType）前回调用viewType,目的是为了根据viewType不同创建不同的视图。那么就可以通过在oncreateViewHolde
创建视图的时候，对viewType进行判断，如果添加了头部，在poition = 0的时候回调头部的viewType的onCreateViewHolder,从而创建头部。尾部也是一样道理。
主要核心：


public MyAdapter.MyHolder onCreateViewHolder(ViewGroup parent,int viewType){
   if(viewType == TYPE_FOOTER){
       retrun new MyHolder(VIEW_HEADER);
   } else if(viewType == TYPE_HEADER){
       return new MyHolder(VIEW_HEADER);
   }else{
       retrun new MyHolder(getLsyout(R.layout.item_list_layout));
   }

}

public int getItemViewType(int position){
   if(isHeaderView(position)){
      return TYPE_HEADER;
   }else if(isFooterView(position)){
      return TYPE_FOOTER;
   }else{
      return TYPE_NORMAL;
   }


}

效果图如下：
![image](https://github.com/luguian/RecycleDemo/blob/master/Screenshot.png)
