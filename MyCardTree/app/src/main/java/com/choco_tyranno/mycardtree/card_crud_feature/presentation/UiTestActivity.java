package com.choco_tyranno.mycardtree.card_crud_feature.presentation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.choco_tyranno.mycardtree.R;
import com.choco_tyranno.mycardtree.card_crud_feature.Logger;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv.CardAdapter;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class UiTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ui_test);

        List<Integer> itemList = new ArrayList<>();
        for (int i =0; i < 10; i++){
            itemList.add(i);
        }


        AtomicBoolean flag = new AtomicBoolean(true);
        RecyclerView rv = findViewById(R.id.test_rv);
        MaterialButton fireBtn = findViewById(R.id.fire_btn);
        fireBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag.set(!flag.get());
            }
        });

        rv.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false){
            @Override
            public boolean canScrollVertically() {
//                final boolean canScroll = flag.get();
//                flag.set(!canScroll);
                return flag.get();
//                return super.canScrollVertically();
            }
        });
        rv.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new TestViewHolder(
                        LayoutInflater.from(UiTestActivity.this).inflate(R.layout.item_test_card, parent, false)
                );
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                ((TestViewHolder)holder).bind(itemList.get(position));
            }

            @Override
            public int getItemCount() {
                return itemList.size();
            }
        });

    }

    public static class TestViewHolder extends RecyclerView.ViewHolder{
        TextView textView;

        public TestViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.test_textView);
        }

        public void bind(int text){
            textView.setText(String.valueOf(text));
        }
    }
}