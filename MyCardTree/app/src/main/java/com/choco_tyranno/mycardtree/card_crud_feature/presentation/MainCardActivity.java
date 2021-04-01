package com.choco_tyranno.mycardtree.card_crud_feature.presentation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;

import com.choco_tyranno.mycardtree.R;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.layer_rv.LayerAdapter;
import com.choco_tyranno.mycardtree.databinding.ActivityMainFrameBinding;

public class MainCardActivity extends AppCompatActivity {
    final String DEBUG_TAG = "CHOCO_ACTIVITY : ";
    RecyclerView layerRV;
    LayerAdapter layerAdapter;
    LinearLayoutManager layerLM;
    CardViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainFrameBinding binding = ActivityMainFrameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel =  new ViewModelProvider(MainCardActivity.this, ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(CardViewModel.class);

        viewModel.getData().observe(MainCardActivity.this,containerWithCards -> {

            Log.d(DEBUG_TAG,""+( (containerWithCards !=null ) ? containerWithCards.size() : "null" ));
        });

    }

    void testInit(){
        layerRV = findViewById(R.id.parent_recyclerview);
        layerAdapter = new LayerAdapter();
        layerAdapter.setHasStableIds(true);
        layerLM = new LinearLayoutManager(MainCardActivity.this, LinearLayoutManager.VERTICAL, false);
        layerRV.setLayoutManager(layerLM);
        layerRV.setAdapter(layerAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}