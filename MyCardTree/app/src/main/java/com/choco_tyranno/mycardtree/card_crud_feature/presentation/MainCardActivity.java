package com.choco_tyranno.mycardtree.card_crud_feature.presentation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.choco_tyranno.mycardtree.R;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.layer.LayerAdapter;

public class MainCardActivity extends AppCompatActivity {
    RecyclerView layerRV;
    LayerAdapter layerAdapter;
    LinearLayoutManager layerLM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_frame);
        testInit();

    }

    void testInit(){
        layerRV = findViewById(R.id.parent_recyclerview);
        layerAdapter = new LayerAdapter();
        layerAdapter.setHasStableIds(true);
        layerLM = new LinearLayoutManager(MainCardActivity.this, LinearLayoutManager.VERTICAL, false);
        layerRV.setLayoutManager(layerLM);
        layerRV.setAdapter(layerAdapter);
        layerAdapter.setTestData();
    }
}