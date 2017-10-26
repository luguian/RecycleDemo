package com.nova.recycleviewdemo.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import com.nova.recycleviewdemo.R;
import com.nova.recycleviewdemo.adapter.MyAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mrecycler;
    private MyAdapter madapter;
    private List<String> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mrecycler = (RecyclerView)findViewById(R.id.recycler);
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
    }
}
