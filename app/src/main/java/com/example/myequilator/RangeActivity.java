package com.example.myequilator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.myequilator.adapters.AdapterForRange;

import java.util.HashSet;

public class RangeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.range_matrix);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_range);
        GridLayoutManager manager = new GridLayoutManager(this, 13, GridLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(new AdapterForRange(this,new HashSet<Integer>()));
    }
}
