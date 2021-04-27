package com.choco_tyranno.mycardtree.card_crud_feature.presentation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.choco_tyranno.mycardtree.card_crud_feature.Logger;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv.ContainerAdapter;
import com.choco_tyranno.mycardtree.databinding.ActivityMainFrameBinding;

import java.util.Optional;

public class MainCardActivity extends AppCompatActivity {
    private ContainerAdapter layerAdapter;
    private LinearLayoutManager layerLM;
    private ActivityMainFrameBinding binding;

    int lastSeqNo = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding();
        setContainerRv();
        contractVm();
    }

    private void mainBinding() {
        binding = ActivityMainFrameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.setLifecycleOwner(this);
        binding.mainScreen.createCardFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                viewModel.addCard(new CardDTO.Builder().bossNo(0).containerNo(1).seqNo(lastSeqNo+1).build());
            }
        });
    }

    private void setContainerRv() {
        RecyclerView rv = binding.mainScreen.mainBody.containerRecyclerview;
        rv.setAdapter(new ContainerAdapter(this));
        rv.setLayoutManager(new LinearLayoutManager(MainCardActivity.this, LinearLayoutManager.VERTICAL, false));
    }

    private void contractVm() {
        CardTreeViewModel viewModel = new ViewModelProvider(MainCardActivity.this).get(CardTreeViewModel.class);
        viewModel.loadData(() -> {
            Optional.ofNullable(viewModel.getData()).ifPresent((liveData -> {
                        runOnUiThread(() -> {
                            liveData.observe(this, (cards) -> {
                                Logger.message("observe");
                                ((ContainerAdapter)binding.mainScreen.mainBody.containerRecyclerview.getAdapter()).submitList(cards);
                            });
                        });
                    })
            );
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        android.os.Process.killProcess(android.os.Process.myPid());
    }

}