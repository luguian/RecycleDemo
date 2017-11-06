package com.nova.recycleviewdemo.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;

import com.nova.recycleviewdemo.R;
import com.nova.recycleviewdemo.adapter.MyAdapter;
import com.nova.recycleviewdemo.view.MyCustomRecycleView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private MyCustomRecycleView mrecycler;
    private MyAdapter madapter;
    private List<String> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mrecycler = (MyCustomRecycleView)findViewById(R.id.recycler);
        initRecyc();
    }

    private void initRecyc(){
        data = new ArrayList<>();
        for(int i = 1;i<10;i++){
            data.add("这是第"+i+1);
        }
        mrecycler.setLayoutManager(new LinearLayoutManager(this));
        madapter = new MyAdapter(data,this);
        mrecycler.setAdapter(madapter);
        madapter.addHeaderView(LayoutInflater.from(this).inflate(R.layout.item_header,null));
        madapter.addFooterView(LayoutInflater.from(this).inflate(R.layout.item_footer,null));
        mrecycler.setOnItemClickListen(new MyCustomRecycleView.OnItemClickListener() {
            @Override
            public void onItemclick(View view, int position) {

            }

            @Override
            public void onDeleteClick(int position) {
                if(position != madapter.getItemCount()){
                    if(madapter.haveHeaderView()){
                        madapter.removeItem(position-1);
                    }else{
                        madapter.removeItem(position);
                    }
                }else{
                    if(madapter.havaFooterView()){
                        madapter.removeItem(position-1);
                    }else{
                        madapter.removeItem(position);
                    }
                }


            }
        });
    }
}
