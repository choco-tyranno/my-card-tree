package com.choco_tyranno.mycardtree.card_crud_feature.presentation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.choco_tyranno.mycardtree.card_crud_feature.Logger;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardDTO;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.detail_page.DetailPageState;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.detail_page.DetailPageViewModel;
import com.choco_tyranno.mycardtree.databinding.ActivityDetailFrameBinding;
import com.choco_tyranno.mycardtree.databinding.ActivityMainFrameBinding;

public class DetailCardActivity extends AppCompatActivity {
    ActivityDetailFrameBinding binding;
    DetailPageViewModel viewModel;
    DetailPageState pageState;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(DetailCardActivity.this).get(DetailPageViewModel.class);

        Intent intent = getIntent();
        CardDTO ownerCard = (CardDTO) intent.getSerializableExtra("post_card");
        pageState = new DetailPageState();

        viewModel.setPageState(pageState);
        viewModel.initListeners();

        mainBinding();
        binding.setViewModel(viewModel);
        binding.setCard(ownerCard);
        binding.setPageState(pageState);
        binding.executePendingBindings();
    }

    private void mainBinding() {
        binding = ActivityDetailFrameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.setLifecycleOwner(this);
    }

    public ActivityDetailFrameBinding getBinding(){
        return binding;
    }

    @Override
    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();
    }
}