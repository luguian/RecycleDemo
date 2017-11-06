package com.nova.recycleviewdemo.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.nova.recycleviewdemo.R;
import com.nova.recycleviewdemo.adapter.MyAdapter;

/**
 * Created by lu on 2017/11/6.
 */

public class MyCustomRecycleView extends RecyclerView {

    private Context mContext;
    //上一次的触摸点
    private int mLastX,mLastY;
    //当前触摸的item的位置
    private int mPosition;
    //item对应的布局
    private LinearLayout mItemLayout;
    //删除按钮
    private TextView mDelete;
    //最大滑动距离(即删除按钮的宽度)
    private int mMaxLength;
    //是否在垂直滑动列表
    private boolean isDragging;
    //item是在否跟随手指移动
    private boolean isItemMoving;
    //item是否开始自动滑动
    private boolean isStartScroll;
    //删除按钮状态 0：关闭 1：将要关闭 2：将要打开 3：打开
    private int mDeleteBtnState;
    //检测手指在滑动过程中的速度
    private VelocityTracker mVelocityTracker;
    private Scroller mScroller;//处理滚动效果的工具类，实现View平滑滚动的一个Helper类
    private OnItemClickListener mListener;
    public MyCustomRecycleView(Context context) {
        this(context,null);
    }
    public MyCustomRecycleView(Context context, @Nullable AttributeSet attrs){
        this(context,attrs,0);
    }
    public MyCustomRecycleView(Context context,@Nullable AttributeSet attrs,int defStyle){
       super(context,attrs,defStyle);
       mContext = context;
        //初始化
       mScroller = new Scroller(context,new LinearInterpolator());
       // 1.当开始追踪的时候，使用obtain来获取VelocityTracker类的实例
       mVelocityTracker = VelocityTracker.obtain();
    }
    @Override
    public boolean onTouchEvent(MotionEvent e){
    //向VelocityTracker添加MotionEvent
        mVelocityTracker.addMovement(e);
        int x = (int) e.getX();
        int y = (int) e.getY();
        switch(e.getAction()) {
            case MotionEvent.ACTION_DOWN://松开

               if(mDeleteBtnState == 0){//删除按钮为关闭
                   View view = findChildViewUnder(x,y);//返回指定位置的view
                   if(view == null){
                       return false;
                   }
                   //得到需要删除的view
                   MyAdapter.MyHolder viewHolder = (MyAdapter.MyHolder)getChildViewHolder(view);
                   //获取根布局
                   if(viewHolder.layout !=null){
                       mItemLayout = viewHolder.layout;
                       //触摸item的位置
                       mPosition = viewHolder.getAdapterPosition();
                       mDelete = (TextView)mItemLayout.findViewById(R.id.item_delete);
                       mMaxLength = mDelete.getWidth();
                       mDelete.setOnClickListener(new OnClickListener() {
                           @Override
                           public void onClick(View v) {
                               mListener .onDeleteClick(mPosition);
                               mItemLayout.scrollTo(0,0);
                               mDeleteBtnState = 0;//关闭
                           }
                       });
                   }else{
                       return false;
                   }

               }else if(mDeleteBtnState == 3){//按钮状态是打开的
                   //开始一个动画控制，由(mItemLayout.getScrollX() , 0)在200时间内前进(-mMaxLength,0)个单位，即到达坐标为(mItemLayout.getScrollX()-mMaxLength , 0)
                   mScroller.startScroll(mItemLayout.getScrollX(),0,-mMaxLength,0,200);
                   invalidate();//重绘
                   mDeleteBtnState = 0;//按钮变为关闭
                   return false;
               }else{
                   return false;
               }
               break;
            case MotionEvent.ACTION_MOVE:
                int dx = mLastX -x;
                int dy =mLastY -y;
                //返回当前滑动View左边界的位置
                int scrollx = mItemLayout.getScrollX();
                if(Math.abs(dx) > Math.abs(dy)){//左边界检测，要有足够的滑动距离
                  isItemMoving = true;
                    if(scrollx + dx <= 0){//滑动距离不够
                        mItemLayout.scrollTo(0,0);//移动到原点
                        return true;
                    }else if(scrollx + dx >= mMaxLength){//右边界检测，往右边滑动大于能滑动距离
                        mItemLayout.scrollTo(mMaxLength, 0);
                        return true;
                    }
                    mItemLayout.scrollBy(dx, 0);//item跟随手指滑动
                }

                break;
            case MotionEvent.ACTION_UP:
                //没有移动 不在垂直滑动列表 监听不为空
                if (!isItemMoving && !isDragging && mListener != null) {
                    mListener.onItemclick(mItemLayout, mPosition);
                }
                isItemMoving = false;

                mVelocityTracker.computeCurrentVelocity(1000);//计算手指滑动的速度
                float xVelocity = mVelocityTracker.getXVelocity();//水平方向速度（向左为负）
                float yVelocity = mVelocityTracker.getYVelocity();//垂直方向速度

                int deltaX = 0;//到达的X坐标
                int upScrollX = mItemLayout.getScrollX();//手指离开屏幕时item的左上角x坐标
                if(Math.abs(xVelocity) > 100 && Math.abs(xVelocity) > Math.abs(yVelocity)){
                   if(xVelocity <= -100){//左滑速度大于100，则删除按钮显示
                       deltaX = mMaxLength - upScrollX;
                       mDeleteBtnState = 2;//删除按钮即将打开
                   }else if(xVelocity > 100){//右滑速度大于100，则删除按钮隐藏
                       deltaX = -upScrollX;
                       mDeleteBtnState = 1;//删除按钮将要关闭
                   }
                }else{
                  if(upScrollX >= mMaxLength / 2){//item的左滑动距离大于删除按钮宽度的一半，则显示删除按钮
                      deltaX = mMaxLength - upScrollX;
                      mDeleteBtnState = 2;//删除按钮即将打开
                  }else if(upScrollX < mMaxLength / 2){//否则隐藏
                      deltaX = -upScrollX;
                      mDeleteBtnState = 1;//删除按钮即将关闭
                  }
                }
                //item自动滑动到指定位置 滚动，startX, startY为开始滚动的位置，dx,dy为滚动的偏移量, duration为完成滚动的时间 默认为250
                mScroller.startScroll(upScrollX, 0, deltaX, 0, 200);
                isStartScroll = true;
                invalidate();

                mVelocityTracker.clear();
                break;

        }
        mLastX = x;
        mLastY = y;
        return super.onTouchEvent(e);
    }
    //当我们执行ontouch或invalidate(）或postInvalidate()都会导致这个方法的执行
    @Override
    public void computeScroll(){
        //先判断mScroller滚动是否完成
        if (mScroller.computeScrollOffset()) {
            //这里调用View的scrollTo()完成实际的滚动
            mItemLayout.scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        } else if (isStartScroll) {//滚动没有完成
            isStartScroll = false;
            if (mDeleteBtnState == 1) {//将要关闭
                mDeleteBtnState = 0;//变为关闭
            }

            if (mDeleteBtnState == 2) {//将要打开
                mDeleteBtnState = 3;//打开
            }
        }
    }
  //  在destroy view的时候调用，所以可以加入取消广播注册等的操作
    @Override
    protected void onDetachedFromWindow() {
        mVelocityTracker.recycle();
        super.onDetachedFromWindow();
    }
    //监听滑动状态
    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        isDragging = state == SCROLL_STATE_DRAGGING;
    }

    public interface OnItemClickListener{
        /**
         *
         * item点击回调
         */
        void onItemclick(View view,int position);
        /**
         *
         * 删除按钮的回调
         */
        void onDeleteClick(int position);
    }

    public void setOnItemClickListen(OnItemClickListener listener){
        mListener = listener;

    }
}
