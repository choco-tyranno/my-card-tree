package com.choco_tyranno.mycardtree.card_crud_feature.presentation.layer;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

public class LayerAdapter extends RecyclerView.Adapter<LayerAdapter.CardLayerViewHolder> {

    List<String> testLayerData;

    public LayerAdapter(){

    }

    @NonNull
    @Override
    public CardLayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull CardLayerViewHolder holder, int position) {

    }


    @Override
    public int getItemCount() {
        return 0;
    }

    abstract class LayerViewHolder extends RecyclerView.ViewHolder {

        public LayerViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    class CardLayerViewHolder extends LayerViewHolder{

        public CardLayerViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public void setTestData(){
        String[] testLayerNumData = {"1", "2", "3"};
        testLayerData.addAll(Arrays.asList(testLayerNumData));

    }

}
